package com.seekweb4.chat.api.Intercept;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekweb4.chat.api.req.AesUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//@ControllerAdvice(basePackages = "com.seekweb4.chat.api")
@Slf4j
public class LogResponseAdvice implements ResponseBodyAdvice<Object> {

	// 定义不需要拦截的方法名列表
	private static final Set<String> EXCLUDED_METHODS = Collections.unmodifiableSet(new HashSet<String>() {{
		add("publicApi");
		add("healthCheck");
		add("status");
	}});

	private static final Set<String> EXCLUDED_CLASSES = Collections.unmodifiableSet(new HashSet<String>() {{
		add("AesController");
	}});
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {


		Method method = returnType.getMethod();
		if (method == null) {
			return true;
		}

		String methodName = method.getName();
		String className = method.getDeclaringClass().getSimpleName();

		// 如果方法名或类名在排除列表中，则不拦截
		if (EXCLUDED_METHODS.contains(methodName) || EXCLUDED_CLASSES.contains(className)) {
			return false;
		}

		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		//返回加密
		String encrypt = AesUtil.encrypt(JSONObject.toJSONString(body));
		body = encrypt;

		try {
			log.info("返回内容: {}", new ObjectMapper().writeValueAsString(AesUtil.decrypt(encrypt)));
		} catch (Exception e) {
			log.error("", e);
		}
		return body;
	}

}
