package com.seekweb4.chat.modules.sys.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.sys.entity.User;

import org.apache.ibatis.annotations.Param;

/**
 * 用户MAPPER接口
 * @author lixinapp
 * @version 2017-05-16
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

	/**
	 * 根据登录名称查询用户
	 * @param user
	 * @return
	 */
	public User getByLoginName(User user);

	/**
	 * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByOfficeId(User user);

	/**
	 * 查询全部用户数目
	 * @return
	 */
	public long findAllCount(User user);

	/**
	 * 更新用户密码
	 * @param user
	 * @return
	 */
	public int updatePasswordById(User user);

	/**
	 * 更新登录信息，如：登录IP、登录时间
	 * @param user
	 * @return
	 */
	public int updateLoginInfo(User user);

	/**
	 * 删除用户角色关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRole(User user);

	/**
	 * 插入用户角色关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRole(User user);

	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo(User user);

	/**
	 *
	 * 查询用户-->用来添加到常用联系人
	 *
	 */
	public List<User> searchUsers(User user);

	/**
	 *
	 */

	public List<User>  findListByOffice(User user);

	int updateTwoFactorCode(String id, String twoFactorCode, Date twoFactorTime);

	User selectByUserId(String id);


	User selectByTwoFactorCode(@Param("twoFactorCode") String twoFactorCode);

}
