package com.seekweb4.chat.modules.group.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.modules.groupitem.service.GroupItemService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.seekweb4.chat.common.annotation.ApiLog;
import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.common.utils.excel.ExportExcel;
import com.seekweb4.chat.common.utils.excel.ImportExcel;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.group.service.GroupService;

/**
 * 群组信息Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/group/group")
public class GroupController extends BaseController {

	@Autowired
	private GroupService groupService;
	@Autowired
	private GroupItemService groupItemService;

	@ModelAttribute
	public Group get(@RequestParam(required=false) String id) {
		Group entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = groupService.get(id);
		}
		if (entity == null){
			entity = new Group();
		}
		return entity;
	}

	/**
	 * 群组信息列表数据
	 */
	@ApiLog("查询群组信息列表")
//	@RequiresPermissions("group:group:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(Group group, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(group);
		Page<Group> page = groupService.findPage(new Page<Group>(request, response), group);
		// TODO
		// 将icon和qrcode的相对路径转换为绝对路径
		if (page.getList() != null) {
			for (Group g : page.getList()) {
				if (StringUtils.isNotBlank(g.getIcon())) {
					g.setIcon(getRealPath(g.getIcon()));
				}
				if (StringUtils.isNotBlank(g.getQrcode())) {
					g.setQrcode(getRealPath(g.getQrcode()));
				}
			}
		}
		return AjaxJson.success().put("page", page);
	}

	/**
	 * 根据Id获取群组信息数据
	 */
	@ApiLog("查询群组信息")
	@RequiresPermissions(value={"group:manage:info:view","group:manage:info:add","group:manage:info:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"group:group:view","group:group:add","group:group:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Group group) {
		if (StringUtils.isNotBlank(group.getId())) {
			Group entity = groupService.get(group.getId());
			// TODO
			if (entity != null) {
				// 将icon和qrcode的相对路径转换为绝对路径
				if (StringUtils.isNotBlank(entity.getIcon())) {
					entity.setIcon(getRealPath(entity.getIcon()));
				}
				if (StringUtils.isNotBlank(entity.getQrcode())) {
					entity.setQrcode(getRealPath(entity.getQrcode()));
				}
				return AjaxJson.success().put("group", entity);
			}
			return AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存群组信息
	 */
	@ApiLog("保存群组信息")
	@RequiresPermissions(value={"group:manage:info:add","group:manage:info:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody(required = false) Group bodyGroup,
	                     Group formGroup) throws Exception{
		// JSON 优先，没有 JSON 时退回到表单 / 参数
		Group group = bodyGroup != null ? bodyGroup : formGroup;
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(group);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		groupService.save(group);//保存
		return AjaxJson.success("保存群组信息成功");
	}


	/**
	 * 批量删除群组信息
	 */
	@ApiLog("删除群组信息")
	@RequiresPermissions("group:manage:info:delete")
//	@RequiresPermissions("group:group:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			Group g = groupService.get(id);
			ImUtils.delGroup(g.getU().getId(),id);
			groupItemService.executeDeleteSql("delete from t_group_item where group_id = '"+id+"'");
			groupService.deleteByLogic(g);
		}
		return AjaxJson.success("删除群组信息成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出群组信息")
//	@RequiresPermissions("group:group:export")
    @GetMapping("export")
    public AjaxJson exportFile(Group group, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "群组信息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Group> page = groupService.findPage(new Page<Group>(request, response, -1), group);
    		new ExportExcel("群组信息", Group.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出群组信息记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入群组信息")
//	@RequiresPermissions("group:group:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Group> list = ei.getDataList(Group.class);
			for (Group group : list){
				try{
					groupService.save(group);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条群组信息记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条群组信息记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入群组信息失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入群组信息数据模板
	 */
	@ApiLog("下载群组信息模板")
//	@RequiresPermissions("group:group:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "群组信息数据导入模板.xlsx";
    		List<Group> list = Lists.newArrayList();
    		new ExportExcel("群组信息数据", Group.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}