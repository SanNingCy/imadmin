package com.seekweb4.chat.agora.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.seekweb4.chat.agora.bean.enums.ReturnCodeEnum;
import lombok.Data;

/**
 * 统一API响应结果封装类
 * 
 * <p>该类用于封装所有API接口的响应结果，提供统一的数据格式。</p>
 * <p>采用泛型设计，支持任意类型的数据返回，确保API响应的一致性。</p>
 * 
 * <p><strong>响应格式：</strong></p>
 * <pre>
 * {
 *   "code": 200,           // 响应状态码
 *   "msg": "success",      // 响应消息（与 AjaxJson 统一，前端使用 msg）
 *   "data": {...}          // 业务数据（可选）
 * }
 * </pre>
 * 
 * <p><strong>主要特性：</strong></p>
 * <ul>
 *   <li>统一响应格式 - 所有API都使用相同的响应结构</li>
 *   <li>泛型支持 - 支持任意类型的数据封装</li>
 *   <li>空值忽略 - 自动忽略null值字段，减少响应体积</li>
 *   <li>多种构建方式 - 提供丰富的静态方法便于创建响应</li>
 *   <li>成功状态检查 - 提供便捷的成功状态判断方法</li>
 * </ul>
 * 
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * {@code
 * // 成功响应（带数据）
 * R<UserInfo> userResult = R.success(userInfo);
 * 
 * // 成功响应（无数据）
 * R<Void> result = R.success(null);
 * 
 * // 错误响应
 * R<String> errorResult = R.error("参数不能为空");
 * 
 * // 使用枚举创建响应
 * R<Object> result = R.build(ReturnCodeEnum.PARAM_ERROR);
 * 
 * // 检查响应是否成功
 * if (result.isSuccess()) {
 *     // 处理成功逻辑
 * }
 * }
 * </pre>
 * 
 * <p><strong>状态码约定：</strong></p>
 * <ul>
 *   <li><strong>200：</strong>请求成功</li>
 *   <li><strong>400：</strong>客户端请求错误（参数错误等）</li>
 *   <li><strong>401：</strong>未授权访问</li>
 *   <li><strong>403：</strong>禁止访问</li>
 *   <li><strong>404：</strong>资源不存在</li>
 *   <li><strong>500：</strong>服务器内部错误</li>
 * </ul>
 * 
 * @param <T> 响应数据的类型，可以是任意Java对象
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 * @see ReturnCodeEnum
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class R<T> implements java.io.Serializable {
    
    /**
     * 响应消息，默认为成功消息（字段名 msg 与 AjaxJson 统一，前端统一用 msg）
     */
    private String msg = ReturnCodeEnum.SUCCESS.getMessage();
    
    /**
     * 响应状态码，默认为成功状态码
     */
    private Integer code = ReturnCodeEnum.SUCCESS.getCode();
    
    /**
     * 响应数据，包含具体的业务数据
     */
    private T data;

    /**
     * 默认构造函数
     * 
     * <p>创建一个成功状态的空响应对象。</p>
     */
    public R() {
        super();
    }

    /**
     * 带数据的构造函数
     * 
     * <p>创建一个包含指定数据的成功响应。</p>
     * 
     * @param data 响应数据
     */
    public R(T data) {
        super();
        this.code = ReturnCodeEnum.SUCCESS.getCode();
        this.data = data;
    }

    /**
     * 带状态码和消息的构造函数
     * 
     * <p>创建一个指定状态码和消息的响应，通常用于错误响应。</p>
     * 
     * @param code 状态码
     * @param message 响应消息
     */
    public R(int code, String message) {
        super();
        this.msg = message;
        this.code = code;
    }

    /**
     * 完整参数构造函数
     * 
     * <p>创建一个包含完整信息的响应对象。</p>
     * 
     * @param data 响应数据
     * @param code 状态码
     * @param message 响应消息
     */
    public R(T data, int code, String message) {
        super();
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    /**
     * 异常构造函数
     * 
     * <p>根据异常创建错误响应，自动设置为服务器错误状态。</p>
     * 
     * @param e 异常对象
     */
    public R(Throwable e) {
        super();
        this.code = ReturnCodeEnum.SERVER_ERROR.getCode();
        this.msg = e.getMessage();
    }

    /**
     * 根据枚举创建响应
     * 
     * <p>使用预定义的返回码枚举创建响应对象。</p>
     * 
     * @param <T> 响应数据类型
     * @param retCodeEnum 返回码枚举
     * @return 响应对象
     */
    public static <T> R<T> build(ReturnCodeEnum retCodeEnum) {
        return new R<>(null, retCodeEnum.getCode(), retCodeEnum.getMessage());
    }

    /**
     * 根据枚举和数据创建响应
     * 
     * <p>使用预定义的返回码枚举和指定数据创建响应对象。</p>
     * 
     * @param <T> 响应数据类型
     * @param retCodeEnum 返回码枚举
     * @param data 响应数据
     * @return 响应对象
     */
    public static <T> R<T> build(ReturnCodeEnum retCodeEnum, T data) {
        return new R<>(data, retCodeEnum.getCode(), retCodeEnum.getMessage());
    }

    /**
     * 创建服务器错误响应
     * 
     * <p>创建一个标准的服务器内部错误响应，通常用于未预期的异常情况。</p>
     * 
     * @param <T> 响应数据类型
     * @return 服务器错误响应对象
     */
    public static <T> R<T> error() {
        return new R<>(null, ReturnCodeEnum.SERVER_ERROR.getCode(), ReturnCodeEnum.SERVER_ERROR.getMessage());
    }

    /**
     * 创建带自定义消息的错误响应
     * 
     * <p>创建一个包含自定义错误消息的服务器错误响应。</p>
     * 
     * @param <T> 响应数据类型
     * @param message 自定义错误消息
     * @return 错误响应对象
     */
    public static <T> R<T> error(String message) {
        return new R<>(null, ReturnCodeEnum.SERVER_ERROR.getCode(), message);
    }

    /**
     * 创建带自定义状态码和消息的错误响应
     * 
     * <p>创建一个完全自定义的错误响应，可指定具体的错误码和消息。</p>
     * 
     * <p><strong>常用错误码：</strong></p>
     * <ul>
     *   <li>400 - 请求参数错误</li>
     *   <li>401 - 未授权访问</li>
     *   <li>403 - 禁止访问</li>
     *   <li>404 - 资源不存在</li>
     *   <li>429 - 请求过于频繁</li>
     * </ul>
     * 
     * @param <T> 响应数据类型
     * @param code 错误状态码
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static <T> R<T> error(Integer code, String message) {
        return new R<>(null, code, message);
    }

    /**
     * 根据枚举创建错误响应
     * 
     * <p>使用预定义的错误码枚举创建错误响应。</p>
     * 
     * @param <T> 响应数据类型
     * @param returnCodeEnum 返回码枚举（通常是错误类型的枚举）
     * @return 错误响应对象
     */
    public static <T> R<T> error(ReturnCodeEnum returnCodeEnum) {
        return new R<>(null, returnCodeEnum.getCode(), returnCodeEnum.getMessage());
    }

    /**
     * 创建成功响应
     * 
     * <p>创建一个包含指定数据的成功响应，这是最常用的成功响应创建方法。</p>
     * 
     * <p><strong>使用示例：</strong></p>
     * <pre>
     * // 返回用户信息
     * return R.success(userInfo);
     * 
     * // 返回操作成功标识
     * return R.success(null);
     * 
     * // 返回列表数据
     * return R.success(userList);
     * </pre>
     * 
     * @param <T> 响应数据类型
     * @param data 响应数据，可以为null
     * @return 成功响应对象
     */
    public static <T> R<T> success(T data) {
        return new R<>(data, ReturnCodeEnum.SUCCESS.getCode(), ReturnCodeEnum.SUCCESS.getMessage());
    }

    /**
     * 创建带自定义消息的成功响应
     * 
     * <p>创建一个包含自定义成功消息和数据的响应。</p>
     * 
     * <p><strong>适用场景：</strong></p>
     * <ul>
     *   <li>需要特殊成功提示的操作</li>
     *   <li>多语言环境下的本地化消息</li>
     *   <li>需要详细说明的成功状态</li>
     * </ul>
     * 
     * @param <T> 响应数据类型
     * @param message 自定义成功消息
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> R<T> success(String message, T data) {
        return new R<>(data, ReturnCodeEnum.SUCCESS.getCode(), message);
    }

    /**
     * 检查响应是否为成功状态
     * 
     * <p>便捷方法用于判断API调用是否成功，常用于业务逻辑中的条件判断。</p>
     * 
     * <p><strong>使用示例：</strong></p>
     * <pre>
     * {@code
     * R<UserInfo> result = userService.getUserInfo(userId);
     * if (result.isSuccess()) {
     *     UserInfo user = result.getData();
     *     // 处理成功逻辑
     * } else {
     *     log.error("获取用户信息失败: {}", result.getMsg());
     * }
     * }
     * </pre>
     * 
     * @return true表示成功（状态码为200），false表示失败
     */
    @JsonIgnore
    public Boolean isSuccess() {
        return this.code.equals(ReturnCodeEnum.SUCCESS.getCode());
    }
}
