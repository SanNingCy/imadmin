package com.seekweb4.chat.api.req;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson2.JSON;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: fxq
 * @Date 2024-11-27 10:45
 */
public class AesUtil {
    private static final String KEY = "1q30vqwerf8yiac3";// 16个字符，24个字符或32个字符生成的密钥
    private static AES aes = SecureUtil.aes(KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 加密
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        return aes.encryptBase64(data);
    }
    /**
     * 解密
     * @param data
     * @return
     */
    public static String decrypt(String data) {
        return aes.decryptStr(data);
    }

    public static void main(String[] args) {
//        String content = "wBY+VMkesgKIyqm/SUX7YUFmXofxCpWx9/LBrr5wbNkIfQpVVtUteyW0k4d5eOmJ3qwEjlnrSChPWMMB0IeQiFdcf8RsYI+deo4ZuIvEBIeVkVan/YLk3++tl13QAPU4FmLaSIAxO4SIJ+sciaJav5t7B4GsZ1jY2jRRE1LMlavQ5n0OBmO2DnpF1lmxFom1H/BOHld5KkYekvPhv1unrwW/u9GthyoZ3j+G21gJ04uPObKsym9rTGYecvinTphc77YTLj450gNX+ZoThi9Vqg==";

        String content = "4F+vgt1Rv5RUM8HiWI6Tdwd16eq6Obl3H40R0alrg0oI5fS6wuRM7vcGTodLV5b5dGusri5AMaezq9tRPTlYjg==";

        System.out.println(decrypt(content));
        //System.out.println(decrypt("oigvIlINeGsQCVrvJSBKRQ=="));

        Map<String,Object> map = new HashMap();
        map.put("eqno","23431242134124");

        String data = JSON.toJSONString(map);

        String result =   encrypt(data);
        System.out.println("\n");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(result);

    }
}
