package com.seekweb4.chat.admin.service;

import com.alibaba.fastjson2.JSON;
import com.seekweb4.chat.common.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 外部API调用服务
 * 负责与外部服务器通信，作为中转层
 */
@Slf4j
@Service
public class ExternalApiService {

    @Value("${external.api.base-url}")
    private String baseUrl;

    /**
     * 发送GET请求到外部API
     * 
     * @param path 接口路径
     * @param params 请求参数
     * @return API响应（包含状态码和响应体）
     */
    public ApiResponse doGet(String path, Map<String, String> params) {
        String url = baseUrl + path;
        log.info("调用外部API GET请求: {}, 参数: {}", url, params);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            // 构建URI
            URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            URI uri = builder.build();
            
            // 创建GET请求
            HttpGet httpGet = new HttpGet(uri);
            
            // 执行请求
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = "";
            
            // 读取响应体（无论状态码是什么都要读取，因为错误信息可能在响应体中）
            if (response.getEntity() != null) {
                responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            
            log.info("外部API响应 - 状态码: {}, 响应体: {}", statusCode, responseBody);
            
            return new ApiResponse(statusCode, responseBody);
        } catch (org.apache.http.NoHttpResponseException e) {
            log.error("外部服务器无响应: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：服务器无响应（NoHttpResponseException），请检查网络连接或稍后重试", e);
        } catch (java.net.ConnectException e) {
            log.error("无法连接到外部服务器: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：无法建立连接（ConnectException），请检查服务器地址和网络", e);
        } catch (java.net.SocketTimeoutException e) {
            log.error("连接外部服务器超时: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：请求超时（SocketTimeoutException），请稍后重试", e);
        } catch (Exception e) {
            log.error("调用外部API失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用外部API失败: " + e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.warn("关闭HTTP连接失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 发送GET请求到外部API（无参数）
     * 
     * @param path 接口路径
     * @return API响应（包含状态码和响应体）
     */
    public ApiResponse doGet(String path) {
        return doGet(path, null);
    }

    /**
     * 发送POST请求到外部API（JSON格式）
     * 
     * @param path 接口路径
     * @param requestBody 请求体对象
     * @return API响应（包含状态码和响应体）
     */
    public ApiResponse doPostJson(String path, Object requestBody) {
        String url = baseUrl + path;
        String jsonBody = requestBody != null ? JSON.toJSONString(requestBody) : "{}";
        log.info("调用外部API POST请求: {}, 请求体: {}", url, jsonBody);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            // 创建POST请求
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            
            // 执行请求
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = "";
            
            // 读取响应体
            if (response.getEntity() != null) {
                responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            
            log.info("外部API响应 - 状态码: {}, 响应体: {}", statusCode, responseBody);
            
            return new ApiResponse(statusCode, responseBody);
        } catch (org.apache.http.NoHttpResponseException e) {
            log.error("外部服务器无响应: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：服务器无响应（NoHttpResponseException），请检查网络连接或稍后重试", e);
        } catch (java.net.ConnectException e) {
            log.error("无法连接到外部服务器: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：无法建立连接（ConnectException），请检查服务器地址和网络", e);
        } catch (java.net.SocketTimeoutException e) {
            log.error("连接外部服务器超时: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：请求超时（SocketTimeoutException），请稍后重试", e);
        } catch (Exception e) {
            log.error("调用外部API失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用外部API失败: " + e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.warn("关闭HTTP连接失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 发送POST请求到外部API（表单格式）
     * 
     * @param path 接口路径
     * @param params 请求参数
     * @return API响应（包含状态码和响应体）
     */
    public ApiResponse doPost(String path, Map<String, String> params) {
        String url = baseUrl + path;
        log.info("调用外部API POST请求: {}, 参数: {}", url, params);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            // 创建POST请求
            HttpPost httpPost = new HttpPost(url);
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    paramList.add(new org.apache.http.message.BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                org.apache.http.client.entity.UrlEncodedFormEntity entity = 
                    new org.apache.http.client.entity.UrlEncodedFormEntity(paramList, "UTF-8");
                httpPost.setEntity(entity);
            }
            
            // 执行请求
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = "";
            
            // 读取响应体
            if (response.getEntity() != null) {
                responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            
            log.info("外部API响应 - 状态码: {}, 响应体: {}", statusCode, responseBody);
            
            return new ApiResponse(statusCode, responseBody);
        } catch (org.apache.http.NoHttpResponseException e) {
            log.error("外部服务器无响应: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：服务器无响应（NoHttpResponseException），请检查网络连接或稍后重试", e);
        } catch (java.net.ConnectException e) {
            log.error("无法连接到外部服务器: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：无法建立连接（ConnectException），请检查服务器地址和网络", e);
        } catch (java.net.SocketTimeoutException e) {
            log.error("连接外部服务器超时: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：请求超时（SocketTimeoutException），请稍后重试", e);
        } catch (Exception e) {
            log.error("调用外部API失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用外部API失败: " + e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.warn("关闭HTTP连接失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 发送PUT请求到外部API（JSON格式）
     * 
     * @param path 接口路径
     * @param requestBody 请求体对象
     * @return API响应（包含状态码和响应体）
     */
    public ApiResponse doPutJson(String path, Object requestBody) {
        String url = baseUrl + path;
        String jsonBody = requestBody != null ? JSON.toJSONString(requestBody) : "{}";
        log.info("调用外部API PUT请求: {}, 请求体: {}", url, jsonBody);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            // 创建PUT请求
            HttpPut httpPut = new HttpPut(url);
            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);
            
            // 执行请求
            response = httpClient.execute(httpPut);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = "";
            
            // 读取响应体（无论状态码是什么都要读取）
            if (response.getEntity() != null) {
                responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            
            log.info("外部API响应 - 状态码: {}, 响应体: {}", statusCode, responseBody);
            
            return new ApiResponse(statusCode, responseBody);
        } catch (org.apache.http.NoHttpResponseException e) {
            log.error("外部服务器无响应: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：服务器无响应（NoHttpResponseException），请检查网络连接或稍后重试", e);
        } catch (java.net.ConnectException e) {
            log.error("无法连接到外部服务器: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：无法建立连接（ConnectException），请检查服务器地址和网络", e);
        } catch (java.net.SocketTimeoutException e) {
            log.error("连接外部服务器超时: {}", e.getMessage(), e);
            throw new RuntimeException("外部服务器连接失败：请求超时（SocketTimeoutException），请稍后重试", e);
        } catch (Exception e) {
            log.error("调用外部API失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用外部API失败: " + e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.warn("关闭HTTP连接失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 解析外部API响应并转换为统一格式
     * 外部服务器返回什么，这里就返回什么，不做任何转换
     * 
     * @param responseBody 外部API响应体
     * @return 解析后的数据对象（可能是JSON对象、数组或字符串）
     */
    public Object parseResponse(String responseBody) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return null;
            }
            // 尝试解析为JSON对象，如果解析失败则返回原始字符串
            // 这样无论外部API返回什么格式，都能原样返回
            try {
                return JSON.parse(responseBody);
            } catch (Exception e) {
                // 如果不是JSON格式，返回原始字符串
                log.debug("外部API响应不是JSON格式，返回原始字符串");
                return responseBody;
            }
        } catch (Exception e) {
            log.warn("解析外部API响应失败，返回原始字符串: {}", e.getMessage());
            return responseBody;
        }
    }
}

