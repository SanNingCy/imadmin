package com.seekweb4.chat.api.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.seekweb4.chat.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI聊天工具
 * @author: fxq
 * @Date 2024-12-16 8:49
 */
@Slf4j
public class AiChatUtils {

    //接口地址
    //private static String host_url = "http://106.52.42.244:83";
    private static String host_url = "http://119.45.9.228:83";
    private static String fanyi_key = "app-E94udtbceJtxbrL16DZG0G9g";

    /**
     * 聊天
     * @param uid
     * @param context
     * @return
     */
    public static Map<String,String> chat(String uid,String context,String conv_id,String key) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer "+key);
        headers.put("Content-Type", "application/json; charset=UTF-8");
        JSONObject bodys = new JSONObject();
        bodys.put("inputs",new JSONObject());
        bodys.put("query",context);
        bodys.put("response_mode","blocking");
        bodys.put("conversation_id",conv_id);
        bodys.put("user",uid);

        Map<String,String> map = new HashMap<>();
        map.put("code","1");
        try {
            HttpResponse resp = HttpRequest.post(host_url+"/v1/chat-messages")
                    .addHeaders(headers).body(bodys.toJSONString()).execute();
            log.error("ai发起会话结果："+resp.body());
            JSONObject jsonObject = JSONObject.parseObject(resp.body());
            if(StringUtils.isNotBlank(jsonObject.getString("answer"))){
                String answer = URLDecoder.decode(jsonObject.getString("answer"), "UTF-8");
                map.put("code","0");
                map.put("answer",answer);
                map.put("conversation_id",jsonObject.getString("conversation_id"));
                log.info("ai回复：{}：",answer);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("ai发起会话接口异常{}",e.getMessage());
        }
        return map;
    }

    /**
     * ai图片回复
     * @param uid
     * @param context
     * @param key
     * @return
     */
    public static Map<String,String> chatImg(String uid,String context,String key) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer "+key);
        headers.put("Content-Type", "application/json; charset=UTF-8");
        JSONObject bodys = new JSONObject();

        JSONObject input = new JSONObject();
        //input.put("type","document");
        input.put("prompt",context);
        bodys.put("inputs",input);

        bodys.put("response_mode","blocking");
        bodys.put("user",uid);

