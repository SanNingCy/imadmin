package com.seekweb4.chat.common.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seekweb4.chat.core.mapper.JsonMapper;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.*;


/**
 * $.ajax后需要接受的JSON
 *
 * @author
 *
 */
public class AjaxJson extends HashMap<String,Object> implements Serializable {

	public AjaxJson(){
		this.put("success", true);
		this.put("code", HttpStatus.OK.value());
		this.put("msg", "操作成功");
	}

	public String getMsg() {
		return (String)this.get("msg");
	}

	public void setMsg(String msg) {//向json中添加属性，在js中访问，请调用data.msg
		this.put("msg", msg);
	}

	public boolean isSuccess() {
		return (boolean)this.get("success");
	}

	public void setSuccess(boolean success) {
		this.put("success", success);
	}

	@JsonIgnore//返回对象时忽略此属性
	public String getJsonStr() {//返回json字符串数组，将访问msg和key的方式统一化，都使用data.key的方式直接访问。
		String json = JsonMapper.getInstance().toJson(this);
		return json;
	}
	public static AjaxJson success() {
		return new AjaxJson();
	}
	@JsonIgnore//返回对象时忽略此属性
	public static AjaxJson success(String msg) {
		AjaxJson j = new AjaxJson();
		j.setMsg(msg);
		return j;
	}
	public static AjaxJson success(Map<String, Object> map) {
		AjaxJson ajaxJson = new AjaxJson();
		ajaxJson.putAll(map);
		return ajaxJson;
	}
	@JsonIgnore//返回对象时忽略此属性
	public static AjaxJson error(String msg) {
		AjaxJson j = new AjaxJson();
		j.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		j.setSuccess(false);
		j.setMsg(msg);
		return j;
	}
	// 未授权
	@JsonIgnore//返回对象时忽略此属性
	public static AjaxJson fail(String msg) {
		AjaxJson j = new AjaxJson();
		j.setCode(HttpStatus.UNAUTHORIZED.value());
		j.setSuccess(false);
		j.setMsg(msg);
		return j;
	}

	// 返回对象时忽略此属性
	@JsonIgnore
	public static AjaxJson error(int code,String msg) {
		AjaxJson j = new AjaxJson();
		j.setCode(code);
		j.setSuccess(false);
		j.setMsg(msg);
		return j;
	}

//	@Override
//	public AjaxJson put(String key, Object value) {
//		super.put(key, value);
//		return this;
//	}

//	@Override
//	public AjaxJson put(String key, Object value) {
//		// 定义顶层字段，不要包进 data
//		final Set<String> TOP_LEVEL_KEYS = new HashSet<>(Arrays.asList("code", "msg", "success","data"));
//		if (TOP_LEVEL_KEYS.contains(key)) {
//			// 顶层字段直接放在外层
//			super.put(key, value);
//		} else {
//			Map<String, Object> dataMap = new HashMap<>(6);
//			dataMap.put(key, value);
//			super.put("data", dataMap);
//		}
//		return this;
//	}
	@Override
	public AjaxJson put(String key, Object value) {
		final Set<String> TOP_LEVEL_KEYS = new HashSet<>(
				Arrays.asList("code", "msg", "success", "data")
		);

		if (TOP_LEVEL_KEYS.contains(key)) {
			super.put(key, value);
		} else {

			// 先尝试从已有的 data 中取
			Map<String, Object> dataMap = (Map<String, Object>) super.get("data");

			if (dataMap == null) {
				dataMap = new HashMap<>(6);
			}

			dataMap.put(key, value);
			super.put("data", dataMap);
		}
		return this;
	}



	public AjaxJson putMap(Map m) {
		super.putAll(m);
		return this;
	}
	public AjaxJson setData(Map<String, Object> data) {
		super.put("data", data);
		return this;
	}

	public int getCode() {
		return (int)this.get("code");
	}

	public void setCode(int code) {
		this.put("code", code);
	}
	public <T> AjaxJson setDataList(List<T> dataList) {
		this.put("dataList", dataList);
		return this;
	}
	public AjaxJson setTotalPage(int totalPage) {
		this.put("totalPage", totalPage);
		return this;
	}
	public AjaxJson setTotalCount(long totalCount) {
		this.put("totalCount", totalCount);
		return this;
	}
}
