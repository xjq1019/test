import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * Created by hyliu on 16/3/2.
 * 鏂囨湰妫�娴�
 */
public class TextAntispamScanSample extends BaseSample {

    public static void main(String[] args) throws Exception {
        //璇锋浛鎹㈡垚浣犺嚜宸辩殑accessKeyId銆乤ccessKeySecret
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint(getEndPointName(), regionId, "Green", getDomain());

        IAcsClient client = new DefaultAcsClient(profile);

        TextScanRequest textScanRequest = new TextScanRequest();
        textScanRequest.setAcceptFormat(FormatType.JSON); // 鎸囧畾api杩斿洖鏍煎紡
        textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 鎸囧畾璇锋眰鏂规硶
        textScanRequest.setEncoding("UTF-8");
        textScanRequest.setRegionId(regionId);


        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
        task1.put("content", "[嚎哭][可怜][呲牙][鬼脸][惊恐][可怜][呲牙][惊讶][流感][害羞][鬼脸][惊讶][惊恐][惊恐][嚎哭][流泪][流泪][可怜][可怜][嚎哭][嚎哭][呲牙][呲牙][鬼脸][鬼脸][惊讶][惊讶][怒][惊恐][惊恐][嚎哭][可怜][可怜][嚎哭][流泪][流泪][流泪][可怜][敲][敲][敲][惊恐][惊恐][惊恐][敲][敲][呲牙][惊讶][惊讶][怒][怒][惊恐][惊恐][可怜][可怜][可怜][调皮][调皮][流泪][流泪][可怜][可怜][可怜][可怜][汗]");

        tasks.add(task1);
        

       /* Map<String, Object> task2 = new LinkedHashMap<String, Object>();
        task2.put("dataId", UUID.randomUUID().toString());
        task2.put("content", "钂欐睏鑽硶杞姛");

        tasks.add(task2);

        Map<String, Object> task3 = new LinkedHashMap<String, Object>();
        task3.put("dataId", UUID.randomUUID().toString());
        task3.put("content", "姝ｅ父浜�");

        tasks.add(task3);*/
        JSONObject data = new JSONObject();
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);

        textScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);

        /**
         * 璇峰姟蹇呰缃秴鏃舵椂闂�
         */
        textScanRequest.setConnectTimeout(3000);
        textScanRequest.setReadTimeout(6000);
        try {
            HttpResponse httpResponse = client.doAction(textScanRequest);

            if(httpResponse.isSuccess()){
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
                System.out.println(JSON.toJSONString(scrResponse, true));
                if (200 == scrResponse.getInteger("code")) {
                    JSONArray taskResults = scrResponse.getJSONArray("data");
                    for (Object taskResult : taskResults) {
                        if(200 == ((JSONObject)taskResult).getInteger("code")){
                            JSONArray sceneResults = ((JSONObject)taskResult).getJSONArray("results");
                            for (Object sceneResult : sceneResults) {
                                String scene = ((JSONObject)sceneResult).getString("scene");
                                String suggestion = ((JSONObject)sceneResult).getString("suggestion");
                                //鏍规嵁scene鍜宻uggetion鍋氱浉鍏崇殑澶勭悊
                                //do something
                                System.out.println("args = [" + scene + "]");
                                System.out.println("args = [" + suggestion + "]");
                            }
                        }else{
                            System.out.println("task process fail:" + ((JSONObject)taskResult).getInteger("code"));
                        }
                    }
                } else {
                    System.out.println("detect not success. code:" + scrResponse.getInteger("code"));
                }
            }else{
                System.out.println("response not success. status:" + httpResponse.getStatus());
            }

        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
