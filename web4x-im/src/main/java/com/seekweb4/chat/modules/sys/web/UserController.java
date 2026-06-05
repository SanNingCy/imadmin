package com.seekweb4.chat.modules.sys.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.repository.AccessoryRepository;
import com.seekweb4.chat.config.properties.FileProperties;
import com.seekweb4.chat.modules.sys.entity.*;
import com.seekweb4.chat.modules.sys.entity.DictValue;
import com.seekweb4.chat.modules.sys.entity.Menu;
import com.seekweb4.chat.modules.sys.entity.Role;
import com.seekweb4.chat.modules.sys.entity.User;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import com.seekweb4.chat.modules.sys.service.RoleService;
import com.seekweb4.chat.modules.sys.service.UserService;
import com.seekweb4.chat.modules.sys.utils.*;
import com.seekweb4.chat.modules.sys.utils.DictUtils;
import com.seekweb4.chat.modules.sys.utils.MenuUtils;
import com.seekweb4.chat.modules.sys.utils.RouterUtils;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seekweb4.chat.common.beanvalidator.BeanValidators;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.common.utils.excel.ExportExcel;
import com.seekweb4.chat.common.utils.excel.ImportExcel;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.ServiceException;
import com.seekweb4.chat.core.web.BaseController;

/**
 * 用户Controller
 *
 * @author lixinapp
 * @version 2016-8-29
 */
@RestController
@RequestMapping("/sys/user")
@Api(tags ="用户管理")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private FileProperties fileProperties;
    @Autowired
    private AccessoryRepository accessoryRepository;
    /*@Autowired
    private AliyunOSSRepository aliyunOSSRepository;*/
    @ModelAttribute
    public User get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return userService.get(id);
        } else {
            return new User();
        }
    }
    @ApiLog("查询用户")
    @GetMapping("queryById")
    @ApiOperation(value = "查询用户")
    public AjaxJson queryById(User user) {
        return AjaxJson.success().put("user", user);
    }

    @ApiLog("查询用户列表")
    @RequiresPermissions("system:user:list")
//    @RequiresPermissions("sys:user:list")
    @GetMapping("list")
    public AjaxJson list(User user, HttpServletRequest request, HttpServletResponse response) {
    	//user.setDataScope("AND a.id NOT IN (SELECT sur.user_id FROM sys_user_role sur WHERE sur.role_id IN ('1','2','3','4','5','6','7'))");
        // 清理查询参数：将空字符串转换为 null，避免影响查询
        normalizeQueryParams(user);
        Page<User> page = userService.findPage(new Page<User>(request, response), user);
        return AjaxJson.success().put("page", page);
    }

    @ApiLog("保存用户")
    @RequiresPermissions(value = {"system:user:add", "system:user:edit"}, logical = Logical.OR)
    @PostMapping("save")
    @ApiOperation(value = "保存用户")
    public AjaxJson save(User user) {
        if (appProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作!");
        }
        // 如果新密码为空，则不更换密码
        if (StringUtils.isNotBlank(user.getNewPassword())) {
            user.setPassword(userService.entryptPassword(user.getNewPassword()));
        }
        /**
         * 后台hibernate-validation插件校验
         */
        String errMsg = beanValidator(user);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }
