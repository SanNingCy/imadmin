package com.seekweb4.chat.modules.sys.security.shiro;

import org.apache.shiro.authc.AuthenticationToken;

import com.seekweb4.chat.modules.sys.security.util.JWTUtil;

public class JWTToken implements AuthenticationToken {
	
    // 密钥
    private String token;
    private String loginType;
    
    public JWTToken(String token) {
        this.token = token;
        this.loginType = JWTUtil.TYPE_SYSTEM;
    }
    
    public JWTToken(String token, String loginType) {
    	this.token = token;
    	this.loginType = loginType;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
    
}
