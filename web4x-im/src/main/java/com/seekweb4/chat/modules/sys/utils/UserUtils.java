package com.seekweb4.chat.modules.sys.utils;

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.CacheUtils;
import com.seekweb4.chat.common.utils.SpringContextHolder;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.service.BaseService;
import com.seekweb4.chat.modules.sys.entity.*;
import com.seekweb4.chat.modules.sys.mapper.*;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.List;

/**
 * 用户工具类
 * @author lixinapp
 * @version 2016-12-05
 */
public class UserUtils {


	public static final String USER_CACHE = "userCache-v2";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "ln";
	public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";

	public static final String CACHE_ROLE_LIST = "roleList-v2";
	public static final String CACHE_TOP_MENU = "topMenu-v2";
	public static final String CACHE_MENU_LIST = "menuList-v2";
	public static final String CACHE_DATA_RULE_LIST = "dataRuleList-v2";
	public static final String CACHE_AREA_LIST = "areaList-v2";
	public static final String CACHE_OFFICE_LIST = "officeList-v2";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList-v2";

	public static final String CACHE_SPLIT = "_user_id_";

	/**
	 * 根据ID获取用户
	 * @param id
	 * @return 取不到返回null
	 */
	public static User get(String id){
		User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
		if (user ==  null){
			user = SpringContextHolder.getBean(UserMapper.class).get(id);
			if (user == null){
				return null;
			}
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}

	public static String getUserNameById(String id){
		User user = get(id);
		if(user == null){
			return "";
		}else{
			return user.getName();
		}
	}


	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return 取不到返回null
	 */
	public static User getByLoginName(String loginName){
		User user = (User) CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		if (user == null){
			user = SpringContextHolder.getBean(UserMapper.class).getByLoginName(new User (null, loginName));
			if (user == null){
				return null;
			}
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}

	/**
	 * 清除当前用户缓存
	 */
	public static void clearCache(){
		removeCache(CACHE_ROLE_LIST);
		removeCache(CACHE_DATA_RULE_LIST);
		removeCache(CACHE_TOP_MENU);
		removeCache(CACHE_MENU_LIST);
		removeCache(CACHE_AREA_LIST);
		removeCache(CACHE_OFFICE_LIST);
		removeCache(CACHE_OFFICE_ALL_LIST);
		UserUtils.clearCache(getUser());
	}

	/**
	 * 清除指定用户缓存
	 * @param user
	 */
	public static void clearCache(User user){
		if (user == null) {
			return;
		}
		if (StringUtils.isNotBlank(user.getId())) {
			CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		}
		if (StringUtils.isNotBlank(user.getLoginName())) {
			CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		}
		if (StringUtils.isNotBlank(user.getOldLoginName())) {
			CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
		}
		if (user.getOffice() != null && user.getOffice().getId() != null){
			CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOffice().getId());
		}
	}

	/**
	 * 改密后从数据库强制刷新 Redis 用户缓存，避免事务提交前其他请求用旧密码回填缓存。
	 */
	public static User reloadByLoginName(String loginName) {
		if (StringUtils.isBlank(loginName)) {
			return null;
		}
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		User user = SpringContextHolder.getBean(UserMapper.class).getByLoginName(new User(null, loginName));
		if (user == null) {
			return null;
		}
		CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
		CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		return user;
	}

	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static User getUser(){
		String token = getToken();
		if (token!=null){
			String loginName = JWTUtil.getLoginName(token);
			if (StringUtils.isNotBlank(loginName)) {
				User user = getByLoginName(loginName);
				if (user != null){
					return user;
				}
			}
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User ();
	}

	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static User getCasUser(){
		String token = getToken();
		if (token!=null){
			User user = getByLoginName(JWTUtil.getLoginName(token));
			if (user != null){
				return user;
			}
			return new User ();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User ();
	}
	/**
	 * 获取当前用户角色列表
	 * @return
	 */
	public static List<Role> getRoleList(){
		@SuppressWarnings("unchecked")
		List<Role> roleList = (List<Role>)getCache(CACHE_ROLE_LIST);
		if (roleList == null){
			User user = getUser();
			if (user.isAdmin()){
				roleList = SpringContextHolder.getBean(RoleMapper.class).findAllList(new Role ());
			}else{
				roleList = user.getRoleList();
			}
			putCache(CACHE_ROLE_LIST, roleList);
		}
		return roleList;
	}

	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static List<Menu> getMenuList(){
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			User user = getUser();
			if (user.isAdmin()){
				menuList = SpringContextHolder.getBean(MenuMapper.class).findAllList(new Menu ());
			}else{
				Menu m = new Menu ();
				m.setUserId(user.getId());
				menuList = SpringContextHolder.getBean(MenuMapper.class).findByUserId(m);
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}

	/**
	 * 获取当前用户授权数据权限
	 * @return
	 */
	public static List<DataRule> getDataRuleList(){
		@SuppressWarnings("unchecked")
		List<DataRule> dataRuleList = (List<DataRule>)getCache(CACHE_DATA_RULE_LIST);
		if (dataRuleList == null){
			User user = getUser();
			if (user.isAdmin()){
				dataRuleList = Lists.newArrayList();
			}else{
				dataRuleList = SpringContextHolder.getBean(DataRuleMapper.class).findByUserId(user);
			}
			putCache(CACHE_DATA_RULE_LIST, dataRuleList);
		}
		return dataRuleList;
	}

	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static Menu getTopMenu() {
		Menu topMenu = (Menu) getCache(CACHE_TOP_MENU);
		if (topMenu == null) {
			topMenu = SpringContextHolder.getBean(MenuMapper.class).get("1");
			putCache(CACHE_TOP_MENU, topMenu);
		}
		return  topMenu;
	}
	/**
	 * 获取当前用户授权的区域
	 * @return
	 */
	public static List<Area> getAreaList(){
		@SuppressWarnings("unchecked")
		List<Area> areaList = (List<Area>)getCache(CACHE_AREA_LIST);
		if (areaList == null){
			areaList = SpringContextHolder.getBean(AreaMapper.class).findAllList(new Area ());
			putCache(CACHE_AREA_LIST, areaList);
		}
		return areaList;
	}

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_LIST);
		if (officeList == null){
			User user = getUser();
			if (user.isAdmin()){
				officeList = SpringContextHolder.getBean(OfficeMapper.class).findAllList(new Office ());
			}else{
				Office office = new Office ();
				BaseService.dataRuleFilter(office);
				officeList = SpringContextHolder.getBean(OfficeMapper.class).findList(office);
			}
			putCache(CACHE_OFFICE_LIST, officeList);
		}
		return officeList;
	}

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeAllList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_ALL_LIST);
		if (officeList == null){
			officeList = SpringContextHolder.getBean(OfficeMapper.class).findAllList(new Office ());
		}
		return officeList;
	}

