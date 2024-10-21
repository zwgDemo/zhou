package com.spzx.user.service.impl;

import com.spzx.user.service.ISmsService;
import com.spzx.user.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ISmsServiceImpl implements ISmsService {

    //发送短信验证码的方法
    @Override
    public Boolean sendPhoneCode(String phone, String code) {
//        String host = "https://dfsns.market.alicloudapi.com";
//        String path = "/data/send_sms";
//        String method = "POST";
//
//        //你自己的AppCode
//        String appcode = "938a8491cea74e43a48edf0a67070000";
//
//        Map<String, String> headers = new HashMap<String, String>();
//        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
//        headers.put("Authorization", "APPCODE " + appcode);
//        //根据API的要求，定义相对应的Content-Type
//        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//        Map<String, String> querys = new HashMap<String, String>();
//        Map<String, String> bodys = new HashMap<String, String>();
//
//        //验证码
//        bodys.put("content", "code:"+code);
//        //内容模板，测试模板
//        bodys.put("template_id", "CST_ptdie100");  //注意，CST_ptdie100该模板ID仅为调试使用，调试结果为"status": "OK" ，即表示接口调用成功，然后联系客服报备自己的专属签名模板ID，以保证短信稳定下发
//        //手机号
//        bodys.put("phone_number", phone);
//
//        try {
//            /**
//             * 使用HttpClient方式远程调用阿里云接口
//             */
//            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//            System.out.println(response.toString());
//            //获取response的body
//            //System.out.println(EntityUtils.toString(response.getEntity()));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "938a8491cea74e43a48edf0a67070051";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("**code**:");
        stringBuffer.append(code);
        stringBuffer.append(",");
        stringBuffer.append("**minute**:5");
        querys.put("param", stringBuffer.toString());

//smsSignId（短信前缀）和templateId（短信模板），可登录国阳云控制台自助申请。参考文档：http://help.guoyangyun.com/Problem/Qm.html

        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从\r\n\t    \t* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java\r\n\t    \t* 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {
        String code = "12345";
        Map<String, String> map = new HashMap<String, String>();

        map.put("**minute**","5");
        map.put("**code**",code);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("**minute**:");
        stringBuffer.append(code);
        stringBuffer.append(",");
        stringBuffer.append("**minute**:5");
        System.out.println(stringBuffer.toString());
    }
}
