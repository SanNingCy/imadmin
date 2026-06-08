package com.seekweb4.chat.modules.sys.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seekweb4.chat.common.utils.CacheUtils;
import com.seekweb4.chat.common.utils.SpringContextHolder;
import com.seekweb4.chat.modules.sys.entity.DictType;
import com.seekweb4.chat.modules.sys.entity.DictValue;
import com.seekweb4.chat.modules.sys.service.DictTypeService;

/**
 * 字典工具类
 * @author lixinapp
 * @version 2016-5-29
 */
public class DictUtils {

	public static final String CACHE_DICT_MAP = "dictMap";

	public static String getDictLabel(String value, String type, String defaultLabel){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)){
			for (DictValue dictValue : getDictList(type)){
				if (value.equals(dictValue.getValue())){
					return dictValue.getLabel();
				}
			}
		}
		return defaultLabel;
	}

	public static String getDictLabels(String values, String type, String defaultValue){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(values)){
			List<String> valueList = Lists.newArrayList();
			for (String value : StringUtils.split(values, ",")){
				valueList.add(getDictLabel(value, type, defaultValue));
			}
			return StringUtils.join(valueList, ",");
		}
		return defaultValue;
	}

	public static String getDictValue(String label, String type, String defaultLabel){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)){
			for (DictValue dictValue : getDictList(type)){
				if (label.equals(dictValue.getLabel())){
					return dictValue.getValue();
				}
			}
		}
		return defaultLabel;
	}

	public static List<DictValue> getDictList(String type){
		List<DictValue> dictList = getDictMap().get(type);
		return dictList != null ? dictList : Lists.newArrayList();
	}

	public static Map<String, List<DictValue>> getDictMap() {
		@SuppressWarnings("unchecked")
		Map<String, List<DictValue>> dictMap = (Map<String, List<DictValue>>) CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap == null || dictMap.containsKey(null)) {
			dictMap = buildDictMapFromDb();
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}
		return dictMap;
	}

	public static void reloadCache() {
		clearCache();
		CacheUtils.put(CACHE_DICT_MAP, buildDictMapFromDb());
	}

	public static void clearCache() {
		CacheUtils.remove(CACHE_DICT_MAP);
	}

	private static Map<String, List<DictValue>> buildDictMapFromDb() {
		Map<String, List<DictValue>> dictMap = Maps.newHashMap();
		List<DictType> dict = SpringContextHolder.getBean(DictTypeService.class).getDict();
		for (DictType dictType : dict) {
			if (StringUtils.isNotBlank(dictType.getType())) {
				dictMap.put(dictType.getType(), Lists.newArrayList(dictType.getDictValueList()));
			}
		}
		return dictMap;
	}

	/*
	 * 反射根据对象和属性名获取属性值
	 */
	public static Object getValue(Object obj, String filed) {
		try {
			Class clazz = obj.getClass();
			PropertyDescriptor pd = new PropertyDescriptor(filed, clazz);
			Method getMethod = pd.getReadMethod();//获得get方法
			if (pd != null) {
				Object o = getMethod.invoke(obj);//执行get方法返回一个Object
				return o;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
