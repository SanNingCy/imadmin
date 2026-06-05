package com.seekweb4.chat.common.utils;

import cn.hutool.core.util.RandomUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 封装各种生成唯一性ID算法的工具类.
 * @author lixinapp
 * @version 2016-01-15
 */
@Service
@Lazy(false)
public class IdGen implements SessionIdGenerator {

	private static SecureRandom random = new SecureRandom();

	/**
	 * 生成订单号
	 * 
	 * @return
	 */
	public static String getOrderNo() {
		String key = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
		StringRedisUtils redisUtils = StringRedisUtils.getInstance();
		long num = redisUtils.incrBy(key, 1L);
		if (num == 1L) {
			redisUtils.expire(key, 60, TimeUnit.SECONDS);
		}
		return key + (String.format("%04d", num));
	}

	/**
	 * 获取数字编号
	 * @param length 长度
	 * @return
	 */
	public static String getNumber(int length) {
		if (length < 1) {
			return "";
		}
		while (true) {
			String number = RandomUtil.randomNumbers(length);
			if (!number.startsWith("0")) {
				return number;
			}
		}
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 使用SecureRandom随机生成Long. 
	 */
	public static long randomLong() {
		return Math.abs(random.nextLong());
	}

	/**
	 * 基于Base62编码的SecureRandom随机生成bytes.
	 */
	public static String randomBase62(int length) {
		byte[] randomBytes = new byte[length];
		random.nextBytes(randomBytes);
		return Encodes.encodeBase62(randomBytes);
	}
	

	@Override
	public Serializable generateId(Session session) {
		return IdGen.uuid();
	}
	
}
