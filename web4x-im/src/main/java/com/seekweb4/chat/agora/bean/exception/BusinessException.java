package com.seekweb4.chat.agora.bean.exception;

import com.seekweb4.chat.agora.bean.enums.ReturnCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 业务异常类
 * 
 * <p>该异常类用于封装业务逻辑处理过程中遇到的各种异常情况。</p>
 * <p>继承自RuntimeException，为非检查异常，支持自动的异常传播和处理。</p>
 * 
 * <p><strong>设计特点：</strong></p>
 * <ul>
 *   <li>统一异常格式 - 提供统一的异常信息结构</li>
 *   <li>多种构造方式 - 支持不同参数组合的异常创建</li>
 *   <li>枚举集成 - 支持使用ReturnCodeEnum创建标准化异常</li>
 *   <li>扩展信息支持 - 可携带额外的上下文信息</li>
 *   <li>HTTP状态码 - 支持设置对应的HTTP响应状态码</li>
 * </ul>
 * 
 * <p><strong>异常信息结构：</strong></p>
 * <ul>
 *   <li><strong>code：</strong>业务错误码，用于标识具体的错误类型</li>
 *   <li><strong>message：</strong>错误描述信息，用于展示给用户或日志记录</li>
 *   <li><strong>status：</strong>HTTP状态码，默认为500（服务器内部错误）</li>
 *   <li><strong>ext：</strong>扩展信息，用于携带额外的上下文数据</li>
 * </ul>
 * 
 * <p><strong>使用示例：</strong></p>
 * <pre>
 * {@code
 * // 使用预定义的错误码枚举
 * throw new BusinessException(ReturnCodeEnum.ROOM_NOT_EXISTS_ERROR);
 * 
 * // 自定义错误信息
 * throw new BusinessException(404, "用户不存在");
 * 
 * // 完整的异常信息
 * throw new BusinessException(400, 10001, "参数验证失败");
 * 
 * // 携带扩展信息的异常
 * Map<Object, Object> extInfo = new HashMap<>();
 * extInfo.put("userId", "12345");
 * extInfo.put("operation", "updateProfile");
 * throw new BusinessException(400, 10002, "用户信息更新失败", extInfo);
 * 
 * // 在业务逻辑中使用
 * public void deleteRoom(String roomId) {
 *     Room room = roomRepository.findById(roomId);
 *     if (room == null) {
 *         throw new BusinessException(ReturnCodeEnum.ROOM_NOT_EXISTS_ERROR);
 *     }
 *     // 删除房间的逻辑...
 * }
 * }
 * </pre>
 * 
 * <p><strong>异常处理建议：</strong></p>
 * <ul>
 *   <li>在Controller层统一捕获并转换为标准的API响应</li>
 *   <li>记录详细的异常日志，包括异常发生的上下文信息</li>
 *   <li>对敏感信息进行脱敏处理后再抛出异常</li>
 *   <li>根据不同的错误类型设置合适的HTTP状态码</li>
 * </ul>
 * 
 * <p><strong>最佳实践：</strong></p>
 * <ul>
 *   <li>优先使用ReturnCodeEnum中预定义的错误码</li>
 *   <li>异常信息要具体明确，便于问题定位</li>
 *   <li>在抛出异常前进行必要的清理工作</li>
 *   <li>避免在循环中频繁抛出异常影响性能</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 * @see ReturnCodeEnum
 * @see RuntimeException
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    
    /**
     * 业务错误码
     * <p>用于标识具体的业务错误类型，便于客户端进行相应的错误处理。</p>
     */
    private Integer code;
    
    /**
     * 错误描述信息
     * <p>详细描述错误的具体情况，用于展示给用户或记录到日志中。</p>
     */
    private String message;
    
    /**
     * HTTP状态码
     * <p>对应的HTTP响应状态码，默认为500（服务器内部错误）。</p>
     */
    private Integer status = 500;
    
    /**
     * 扩展信息
     * <p>用于携带额外的上下文信息，如用户ID、操作类型、请求参数等。</p>
     */
    private Map<Object, Object> ext;

    /**
     * 仅包含错误消息的构造函数
     * 
     * <p>创建一个只包含错误描述的业务异常，适用于简单的错误场景。</p>
     * 
     * @param message 错误描述信息
     */
    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * 仅包含错误码的构造函数
     * 
     * <p>创建一个只包含错误码的业务异常，错误信息需要通过其他方式获取。</p>
     * 
     * @param code 业务错误码
     */
    public BusinessException(Integer code) {
        this.code = code;
    }

    /**
     * 包含错误码和消息的构造函数
     * 
     * <p>创建一个包含完整错误信息的业务异常，这是最常用的构造方式。</p>
     * 
     * <p><strong>使用示例：</strong></p>
     * <pre>
     * throw new BusinessException(404, "用户不存在");
     * throw new BusinessException(10001, "房间已满，无法加入");
     * </pre>
     * 
     * @param code 业务错误码
     * @param message 错误描述信息
     */
    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 包含HTTP状态码、错误码和消息的构造函数
     * 
     * <p>创建一个包含完整信息的业务异常，包括对应的HTTP状态码。</p>
     * 
     * <p><strong>状态码选择建议：</strong></p>
     * <ul>
     *   <li>400 - 客户端请求错误（参数错误、格式错误等）</li>
     *   <li>401 - 未授权（Token无效、未登录等）</li>
     *   <li>403 - 禁止访问（权限不足等）</li>
     *   <li>404 - 资源不存在</li>
     *   <li>409 - 资源冲突（重复创建等）</li>
     *   <li>500 - 服务器内部错误</li>
     * </ul>
     * 
     * @param status HTTP状态码
     * @param code 业务错误码
     * @param message 错误描述信息
     */
    public BusinessException(Integer status, Integer code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    /**
     * 使用枚举和HTTP状态码的构造函数
     * 
     * <p>使用预定义的返回码枚举创建业务异常，并指定HTTP状态码。</p>
     * <p>这种方式可以确保错误码和消息的一致性和标准化。</p>
     * 
     * <p><strong>使用示例：</strong></p>
     * <pre>
     * // 房间不存在错误，返回404状态码
     * throw new BusinessException(404, ReturnCodeEnum.ROOM_NOT_EXISTS_ERROR);
     * 
     * // 参数错误，返回400状态码
     * throw new BusinessException(400, ReturnCodeEnum.PARAMS_ERROR);
     * </pre>
     * 
     * @param status HTTP状态码
     * @param returnCodeEnum 预定义的返回码枚举
     */
    public BusinessException(Integer status, ReturnCodeEnum returnCodeEnum) {
        this.status = status;
        this.code = returnCodeEnum.getCode();
        this.message = returnCodeEnum.getMessage();
    }

    /**
     * 仅使用枚举的构造函数
     * 
     * <p>使用预定义的返回码枚举创建业务异常，HTTP状态码使用默认值500。</p>
     * <p>这是推荐的异常创建方式，可以确保错误码的标准化和一致性。</p>
     * 
     * <p><strong>使用示例：</strong></p>
     * <pre>
     * // 房间不存在
     * throw new BusinessException(ReturnCodeEnum.ROOM_NOT_EXISTS_ERROR);
     * 
     * // 用户踢出失败
     * throw new BusinessException(ReturnCodeEnum.USER_KICK_OUT_ERROR);
     * 
     * // 获取锁失败
     * throw new BusinessException(ReturnCodeEnum.ROOM_ACQUIRE_LOCK_ERROR);
     * </pre>
     * 
     * @param returnCodeEnum 预定义的返回码枚举
     */
    public BusinessException(ReturnCodeEnum returnCodeEnum) {
        this.code = returnCodeEnum.getCode();
        this.message = returnCodeEnum.getMessage();
    }
}
