package com.seekweb4.chat.agora.config;

import com.seekweb4.chat.agora.bean.dto.R;
import com.seekweb4.chat.agora.bean.enums.ReturnCodeEnum;
import com.seekweb4.chat.agora.bean.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

/**
 * 声网/Agora 模块全局异常处理（与若依 GlobalExceptionHandler 区分 bean 名）
 */
@Slf4j
@ControllerAdvice(basePackages = "com.seekweb4.chat.agora")
@ResponseBody
public class AgoraGlobalExceptionHandler {

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public R<Object> HttpRequestMethodNotSupportedExceptionHandle(Exception e, HttpServletResponse httpResponse,
            HttpServletRequest servletRequest) {
        log.error("HttpRequestMethodNotSupportedExceptionHandle, unsupported method exception, {}",
                e.getClass().getName(), e);
        httpResponse.setStatus(HttpStatus.NOT_IMPLEMENTED.value());
        return R.error(ReturnCodeEnum.METHOD_ERROR);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public R<Object> MethodArgumentTypeMismatchExceptionHandle(Exception e, HttpServletResponse httpResponse,
            HttpServletRequest servletRequest) {
        log.error("MethodArgumentTypeMismatchExceptionHandle, param exception, {}", e.getClass().getName(), e);
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        return R.error(ReturnCodeEnum.PARAMS_ERROR);
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public R<Object> MethodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e,
            HttpServletResponse httpResponse, HttpServletRequest servletRequest) {
        StringBuilder msg = new StringBuilder();
        for (ObjectError objectError : e.getBindingResult().getAllErrors()) {
            msg.append(objectError.getDefaultMessage()).append("; ");
        }

        log.error("MethodArgumentNotValidExceptionHandle, param error, error:{}", msg.toString());
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        return R.error(ReturnCodeEnum.PARAMS_ERROR.getCode(), msg.toString());
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    public R<Object> ConstraintViolationExceptionHandle(ConstraintViolationException e,
            HttpServletResponse httpResponse, HttpServletRequest servletRequest) {
        String msg = e.getMessage();
        log.error("ConstraintViolationExceptionHandle, param error, error:{}", msg);
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        return R.error(ReturnCodeEnum.PARAMS_ERROR.getCode(), msg);
    }

    @ExceptionHandler(value = BindException.class)
    public R<Object> BindExceptionHandle(BindException e, HttpServletResponse httpResponse,
            HttpServletRequest servletRequest) {
        String msg = e.getAllErrors().get(0).getDefaultMessage();
        log.error("BindExceptionHandle, param error, error:{}", msg);
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        return R.error(ReturnCodeEnum.PARAMS_ERROR.getCode(), msg);
    }

    @ExceptionHandler(value = Exception.class)
    public R<Object> ExceptionHandle(Exception e, HttpServletResponse httpResponse, HttpServletRequest servletRequest) {
        String msg = e.getMessage();
        log.error("ExceptionHandle, exception, {}, {}", e.getClass().getName(), msg, e);
        httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return R.error(ReturnCodeEnum.SERVER_ERROR.getCode(), msg);
    }

    @ExceptionHandler(value = BusinessException.class)
    public R<Object> BusinessExceptionHandle(BusinessException e, HttpServletResponse httpResponse,
            HttpServletRequest servletRequest) {
        log.error("BusinessExceptionHandle, business exception, code:{}, error:{}", e.getCode(), e.getMessage());

        if (e.getStatus() != null) {
            httpResponse.setStatus(e.getStatus());
        } else {
            httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return R.error(e.getCode(), e.getMessage());
    }
}
