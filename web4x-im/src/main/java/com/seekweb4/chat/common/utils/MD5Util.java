package com.seekweb4.chat.common.utils;

import java.security.MessageDigest;

public class MD5Util {
	/**
	 * md5加密
	 * 
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		return md5(str, "UTF-8");
	}
	
	/**
	 * md5加密
	 * 
	 * @param str 内容
	 * @param charset 字符集
	 * @return
	 */
	public static String md5(String str, String charset) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes(charset));
			byte[] byteDigest = md.digest();
			int i;
			StringBuffer buf = new StringBuffer();
			for (byte element : byteDigest) {
				i = element;
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString();
			// 16位的加密
			// return buf.toString().substring(8, 24);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(MD5Util.md5("123456"));

	}
}
