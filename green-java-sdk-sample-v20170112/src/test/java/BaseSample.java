import java.io.IOException;
import java.util.Properties;

/**
 * Created by liuhai.lh on 17/01/12.
 */
public class BaseSample {

    protected static String accessKeyId = "LTAIc5NLM8g6z7kk";
    protected static String accessKeySecret = "PI6EjUyCiq1GKDcUgUKRV6Jl5Wlys1";

    protected static String regionId = "cn-shanghai";

    static {
/*        Properties properties = new Properties();

        try {
            properties.load(BaseSample.class.getResourceAsStream("config.properties"));
            accessKeyId = properties.getProperty("accessKeyId");
            accessKeySecret = properties.getProperty("accessKeySecret");
            regionId = properties.getProperty("regionId");
        } catch(IOException e) {
            e.printStackTrace();
        }*/

    }


    protected static String getDomain(){
         if("cn-shanghai".equals(regionId)){
             return "green.cn-shanghai.aliyuncs.com";
         }

         if ("cn-beijing".equals(regionId)) {
            return "green.cn-beijing.aliyuncs.com";
         }

         if ("ap-southeast-1".equals(regionId)) {
            return "green.ap-southeast-1.aliyuncs.com";
         }

         if ("us-west-1".equals(regionId)) {
            return "green.us-west-1.aliyuncs.com";
         }

         return "green.cn-shanghai.aliyuncs.com";
    }

    protected static String getEndPointName(){
        return regionId;
    }

}
