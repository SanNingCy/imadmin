package com.seekweb4.chat.api.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author: fxq
 * @Date 2024-01-22 11:08
 */
@Slf4j
public class IpAddressUtil {

    /**
     * 获取ip的地区
     * @param ip
     * @return
     */
    public static String getCity(String ip){
        try {
            String response = getIPLocation(ip);
            JSONObject jsonObject = JSONObject.parseObject(response);
            String country = jsonObject.getString("country");
            String city = jsonObject.getString("city");
            System.out.println(city);
            return country+city;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getIPLocation(String ip) throws Exception {
        URL url = new URL("http://ip-api.com/json/" + ip+"?lang=zh-CN");
        BufferedReader reader = null;
        try {
            InputStream inputStream = url.openConnection().getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            System.out.println(result.toString());
            return result.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static void main(String[] args) {
        getCity("113.132.208.111");
    }
}
