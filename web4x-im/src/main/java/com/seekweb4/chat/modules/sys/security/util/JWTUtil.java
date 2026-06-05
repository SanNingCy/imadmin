package com.seekweb4.chat.modules.sys.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.modules.sys.utils.UserUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JWTUtil {
    public static final String TOKEN = "token";
    public static final String APPTOKEN = "apptoken";
    public static final String TYPE_APP = "App";
	public static final String TYPE_SYSTEM = "System";

    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String SECRET = "lixinkeji";

    public static final String APPTOKEN_CACHE = "apptokencache:";

    /**
     * 校验token是否正确
     * @param token 密钥
     * @return 是否正确
     */
    public static int verify(String token) {
        try {
            String userName = JWTUtil.getLoginName(token);
            String password = UserUtils.getByLoginName(userName).getPassword();
            Algorithm algorithm = Algorithm.HMAC256(password);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", userName)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return 0;
        } catch (TokenExpiredException e){
            return 1;
        } catch (Exception exception) {
            return 2;
        }
    }
    
    /**
     * 校验APPtoken是否正确
     * @param token 密钥
     * @return 是否正确
     */
    /*public static int verifyApp(String token) {
    	try {
    		String uid = JWTUtil.getUid(token);
    		Algorithm algorithm = Algorithm.HMAC256(SECRET);
    		JWTVerifier verifier = JWT.require(algorithm)
    				.withClaim("uid", uid)
    				.build();
    		DecodedJWT jwt = verifier.verify(token);
    		return 0;
    	} catch (TokenExpiredException e){
    		return 1;
    	} catch (Exception exception) {
    		return 2;
    	}
    }*/

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getLoginName(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成签名
     * @param username 用户名
     * @param password 用户的密码
     * @return 加密的token
     */
    public static String createAccessToken(String username, String password) {
        Date date = new Date(System.currentTimeMillis() + AppProperites.newInstance().getEXPIRE_TIME());
        Algorithm algorithm = Algorithm.HMAC256(password);
        // 附带username信息
        return JWT.create()
                .withClaim("username", username)
                .withExpiresAt(date)
                .sign(algorithm);
    }
    
    /**
     * 生成APP签名
     * @param uid 用户ID
     * @return 加密的token
     */
    public static String createAppToken(String uid) {
    	// 附带username信息
    	String token = JWT.create().withClaim("uid", uid).sign(Algorithm.HMAC256(SECRET));
        StringRedisUtils.getInstance().setEx(APPTOKEN_CACHE + uid + ":" + token, token, AppProperites.newInstance().getAPP_EXPIRE_TIME(), TimeUnit.MILLISECONDS);
        return token;
    }

    /**
     * refresh TOKEN 刷新用
     * @param username 用户名
     * @param password 用户的密码
     * @return 加密的token
     */
    public static String createRefreshToken(String username, String password) {
        Date date = new Date(System.currentTimeMillis() + 3* AppProperites.newInstance().getEXPIRE_TIME());
        Algorithm algorithm = Algorithm.HMAC256(password);
        // 附带username信息
        return JWT.create()
                .withClaim("username", username)
                .withExpiresAt(date)
                .sign(algorithm);
    }
}
