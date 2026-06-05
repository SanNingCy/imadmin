//package com.seekweb4.chat.common.utils;
//
//import cn.hutool.core.util.RandomUtil;
//import cn.hutool.http.HttpUtil;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.seekweb4.chat.config.properties.AppProperites;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.zip.Deflater;
//
///**
// * 腾讯即时通讯IM工具类
// */
//@Slf4j
//@Component
//public class TencentIMUtils {
//    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TencentIMUtils.class);
//    private static long SDKAPPID;
//    private static String SECRETKEY;
//    private static String IDENTIFIER;
//    private static final int EXPIRETIME = 604800;
//    private static final String URL = "https://console.tim.qq.com";
//    private static String avatar = AppProperites.newInstance().filePath + AppProperites.USERFILES_BASE_URL + "icon.png";
//
//    @Autowired
//    private Environment env;
//    //@PostConstruct
//    private void init() {
//        SDKAPPID = Long.parseLong(env.getProperty("tencentIM.sdkappid"));
//        SECRETKEY = env.getProperty("tencentIM.secretkey");
//        IDENTIFIER = env.getProperty("tencentIM.identifier");
//    }
//    /**
//     * 单个账号导入
//     * @param userId 用户id
//     * @param nickname	用户昵称
//     * @param icon 	用户头像
//     * @return
//     */
//    public static void account_import(String userId, String nickname, String icon) {
//        String reqUrl = URL + "/v4/im_open_login_svc/account_import?sdkappid=" + SDKAPPID + "&identifier=" + IDENTIFIER + "&usersig=" + getUserSig(IDENTIFIER) + "&random=" + getRandom() + "&contenttype=json";
//        JSONObject data = new JSONObject();
//        data.put("UserID", userId);
//        data.put("Nick", nickname);
//        data.put("FaceUrl", StringUtils.isNotBlank(icon) ? icon : avatar);
//        String body = data.toString();
//        log.debug("单个账号导入请求参数:{}", body);
//        String result = HttpUtil.post(reqUrl, body);
//        log.debug("单个账号导入请求结果:{}", result);
//    }
//    /**
//     * 单发单聊文本消息
//     * @param From_Account 发送账号
//     * @param To_Account 接收账号
//     * @param content 消息内容
//     * @return
//     */
//    public static void sendTextMsg(String From_Account, String To_Account, String content) {
//        JSONArray MsgBody = new JSONArray();
//        JSONObject body = new JSONObject();
//        body.put("MsgType", "TIMTextElem");
//        JSONObject msgContent = new JSONObject();
//        msgContent.put("Text", content);
//        body.put("MsgContent", msgContent);
//        MsgBody.add(body);
//        sendmsg(From_Account, To_Account, MsgBody);
//    }
//    /**
//     * 单发单聊自定义消息
//     * @param From_Account 发送账号
//     * @param To_Account 接收账号
//     * @param content 消息内容
//     * @return
//     */
//    public static void sendCustomerMsg(String From_Account, String To_Account, String content) {
//        JSONArray MsgBody = new JSONArray();
//        JSONObject body = new JSONObject();
//        body.put("MsgType", "TIMCustomElem");
//        JSONObject msgContent = new JSONObject();
//        msgContent.put("Data", content);
//        msgContent.put("Desc", "自定义消息");
//        msgContent.put("Ext", content);
//        body.put("MsgContent", msgContent);
//        MsgBody.add(body);
//        sendmsg(From_Account, To_Account, MsgBody);
//    }
//    /**
//     * 单发单聊消息
//     * @param From_Account 发送账号
//     * @param To_Account 接收账号
//     * @param MsgBody 消息内容
//     * @return
//     */
//    public static void sendmsg(String From_Account, String To_Account, JSONArray MsgBody) {
//        String reqUrl = URL + "/v4/openim/sendmsg?sdkappid=" + SDKAPPID + "&identifier=" + IDENTIFIER + "&usersig=" + getUserSig(IDENTIFIER) + "&random=" + getRandom() + "&contenttype=json";
//        JSONObject data = new JSONObject();
//        data.put("From_Account", From_Account);
//        data.put("To_Account", To_Account);
//        data.put("MsgBody", MsgBody);
//        data.put("MsgRandom", getRandom());
//        String result = HttpUtil.post(reqUrl, data.toString());
//        log.debug("单发单聊消息请求结果:{}", result);
//    }
//    /**
//     * 设置用户资料
//     * @param userId 用户id
//     * @param nickname	用户昵称
//     * @param icon 	用户头像
//     * @return
//     */
//    public static void portrait_set(String userId, String nickname, String icon) {
//        String reqUrl = URL + "/v4/profile/portrait_set?sdkappid=" + SDKAPPID + "&identifier=" + IDENTIFIER + "&usersig=" + getUserSig(IDENTIFIER) + "&random=" + getRandom() + "&contenttype=json";
//        JSONObject data = new JSONObject();
//        data.put("From_Account", userId);
//        JSONArray profileItem = new JSONArray();
//        if (StringUtils.isNotBlank(nickname)) {
//            JSONObject tag = new JSONObject();
//            tag.put("Tag", "Tag_Profile_IM_Nick");
//            tag.put("Value", nickname);
//            profileItem.add(tag);
//        }
//        if (StringUtils.isNotBlank(icon)) {
//            JSONObject tag = new JSONObject();
//            tag.put("Tag", "Tag_Profile_IM_Image");
//            tag.put("Value", icon);
//            profileItem.add(tag);
//        }
//        data.put("ProfileItem", profileItem);
//        String result = HttpUtil.post(reqUrl, data.toString());
//        log.debug("设置用户资料请求结果:{}", result);
//    }
//
//    /**
//     * 生成签名票据
//     * @param userId 用户ID
//     * @return
//     */
//    public static String getUserSig(String userId) {
//        return genUserSig(userId, EXPIRETIME, null);
//    }
//    /**
//     * 获取随机数
//     * @return
//     */
//    private static long getRandom() {
//        return RandomUtil.randomLong(0L, 4294967295L);
//    }
//
//    private static String genUserSig(String userid, long expire, byte[] userbuf) {
//        long currTime = System.currentTimeMillis() / 1000;
//        JSONObject sigDoc = new JSONObject();
//        sigDoc.put("TLS.ver", "2.0");
//        sigDoc.put("TLS.identifier", userid);
//        sigDoc.put("TLS.sdkappid", SDKAPPID);
//        sigDoc.put("TLS.expire", expire);
//        sigDoc.put("TLS.time", currTime);
//
//        String base64UserBuf = null;
//        if (null != userbuf) {
//            base64UserBuf = Base64.getEncoder().encodeToString(userbuf).replaceAll("\\s*", "");
//            sigDoc.put("TLS.userbuf", base64UserBuf);
//        }
//        String sig = hmacsha256(userid, currTime, expire, base64UserBuf);
//        if (sig.length() == 0) {
//            return "";
//        }
//        sigDoc.put("TLS.sig", sig);
//        Deflater compressor = new Deflater();
//        compressor.setInput(sigDoc.toString().getBytes(StandardCharsets.UTF_8));
//        compressor.finish();
//        byte[] compressedBytes = new byte[2048];
//        int compressedBytesLength = compressor.deflate(compressedBytes);
//        compressor.end();
//        return (new String(base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
//                0, compressedBytesLength)))).replaceAll("\\s*", "");
//    }
//
//    private static String hmacsha256(String identifier, long currTime, long expire, String base64Userbuf) {
//        String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
//                + "TLS.sdkappid:" + SDKAPPID + "\n"
//                + "TLS.time:" + currTime + "\n"
//                + "TLS.expire:" + expire + "\n";
//        if (null != base64Userbuf) {
//            contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
//        }
//        try {
//            byte[] byteKey = SECRETKEY.getBytes(StandardCharsets.UTF_8);
//            Mac hmac = Mac.getInstance("HmacSHA256");
//            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
//            hmac.init(keySpec);
//            byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
//            return (Base64.getEncoder().encodeToString(byteSig)).replaceAll("\\s*", "");
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    private static byte[] base64EncodeUrl(byte[] input) {
//        byte[] base64 = Base64.getEncoder().encode(input);
//        for (int i = 0; i < base64.length; ++i)
//            switch (base64[i]) {
//                case '+':
//                    base64[i] = '*';
//                    break;
//                case '/':
//                    base64[i] = '-';
//                    break;
//                case '=':
//                    base64[i] = '_';
//                    break;
//                default:
//                    break;
//            }
//        return base64;
//    }
//}
