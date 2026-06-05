package com.seekweb4.chat.asset.config;


import com.seekweb4.chat.common.json.AjaxJson;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 自定义业务异常类
 * @author coderpwh
 */
@Getter
public class BusinessException extends RuntimeException {

    private Integer code;
    private String message;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(AjaxJson resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
