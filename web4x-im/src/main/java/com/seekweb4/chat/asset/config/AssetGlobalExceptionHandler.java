package com.seekweb4.chat.asset.config;

/**
 * @author coderpwh
 */

import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.Set;

/**
 * 全局异常处理器
 * @author coderpwh
 */
@Slf4j
@RestControllerAdvice
public class AssetGlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public AjaxJson handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常，请求路径: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        if (e.getCode() != null) {
            return AjaxJson.error(e.getCode(), e.getMessage());

        }
        return AjaxJson.error(e.getMessage());
    }

    /**
     * 处理参数校验异常(Validated + @RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AjaxJson handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("参数验证失败，请求路径: {}", request.getRequestURI());
        String message = getValidationErrorMessage(e.getBindingResult());
        return AjaxJson.error(message);
    }

    /**
     * 处理参数绑定异常(Validated + 表单参数)
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AjaxJson handleBindException(BindException e, HttpServletRequest request) {
        log.error("参数绑定失败，请求路径: {}", request.getRequestURI());
        String message = getValidationErrorMessage(e.getBindingResult());
        return AjaxJson.error(message);
    }

    /**
     * 处理单个参数校验异常(Validated + @RequestParam/@PathVariable)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AjaxJson handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error("参数约束违反，请求路径: {}", request.getRequestURI());
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            message.append(violation.getMessage()).append("; ");
        }
        return AjaxJson.error(message.toString());
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AjaxJson handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("参数类型不匹配，请求路径: {}, 参数名: {}, 异常信息: {}",
                request.getRequestURI(), e.getName(), e.getMessage());
        String message = String.format("参数 '%s' 类型不匹配", e.getName());
        return AjaxJson.error(message);
    }

    /**
     * 处理访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AjaxJson handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("访问被拒绝，请求路径: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        return AjaxJson.error("没有权限访问该资源");
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxJson handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常，请求路径: {}", request.getRequestURI(), e);
        return AjaxJson.error(ResultCode.FAILED.getMessage());
    }

    /**
     * 处理数据库异常
     */
    @ExceptionHandler({
            org.springframework.dao.DataAccessException.class,
            org.springframework.jdbc.BadSqlGrammarException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxJson handleDatabaseException(Exception e, HttpServletRequest request) {
        log.error("数据库异常，请求路径: {}", request.getRequestURI(), e);
        return AjaxJson.error(ResultCode.FAILED.getCode(),ResultCode.FAILED.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxJson handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常，请求路径: {}", request.getRequestURI(), e);
        return AjaxJson.error(ResultCode.FAILED.getCode(),ResultCode.FAILED.getMessage());
    }

    /**
     * 获取验证错误信息
     */
    private String getValidationErrorMessage(BindingResult bindingResult) {
        StringBuilder message = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            message.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }
        return message.toString();
    }
}