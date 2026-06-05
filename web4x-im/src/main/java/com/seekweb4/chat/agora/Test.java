package com.seekweb4.chat.agora;


import java.util.Base64;

public class Test {
    public static void main(String[] args) {

        // 客户 ID
        // 需要设置环境变量 AGORA_CUSTOMER_KEY
        final String customerKey = "0040aa35a98749ecb5b207948d5351f2";
        // 客户密钥
        // 需要设置环境变量 AGORA_CUSTOMER_SECRET
        final String customerSecret = "aed98e56a615417aa0c9ace3fb5359cd";

        // 拼接客户 ID 和客户密钥并使用 base64 编码
        String plainCredentials = customerKey + ":" + customerSecret;
        String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
        // 创建 authorization header
        String authorizationHeader = "Basic " + base64Credentials;

        System.out.println(authorizationHeader);
    }
}
