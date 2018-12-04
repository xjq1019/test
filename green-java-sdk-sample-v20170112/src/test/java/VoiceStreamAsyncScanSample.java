import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.green.model.v20180509.VoiceAsyncScanRequest;
import com.aliyuncs.green.model.v20180509.VoiceAsyncScanResultsRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.*;

/**
 * 语音流检测示例
 */
public class VoiceStreamAsyncScanSample extends BaseSample {

    public static void main(String[] args) throws Exception {
        //请替换成你自己的accessKeyId、accessKeySecret
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint(getEndPointName(), regionId, "Green", getDomain());
        final IAcsClient client = new DefaultAcsClient(profile);

        VoiceAsyncScanRequest asyncScanRequest = new VoiceAsyncScanRequest(); //class different vs common
        asyncScanRequest.setAcceptFormat(FormatType.JSON); // 指定api返回格式
        asyncScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法
        asyncScanRequest.setRegionId(regionId);
        asyncScanRequest.setConnectTimeout(3000);
        asyncScanRequest.setReadTimeout(6000);

        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        //FIXME  修改这里的语音流地址
        tasks.add(buildTask("http://xxxxxxxxxxxxxx"));
        JSONObject data = new JSONObject();

        System.out.println("==========Task count:" + tasks.size());
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);
        asyncScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);
        System.out.println(JSON.toJSONString(data, true));

        try {
            HttpResponse httpResponse = client.doAction(asyncScanRequest);

            if (httpResponse.isSuccess()) {
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
                System.out.println(JSON.toJSONString(scrResponse, true));
                if (200 == scrResponse.getInteger("code")) {
                    JSONArray taskResults = scrResponse.getJSONArray("data");
                    for (Object taskResult : taskResults) {
                        Integer code = ((JSONObject) taskResult).getInteger("code");
                        if (200 == code) {
                            final String taskId = ((JSONObject) taskResult).getString("taskId");
                            System.out.println("submit async task success, taskId = [" + taskId + "]");
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        pollingScanResult(client, taskId);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        } else {
                            System.out.println("task process fail: " + code);
                        }
                    }
                } else {
                    System.out.println("detect not success. code: " + scrResponse.getInteger("code"));
                }
            } else {
                System.out.println("response not success. status: " + httpResponse.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> buildTask(String streamUrl) {
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("type", "stream");
        task1.put("url", streamUrl);
        return task1;
    }

    public static void pollingScanResult(IAcsClient client, String taskId) throws InterruptedException {
        int failCount = 0;
        boolean stop = false;
        do {
            // 10秒查询一次
            Thread.sleep(10 * 1000);
            JSONObject scanResult = getScanResult(client, taskId);
            if (scanResult == null || 200 != scanResult.getInteger("code")) {
                failCount++;
                System.out.println(taskId + ": get result fail, failCount=" + failCount);
                if (scanResult != null) {
                    System.out.println(taskId + ": errorMsg:" + scanResult.getString("msg"));
                }
                if (failCount > 20) {
                    break;
                }
                continue;
            }

            JSONArray taskResults = scanResult.getJSONArray("data");
            if (taskResults.isEmpty()) {
                System.out.println("failed");
                break;
            }

            for (Object taskResult : taskResults) {
                JSONObject result = (JSONObject) taskResult;
                Integer code = result.getInteger("code");
                if (280 == code) {
                    System.out.println(taskId + ": processing status: " + result.getString("msg"));
                } else if (200 == code) {
                    System.out.println(taskId + ": ========== SUCCESS ===========");
                    System.out.println(JSON.toJSONString(scanResult, true));
                    System.out.println(taskId + ": ========== SUCCESS ===========");
                    stop = true;
                } else {
                    System.out.println(taskId + ": ========== FAILED ===========");
                    System.out.println(JSON.toJSONString(scanResult, true));
                    System.out.println(taskId + ": ========== FAILED ===========");
                    stop = true;
                }
            }
        } while (!stop);
    }

    private static JSONObject getScanResult(IAcsClient client, String taskId) {
        VoiceAsyncScanResultsRequest getResultsRequest = new VoiceAsyncScanResultsRequest();
        getResultsRequest.setAcceptFormat(FormatType.JSON); // 指定api返回格式
        getResultsRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法
        getResultsRequest.setEncoding("utf-8");
        getResultsRequest.setRegionId(regionId);


        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("taskId", taskId);
        tasks.add(task1);

        /**
         * 请务必设置超时时间
         */
        getResultsRequest.setConnectTimeout(3000);
        getResultsRequest.setReadTimeout(6000);

        try {
            getResultsRequest.setHttpContent(JSON.toJSONString(tasks).getBytes("UTF-8"), "UTF-8", FormatType.JSON);

            HttpResponse httpResponse = client.doAction(getResultsRequest);
            if (httpResponse.isSuccess()) {
                return JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
            } else {
                System.out.println("response not success. status: " + httpResponse.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
