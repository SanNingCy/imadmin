package com.seekweb4.chat.api.req;


/**
 * 接口请求数据body封装
 * 
 * @author
 * 
 */
public class SearchReqJson extends ReqJson {
	/**
	 * 当前页码
	 * @return
	 */
	public String getKey() {
		return getString("key");
	}

}
