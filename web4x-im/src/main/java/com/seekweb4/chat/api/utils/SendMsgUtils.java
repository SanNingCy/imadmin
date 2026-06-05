package com.seekweb4.chat.api.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;

/**
 * 复刻发短信工具类
 * @author: fxq
 * @Date 2024-11-26 17:11
 */
public class SendMsgUtils {

    /**
     * 短信发送接口
     *
     * ### 调用传递的 JSON 格式 ###
     * {
     *     "apiKey": "your_shared_api_key_here",         // 必填：请求 API 密钥，用于验证权限
     *     "areaCode": "86",                             // 必填：手机号区号（例如中国为 "86"）
     *     "phoneNumber": "13800138000",                 // 必填：接收短信的手机号
     *     "verificationCode": "123456",                 // 必填：短信验证码（外部生成或传入）
     *     "requestType": "register",                    // 必填：请求类型（如 "register", "login", "modify_password"）
     *     "clientIp": "192.168.1.1"                     // 必填：客户端用户的外网 IP 地址
     * }
     *
     * ### 返回格式 ###
     * 成功响应：
     * {
     *     "statusCode": 200,
     *     "message": "短信发送成功"
     * }
     *
     * 错误响应：
     * {
     *     "statusCode": 400,                            // 状态码：400（参数错误）、403（未授权）、429（请求过于频繁）、500（服务器内部错误）
     *     "message": "错误描述"
     * }
     */
    public static JSONObject sendMsg(String qu,String phone,String code,String ip){
        JSONObject param = new JSONObject();
        //param.put("apiKey","");
        param.put("areaCode",qu);
        param.put("phoneNumber",phone);
        param.put("verificationCode",code);
        param.put("requestType","login");
        param.put("clientIp",ip);
        String post = HttpUtil.post("http://127.0.0.1:9188/sendsms", param.toJSONString());
        JSONObject resu = JSONObject.parseObject(post);
        return resu;
    }

}
