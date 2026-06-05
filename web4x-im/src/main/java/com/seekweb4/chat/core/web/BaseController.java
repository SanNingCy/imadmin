package com.seekweb4.chat.core.web;

import com.seekweb4.chat.api.utils.QrCodeUtil;
import com.seekweb4.chat.common.beanvalidator.BeanValidators;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.utils.Reflections;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.core.mapper.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 控制器支持类
 * @author lixinapp
 * @version 2016-3-23
 */
public abstract class BaseController {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 验证Bean实例对象
	 */
	@Autowired
	protected Validator validator;
	@Autowired
	protected AppProperites appProperites;

	/**
	 * 服务端参数有效性验证
	 * @param object 验证的实体对象
	 * @param groups 验证组
	 */
	protected String beanValidator(Object object, Class<?>... groups) {
		try{
			BeanValidators.validateWithException(validator, object, groups);
		}catch(ConstraintViolationException ex){
			List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
			list.add(0, "数据验证失败：");
			return getMessage(list.toArray(new String[]{}));
		}
		return "";
	}

	protected String getMessage( String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages){
			sb.append(message).append(messages.length>1?"<br/>":"");
		}
		return sb.toString();
	}

	/**
	 * 客户端返回JSON字符串
	 * @param response
	 * @param object
	 * @return
	 */
	protected String renderString(HttpServletResponse response, Object object) {
		return renderString(response, JsonMapper.toJsonString(object));
	}

	/**
	 * 客户端返回字符串
	 * @param response
	 * @param string
	 * @return
	 */
	protected String renderString(HttpServletResponse response, String string) {
		try {
			response.reset();
	        response.setContentType("application/json");
	        response.setCharacterEncoding("utf-8");
			response.getWriter().print(string);
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	/**
	 * 初始化数据绑定
	 * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
	 * 2. 将字段中Date类型转换为String类型
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// 设置需要包裹的元素个数，默认为256
		binder.setAutoGrowCollectionLimit(1024);
		// String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
//		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
//			@Override
//			public void setAsText(String text) {
//				setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
//			}
//			@Override
//			public String getAsText() {
//				Object value = getValue();
//				return value != null ? value.toString() : "";
//			}
//		});
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(DateUtils.parseDate(text));
			}
//			@Override
//			public String getAsText() {
//				Object value = getValue();
//				return value != null ? DateUtils.formatDateTime((Date)value) : "";
//			}
		});
	}

	protected String getRealPath(String url) {
		if (StringUtils.isNotBlank(url)) {
			if (StringUtils.startsWithIgnoreCase(url, "http")) {
				return url;
			}
			return appProperites.getFilePath() + url;
		}
		return "";
	}

	/**
	 * 登录二维码 qrcode2 对外展示：仍以 .jpg 结尾的当作静态图路径走 {@link #getRealPath(String)}；
	 * 否则视为需编码的字符串，生成二维码 JPEG 的 data URL。
	 */
	protected String resolveQrcode2ForResponse(String qrcode2) {
		if (StringUtils.isBlank(qrcode2)) {
			return "";
		}
		String v = qrcode2.trim();
		if (StringUtils.endsWithIgnoreCase(v, ".jpg") || StringUtils.startsWithIgnoreCase(v, "http")) {
			return getRealPath(v);
		}
		String dataUrl = QrCodeUtil.encodeStringToQrJpegDataUrl(v);
		return StringUtils.isNotBlank(dataUrl) ? dataUrl : "";
	}


	protected boolean isBlank(String...values) {
		for (String val : values) {
			if (StringUtils.isBlank(val)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 规范化查询参数：自动处理实体对象中所有嵌套对象的 JSON 字符串参数
	 * 将 JSON 字符串格式的 id（如 {"id":"xxx"}）提取为实际的 id 值
	 * 将空字符串参数转换为 null，避免影响查询
	 * 
	 * @param entity 实体对象
	 */
	protected void normalizeQueryParams(Object entity) {
		if (entity == null) {
			return;
		}
		
		Class<?> clazz = entity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		
		for (Field field : fields) {
			try {
				// 跳过基本类型、String、List、Map 等类型
				Class<?> fieldType = field.getType();
				if (fieldType.isPrimitive() || 
					fieldType == String.class || 
					fieldType == List.class || 
					fieldType == Map.class ||
					fieldType.getName().startsWith("java.") ||
					fieldType.getName().startsWith("javax.")) {
					continue;
				}
				
				// 获取字段值
				Object fieldValue = Reflections.getFieldValue(entity, field.getName());
				if (fieldValue == null) {
					continue;
				}
				
				// 检查是否有 getId() 方法
				Method getIdMethod = Reflections.getAccessibleMethod(fieldValue, "getId", new Class[]{});
				if (getIdMethod == null) {
					continue;
				}
				
				// 获取 id 值
				Object idValue = Reflections.invokeMethod(fieldValue, "getId", new Class[]{}, new Object[]{});
				if (idValue == null || !(idValue instanceof String)) {
					continue;
				}
				
				String idStr = (String) idValue;
				
				// 如果是 JSON 字符串格式，提取 id 值
				if (idStr.startsWith("{")) {
					String extractedId = extractIdFromJson(idStr);
					if (StringUtils.isNotBlank(extractedId)) {
						// 创建新对象并设置 id
						Object newObject = fieldType.getConstructor(String.class).newInstance(extractedId);
						Reflections.setFieldValue(entity, field.getName(), newObject);
					} else {
						// id 为空，设置为 null
						Reflections.setFieldValue(entity, field.getName(), null);
					}
				} else if (idStr.isEmpty()) {
					// id 为空字符串：只清空嵌套对象的 id，保留 group.idno / u.idno 等其它查询条件
					// （前端常同时带 group.id= 与 group.idno=，原先整对象置 null 会导致按 idno 查不出）
					Method setIdMethod = Reflections.getAccessibleMethod(fieldValue, "setId", new Class[]{String.class});
					if (setIdMethod != null) {
						Reflections.invokeMethod(fieldValue, "setId", new Class[]{String.class}, new Object[]{null});
					} else {
						Reflections.setFieldValue(entity, field.getName(), null);
					}
				}
			} catch (Exception e) {
				// 忽略处理失败的字段，继续处理其他字段
				logger.debug("处理字段 {} 时出错: {}", field.getName(), e.getMessage());
			}
		}
		
		// 处理 String 类型字段，将空字符串转换为 null
		for (Field field : fields) {
			if (field.getType() == String.class) {
				try {
					Object fieldValue = Reflections.getFieldValue(entity, field.getName());
					if (fieldValue != null && ((String) fieldValue).isEmpty()) {
						Reflections.setFieldValue(entity, field.getName(), null);
					}
				} catch (Exception e) {
					// 忽略处理失败的字段
					logger.debug("处理 String 字段 {} 时出错: {}", field.getName(), e.getMessage());
				}
			}
		}
	}

	/**
	 * 从 JSON 字符串中提取 id 值，例如：{"id":"xxx"} -> "xxx"
	 */
	private String extractIdFromJson(String jsonStr) {
		if (jsonStr == null || !jsonStr.startsWith("{")) {
			return null;
		}
		Pattern pattern = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]*)\"");
		Matcher matcher = pattern.matcher(jsonStr);
		return matcher.find() ? matcher.group(1) : null;
	}
}
