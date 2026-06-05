package com.seekweb4.chat.api.utils.sign;

import cn.hutool.json.JSONUtil;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * @author coderpwh
 */
public class SignUtil {


    /***
     * 构建签名内容
     * @param params
     * @return
     */
    public static String buildSignContent(Map<String, Object> params) {
        Map<String, Object> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : sorted.entrySet()) {
            if ("sign".equals(e.getKey()) || e.getValue() == null) continue;
            if (sb.length() > 0) sb.append("&");
            sb.append(e.getKey()).append("=").append(e.getValue());
        }
        return sb.toString();
    }


    /***
     *  获取签名
     * @param data
     * @param base64PrivateKey
     * @return
     * @throws Exception
     */
    public static String sign(String data, String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(signature.sign());
    }






    public static void main(String[] args) throws Exception {
        String receivingAddress = "0x002";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", "im");
        params.put("id", 3);

        params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
        params.put("timestamp", System.currentTimeMillis() / 1000);

        params.put("receivingAddress", receivingAddress);

        // 客户端私钥
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOznQnLhH6eyrJ7ihfy5hKULa41nGfovnZy8JVIxAHC3htFjW2QU+tAi9I+/HYTV+K27RArgtadoAIMvR4WOhKPbiRF/X810NxbxG1YkDt3eY8nTKtj/u2bcIguIEAJKmZK4QmGpXKmP3tdrOGvjHKQXKKrYGsRdhaI87l9kVeFtAgMBAAECgYBdgfqMdaeU/cV9AVR5qJBexN8y9Rsf0WOY1fq1MnpGA2rHgzwziMRnGyUFCB6SrBJ4II5+7KtG5JovLZ5BbTuU3ylfKUZWSl0rPBFP8N5uNGJ4623aKapCX1oo++l2vGpcFhQZyu379X+RRdabxgkFoDfc02hyGPoOaUdGG6LN0QJBAO0N5lf/tkJpjoyN+Vj273ZLv1ZSDUd4BAUvz81nR6P5f0T1a4RrAzKQ7J655JCFxpHSawC4Ka5GYDaZNmCmXmsCQQD/1kWH/SunQjccgEJyhF2I+5sJEjf9u1R+Y1N1wYXnfsePe9UFUThwvt31i7HUx0gm8zxJLmA9/b/z/OPyxgWHAkBnlFc9xEIDKtRanf6B3QQtEFP+h8O9orc+/PEzsxkekPoHS6U7KvcutRxvDpLMg2eMeADBQ7cSqzWHJdMY0BkZAkEAtkCPsevCEFoUZqJcM7Zl57LN5C1tY7zy2UKq7wVY/ewMAARIYYxVt8PQ3R6SJbF9jfnTJZL7Ds5Y01iBjq5QuwJAYu+mIIbpma1+4zlCDAHwtJze392SaNPW4AdQppShn/FHS0yHN4pUIs20nyI7LiKN0f1iQUxn7mXpVw3NWvCSUQ==";

        String signContent = buildSignContent(params);
        String sign = sign(signContent, privateKey);
        params.put("sign", sign);


        String json = JSONUtil.toJsonStr(params);

        System.out.println("加密后的body体,把加密后的body放到post请求的body里面即可：" + json);
    }


}
