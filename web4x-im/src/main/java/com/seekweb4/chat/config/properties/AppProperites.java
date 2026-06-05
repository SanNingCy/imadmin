package com.seekweb4.chat.config.properties;

import com.seekweb4.chat.common.utils.SpringContextHolder;
import com.seekweb4.chat.common.utils.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.Serializable;

/**
 * 全局配置类
 *
 * @author lixinapp
 * @version 2017-06-25
 */
@Data
@Configuration
public class AppProperites implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(AppProperites.class);

    @Autowired
    private Environment environment;

    @Value("${demoMode}")
    private String demoMode;

    @Value("${userfiles.basedir}")
    private String userfilesBasedir;

    @Value("${jwt.accessToken.expireTime}")
    public long EXPIRE_TIME;

    @Value("${jwt.appToken.expireTime}")
    public long APP_EXPIRE_TIME;

    /** 供 MySQLBackupUtil 等使用；从 Environment 解析，兼容若依 druid.master 与 IM 扁平配置 */
    public String jdbcUrl;

    public String jdbcUsername;

    public String jdbcPassword;

    @PostConstruct
    void resolveJdbcFromEnvironment() {
        jdbcUrl = firstProperty(
                "spring.datasource.url",
                "spring.datasource.druid.master.url");
        jdbcUsername = firstProperty(
                "spring.datasource.username",
                "spring.datasource.druid.master.username");
        jdbcPassword = firstProperty(
                "spring.datasource.password",
                "spring.datasource.druid.master.password");
        if (StringUtils.isBlank(jdbcUrl)) {
            logger.warn("未解析到 JDBC URL，请检查 spring.datasource 或 spring.datasource.druid.master 配置");
        }
    }

    private String firstProperty(String... keys) {
        for (String key : keys) {
            String value = environment.getProperty(key);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return "";
    }

    @Value("${filePath}")
    public String filePath;

    @Value("${config.accessory.type}")
    public String uploadType;

    @Value("${plugin.Page}")
    public String Page;
    @Value("${plugin.Area}")
    public String Area;
    @Value("${plugin.Office}")
    public String Office;
    @Value("${plugin.User}")
    public String User;
    @Value("${plugin.UserUtils}")
    public String UserUtils;
    /**
     * 上传文件基础虚拟路径
     */
    public static final String USERFILES_BASE_URL = "/userfiles/";

    /**
     * 是/否
     */
    public static final String YES = "1";
    public static final String NO = "0";

    /**
     * 对/错
     */
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    /**
     * 显示/隐藏
     */
    public static final String SHOW = "1";
    public static final String HIDE = "0";

    public static AppProperites newInstance() {
        return SpringContextHolder.getBean(AppProperites.class);
    }

    /**
     * 获取上传文件的根目录
     *
     * @return
     */
    public String getUserfilesBaseDir() {
        String dir = this.userfilesBasedir;
        if (StringUtils.isBlank(dir)) {
            try {
                //return new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getParentFile().getCanonicalPath();
                return new File("/").getCanonicalPath();
            } catch (Exception e) {
                return "";
            }
        }
        if (!dir.endsWith("/")) {
            dir += "/";
        }

        return dir;
    }

    /**
     * 是否是演示模式，演示模式下不能修改用户、角色、密码、菜单、授权
     */
    public Boolean isDemoMode() {
        String dm = this.demoMode;
        return "true".equals(dm) || "1".equals(dm);
    }

    /**
     * 获取文件路径
     * @return 文件路径
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * 获取过期时间
     * @return 过期时间
     */
    public long getEXPIRE_TIME() {
        return this.EXPIRE_TIME;
    }

    /**
     * 获取应用过期时间
     * @return 应用过期时间
     */
    public long getAPP_EXPIRE_TIME() {
        return this.APP_EXPIRE_TIME;
    }

    /**
     * 获取用户模块
     * @return 用户模块
     */
    public String getUser() {
        return this.User;
    }

    /**
     * 获取办公模块
     * @return 办公模块
     */
    public String getOffice() {
        return this.Office;
    }

    /**
     * 获取区域模块
     * @return 区域模块
     */
    public String getArea() {
        return this.Area;
    }

}
