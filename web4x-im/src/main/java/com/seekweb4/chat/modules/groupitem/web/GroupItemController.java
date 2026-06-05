package com.seekweb4.chat.modules.groupitem.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

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
import com.seekweb4.chat.modules.groupitem.entity.GroupItem;
import com.seekweb4.chat.modules.groupitem.service.GroupItemService;

/**
 * 群成员Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/groupitem/groupItem")
public class GroupItemController extends BaseController {

	@Autowired
	private GroupItemService groupItemService;

	@ModelAttribute
	public GroupItem get(@RequestParam(required=false) String id) {
		GroupItem entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = groupItemService.get(id);
		}
		if (entity == null){
			entity = new GroupItem();
		}
		return entity;
	}

	/**
	 * 群成员列表数据
	 */
	@ApiLog("查询群成员列表")
//	@RequiresPermissions("groupitem:groupItem:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    GroupItem groupItem, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(groupItem);
		Page<GroupItem> page = groupItemService.findPage(new Page<GroupItem>(request, response), groupItem);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取群成员数据
	 */
	@ApiLog("查询群成员")
//	@RequiresPermissions(value={"group:manage:member:view","group:manage:member:add","group:manage:member:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"groupitem:groupItem:view","groupitem:groupItem:add","groupitem:groupItem:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    GroupItem groupItem) {
		if (StringUtils.isNotBlank(groupItem.getId())) {
			GroupItem entity = groupItemService.get(groupItem.getId());
			return entity != null ? AjaxJson.success().put("groupItem", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存群成员
	 */
	@ApiLog("保存群成员")
//	@RequiresPermissions(value={"groupitem:groupItem:add","groupitem:groupItem:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   GroupItem groupItem) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(groupItem);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		groupItemService.save(groupItem);//保存
		return AjaxJson.success("保存群成员成功");
	}


	/**
	 * 批量删除群成员
	 */
	@ApiLog("删除群成员")
//	@RequiresPermissions("groupitem:groupItem:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			groupItemService.delete(new GroupItem(id));
		}
		return AjaxJson.success("删除群成员成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出群成员")
//	@RequiresPermissions("groupitem:groupItem:export")
    @GetMapping("export")
    public AjaxJson exportFile(GroupItem groupItem, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "群成员"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<GroupItem> page = groupItemService.findPage(new Page<GroupItem>(request, response, -1), groupItem);
    		new ExportExcel("群成员", GroupItem.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出群成员记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入群成员")
//	@RequiresPermissions("groupitem:groupItem:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<GroupItem> list = ei.getDataList(GroupItem.class);
			for (GroupItem groupItem : list){
				try{
					groupItemService.save(groupItem);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条群成员记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条群成员记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入群成员失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入群成员数据模板
	 */
	@ApiLog("下载群成员模板")
//	@RequiresPermissions("groupitem:groupItem:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "群成员数据导入模板.xlsx";
    		List<GroupItem> list = Lists.newArrayList();
    		new ExportExcel("群成员数据", GroupItem.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 解除群成员禁言
	 */
	@ApiLog("解除群成员禁言")
//	@RequiresPermissions("groupitem:groupItem:edit")
	@PostMapping(value = "unban", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson unban(@RequestBody java.util.Map<String, Object> params) {
		String id = null;
		if (params != null && params.containsKey("id")) {
			id = params.get("id").toString();
		}
		
		if (StringUtils.isBlank(id)) {
			return AjaxJson.error("群成员ID不能为空");
		}
		
		boolean success = groupItemService.unbanMember(id);
		if (success) {
			return AjaxJson.success("解除禁言成功");
		} else {
			return AjaxJson.error("解除禁言失败，请检查群成员是否存在或已被解禁");
		}
	}


}