package com.seekweb4.chat.modules.sys.entity;

import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.seekweb4.chat.config.swagger.IgnoreSwaggerParameter;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.Collections3;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import com.seekweb4.chat.common.utils.excel.fieldtype.RoleListType;
import com.seekweb4.chat.core.persistence.DataEntity;
/**
 * 用户Entity
 * @author lixinapp
 * @version 2016-12-05
 */
public class User extends DataEntity<User> {

	private static final long serialVersionUID = 1L;
	@IgnoreSwaggerParameter
	private Office company;	// 归属公司
	@IgnoreSwaggerParameter
	private Office office;	// 归属部门
	private String loginName;// 登录名
	private String password;// 密码
	private String no;		// 工号
	private String name;	// 姓名
	private String email;	// 邮箱
	private String phone;	// 电话
	private String mobile;	// 手机
	@IgnoreSwaggerParameter
	private String loginIp;	// 最后登陆IP
	@IgnoreSwaggerParameter
	private Date loginDate;	// 最后登陆日期
	private String loginFlag;	// 是否允许登陆
	private String photo;	// 头像
	private String qrCode;	//二维码
	private String oldLoginName;// 原登录名
	private String newPassword;	// 新密码
	private String sign;//签名
	@IgnoreSwaggerParameter
	private String oldLoginIp;	// 上次登陆IP
	@IgnoreSwaggerParameter
	private Date oldLoginDate;	// 上次登陆日期
	@IgnoreSwaggerParameter
	private Role role;	// 根据角色查询用户条件
	private Post post;
	private String im;

	/***
	 * 谷歌验证码
	 */
	private String twoFactorCode;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date twoFactorTime;

	/** 后台用户列表查询参数：已超过 N 天未登录(sys_user.login_date) */
	@JsonIgnore
	private Integer inactiveDays;


	public String getIm() {
		return im;
	}

	public void setIm(String im) {
		this.im = im;
	}

	@IgnoreSwaggerParameter
	private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表

	public User() {
		super();
	}

	public User(String id){
		super(id);
	}

	public User(String id, String loginName){
		super(id);
		this.loginName = loginName;
	}

	public User(Role role){
		super();
		this.role = role;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}

	public String getId() {
		return id;
	}

	//@NotNull(message="归属公司不能为空")
	@ExcelField(title="归属公司", align=2, sort=20)
	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}

	//@NotNull(message="归属部门不能为空")
	@ExcelField(title="归属部门", align=2, sort=25)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Length(min=1, max=100, message="登录名长度必须介于 1 和 100 之间")
	@ExcelField(title="登录名", align=2, sort=30)
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@JsonIgnore
	@Length(min=1, max=100, message="密码长度必须介于 1 和 100 之间")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Length(min=1, max=100, message="姓名长度必须介于 1 和 100 之间")
	@ExcelField(title="姓名", align=2, sort=40)
	public String getName() {
		return name;
	}

	//@Length(min=1, max=100, message="工号长度必须介于 1 和 100 之间")
	@ExcelField(title="工号", align=2, sort=45)
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setName(String name) {
		this.name = name;
	}

	//@Email(message="邮箱格式不正确")
	//@Length(min=0, max=200, message="邮箱长度必须介于 1 和 200 之间")
	@ExcelField(title="邮箱", align=1, sort=50)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	//@Length(min=0, max=200, message="电话长度必须介于 1 和 200 之间")
	@ExcelField(title="电话", align=2, sort=60)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	//@Length(min=0, max=200, message="手机长度必须介于 1 和 200 之间")
	@ExcelField(title="手机", align=2, sort=70)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@ExcelField(title="备注", align=1, sort=900)
	public String getRemarks() {
		return remarks;
	}

	@ExcelField(title="创建时间", type=0, align=1, sort=90)
	public Date getCreateDate() {
		return createDate;
	}

	@ExcelField(title="最后登录IP", type=0, align=1, sort=100)
	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="最后登录日期", type=0, align=1, sort=110)
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getOldLoginName() {
		return oldLoginName;
	}

	public void setOldLoginName(String oldLoginName) {
		this.oldLoginName = oldLoginName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldLoginIp() {
		if (oldLoginIp == null){
			return loginIp;
		}
		return oldLoginIp;
	}

	public void setOldLoginIp(String oldLoginIp) {
		this.oldLoginIp = oldLoginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOldLoginDate() {
		if (oldLoginDate == null){
			return loginDate;
		}
		return oldLoginDate;
	}

	public void setOldLoginDate(Date oldLoginDate) {
		this.oldLoginDate = oldLoginDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@JsonIgnore
	@ExcelField(title="拥有角色", align=1, sort=800, fieldType=RoleListType.class)
	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	public List<String> getRoleIdList() {
		List<String> roleIdList = Lists.newArrayList();
		for (Role role : roleList) {
			roleIdList.add(role.getId());
		}
		return roleIdList;
	}

	public void setRoleIdList(List<String> roleIdList) {
		roleList = Lists.newArrayList();
		for (String roleId : roleIdList) {
			Role role = new Role();
			role.setId(roleId);
			roleList.add(role);
		}
	}

	/**
	 * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
	 */
	public String getRoleNames() {
		return Collections3.extractToString(roleList, "name", ",");
	}

	public String getRoleIds() {
		return Collections3.extractToString(roleList, "id", ",");
	}


	public boolean isAdmin(){
		return isAdmin(this.id);
	}

	/**
	 * 是否为商家
	 * @return
	 */
	public boolean isShop(){
		return CollectionUtil.containsAny(getRoleIdList(), Lists.newArrayList("1", "2", "3", "4", "5", "6", "7"));
	}

	public static boolean isAdmin(String id){
		return id != null && "1".equals(id);
	}

	@Override
	public String toString() {
		return id;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getQrCode() {
		return qrCode;
	}

	/**
	 * @param sign the createAccessToken to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}

	/**
	 * @return the createAccessToken
	 */
	public String getSign() {
		return sign;
	}


	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public String getTwoFactorCode() {
		return twoFactorCode;
	}

	public void setTwoFactorCode(String twoFactorCode) {
		this.twoFactorCode = twoFactorCode;
	}

	public Date getTwoFactorTime() {
		return twoFactorTime;
	}

	public void setTwoFactorTime(Date twoFactorTime) {
		this.twoFactorTime = twoFactorTime;
	}

	@JsonIgnore
	public Integer getInactiveDays() {
		return inactiveDays;
	}

	public void setInactiveDays(Integer inactiveDays) {
		this.inactiveDays = inactiveDays;
	}
}
