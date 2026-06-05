package com.seekweb4.chat.modules.sys.web;

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.sys.entity.Role;
import com.seekweb4.chat.modules.sys.entity.User;
import com.seekweb4.chat.core.service.ServiceException;
import com.seekweb4.chat.modules.sys.service.MenuService;
import com.seekweb4.chat.modules.sys.service.RoleService;
import com.seekweb4.chat.modules.sys.service.UserService;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 角色Controller
 * @author lixinapp
 * @version 2016-12-05
 */
@RestController
@RequestMapping("/sys/role")
public class RoleController extends BaseController {

    /**
     * 超级管理员角色英文名（默认 admin）
     */
    private static final String ADMIN_ROLE_ENNAME = "admin";

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private MenuService menuService;

	@ModelAttribute("role")
	public Role get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return roleService.get(id);
		}else{
			return new Role();
		}
	}

	@ApiLog("查询角色列表")
	@RequiresPermissions("system:role:list")
//	@RequiresPermissions("sys:role:list")
	@GetMapping("list")
	public AjaxJson data(Role role, HttpServletRequest request, HttpServletResponse response) {
		Page<Role> page = roleService.findPage(new Page<Role>(request, response), role);
		return AjaxJson.success().put("page", page);
	}

//	@RequiresPermissions("sys:role:list")
	@RequiresPermissions("system:role:list")
	@GetMapping("roleList")
	public AjaxJson roleList(Role role, HttpServletRequest request, HttpServletResponse response) {
		//role.setDataScope(" AND a.id NOT IN ('1','2','3','4','5','6','7')");
		Page<Role> page = roleService.findPage(new Page<Role>(request, response), role);
		return AjaxJson.success().put("page", page);
	}

	@ApiLog("查询角色")
	@RequiresPermissions(value={"system:role:view","system:role:add","system:role:edit","system:role:auth"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(Role role) {

		String newDataRuleIds = "";
		if (role != null) {
			if (StringUtils.isNotBlank(role.getDataRuleIds())) {
				for (String id : role.getDataRuleIds().split(",")) {
					newDataRuleIds = newDataRuleIds + "dataRule-" + id + ",";
				}
			}
			if (newDataRuleIds.length() > 1) {
				role.setDataRuleIds(newDataRuleIds.substring(0, newDataRuleIds.length() - 1));
			}
			role.setMenuIdList(roleService.queryAllNotChildrenMenuId(role.getId()));
		}


		return AjaxJson.success().put("role", role);
	}

	@ApiLog("保存角色")
//	@RequiresPermissions(value={"sys:role:assign","sys:role:auth","sys:role:add","sys:role:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"sys:role:assign","sys:role:auth","sys:role:add","sys:role:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(@RequestBody Role role, Model model) {
		if(!UserUtils.getUser().isAdmin()&&role.getSysData().equals(AppProperites.YES)){
			return AjaxJson.error("越权操作，只有超级管理员才能修改此数据！");
		}
		if(appProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		// 如果是编辑/授权场景（有id），从数据库补全必要字段，避免仅传部分字段导致校验出错
		if (StringUtils.isNotBlank(role.getId())) {
			Role dbRole = roleService.get(role.getId());
			if (dbRole != null) {
				// 补全名称相关字段（前端授权时可能只传 id 和 menuIdList）
				if (StringUtils.isBlank(role.getName())) {
					role.setName(dbRole.getName());
				}
				if (StringUtils.isBlank(role.getEnname())) {
					role.setEnname(dbRole.getEnname());
				}
				if (StringUtils.isBlank(role.getOldName())) {
					role.setOldName(dbRole.getName());
				}
				if (StringUtils.isBlank(role.getOldEnname())) {
					role.setOldEnname(dbRole.getEnname());
				}
				// 补全系统标识、可用状态等（防止被前端覆盖为空）
				if (StringUtils.isBlank(role.getSysData())) {
					role.setSysData(dbRole.getSysData());
				}
				if (StringUtils.isBlank(role.getUseable())) {
					role.setUseable(dbRole.getUseable());
				}
				if (StringUtils.isBlank(role.getRemarks())) {
					role.setRemarks(dbRole.getRemarks());
				}
			}
		}
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(role);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		if (!"true".equals(checkName(role.getOldName(), role.getName()))){
			return AjaxJson.error("保存角色'" + role.getName() + "'失败, 角色名已存在");
		}
		if (!"true".equals(checkEnname(role.getOldEnname(), role.getEnname()))){
			return AjaxJson.error("保存角色'" + role.getName() + "'失败, 英文名已存在");
		}
		if(StringUtils.isNotBlank(role.getDataRuleIds())){
			String dataRuleIds = role.getDataRuleIds();
			String newDataRuleIds= "";
			String[]ruleIds = dataRuleIds.split(",");
			for(String ruleId:ruleIds){
				if(ruleId.startsWith("dataRule-")){
					newDataRuleIds = newDataRuleIds + ruleId.substring(9) + ",";
				}
			}
		if(newDataRuleIds.length() > 1){
			role.setDataRuleIds(newDataRuleIds.substring(0, newDataRuleIds.length()-1));
		}

		}
		try {
			roleService.saveRole(role);
			return AjaxJson.success("保存角色'" + role.getName() + "'成功");
		} catch (ServiceException e) {
			return AjaxJson.error(e.getMessage());
		} catch (Exception e) {
			return AjaxJson.error("保存角色失败：" + e.getMessage());
		}
	}

	/**
	 * 删除角色
	 */
	@ApiLog("删除角色")
	@RequiresPermissions("system:role:delete")
	@DeleteMapping("delete")
	public AjaxJson delete( String ids) {
		if(appProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		StringBuffer msg = new StringBuffer();
        User currentUser = UserUtils.getUser();
        List<String> currentRoleIds = currentUser != null ? currentUser.getRoleIdList() : Lists.newArrayList();
		for(String id : ids.split(",")){
			Role role = roleService.get(id);
			if(!UserUtils.getUser().isAdmin() && role.getSysData().equals(AppProperites.YES)){
				msg.append( "越权操作，只有超级管理员才能修改["+role.getName()+"]数据！<br/>");
            }else if (role != null && currentRoleIds.contains(role.getId())){
                msg.append("不能删除当前登录用户所属的角色["+role.getName()+"]！<br/>");
            }else if (role != null && StringUtils.equalsIgnoreCase(role.getEnname(), ADMIN_ROLE_ENNAME)){
                msg.append("不能删除系统管理员角色["+role.getName()+"]！<br/>");
            }else{
				roleService.deleteRole(role);
				msg.append( "删除角色["+role.getName()+"]成功<br/>");

			}
		}
		return AjaxJson.success(msg.toString());
	}

	/**
	 * 获取所属角色用户
	 * @return
	 */
//	@RequiresPermissions("sys:role:assign")
	@GetMapping("assign")
	public AjaxJson assign(User user, HttpServletRequest request, HttpServletResponse response) {
		Page<User> page = userService.findPage(new Page<User>(request, response), user);
		return AjaxJson.success().put("page", page);
	}



	/**
	 * 角色分配 -- 从角色中移除用户
	 * @param userId
	 * @param roleId
	 * @return
	 */
//	@RequiresPermissions("sys:role:assign")
	@DeleteMapping("outrole")
	public AjaxJson outrole(String userId, String roleId) {
		if(appProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		Role role = roleService.get(roleId);
		User user = userService.get(userId);
		if (UserUtils.getUser().getId().equals(userId) && !UserUtils.getUser().isAdmin()) {
			return AjaxJson.error("无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
		}else {
			if (user.getRoleList().size() <= 1){
				return AjaxJson.error("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！这已经是该用户的唯一角色，不能移除。");
			}else{
				Boolean flag = roleService.outUserInRole(role, user);
				if (!flag) {
					return AjaxJson.error("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！");
				}else {
					return AjaxJson.success("用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！");
				}
			}
		}
	}

	/**
	 * 角色分配
	 * @param role
	 * @return
	 */
	@ApiLog("角色分配")
//	@RequiresPermissions("sys:role:assign")
	@PostMapping("assignrole")
	public AjaxJson assignRole(Role role, String[] ids) {
		if(appProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		StringBuilder msg = new StringBuilder();
		int newNum = 0;
		for (int i = 0; i < ids.length; i++) {
			User user = roleService.assignUserToRole(role, userService.get(ids[i]));
			if (null != user) {
				msg.append("<br/>新增用户【" + user.getName() + "】到角色【" + role.getName() + "】！");
				newNum++;
			}
		}
		return AjaxJson.success("已成功分配 "+newNum+" 个用户"+msg);
	}

	/**
	 * 验证角色名是否有效
	 * @param oldName
	 * @param name
	 * @return
	 */
	public String checkName(String oldName, String name) {
		if (name!=null && name.equals(oldName)) {
			return "true";
		} else if (name!=null && roleService.getRoleByName(name) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 验证角色英文名是否有效
	 * @return
	 */
	public String checkEnname(String oldEnname, String enname) {
		if (enname!=null && enname.equals(oldEnname)) {
			return "true";
		} else if (enname!=null && roleService.getRoleByEnname(enname) == null) {
			return "true";
		}
		return "false";
	}

}
