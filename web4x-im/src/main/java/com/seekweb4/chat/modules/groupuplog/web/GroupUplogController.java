package com.seekweb4.chat.modules.groupuplog.web;

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
import com.seekweb4.chat.modules.groupuplog.entity.GroupUplog;
import com.seekweb4.chat.modules.groupuplog.service.GroupUplogService;

/**
 * 群升级记录Controller
 * @author lixinapp
 * @version 2025-03-24
 */
@RestController
@RequestMapping(value = "/groupuplog/groupUplog")
public class GroupUplogController extends BaseController {

	@Autowired
	private GroupUplogService groupUplogService;

	@ModelAttribute
	public GroupUplog get(@RequestParam(required=false) String id) {
		GroupUplog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = groupUplogService.get(id);
		}
		if (entity == null){
			entity = new GroupUplog();
		}
		return entity;
	}

	/**
	 * 群升级记录列表数据
	 */
	@ApiLog("查询群升级记录列表")
//	@RequiresPermissions("groupuplog:groupUplog:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    GroupUplog groupUplog, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(groupUplog);
		Page<GroupUplog> page = groupUplogService.findPage(new Page<GroupUplog>(request, response), groupUplog);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取群升级记录数据
	 */
	@ApiLog("查询群升级记录")
//	@RequiresPermissions(value={"group:manage:upgrade-logs:view","group:manage:upgrade-logs:add","group:manage:upgrade-logs:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"groupuplog:groupUplog:view","groupuplog:groupUplog:add","groupuplog:groupUplog:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    GroupUplog groupUplog) {
		if (StringUtils.isNotBlank(groupUplog.getId())) {
			GroupUplog entity = groupUplogService.get(groupUplog.getId());
			return entity != null ? AjaxJson.success().put("groupUplog", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存群升级记录
	 */
	@ApiLog("保存群升级记录")
//	@RequiresPermissions(value={"groupuplog:groupUplog:add","groupuplog:groupUplog:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   GroupUplog groupUplog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(groupUplog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		groupUplogService.save(groupUplog);//保存
		return AjaxJson.success("保存群升级记录成功");
	}


	/**
	 * 批量删除群升级记录
	 */
	@ApiLog("删除群升级记录")
//	@RequiresPermissions("groupuplog:groupUplog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			groupUplogService.delete(new GroupUplog(id));
		}
		return AjaxJson.success("删除群升级记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出群升级记录")
//	@RequiresPermissions("groupuplog:groupUplog:export")
    @GetMapping("export")
    public AjaxJson exportFile(GroupUplog groupUplog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "群升级记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<GroupUplog> page = groupUplogService.findPage(new Page<GroupUplog>(request, response, -1), groupUplog);
    		new ExportExcel("群升级记录", GroupUplog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出群升级记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入群升级记录")
//	@RequiresPermissions("groupuplog:groupUplog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<GroupUplog> list = ei.getDataList(GroupUplog.class);
			for (GroupUplog groupUplog : list){
				try{
					groupUplogService.save(groupUplog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条群升级记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条群升级记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入群升级记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入群升级记录数据模板
	 */
	@ApiLog("下载群升级记录模板")
//	@RequiresPermissions("groupuplog:groupUplog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "群升级记录数据导入模板.xlsx";
    		List<GroupUplog> list = Lists.newArrayList();
    		new ExportExcel("群升级记录数据", GroupUplog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}