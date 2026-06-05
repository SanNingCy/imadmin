package com.seekweb4.chat.api.error;

import com.seekweb4.chat.common.json.AjaxJson;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;

/**
 * API异常处理
 * @author mall
 *
 */
@ControllerAdvice(basePackages = {"com.seekweb4.chat.api"})
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 2)
public class ErrorAdvice {
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public AjaxJson response(Exception e) {
		if (!(e instanceof BizException)) {
			log.error("APP端报错:", e);
		}
		return AjaxJson.error(e.getMessage());
	}

}