	/**
	 * 获取授权主要对象
	 */
	public static Subject getSubject(){
		return SecurityUtils.getSubject();
	}

	/**
	 * 获取当前登录者对象
	 */
	public static String getToken(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Object token = subject.getPrincipal();
			if (token != null){
				return token.toString();
			}
		}catch (UnavailableSecurityManagerException e) {

		}catch (InvalidSessionException e){

		}
		return null;
	}

	public static Session getSession(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null){
				session = subject.getSession();
			}
			if (session != null){
				return session;
			}
//			subject.logout();
		}catch (InvalidSessionException e){

		}
		return null;
	}




	// 根据key，获取和当前用户的缓存
	public static Object getCache(String key) {
		Object obj = CacheUtils.get (USER_CACHE, key + CACHE_SPLIT + getUser ().getId ());
		return obj;
	}

	//设置当前用户的缓存
	public static void putCache(String key, Object value) {
		CacheUtils.put(USER_CACHE, key + CACHE_SPLIT +getUser ().getId (), value);
	}

	//清除当前用户的缓存
	public static void removeCache(String key) {
		CacheUtils.remove (USER_CACHE, key + CACHE_SPLIT +getUser ().getId ());
	}



	/**
	 * 导出Excel调用,根据姓名转换为ID
	 */
	public static User getByUserName(String name){
		User u = new User ();
		u.setName(name);
		List<User> list = SpringContextHolder.getBean(UserMapper.class).findList(u);
		if(list.size()>0){
			return list.get(0);
		}else{
			return new User ();
		}
	}
	/**
	 * 导出Excel使用，根据名字转换为id
	 */
	public static Office getByOfficeName(String name){
		Office o = new Office ();
		o.setName(name);
		List<Office> list = SpringContextHolder.getBean(OfficeMapper.class).findList(o);
		if(list.size()>0){
			return list.get(0);
		}else{
			return new Office ();
		}
	}
	/**
	 * 导出Excel使用，根据名字转换为id
	 */
	public static Area getByAreaName(String name){
		Area a = new Area ();
		a.setName(name);
		List<Area> list = SpringContextHolder.getBean(AreaMapper.class).findList(a);
		if(list.size()>0){
			return list.get(0);
		}else{
			return new Area ();
		}
	}


	public static boolean hasPermission(String permission){
		return SecurityUtils.getSubject().isPermitted(permission);
	}

}
