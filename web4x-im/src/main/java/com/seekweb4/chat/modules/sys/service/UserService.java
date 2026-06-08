package com.seekweb4.chat.modules.sys.service;

import com.seekweb4.chat.common.utils.*;
import com.seekweb4.chat.common.utils.CacheUtils;
import com.seekweb4.chat.common.utils.MD5Util;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.core.service.ServiceException;
import com.seekweb4.chat.modules.sys.entity.Office;
import com.seekweb4.chat.modules.sys.entity.User;
import com.seekweb4.chat.modules.sys.mapper.UserMapper;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;
import java.util.List;

/**
 * 用户管理
 * @author lixinapp
 * @version 2016-12-05
 */
@Service
@Transactional(readOnly = true)
public class UserService  extends CrudService<UserMapper, User> {

	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;

	/**
	 * 获取用户
	 * @param id
	 * @return
	 */
	public User get(String id) {
		return UserUtils.get(id);
	}

	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return
	 */
	public User getUserByLoginName(String loginName) {
		return UserUtils.getByLoginName(loginName);
	}


	/**
	 * 通过部门ID获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> findUserByOfficeId(String officeId) {
		List<User> list = (List<User>) CacheUtils.get(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId);
		if (list == null){
			User user = new User();
			user.setOffice(new Office(officeId));
			list = mapper.findUserByOfficeId(user);
			CacheUtils.put(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId, list);
		}
		return list;
	}

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		if (StringUtils.isBlank(user.getId())){
			user.preInsert();
			mapper.insert(user);
		}else{
			// 清除原用户机构用户缓存
			User oldUser = mapper.get(user.getId());
			if (oldUser.getOffice() != null && oldUser.getOffice().getId() != null){
				CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + oldUser.getOffice().getId());
			}
			// 更新用户数据
			user.preUpdate();
			mapper.update(user);
		}
		if (StringUtils.isNotBlank(user.getId())){
			// 更新用户与角色关联
			mapper.deleteUserRole(user);
			if (user.getRoleList() != null && user.getRoleList().size() > 0){
				mapper.insertUserRole(user);
			}else{
				throw new ServiceException(user.getLoginName() + "没有设置角色！");
			}
			// 清除用户缓存
			UserUtils.clearCache(user);
		}
	}

	@Transactional(readOnly = false)
	public void updateUserInfo(User user) {
		user.preUpdate();
		mapper.updateUserInfo(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
	}

	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		mapper.deleteUserRole(user);
		mapper.delete(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
	}

	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		User user = new User(id);
		user.setPassword(entryptPassword(newPassword));
		mapper.updatePasswordById(user);
		user.setLoginName(loginName);
		UserUtils.clearCache(user);
		scheduleReloadUserCacheAfterCommit(loginName);
	}

	private void scheduleReloadUserCacheAfterCommit(String loginName) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					UserUtils.reloadByLoginName(loginName);
				}
			});
		} else {
			UserUtils.reloadByLoginName(loginName);
		}
	}

	@Transactional(readOnly = false)
	public void updateUserLoginInfo(User user) {
		// 保存上次登录信息
		user.setOldLoginIp(user.getLoginIp());
		user.setOldLoginDate(user.getLoginDate());
		// 更新本次登录信息
		//user.setLoginIp(UserUtils.getSession().getHost());
		user.setLoginIp(StringUtils.getRemoteAddr());
		user.setLoginDate(new Date());
		mapper.updateLoginInfo(user);
	}

	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		return MD5Util.md5(plainPassword);
//		byte[] salt = Digests.generateSalt(SALT_SIZE);
//		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
//		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}

	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		plainPassword = MD5Util.md5(plainPassword);
		return plainPassword.contentEquals(password);
		/*byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));*/
	}

	@Transactional(readOnly = false)
	public int updateTwoFactorCode(User user) {
		return mapper.updateTwoFactorCode(user.getId(), user.getTwoFactorCode(), user.getTwoFactorTime());
	}

	@Transactional(readOnly = false)
	public User selectByUserId(String id) {
		return  mapper.selectByUserId(id);
	}

	public User selectByTwoFactorCode(String twoFactorCode) {
		return  mapper.selectByTwoFactorCode(twoFactorCode);
	}
}
