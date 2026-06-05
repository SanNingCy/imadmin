package com.seekweb4.chat.api.Intercept;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.seekweb4.chat.api.req.AesUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.alibaba.fastjson2.JSONObject;

import lombok.extern.slf4j.Slf4j;

//@ControllerAdvice(basePackages = "com.seekweb4.chat.api")
@Slf4j
public class LogRequestAdvice implements RequestBodyAdvice{


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
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {

		Method method = methodParameter.getMethod();
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
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		Method method = parameter.getMethod();
		log.info("请求路径:{}.{}, 请求参数:{}", method.getDeclaringClass().getSimpleName(), method.getName(), JSONObject.toJSONString(body));
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		return inputMessage;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		//入参解密
		log.info("原始入参：{}",JSONObject.toJSONString(body));
		JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(body));
		String mingwen = AesUtil.decrypt(jsonObject.getString("encrData"));
		body = JSONObject.parseObject(mingwen, targetType);

		Method method = parameter.getMethod();
		log.info("请求路径:{}.{}, 请求参数:{}", method.getDeclaringClass().getSimpleName(), method.getName(), JSONObject.toJSONString(body));
		return body;
	}
}