        Map<String,String> map = new HashMap<>();
        map.put("code","1");
        log.error("ai发起图片会话参数："+bodys.toJSONString());
        try {
            HttpResponse response = HttpRequest.post(host_url+"/v1/workflows/run")
                    .header("Authorization", "Bearer "+key)
                    .header("Content-Type","application/json; charset=UTF-8")
                    .body(bodys.toJSONString()) // 设置POST请求的body内容
                    .execute();
            log.error("ai发起图片会话结果："+response.body());
            JSONObject jsonObject = JSONObject.parseObject(response.body());
            if(StringUtils.isNotBlank(jsonObject.getString("data"))){
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject outputs = data.getJSONObject("outputs");
                String pic = URLDecoder.decode(outputs.getString("imageout"), "UTF-8");
                //log.error("图片地址："+pic);
                map.put("code","0");
                map.put("pic",pic);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("ai发起会话接口异常{}",e.getMessage());
        }
        return map;
    }
    /**
     * ai翻译
     * @param uid
     * @param conv_id
     * @param context   内容
     * @param yuyan     目标语种
     * @return
     */
    public static Map<String,String> aifanyi(String uid,String conv_id,String context,String yuyan) {
        JSONObject bodys = new JSONObject();

        bodys.put("inputs",new JSONObject());
        bodys.put("query","把 "+context+" 翻译成 "+yuyan);
        bodys.put("response_mode","blocking");
        bodys.put("user",uid);
        bodys.put("conversation_id",conv_id);

        Map<String,String> map = new HashMap<>();
        map.put("code","1");
        log.error("ai翻译参数："+bodys.toJSONString());
        try {
            HttpResponse response = HttpRequest.post(host_url+"/v1/chat-messages")
                    .header("Authorization", "Bearer "+fanyi_key)
                    .header("Content-Type","application/json; charset=UTF-8")
                    .body(bodys.toJSONString()) // 设置POST请求的body内容
                    .execute();
            log.error("ai翻译结果："+response.body());
            JSONObject jsonObject = JSONObject.parseObject(response.body());
            if(StringUtils.isNotBlank(jsonObject.getString("answer"))){
                map.put("code","0");
                map.put("answer",jsonObject.getString("answer"));
                //map.put("conversation_id",jsonObject.getString("conversation_id"));
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("ai翻译接口异常{}",e.getMessage());
        }
        return map;
    }
    public static void main(String[] args) {
        chat("123456231","你是谁","","app-M2BQEHL4FBilCktbSRzWtQJL");
        //chatImg("123456","小鸡啄食","app-AH2uOHCT1mDzRfUc12oZlWPU");
//        String testString = "![图片1](https://p9-dreamina-sign.byteimg.com/tos-cn-i-tb4s082cfz/a788ebb9227a4b019e555eda5e8ff400~tplv-tb4s082cfz-aigc_resize:0:0.jpg?lk3s=43402efa&x-expires=1749600000&x-signature=Rybr7GhxSP5rlYduBGAVS7bwhoQ=&format=.jpg)\n" +
//                "![图片2](https://p26-dreamina-sign.byteimg.com/tos-cn-i-tb4s082cfz/1670f10821b64dd1bb6109b94e012b67~tplv-tb4s082cfz-aigc_resize:0:0.jpg?lk3s=43402efa&x-expires=1749600000&x-signature=P1LLj45ZMVloxzaQLrBCUgLcLTY=&format=.jpg)\n" +
//                "![图片3](https://p9-dreamina-sign.byteimg.com/tos-cn-i-tb4s082cfz/be9afefaf396474fb11c71beddd8bef1~tplv-tb4s082cfz-aigc_resize:0:0.jpg?lk3s=43402efa&x-expires=1749600000&x-signature=YweoRRJbWp+OfdnQ2YPtqofvrw4=&format=.jpg)\n" +
//                "![图片4](https://p26-dreamina-sign.byteimg.com/tos-cn-i-tb4s082cfz/4d62b7e037564af2b8d2cc992234b473~tplv-tb4s082cfz-aigc_resize:0:0.jpg?lk3s=43402efa&x-expires=1749600000&x-signature=UTjESZxdePP5ezMBIOIR4GVCSjs=&format=.jpg)";
//        List<String> extracted = extractParenthesesContent(testString);
//
//        System.out.println("提取结果:");
//        for (String s : extracted) {
//            System.out.println(s);
//        }
        //aifanyi("123","","你今早吃的什么","英语");
    }
    public static List<String> extractParenthesesContent(String input) {
        List<String> result = new ArrayList<>();
        // 正则表达式匹配小括号内的内容
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        return result;
    }
    /**
     * 图片返回
     * {
     *     "task_id": "c734d0b7-cdb3-495f-bc51-0145e4e93a91",
     *     "workflow_run_id": "a91de745-56ce-48ac-b299-2b11702fdf02",
     *     "data": {
     *         "id": "a91de745-56ce-48ac-b299-2b11702fdf02",
     *         "workflow_id": "0da86c84-8b5e-4713-a596-c79e208155de",
     *         "status": "succeeded",
     *         "outputs": {
     *             "imageout": "![\u56fe\u72471](https://p3-dreamina-sign.byteimg.com/tos-cn-i-tb4s082cfz/fb1de490ebde49f295849180f1772068~tplv-tb4s082cfz-aigc_resize:0:0.jpg?lk3s=43402efa&x-expires=1749600000&x-signature=%2FFNO1ss43v8Lw8g%2FoKrlcI2HXVA%3D&format=.jpg)\n![\u56fe\u72472](https://p26-dreamina-sign.byteimg.com/tos-cn-i-tb4s082cfz/bc3995dfc67d4ffbbb1e32202dd4ee93~tplv-tb4s082cfz-aigc_resize:0:0.jpg?lk3s=43402efa&x-expires=1749600000&x-signature=i7FZVxwFfKhBOA4JyrNr1ldv9fY%3D&format=.jpg)\n![\u56fe\u72473](https://p9-dreamina-sign.byteimg.com/tos-cn-i-tb4s082cfz/567db132a5d3489aab4df0593309725e~tplv-tb4s082cfz-aigc_resize:0:0.jpg?lk3s=43402efa&x-expires=1749600000&x-signature=uHCdDUaYIAMkrJzOBL93Q2Ez34A%3D&format=.jpg)\n![\u56fe\u72474](https://p9-dreamina-sign.byteimg.com/tos-cn-i-tb4s082cfz/bb49347975c64fb5a9395e23d396e4d5~tplv-tb4s082cfz-aigc_resize:0:0.jpg?lk3s=43402efa&x-expires=1749600000&x-signature=3Ob%2B9g8bmBHSjnbB%2Blo%2FC17ZYjg%3D&format=.jpg)\n"
     *         },
     *         "error": null,
     *         "elapsed_time": 14.072568394010887,
     *         "total_tokens": 0,
     *         "total_steps": 4,
     *         "created_at": 1747442911,
     *         "finished_at": 1747442925
     *     }
     * }
     *
     * */
    /**
     * 文字ai返回示例
     * {
     *     "event": "message",
     *     "task_id": "08e24e1c-9b21-4157-9726-bb0d47d3ebdf",
     *     "id": "a6e73e87-6170-4c06-af95-15030ded75b6",
     *     "message_id": "a6e73e87-6170-4c06-af95-15030ded75b6",
     *     "conversation_id": "142ff4f4-8e31-44b7-8853-b5a5d17bec75",
     *     "mode": "chat",
     *     "answer": "\u4f60\u597d\uff01\u6709\u4ec0\u4e48\u6211\u53ef\u4ee5\u5e2e\u52a9\u4f60\u7684\u5417\uff1f",
     *     "metadata": {
     *         "usage": {
     *             "prompt_tokens": 9,
     *             "prompt_unit_price": "0",
     *             "prompt_price_unit": "0",
     *             "prompt_price": "0",
     *             "completion_tokens": 10,
     *             "completion_unit_price": "0",
     *             "completion_price_unit": "0",
     *             "completion_price": "0",
     *             "total_tokens": 19,
     *             "total_price": "0",
     *             "currency": "USD",
     *             "latency": 1.6821587630547583
     *         }
     *     },
     *     "created_at": 1743468606
     * }
     * */
}