//        if (!isCheckLoginName(user.getOldLoginName(), user.getLoginName())) {
//            return AjaxJson.error("保存用户'" + user.getLoginName() + "'失败，登录名已存在!");
//        }
        // 角色数据有效性验证，过滤不在授权内的角色
        List<Role> roleList = Lists.newArrayList();
        List<String> roleIdList = user.getRoleIdList();
        if (roleIdList != null) {
            for (String roleId : roleIdList) {
                roleList.add(roleService.get(roleId));
            }
        }
        user.setRoleList(roleList);

        // 保存用户信息（捕获业务异常并返回标准AjaxJson）
        try {
            userService.saveUser(user);
        } catch (ServiceException e) {
            return AjaxJson.error(e.getMessage());
        }
        // 清除当前用户缓存
        if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
            UserUtils.clearCache();
        }
        return AjaxJson.success("保存用户'" + user.getLoginName() + "'成功!");
    }

    /**
     * 批量删除用户
     */
    @ApiLog("删除用户")
    @ApiOperation(value = "删除用户")
    @RequiresPermissions("system:user:delete")
    @DeleteMapping("delete")
    public AjaxJson delete(String ids) {
        String idArray[] = ids.split(",");
        AjaxJson j = new AjaxJson();
        if (appProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作!");
        }
        StringBuffer msg = new StringBuffer();
        boolean success = true;
        for (String id : idArray) {
            User user = userService.get(id);
            if (UserUtils.getUser().getId().equals(user.getId())) {
                success = false;
                msg.append("["+user.getLoginName()+"]删除失败，不允许删除当前用户!<br/>");
            } else if (User.isAdmin(user.getId())) {
                success = false;
                msg.append("["+user.getLoginName()+"]删除失败，不允许删除超级管理员!<br/>");//删除用户失败, 不允许删除超级管理员用户
            } else {
                msg.append("["+user.getLoginName()+"]删除成功!<br/>");
                userService.deleteUser(user);//删除用户成功
            }
        }
        if(success){
            return AjaxJson.success(msg.toString());
        }else {
            return AjaxJson.error(msg.toString());
        }
    }

    /**
     * 导出用户数据
     *
     * @param user
     * @param request
     * @param response
     * @return
     */
    @ApiLog("导出用户")
    @RequiresPermissions("sys:user:export")
    @GetMapping("export")
    @ApiOperation(value = "导出用户excel")
    public void exportFile(User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = "用户数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
        Page<User> page = userService.findPage(new Page<User>(request, response, -1), user);
        new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
    }

    /**
     * 导入用户数据
     *
     * @param file
     * @param redirectAttributes
     * @return
     */
    @ApiLog("导入用户")
    @RequiresPermissions("sys:user:import")
    @PostMapping("import")
    @ApiOperation(value = "导入用户excel")
    public AjaxJson importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
        if (appProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作！");
        }
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<User> list = ei.getDataList(User.class);
            for (User user : list) {
                try {
                    if (isCheckLoginName("", user.getLoginName())) {
                        user.setPassword(userService.entryptPassword("123456"));
                        BeanValidators.validateWithException(validator, user);
                        userService.saveUser(user);
                        successNum++;
                    } else {
                        failureMsg.append("<br/>登录名 " + user.getLoginName() + " 已存在; ");
                        failureNum++;
                    }
                } catch (ConstraintViolationException ex) {
                    failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：");
                    List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
                    for (String message : messageList) {
                        failureMsg.append(message + "; ");
                        failureNum++;
                    }
                } catch (Exception ex) {
                    failureNum++;
                    failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：" + ex.getMessage());
                }
            }
            if (failureNum > 0) {
                failureMsg.insert(0, "，失败 " + failureNum + " 条用户，导入信息如下：");
            }
            return AjaxJson.success("已成功导入 " + successNum + " 条用户" + failureMsg);
        } catch (Exception e) {
            return AjaxJson.error("导入用户失败！失败信息：" + e.getMessage());
        }
    }

    /**
     * 下载导入用户数据模板
     *
     * @param response
     * @return
     */
    @ApiLog("下载用户模板")
    @RequiresPermissions("sys:user:import")
    @GetMapping("import/template")
    @ApiOperation(value = "下载模板")
    public AjaxJson importFileTemplate(HttpServletResponse response) {
        try {
            String fileName = "用户数据导入模板.xlsx";
            List<User> list = Lists.newArrayList();
            list.add(UserUtils.getUser());
            new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            return AjaxJson.error("导入模板下载失败！失败信息：" + e.getMessage());
        }
    }



    private boolean isCheckLoginName(String oldLoginName, String loginName) {
        if (loginName != null && loginName.equals(oldLoginName)) {
           return true;
        } else if (loginName != null && userService.getUserByLoginName(loginName) == null) {
            return true;
        }
      return false;
    }


    /**
     * 用户信息显示编辑保存
     *
     * @param user
     * @return
     */
    @ApiLog("修改个人资料")
    @RequiresPermissions("user")
    @PostMapping("saveInfo")
    @ApiOperation(value = "修改个人资料")
    public AjaxJson infoEdit(User user) {
        User currentUser = UserUtils.getUser();
        if (appProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作！");
        }
        if (user.getName() != null)
            currentUser.setName(user.getName());
        if (user.getEmail() != null)
            currentUser.setEmail(user.getEmail());
        if (user.getPhone() != null)
            currentUser.setPhone(user.getPhone());
        if (user.getMobile() != null)
            currentUser.setMobile(user.getMobile());
        if (user.getRemarks() != null)
            currentUser.setRemarks(user.getRemarks());
        if (user.getSign() != null)
            currentUser.setSign(user.getSign());
        userService.updateUserInfo(currentUser);
        return AjaxJson.success("修改个人资料成功!");
    }


    /**
     * 用户头像显示编辑保存
     *
     * @return
     * @throws IOException
     * @throws IllegalStateException
     */
    @ApiLog("上传用户头像")
    @RequiresPermissions("user")
    @PostMapping("imageUpload")
    @ApiOperation(value = "上传头像")
    public AjaxJson imageUpload(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws IllegalStateException, IOException {
        // 个人中心：始终更新当前登录用户头像
        User currentUser = UserUtils.getUser();
        // 判断文件是否为空
        if (!file.isEmpty()) {
            if(fileProperties.isImage(file.getOriginalFilename())){
                String name = System.currentTimeMillis() + "." + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
                //String realPath = FileKit.getAttachmentDir() + "sys/user/images/";
                String url = accessoryRepository.save(file, UserUtils.getUser().getId(), name);;
                currentUser.setPhoto(url);
                userService.updateUserInfo(currentUser);
                return AjaxJson.success("上传成功!").put("path", currentUser.getPhoto());
            }else{
                return AjaxJson.error ("请上传图片!");
            }

        }else{
            return AjaxJson.error ("文件不存在!");
        }


    }

    /**
     * 后台用户管理：为指定用户上传头像（支持新增和编辑场景）
     *
     * @param userId 目标用户ID（可选，新增时可不传，编辑时必须传）
     * @param file   头像文件
     */
    @ApiLog("为指定用户上传头像")
    @PostMapping("imageUploadById")
    @ApiOperation(value = "为指定用户上传头像")
    public AjaxJson imageUploadById(@RequestParam(required = false) String userId, MultipartFile file) throws IllegalStateException, IOException {
        if (file == null || file.isEmpty()) {
            return AjaxJson.error("文件不存在!");
        }
        if (!fileProperties.isImage(file.getOriginalFilename())) {
            return AjaxJson.error("请上传图片!");
        }

        String name = System.currentTimeMillis() + "." + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        
        // 如果传了userId，说明是编辑场景，需要更新用户头像
        if (StringUtils.isNotBlank(userId)) {
            User targetUser = userService.get(userId);
            if (targetUser == null) {
                return AjaxJson.error("用户不存在!");
            }
            // 使用userId作为文件存储路径的一部分
            String url = accessoryRepository.save(file, userId, name);
            targetUser.setPhoto(url);
            userService.updateUserInfo(targetUser);
            return AjaxJson.success("上传成功!").put("path", targetUser.getPhoto());
        } else {
            // 新增场景：userId为空，只上传文件并返回URL，不更新任何用户
            // 使用当前登录用户的ID作为临时存储路径（或者可以用一个临时目录）
            String tempUserId = UserUtils.getUser().getId();
            String url = accessoryRepository.save(file, tempUserId, name);
            // 返回头像URL，前端在保存用户时把这个URL一起提交
            return AjaxJson.success("上传成功!").put("path", url);
        }
    }

    /**
     * 返回用户信息
     *
     * @return
     */
    @RequiresPermissions("user")
    @GetMapping("info")
    @ApiOperation(value = "获取当前用户信息")
    public AjaxJson infoData() {
        User user = UserUtils.getUser();
        return AjaxJson.success("获取个人信息成功!").put("role", UserUtils.getRoleList()).put("user", user);
    }

    @ApiLog("修改用户密码")
    @RequiresPermissions("user")
    @PostMapping("savePwd")
    @ApiOperation(value = "修改密码")
    public AjaxJson savePwd(String oldPassword, String newPassword, Model model) {
        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
            if (appProperites.isDemoMode()) {
                return AjaxJson.error("演示模式，不允许操作！");
            }
            if (UserService.validatePassword(oldPassword, user.getPassword())) {
                userService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
                return AjaxJson.success("修改密码成功！");
            } else {
                return AjaxJson.error("修改密码失败，旧密码错误！");
            }
        }
        return AjaxJson.error("参数错误！");
    }


    /**
     * 保存签名
     */
    @ApiLog("保存用户签名")
    @ApiOperation(value = "用户签名")
    @PostMapping("saveSign")
    public AjaxJson saveSign(User user, boolean __ajax, HttpServletResponse response, Model model) throws Exception {
        AjaxJson j = new AjaxJson();
        User currentUser = UserUtils.getUser();
        currentUser.setSign(user.getSign());
        userService.updateUserInfo(currentUser);
        return AjaxJson.success("设置签名成功!");
    }

    @RequiresPermissions("user")
    @GetMapping("treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String officeId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<User> list = userService.findUserByOfficeId(officeId);
        for (int i = 0; i < list.size(); i++) {
            User e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", "u_" + e.getId());
            map.put("pId", officeId);
            map.put("name", StringUtils.replace(e.getName(), " ", ""));
            mapList.add(map);
        }
        return mapList;
    }


    /**
     * web端ajax验证手机号是否可以注册（数据库中不存在）
     */
    @ApiOperation("验证手机号")
    @GetMapping("validateMobile")
    public AjaxJson validateMobile(String mobile, HttpServletResponse response, Model model) {
        User user = userService.findUniqueByProperty("mobile", mobile);
        if (user == null) {
            return AjaxJson.success().put("noExist", true);
        } else {
            return AjaxJson.success().put("noExist", false);
        }
    }



    @ApiOperation(value = "用户权限")
    @RequiresPermissions("user")
    @GetMapping("getPermissions")
    public Set<String> getPermissions() {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        String token = UserUtils.getToken();
        String loginName = JWTUtil.getLoginName(token);
        User user = UserUtils.getByLoginName(loginName);
        if (user != null) {

            List<Menu> list = UserUtils.getMenuList();
            for (Menu menu : list) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(menu.getPermission())) {
                    // 添加基于Permission的权限信息
                    for (String permission : org.apache.commons.lang3.StringUtils.split(menu.getPermission(), ",")) {
                        info.addStringPermission(permission);
                    }
                }
            }
            // 添加用户权限
            info.addStringPermission("user");
            // 添加用户角色信息
            for (Role role : user.getRoleList()) {
                info.addRole(role.getEnname());
            }
        }
        return info.getStringPermissions();
    }


    @GetMapping("getMenus")
    @RequiresPermissions("user")
    public AjaxJson getMenus() {
        AjaxJson j = new AjaxJson();
        j.put("dictList", this.getDicts());
        j.put("permissions", this.getPermissions());
        j.put("menuList", MenuUtils.getMenus());
        j.put("routerList", RouterUtils.getRoutersByMenu());
        return j;
    }

    private Map<String, List<DictValue>> getDicts() {
        return DictUtils.getDictMap();
    }


}
