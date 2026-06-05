package com.seekweb4.chat.admin.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.seekweb4.chat.admin.service.ApiResponse;
import com.seekweb4.chat.admin.service.ExternalApiService;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.web.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 钱包管理端接口控制器
 * 作为中转层，前端调用本接口，本接口调用外部服务器接口
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    private ExternalApiService externalApiService;

    /**
     * 新增代币元数据
     * POST /admin/tokens
     */
    @PostMapping(value = "/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson addToken(HttpServletRequest request, @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            // 合并URL查询参数到请求体
            Map<String, Object> mergedBody = mergeParamsToBody(request, requestBody);
            ApiResponse apiResponse = externalApiService.doPostJson("/admin/tokens", mergedBody);
            return handleApiResponse(apiResponse, "新增代币元数据");
        } catch (Exception e) {
            log.error("新增代币元数据失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 更新代币元数据
     * PUT /admin/tokens
     */
    @PutMapping(value = "/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson updateToken(HttpServletRequest request, @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            // 合并URL查询参数到请求体
            Map<String, Object> mergedBody = mergeParamsToBody(request, requestBody);
            ApiResponse apiResponse = externalApiService.doPutJson("/admin/tokens", mergedBody);
            return handleApiResponse(apiResponse, "更新代币元数据");
        } catch (Exception e) {
            log.error("更新代币元数据失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 代币列表
     * GET /admin/tokens
     */
    @GetMapping(value = "/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson getTokenList(HttpServletRequest request) {
        try {
            // 获取URL查询参数
            Map<String, String> params = getRequestParams(request);
            // 尝试从请求体中读取JSON参数（GET请求虽然不常用请求体，但有些工具会发送）
            Map<String, Object> requestBody = getRequestBodyAsMap(request);
            if (requestBody != null && !requestBody.isEmpty()) {
                for (Map.Entry<String, Object> entry : requestBody.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null) {
                        // 将请求体参数转换为查询参数，URL参数优先级更高
                        if (!params.containsKey(key)) {
                            params.put(key, String.valueOf(value));
                        }
                    }
                }
            }
            log.info("GET /admin/tokens 最终参数: {}", params);
            ApiResponse apiResponse = externalApiService.doGet("/admin/tokens", params);
            return handleApiResponse(apiResponse, "查询代币列表");
        } catch (Exception e) {
            log.error("查询代币列表失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 上架/下架代币
     * POST /admin/tokens/status
     */
    @PostMapping(value = "/tokens/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson updateTokenStatus(HttpServletRequest request, @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            // 合并URL查询参数到请求体
            Map<String, Object> mergedBody = mergeParamsToBody(request, requestBody);
            ApiResponse apiResponse = externalApiService.doPostJson("/admin/tokens/status", mergedBody);
            return handleApiResponse(apiResponse, "更新代币状态");
        } catch (Exception e) {
            log.error("更新代币状态失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 热门开关
     * POST /admin/tokens/hot
     */
    @PostMapping(value = "/tokens/hot", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson updateTokenHot(HttpServletRequest request, @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            // 合并URL查询参数到请求体
            Map<String, Object> mergedBody = mergeParamsToBody(request, requestBody);
            ApiResponse apiResponse = externalApiService.doPostJson("/admin/tokens/hot", mergedBody);
            return handleApiResponse(apiResponse, "更新代币热门状态");
        } catch (Exception e) {
            log.error("更新代币热门状态失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 删除代币
     * POST /admin/tokens/delete
     */
    @PostMapping(value = "/tokens/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson deleteToken(HttpServletRequest request, @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            // 合并URL查询参数到请求体
            Map<String, Object> mergedBody = mergeParamsToBody(request, requestBody);
            ApiResponse apiResponse = externalApiService.doPostJson("/admin/tokens/delete", mergedBody);
            return handleApiResponse(apiResponse, "删除代币");
        } catch (Exception e) {
            log.error("删除代币失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 查询钱包数量限制
     * GET /admin/wallet/limits
     */
    @GetMapping(value = "/wallet/limits", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson getWalletLimits(HttpServletRequest request) {
        try {
            Map<String, String> params = getRequestParams(request);
            // 尝试从请求体中读取JSON参数
            Map<String, Object> requestBody = getRequestBodyAsMap(request);
            if (requestBody != null && !requestBody.isEmpty()) {
                for (Map.Entry<String, Object> entry : requestBody.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null && !params.containsKey(key)) {
                        params.put(key, String.valueOf(value));
                    }
                }
            }
            ApiResponse apiResponse = externalApiService.doGet("/admin/wallet/limits", params);
            return handleApiResponse(apiResponse, "查询钱包数量限制");
        } catch (Exception e) {
            log.error("查询钱包数量限制失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 设置钱包数量限制
     * POST /admin/wallet/limits
     */
    @PostMapping(value = "/wallet/limits", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson setWalletLimits(HttpServletRequest request, @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            // 合并URL查询参数到请求体
            Map<String, Object> mergedBody = mergeParamsToBody(request, requestBody);
            log.info("POST /admin/wallet/limits 合并后的请求体: {}", mergedBody);
            ApiResponse apiResponse = externalApiService.doPostJson("/admin/wallet/limits", mergedBody);
            return handleApiResponse(apiResponse, "设置钱包数量限制");
        } catch (Exception e) {
            log.error("设置钱包数量限制失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 查询小额隐藏阈值
     * GET /admin/assets/dust-threshold
     */
    @GetMapping(value = "/assets/dust-threshold", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson getDustThreshold(HttpServletRequest request) {
        try {
            Map<String, String> params = getRequestParams(request);
            // 尝试从请求体中读取JSON参数
            Map<String, Object> requestBody = getRequestBodyAsMap(request);
            if (requestBody != null && !requestBody.isEmpty()) {
                for (Map.Entry<String, Object> entry : requestBody.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null && !params.containsKey(key)) {
                        params.put(key, String.valueOf(value));
                    }
                }
            }
            ApiResponse apiResponse = externalApiService.doGet("/admin/assets/dust-threshold", params);
            return handleApiResponse(apiResponse, "查询小额隐藏阈值");
        } catch (Exception e) {
            log.error("查询小额隐藏阈值失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 设置小额隐藏阈值
     * POST /admin/assets/dust-threshold
     */
    @PostMapping(value = "/assets/dust-threshold", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson setDustThreshold(HttpServletRequest request, @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            // 合并URL查询参数到请求体
            Map<String, Object> mergedBody = mergeParamsToBody(request, requestBody);
            ApiResponse apiResponse = externalApiService.doPostJson("/admin/assets/dust-threshold", mergedBody);
            return handleApiResponse(apiResponse, "设置小额隐藏阈值");
        } catch (Exception e) {
            log.error("设置小额隐藏阈值失败", e);
            String errorMsg = getErrorMessage(e);
            return AjaxJson.error(errorMsg);
        }
    }

    /**
     * 处理外部API响应
     * 直接返回外部服务器的响应格式，不做任何包装，保持和外部API一致
     */
    private AjaxJson handleApiResponse(ApiResponse apiResponse, String operation) {
        // 解析外部服务器的响应
        Object parsedResponse = externalApiService.parseResponse(apiResponse.getBody());
        
        if (apiResponse.isSuccess()) {
            // 外部服务器返回成功（HTTP 2xx）
            // 如果外部服务器返回的是标准格式 {code, msg, success, data}，直接返回
            if (parsedResponse instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) parsedResponse;
                // 检查是否是标准格式：包含 code, msg, success, data 字段
                if (jsonObj.containsKey("code") && jsonObj.containsKey("msg") && 
                    jsonObj.containsKey("success") && jsonObj.containsKey("data")) {
                    // 直接返回外部服务器的响应格式，不做任何包装
                    AjaxJson result = new AjaxJson();
                    result.put("code", jsonObj.get("code"));
                    result.put("msg", jsonObj.get("msg"));
                    result.put("success", jsonObj.get("success"));
                    result.put("data", jsonObj.get("data"));
                    return result;
                }
            }
            
            // 如果不是标准格式，用AjaxJson包装
            return AjaxJson.success().put("data", parsedResponse);
        } else {
            // 外部服务器返回错误（HTTP 非2xx）
            // 如果外部服务器返回的是标准格式 {code, msg, success}，直接返回
            if (parsedResponse instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) parsedResponse;
                // 检查是否是标准格式：包含 code, msg, success 字段
                if (jsonObj.containsKey("code") && jsonObj.containsKey("msg") && 
                    jsonObj.containsKey("success")) {
                    // 直接返回外部服务器的错误响应格式
                    AjaxJson result = new AjaxJson();
                    result.put("code", jsonObj.get("code"));
                    result.put("msg", jsonObj.get("msg"));
                    result.put("success", jsonObj.get("success"));
                    if (jsonObj.containsKey("data")) {
                        result.put("data", jsonObj.get("data"));
                    }
                    return result;
                }
            }
            
            // 如果不是标准格式，尝试提取错误信息
            String errorMsg = extractErrorMessage(apiResponse.getBody(), apiResponse.getStatusCode(), operation);
            int errorCode = apiResponse.getStatusCode();
            return AjaxJson.error(errorCode, errorMsg);
        }
    }

    /**
     * 从外部API响应中提取错误信息
     * 优先使用外部服务器返回的错误信息，如果没有则使用默认信息
     */
    private String extractErrorMessage(String responseBody, int statusCode, String operation) {
        if (responseBody != null && !responseBody.trim().isEmpty()) {
            try {
                // 尝试解析为JSON，提取错误信息
                Object parsed = JSON.parse(responseBody);
                if (parsed instanceof JSONObject) {
                    JSONObject jsonObj = (JSONObject) parsed;
                    // 尝试常见的错误信息字段
                    if (jsonObj.containsKey("message")) {
                        return jsonObj.getString("message");
                    } else if (jsonObj.containsKey("msg")) {
                        return jsonObj.getString("msg");
                    } else if (jsonObj.containsKey("error")) {
                        Object error = jsonObj.get("error");
                        if (error instanceof String) {
                            return (String) error;
                        } else if (error instanceof JSONObject) {
                            JSONObject errorObj = (JSONObject) error;
                            if (errorObj.containsKey("message")) {
                                return errorObj.getString("message");
                            }
                        }
                    }
                }
                // 如果无法提取，返回原始响应
                return responseBody;
            } catch (Exception e) {
                // 解析失败，返回原始响应
                return responseBody;
            }
        }
        // 如果没有响应体，根据状态码返回默认错误信息
        return getDefaultErrorMessage(statusCode, operation);
    }

    /**
     * 根据HTTP状态码返回默认错误信息
     */
    private String getDefaultErrorMessage(int statusCode, String operation) {
        switch (statusCode) {
            case 400:
                return operation + "失败：请求参数错误（HTTP 400）";
            case 401:
                return operation + "失败：未授权访问（HTTP 401）";
            case 403:
                return operation + "失败：禁止访问（HTTP 403）";
            case 404:
                return operation + "失败：资源不存在（HTTP 404）";
            case 500:
                return operation + "失败：服务器内部错误（HTTP 500）";
            case 502:
                return operation + "失败：网关错误（HTTP 502）";
            case 503:
                return operation + "失败：服务不可用（HTTP 503）";
            default:
                return operation + "失败：外部服务器返回错误（HTTP " + statusCode + "）";
        }
    }

    /**
     * 获取友好的错误信息（用于网络连接异常等）
     */
    private String getErrorMessage(Exception e) {
        String errorMsg = e.getMessage();
        if (errorMsg == null) {
            errorMsg = "操作失败，请稍后重试";
        } else if (errorMsg.contains("外部API返回空响应")) {
            errorMsg = "外部服务器返回空响应，请稍后重试";
        } else if (errorMsg.contains("NoHttpResponseException") || errorMsg.contains("failed to respond")) {
            errorMsg = "外部服务器连接失败：服务器无响应（NoHttpResponseException），请检查网络连接或稍后重试";
        } else if (errorMsg.contains("ConnectException")) {
            errorMsg = "外部服务器连接失败：无法建立连接（ConnectException），请检查服务器地址和网络";
        } else if (errorMsg.contains("SocketTimeoutException")) {
            errorMsg = "外部服务器连接失败：请求超时（SocketTimeoutException），请稍后重试";
        } else if (errorMsg.contains("调用外部API失败")) {
            // 保留原始错误信息
            errorMsg = errorMsg.replace("调用外部API失败: ", "");
        }
        return errorMsg;
    }

    /**
     * 获取请求参数（URL查询参数）
     */
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        java.util.Map<String, String> params = new java.util.HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                params.put(entry.getKey(), values[0]);
            }
        }
        return params;
    }

    /**
     * 从请求体中读取JSON并转换为Map
     * 用于处理GET请求中可能存在的请求体（虽然不常见，但某些工具会发送）
     */
    private Map<String, Object> getRequestBodyAsMap(HttpServletRequest request) {
        try {
            // 检查Content-Type是否为JSON
            String contentType = request.getContentType();
            if (contentType == null || !contentType.contains("application/json")) {
                return null;
            }
            
            // 读取请求体
            StringBuilder body = new StringBuilder();
            try (java.io.BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }
            
            String bodyStr = body.toString();
            if (bodyStr == null || bodyStr.trim().isEmpty()) {
                return null;
            }
            
            // 解析JSON
            return JSON.parseObject(bodyStr, Map.class);
        } catch (Exception e) {
            // 如果读取失败，返回null（不影响URL参数的处理）
            log.debug("读取请求体失败（可能是GET请求无请求体）: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 将URL查询参数合并到请求体中
     * 用于POST/PUT请求，将查询参数合并到请求体JSON中
     * 
     * @param request HTTP请求对象
     * @param requestBody 原始请求体
     * @return 合并后的请求体
     */
    private Map<String, Object> mergeParamsToBody(HttpServletRequest request, Map<String, Object> requestBody) {
        // 创建合并后的请求体
        Map<String, Object> mergedBody = new java.util.HashMap<>();
        
        // 先添加原始请求体中的参数
        if (requestBody != null && !requestBody.isEmpty()) {
            mergedBody.putAll(requestBody);
        }
        
        // 获取URL查询参数
        Map<String, String> queryParams = getRequestParams(request);
        
        // 将查询参数合并到请求体中（查询参数优先级更高，会覆盖请求体中的同名参数）
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                // 尝试将字符串值转换为合适的类型
                Object convertedValue = convertStringValue(value);
                mergedBody.put(key, convertedValue);
            }
        }
        
        return mergedBody;
    }

    /**
     * 将字符串值转换为合适的类型（数字、布尔值等）
     */
    private Object convertStringValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        // 尝试转换为整数
        try {
            if (value.matches("^-?\\d+$")) {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            // 忽略，继续尝试其他类型
        }
        
        // 尝试转换为浮点数
        try {
            if (value.matches("^-?\\d+\\.\\d+$")) {
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException e) {
            // 忽略，继续尝试其他类型
        }
        
        // 尝试转换为布尔值
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        
        // 默认返回字符串
        return value;
    }
}

