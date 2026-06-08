package com.web4x.web.controller.system;

import java.util.Date;
import java.util.List;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.web4x.common.config.Web4xConfig;
import com.web4x.common.condition.ImShiroConditionSupport;
import com.web4x.common.constant.ShiroConstants;
import com.web4x.common.core.controller.BaseController;
import com.web4x.common.core.domain.AjaxResult;
import com.web4x.common.core.domain.entity.SysMenu;
import com.web4x.common.core.domain.entity.SysUser;
import com.web4x.common.core.text.Convert;
import com.web4x.common.utils.CookieUtils;
import com.web4x.common.utils.DateUtils;
import com.web4x.common.utils.ServletUtils;
import com.web4x.common.utils.StringUtils;
import com.web4x.framework.shiro.service.SysPasswordService;
import com.web4x.integration.im.ImSysUserPrincipalBridge;
import com.web4x.integration.im.SysMenuViewSupport;
import com.web4x.system.service.ISysConfigService;
import com.web4x.system.service.ISysMenuService;

/**
 * 首页 业务处理
 * 
 * @author web4x
 */
@Controller
public class SysIndexController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired(required = false)
    private ImSysUserPrincipalBridge imUserBridge;

    @Autowired
    private Environment environment;

    @GetMapping("/index")
    public String index(ModelMap mmap, HttpServletRequest request)
    {
        SysUser user = getSysUser();
        if (user == null)
        {
            return "redirect:/login";
        }
        List<SysMenu> menus = SysMenuViewSupport.normalizeForSidebar(menuService.selectMenusByUser(user));
        mmap.put("menus", menus);
        mmap.put("user", user);
        mmap.put("sideTheme", configService.selectConfigByKey("sys.index.sideTheme"));
        mmap.put("skinName", configService.selectConfigByKey("sys.index.skinName"));
        Boolean footer = Convert.toBool(configService.selectConfigByKey("sys.index.footer"), true);
        Boolean tagsView = Convert.toBool(configService.selectConfigByKey("sys.index.tagsView"), true);
        mmap.put("footer", footer);
        mmap.put("tagsView", tagsView);
        mmap.put("mainClass", contentMainClass(footer, tagsView));
        mmap.put("copyrightYear", Web4xConfig.getCopyrightYear());
        mmap.put("demoEnabled", Web4xConfig.isDemoEnabled());
        boolean skipPwdPrompt = shouldSkipPasswordPrompt();
        mmap.put("isDefaultModifyPwd", skipPwdPrompt ? false : initPasswordIsModify(user.getPwdUpdateDate()));
        mmap.put("isPasswordExpired", skipPwdPrompt ? false : passwordIsExpiration(user.getPwdUpdateDate()));
        mmap.put("isMobile", ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")));

        // 菜单导航显示风格
        String menuStyle = configService.selectConfigByKey("sys.index.menuStyle");
        // 移动端，默认使左侧导航菜单，否则取默认配置
        String indexStyle = ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")) ? "index" : menuStyle;

        // 优先Cookie配置导航菜单
        Cookie[] cookies = ServletUtils.getRequest().getCookies();
        for (Cookie cookie : cookies)
        {
            if (StringUtils.isNotEmpty(cookie.getName()) && "nav-style".equalsIgnoreCase(cookie.getName()))
            {
                indexStyle = cookie.getValue();
                break;
            }
        }
        String webIndex = "topnav".equalsIgnoreCase(indexStyle) ? "index-topnav" : "index";
        // CSRF Token
        request.getSession().setAttribute(ShiroConstants.CSRF_TOKEN, ServletUtils.generateToken());
        return webIndex;
    }

    // 锁定屏幕
    @GetMapping("/lockscreen")
    public String lockscreen(ModelMap mmap)
    {
        mmap.put("user", getSysUser());
        ServletUtils.getSession().setAttribute(ShiroConstants.LOCK_SCREEN, true);
        return "lock";
    }

    // 解锁屏幕
    @PostMapping("/unlockscreen")
    @ResponseBody
    public AjaxResult unlockscreen(String password)
    {
        SysUser user = getSysUser();
        if (StringUtils.isNull(user))
        {
            return AjaxResult.error("服务器超时，请重新登录");
        }
        boolean ok = imUserBridge != null
                && imUserBridge.validatePassword(user.getLoginName(), password);
        if (!ok)
        {
            ok = passwordService.matches(user, password);
        }
        if (ok)
        {
            ServletUtils.getSession().removeAttribute(ShiroConstants.LOCK_SCREEN);
            return AjaxResult.success();
        }
        return AjaxResult.error("密码不正确，请重新输入。");
    }

    // 切换主题
    @GetMapping("/system/switchSkin")
    public String switchSkin()
    {
        return "skin";
    }

    // 切换菜单
    @GetMapping("/system/menuStyle/{style}")
    public void menuStyle(@PathVariable String style, HttpServletResponse response)
    {
        CookieUtils.setCookie(response, "nav-style", style);
    }

    // 系统介绍
    @GetMapping("/system/main")
    public String main(ModelMap mmap)
    {
        mmap.put("version", Web4xConfig.getVersion());
        return "main";
    }

    @GetMapping({"/dictionary", "/system/dict"})
    public String dictionary()
    {
        return "redirect:/system/dict/data";
    }

    // content-main class
    public String contentMainClass(Boolean footer, Boolean tagsView)
    {
        if (!footer && !tagsView)
        {
            return "tagsview-footer-hide";
        }
        else if (!footer)
        {
            return "footer-hide";
        }
        else if (!tagsView)
        {
            return "tagsview-hide";
        }
        return StringUtils.EMPTY;
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    /**
     * web4x-admin 集成 IM 时，若依账号仅用于后台登录鉴权，不弹初始/过期改密提示。
     */
    private boolean shouldSkipPasswordPrompt()
    {
        if (imUserBridge != null)
        {
            return true;
        }
        return ImShiroConditionSupport.resolveProperty(environment, ImShiroConditionSupport.PROPERTY) != null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            if (StringUtils.isNull(pwdUpdateDate))
            {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
