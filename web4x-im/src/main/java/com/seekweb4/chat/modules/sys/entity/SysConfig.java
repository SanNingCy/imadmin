package com.seekweb4.chat.modules.sys.entity;


import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import org.apache.ibatis.type.Alias;

/**
 * 系统配置Entity
 */
@Alias("ImSysConfig")
public class SysConfig extends DataEntity<SysConfig> {
	
	private static final long serialVersionUID = 1L;
	/*
		邮箱配置信息
	 */
	private String smtp;		// 邮箱服务器地址
	private String port;		// 邮箱服务器端口
	private String mailName;		// 系统邮箱地址
	private String mailPassword;		// 系统邮箱密码
	/*
		阿里大鱼配置信息
	 */
	private String accessKeyId;// 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找);
	private String accessKeySecret; // 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
	private String signature; //必填:短信签名-可在短信控制台中找到
	private String templateCode;//必填:短信模板-可在短信控制台中找到-->
	/*
	   外观配置
	 */
	private String defaultTheme;//默认主题
	private String defaultLayout;
	private String productName;//产品名称
	private String logo;//产品logo;


	/**
	 * 首页配置
	 */
	private String homeUrl;

	/*
	  登录配置
	 */
	private String multiAccountLogin;//允许多登录1，不允许0
	private String singleLoginType; //后登陆踢出先登录1，已经登陆禁止再登陆2.
	
	public SysConfig() {
		super();
	}

	public SysConfig(String id){
		super(id);
	}

	@ExcelField(title="邮箱服务器地址", align=2, sort=1)
	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}
	
	@ExcelField(title="邮箱服务器端口", align=2, sort=2)
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	@ExcelField(title="系统邮箱地址", align=2, sort=3)
	public String getMailName() {
		return mailName;
	}

	public void setMailName(String mailName) {
		this.mailName = mailName;
	}
	
	@ExcelField(title="系统邮箱密码", align=2, sort=4)
	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}
	
	@ExcelField(title="accessKeyId", align=2, sort=5)
	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}
	
	@ExcelField(title="accessKeySecret", align=2, sort=6)
	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}
	
	@ExcelField(title="短信签名", align=2, sort=7)
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	@ExcelField(title="短信模板", align=2, sort=8)
	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	
	@ExcelField(title="默认主题", align=2, sort=9)
	public String getDefaultTheme() {
		return defaultTheme;
	}

	public void setDefaultTheme(String defaultTheme) {
		this.defaultTheme = defaultTheme;
	}
	
	@ExcelField(title="产品名称", align=2, sort=10)
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	@ExcelField(title="logo", align=2, sort=11)
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	@ExcelField(title="允许多登录", align=2, sort=12)
	public String getMultiAccountLogin() {
		return multiAccountLogin;
	}

	public void setMultiAccountLogin(String multiAccountLogin) {
		this.multiAccountLogin = multiAccountLogin;
	}
	
	@ExcelField(title="单一登录方式", align=2, sort=13)
	public String getSingleLoginType() {
		return singleLoginType;
	}

	public void setSingleLoginType(String singleLoginType) {
		this.singleLoginType = singleLoginType;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public String getDefaultLayout() {
		return defaultLayout;
	}

	public void setDefaultLayout(String defaultLayout) {
		this.defaultLayout = defaultLayout;
	}
}