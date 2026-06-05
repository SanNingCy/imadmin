package com.seekweb4.chat.api.req;

import com.alibaba.fastjson2.JSONObject;

/**
 * 接口请求数据body封装
 * 
 * @author
 * 
 */
public class ReqJson extends JSONObject {
	/**
	 * 当前页码
	 * @return
	 */
	public int getPageNo() {
		int value = getIntValue("pageNo");
		return value > 0 ? value : 1;
	}
	
	/**
	 * 页面大小
	 * @return
	 */
	public int getPageSize() {
		int value = getIntValue("pageSize");
		return value > 0 ? value : 10;
	}
	
	/**
	 * 用户ID
	 * @return
	 */
	public String getUid() {
		return getString("uid");
	}

}
