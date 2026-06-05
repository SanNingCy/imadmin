package com.seekweb4.chat.api.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP工具类
 * 提供GET、POST等HTTP请求方法
 */
public class HttpUtil {

    private static final int CONNECT_TIMEOUT = 5000; // 连接超时时间（毫秒）
    private static final int READ_TIMEOUT = 10000;    // 读取超时时间（毫秒）

    /**
     * 发送GET请求
     * @param urlString 请求URL
     * @return 响应内容
     */
    public static String get(String urlString) throws IOException {
        return get(urlString, null);
    }

    /**
     * 发送GET请求（带参数）
     * @param urlString 请求URL
     * @param params 请求参数
     * @return 响应内容
     */
    public static String get(String urlString, Map<String, String> params) throws IOException {
        if (params != null && !params.isEmpty()) {
            urlString = urlString + "?" + buildQueryString(params);
        }

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(conn.getInputStream());
            } else {
                throw new IOException("HTTP GET请求失败，响应码：" + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 发送POST请求（JSON格式）
     * @param urlString 请求URL
     * @param jsonBody JSON格式的请求体
     * @return 响应内容
     */
    public static String postJson(String urlString, String jsonBody) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // 写入请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                return readResponse(conn.getInputStream());
            } else {
                throw new IOException("HTTP POST请求失败，响应码：" + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 发送POST请求（表单格式）
     * @param urlString 请求URL
     * @param params 表单参数
     * @return 响应内容
     */
    public static String postForm(String urlString, Map<String, String> params) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // 写入表单数据
            String postData = buildQueryString(params);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                return readResponse(conn.getInputStream());
            } else {
                throw new IOException("HTTP POST请求失败，响应码：" + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 发送带自定义请求头的GET请求
     * @param urlString 请求URL
     * @param headers 请求头
     * @return 响应内容
     */
    public static String getWithHeaders(String urlString, Map<String, String> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            // 设置自定义请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(conn.getInputStream());
            } else {
                throw new IOException("HTTP GET请求失败，响应码：" + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 构建查询字符串
     */
    private static String buildQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    /**
     * 读取响应内容
     */
    private static String readResponse(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}