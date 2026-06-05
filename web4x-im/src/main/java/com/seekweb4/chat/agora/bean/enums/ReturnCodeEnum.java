package com.seekweb4.chat.agora.bean.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * API返回码枚举类
 * 
 * <p>该枚举定义了系统中所有API接口的返回状态码和对应的消息。</p>
 * <p>采用分层设计，不同类型的错误使用不同的状态码范围，便于错误定位和处理。</p>
 * 
 * <p><strong>状态码分类：</strong></p>
 * <ul>
 *   <li><strong>通用状态码（0-999）：</strong>基础的HTTP状态码和系统级错误</li>
 *   <li><strong>房间相关（10100-10199）：</strong>房间操作相关的错误码</li>
 *   <li><strong>应用配置相关（10600-10699）：</strong>应用配置相关的错误码</li>
 *   <li><strong>用户管理相关（10700-10799）：</strong>用户操作相关的错误码</li>
 * </ul>
 * 
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * {@code
 * // 创建成功响应
 * R<String> result = R.build(ReturnCodeEnum.SUCCESS, "操作成功");
 * 
 * // 创建错误响应
 * R<Void> error = R.error(ReturnCodeEnum.PARAMS_ERROR);
 * 
 * // 根据错误码查找枚举
 * ReturnCodeEnum enumValue = ReturnCodeEnum.getEnumByCode(404);
 * }
 * </pre>
 * 
 * <p><strong>错误处理建议：</strong></p>
 * <ul>
 *   <li>4xx错误码通常表示客户端请求问题，需要客户端调整</li>
 *   <li>5xx错误码通常表示服务端问题，需要服务端修复</li>
 *   <li>业务错误码（10000+）表示具体的业务逻辑错误</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 */
@Getter
public enum ReturnCodeEnum {
    
    // =================== 通用状态码 ===================
    
    /**
     * 操作成功
     * <p>请求已成功处理，这是最常见的成功状态码。</p>
     */
    SUCCESS(0, "Success"),
    
    /**
     * 通用错误
     * <p>未分类的一般性错误，通常用于临时错误处理。</p>
     */
    ERROR(-1, "error"),
    
    /**
     * 请求参数错误
     * <p>客户端请求参数不正确、缺失或格式错误。</p>
     * <p><strong>常见场景：</strong>必填参数缺失、参数格式不正确、参数值超出范围等。</p>
     */
    PARAMS_ERROR(400, "Bad Request"),
    
    /**
     * 未授权访问
     * <p>请求缺少有效的身份验证凭据。</p>
     * <p><strong>解决方案：</strong>检查Token是否有效、是否已过期、是否正确传递。</p>
     */
    UNAUTHORIZED(401, "Unauthorized"),
    
    /**
     * 禁止访问
     * <p>服务器理解请求但拒绝授权，通常表示权限不足。</p>
     * <p><strong>应用场景：</strong>用户角色权限不够、IP访问限制、资源访问被禁止等。</p>
     */
    FORBIDDEN(403, "Forbidden"),
    
    /**
     * 服务器内部错误
     * <p>服务器遇到意外错误，无法完成请求。</p>
     * <p><strong>常见原因：</strong>数据库连接失败、第三方服务异常、代码逻辑错误等。</p>
     */
    SERVER_ERROR(500, "Internal Server Error"),
    
    /**
     * 方法未实现
     * <p>服务器不支持请求的功能或方法。</p>
     * <p><strong>使用场景：</strong>API版本不兼容、功能尚未开发完成等。</p>
     */
    METHOD_ERROR(501, "Not Implemented"),

    // =================== 房间相关错误码（10100-10199）===================
    
    /**
     * 获取房间锁失败
     * <p>在并发场景下无法获取房间的分布式锁。</p>
     * <p><strong>影响：</strong>可能导致房间状态不一致，需要重试操作。</p>
     */
    ROOM_ACQUIRE_LOCK_ERROR(10101, "Acquire lock failed"),
    
    /**
     * 释放房间锁失败
     * <p>释放房间分布式锁时出现异常。</p>
     * <p><strong>风险：</strong>可能导致死锁，需要监控和自动清理机制。</p>
     */
    ROOM_RELEASE_LOCK_ERROR(10102, "Release lock failed"),
    
    /**
     * 房间不存在
     * <p>请求操作的房间ID在系统中不存在。</p>
     * <p><strong>解决方案：</strong>检查房间ID是否正确，或先创建房间再进行操作。</p>
     */
    ROOM_NOT_EXISTS_ERROR(10104, "Room not exists"),

    // =================== 应用配置相关错误码（10600-10699）===================
    
    /**
     * 应用证书不存在
     * <p>系统中找不到对应的应用证书配置。</p>
     * <p><strong>解决方案：</strong>检查AppId是否正确，确认证书配置是否已添加到系统中。</p>
     */
    App_Cert_NOT_EXISTS_ERROR(10601, "App Cert not exists"),

    // =================== 用户管理相关错误码（10700-10799）===================
    
    /**
     * 踢出用户失败
     * <p>执行用户踢出操作时发生错误。</p>
     * <p><strong>可能原因：</strong>用户不在房间内、网络异常、权限不足等。</p>
     */
    USER_KICK_OUT_ERROR(10701, "kick out user err"),
    
    /**
     * 踢出用户权限配置不存在
     * <p>系统中找不到用户踢出操作的权限配置。</p>
     * <p><strong>解决方案：</strong>检查踢出权限配置是否正确设置，确认操作者是否有相应权限。</p>
     */
    USER_KICK_OUT_AUTH_NOT_FOUND_ERROR(107012, "kick out user auth not found");

    /**
     * 状态码数值
     */
    private final Integer code;
    
    /**
     * 状态码对应的描述信息
     */
    private final String message;

    /**
     * 构造函数
     * 
     * @param code 状态码数值
     * @param message 状态码描述信息
     */
    ReturnCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据状态码获取对应的枚举值
     * 
     * <p>通过状态码数值查找对应的枚举实例，常用于错误码解析和处理。</p>
     * 
     * <p><strong>使用示例：</strong></p>
     * <pre>
     * {@code
     * ReturnCodeEnum enumValue = ReturnCodeEnum.getEnumByCode(400);
     * if (enumValue != null) {
     *     System.out.println("错误信息: " + enumValue.getMessage());
     * }
     * }
     * </pre>
     * 
     * @param code 要查找的状态码
     * @return 对应的枚举实例，如果找不到则返回null
     */
    public static ReturnCodeEnum getEnumByCode(Integer code) {
        for (ReturnCodeEnum retCodeEnum : ReturnCodeEnum.values()) {
            if (Objects.equals(retCodeEnum.code, code)) {
                return retCodeEnum;
            }
        }
        return null;
    }
}
