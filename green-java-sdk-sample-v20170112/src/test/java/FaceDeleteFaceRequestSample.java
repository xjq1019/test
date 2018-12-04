import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20180509.DeleteFacesRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * Created by wangxuan on 2018/06/25.
 * 删除人脸
 *
 * @author wangxuan
 * @date 2018/06/25
 */
public class FaceDeleteFaceRequestSample extends BaseSample {

    public static void main(String[] args) throws Exception {
        //请替换成你自己的accessKeyId、accessKeySecret
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint(getEndPointName(), regionId, "Green", getDomain());
        IAcsClient client = new DefaultAcsClient(profile);

        DeleteFacesRequest deleteFacesRequest = new DeleteFacesRequest();
        deleteFacesRequest.setAcceptFormat(FormatType.JSON); // 指定api返回格式
        deleteFacesRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法
        deleteFacesRequest.setEncoding("utf-8");

        JSONObject data = new JSONObject();
        /**
         * personId: 用户自定义个体Id，必填
         * faceIds: 已添加的人脸Id
         */
        data.put("personId", "personId_test_3");
        data.put("faceIds", Arrays.asList("31820666292926465"));

        deleteFacesRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);

        /**
         * 请务必设置超时时间
         */
        deleteFacesRequest.setConnectTimeout(3000);
        deleteFacesRequest.setReadTimeout(6000);

        try {
            HttpResponse httpResponse = client.doAction(deleteFacesRequest);

            if (httpResponse.isSuccess()) {
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
                System.out.println(JSON.toJSONString(scrResponse, true));
                if (200 == scrResponse.getInteger("code")) {
                    JSONObject resultObject = scrResponse.getJSONObject("data");
                    if (200 == resultObject.getInteger("code")) {
                        System.out.println(resultObject.getString("personId"));
                    } else {
                        System.out.println("task process fail:" + resultObject.getInteger("code"));
                    }
                } else {
                    System.out.println("detect not success. code:" + scrResponse.getInteger("code"));
                }
            } else {
                System.out.println("response not success. status:" + httpResponse.getStatus());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
