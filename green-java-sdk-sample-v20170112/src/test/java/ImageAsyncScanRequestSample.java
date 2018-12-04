import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.aliyuncs.green.model.v20180509.ImageAsyncScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * Created by liuhai.lh on 2017/2/17.
 * 鍥剧墖寮傛妫�娴嬫帴鍙�
 * @author liuhai.lh
 * @date 2017/02/17
 */
public class ImageAsyncScanRequestSample extends BaseSample{

    public static void main(String[] args) throws Exception {
        //璇锋浛鎹㈡垚浣犺嚜宸辩殑accessKeyId銆乤ccessKeySecret
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint(getEndPointName(), regionId, "Green", getDomain());
        IAcsClient client = new DefaultAcsClient(profile);

        ImageAsyncScanRequest imageAsyncScanRequest = new ImageAsyncScanRequest();
        imageAsyncScanRequest.setAcceptFormat(FormatType.JSON); // 鎸囧畾api杩斿洖鏍煎紡
        imageAsyncScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 鎸囧畾璇锋眰鏂规硶
        imageAsyncScanRequest.setEncoding("utf-8");
        imageAsyncScanRequest.setRegionId(regionId);


        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
        task1.put("url", "http://starrank-1254164914.cossh.myqcloud.com/youxi/7e9535c0-0ad0-48cc-88f6-cd182656a14b.png");
        task1.put("time", new Date());

        tasks.add(task1);
        JSONObject data = new JSONObject();
        /**
         * porn: 鑹叉儏
         * terrorism: 鏆存亹
         * qrcode: 浜岀淮鐮�
         * ad: 鍥剧墖骞垮憡
         * ocr: 鏂囧瓧璇嗗埆
         */
        data.put("scenes", Arrays.asList("porn", "ocr", "qrcode", "sface"));
        data.put("tasks", tasks);

        imageAsyncScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);

        /**
         * 璇峰姟蹇呰缃秴鏃舵椂闂�
         */
        imageAsyncScanRequest.setConnectTimeout(3000);
        imageAsyncScanRequest.setReadTimeout(6000);

        try {
            HttpResponse httpResponse = client.doAction(imageAsyncScanRequest);

            if(httpResponse.isSuccess()){
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
                System.out.println(JSON.toJSONString(scrResponse, true));
                if (200 == scrResponse.getInteger("code")) {
                    JSONArray taskResults = scrResponse.getJSONArray("data");
                    for (Object taskResult : taskResults) {
                        if(200 == ((JSONObject)taskResult).getInteger("code")){
                            String taskId = ((JSONObject)taskResult).getString("taskId");
                            // 灏唗askId 淇濆瓨涓嬫潵锛岄棿闅斾竴娈垫椂闂存潵杞缁撴灉, 鍙傜収ImageAsyncScanResultsRequest
                            System.out.println("args = [" + taskId + "]");
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
