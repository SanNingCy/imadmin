package com.seekweb4.chat.modules.sys.web;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.auth0.jwt.JWT;
import com.google.common.collect.Maps;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.CacheUtils;
import com.seekweb4.chat.common.utils.IdGen;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.core.web.GlobalErrorController;
import com.seekweb4.chat.modules.sys.entity.User;
import com.seekweb4.chat.modules.sys.entity.LogType;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import com.seekweb4.chat.modules.sys.service.UserService;
import com.seekweb4.chat.modules.sys.utils.IpUtils2;
import com.seekweb4.chat.modules.sys.utils.LogUtils;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import com.seekweb4.chat.common.utils.CacheUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录Controller
 *
 * @author lixinapp
 * @version 2016-5-31
 */
@RestController
@Api(tags = "登录管理")
public class LoginController extends BaseController {
    @Autowired
    private StringRedisUtils redisUtils;

    private static final String CODE_CACHE = "SYSUSER_CODE:";
    @PostMapping(value = "/sys/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("登录接口")
    //  public AjaxJson login(@RequestBody Map<String, String> requestMap, HttpServletRequest request/*, @RequestParam("code") String code, @RequestParam("uuid") String uuid*/) {
    //     String userName = requestMap.get("userName");
    //     String password = requestMap.get("password");
    public AjaxJson login(
            @RequestBody(required = false) Map<String, String> requestMap,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String password,
            HttpServletRequest request/*, @RequestParam("code") String code, @RequestParam("uuid") String uuid*/) {
        // 优先从请求体获取，如果没有则从URL参数获取
        if (requestMap != null) {
            if (StringUtils.isBlank(userName)) {
                userName = requestMap.get("userName");
            }
            if (StringUtils.isBlank(password)) {
                password = requestMap.get("password");
            }
        }

        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            LogUtils.saveLog(request, "后台登录失败-用户名或密码为空", LogType.LOGIN.getType());
            return AjaxJson.error("用户名或密码不能为空");
        }

        AjaxJson j = new AjaxJson();
        /*if(!code.equals(redisUtils.get(CODE_CACHE + uuid))){
            j.setSuccess(false);
            j.setMsg("验证码错误");
            return j;
        }*/
        System.out.println("登录ip："+ IpUtils2.getIpAddr(request));
        User user = UserUtils.getByLoginName(userName);
        if (user != null && UserService.validatePassword(password, user.getPassword())) {
            if (AppProperites.NO.equals(user.getLoginFlag())){
                LogUtils.saveLog(request, "后台登录失败-账号被禁用", LogType.LOGIN.getType());
                j.setSuccess(false);
                j.setMsg("该用户已经被禁止登陆!");
                j.setCode(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.value());
            }else {
                // 登录成功后清除菜单缓存，确保获取最新的菜单数据
                String cacheKeyMenuList = UserUtils.CACHE_MENU_LIST + UserUtils.CACHE_SPLIT + user.getId();
                String cacheKeyTopMenu = UserUtils.CACHE_TOP_MENU + UserUtils.CACHE_SPLIT + user.getId();
                CacheUtils.remove(UserUtils.USER_CACHE, cacheKeyMenuList);
                CacheUtils.remove(UserUtils.USER_CACHE, cacheKeyTopMenu);
                
                j.setSuccess(true);
                j.put(JWTUtil.TOKEN, JWTUtil.createAccessToken(userName, user.getPassword()));
                j.put(JWTUtil.REFRESH_TOKEN, JWTUtil.createRefreshToken(userName, user.getPassword()));
                LogUtils.saveLog(request, "后台登录成功", LogType.LOGIN.getType());
            }
        } else {
            LogUtils.saveLog(request, "后台登录失败-用户名或密码错误", LogType.LOGIN.getType());
            j.setSuccess(false);
            j.setMsg("用户名或者密码错误!");
            // 认证失败统一返回 401
            j.setCode(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return j;
    }

    /**
     * cas登录
     * vue 传递ticket参数验证，并返回token
     */
    /*@RequestMapping("/sys/casLogin")
    public AjaxJson casLogin(@RequestParam(name="ticket") String ticket,
                             @RequestParam(name="service") String service, @Value ("${cas.server-url-prefix}")String casServer) throws Exception  {
        AjaxJson j = new AjaxJson ();
        //ticket检验器
        TicketValidator ticketValidator = new Cas20ServiceTicketValidator (casServer);
        try {
            // 去CAS服务端中验证ticket的合法性
            Assertion casAssertion = ticketValidator.validate(ticket, service);
            // 从CAS服务端中获取相关属性,包括用户名、是否设置RememberMe等
            AttributePrincipal casPrincipal = casAssertion.getPrincipal();
            String loginName = casPrincipal.getName();
            // 校验用户名密码
            User user = UserUtils.getByLoginName (loginName);
            if (user != null) {
                if (AppProperites.NO.equals(user.getLoginFlag())){
                    throw new AuthenticationException ("msg:该已帐号禁止登录.");
                }

                j.put (JWTUtil.TOKEN, JWTUtil.createAccessToken (user.getLoginName (), user.getPassword ()));
                j.put (JWTUtil.REFRESH_TOKEN, JWTUtil.createRefreshToken (user.getLoginName (), user.getPassword ()));
                return j;


            } else {
                AuthenticationException e =  new AuthenticationException ("用户【"+loginName+"】不存在!");
                logger.error ("用户【loginName:"+loginName+"】不存在!", e);
                throw e;
            }
        } catch (TicketValidationException e) {
            logger.error ("Unable to validate ticket [" + ticket + "]", e);
            throw new AuthenticationException ("未通过验证的ticket [" + ticket + "]", e);
        }

    }*/

    @GetMapping("/sys/refreshToken")
    @ApiOperation("刷新token")
    public AjaxJson accessTokenRefresh(String refreshToken, HttpServletRequest request, HttpServletResponse response){

        if (JWTUtil.verify(refreshToken) == 1) {
            GlobalErrorController.response4022(request, response);

        }else if (JWTUtil.verify(refreshToken) == 2) {
            return AjaxJson.error("用户名密码错误");
        }

        String loginName = JWTUtil.getLoginName(refreshToken);
        String password = UserUtils.getByLoginName(loginName).getPassword();
        //创建新的accessToken
        String accessToken = JWTUtil.createAccessToken(loginName, password);

        //下面判断是否刷新 REFRESH_TOKEN，如果refreshToken 快过期了 需要重新生成一个替换掉
        long minTimeOfRefreshToken = 2* AppProperites.newInstance().getEXPIRE_TIME();//REFRESH_TOKEN 有效时长是应该为accessToken有效时长的2倍
        Long refreshTokenExpirationTime = JWT.decode(refreshToken).getExpiresAt().getTime();//refreshToken创建的起始时间点
        //(refreshToken过期时间- 当前时间点) 表示refreshToken还剩余的有效时长，如果小于2倍accessToken时长 ，则刷新 REFRESH_TOKEN
        if(refreshTokenExpirationTime - System.currentTimeMillis() <= minTimeOfRefreshToken){
            //刷新refreshToken
            refreshToken = JWTUtil.createRefreshToken(loginName, password);
        }

        return AjaxJson.success().put(JWTUtil.TOKEN, accessToken).put(JWTUtil.REFRESH_TOKEN, refreshToken);
    }

    /**
     * 退出登录
     * @throws IOException
     */
    @ApiOperation("用户退出")
    @GetMapping("/sys/logout")
    public AjaxJson logout() {
        AjaxJson j = new AjaxJson();
        String token = UserUtils.getToken();
        if (StringUtils.isNotBlank(token)) {
            UserUtils.clearCache();
            UserUtils.getSubject().logout();
        }
        j.setMsg("退出成功");
        return j;
    }


    /**
     * 是否是验证码登录
     *
     * @param useruame 用户名
     * @param isFail   计数加1
     * @param clean    计数清零
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {
        Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
        if (loginFailMap == null) {
            loginFailMap = Maps.newHashMap();
            CacheUtils.put("loginFailMap", loginFailMap);
        }
        Integer loginFailNum = loginFailMap.get(useruame);
        if (loginFailNum == null) {
            loginFailNum = 0;
        }
        if (isFail) {
            loginFailNum++;
            loginFailMap.put(useruame, loginFailNum);
        }
        if (clean) {
            loginFailMap.remove(useruame);
        }
        return loginFailNum >= 3;
    }
    /**
     * 获取登陆验证码
     * @throws
     */
    @GetMapping("/sys/getCode")
    public AjaxJson getCode(){
        //HuTool定义图形验证码的长和宽,验证码的位数，干扰线的条数
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(116, 36,4,50);
        //将验证码放入redis
        String uuid = IdGen.uuid();
        redisUtils.setEx(CODE_CACHE + uuid, lineCaptcha.getCode(), 10, TimeUnit.MINUTES);
        return AjaxJson.success().put("uuid", uuid).put("captcha", lineCaptcha.getImageBase64Data());
    }
    /*@GetMapping("/sys/getCode")
    public void getCode(HttpServletResponse response, HttpSession session){
        //HuTool定义图形验证码的长和宽,验证码的位数，干扰线的条数
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(116, 36,4,50);
        //将验证码放入session
        session.setAttribute("code",lineCaptcha.getCode());
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            lineCaptcha.write(outputStream);
            lineCaptcha.getImageBase64();
            outputStream.close();
        } catch (IOException e) {
            logger.error ("{}", e );
        }
    }*/
}
