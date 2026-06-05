package com.seekweb4.chat.modules.changenamelog.web;

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
import com.seekweb4.chat.modules.changenamelog.entity.ChangeNameLog;
import com.seekweb4.chat.modules.changenamelog.service.ChangeNameLogService;

/**
 * 修改昵称记录Controller
 * @author lixinapp
 * @version 2024-11-15
 */
@RestController
@RequestMapping(value = "/changenamelog/changeNameLog")
public class ChangeNameLogController extends BaseController {

	@Autowired
	private ChangeNameLogService changeNameLogService;

	@ModelAttribute
	public ChangeNameLog get(@RequestParam(required=false) String id) {
		ChangeNameLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = changeNameLogService.get(id);
		}
		if (entity == null){
			entity = new ChangeNameLog();
		}
		return entity;
	}

	/**
	 * 修改昵称记录列表数据
	 */
	@ApiLog("查询修改昵称记录列表")
	@RequiresPermissions("changenamelog:changeNameLog:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    ChangeNameLog changeNameLog, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(changeNameLog);
		Page<ChangeNameLog> page = changeNameLogService.findPage(new Page<ChangeNameLog>(request, response), changeNameLog);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取修改昵称记录数据
	 */
	@ApiLog("查询修改昵称记录")
	@RequiresPermissions(value={"changenamelog:changeNameLog:view","changenamelog:changeNameLog:add","changenamelog:changeNameLog:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    ChangeNameLog changeNameLog) {
		if (StringUtils.isNotBlank(changeNameLog.getId())) {
			ChangeNameLog entity = changeNameLogService.get(changeNameLog.getId());
			return entity != null ? AjaxJson.success().put("changeNameLog", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存修改昵称记录
	 */
	@ApiLog("保存修改昵称记录")
	@RequiresPermissions(value={"changenamelog:changeNameLog:add","changenamelog:changeNameLog:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    ChangeNameLog changeNameLog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(changeNameLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		changeNameLogService.save(changeNameLog);//保存
		return AjaxJson.success("保存修改昵称记录成功");
	}


	/**
	 * 批量删除修改昵称记录
	 */
	@ApiLog("删除修改昵称记录")
	@RequiresPermissions("changenamelog:changeNameLog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			changeNameLogService.delete(new ChangeNameLog(id));
		}
		return AjaxJson.success("删除修改昵称记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出修改昵称记录")
	@RequiresPermissions("changenamelog:changeNameLog:export")
    @GetMapping("export")
    public AjaxJson exportFile(ChangeNameLog changeNameLog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "修改昵称记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ChangeNameLog> page = changeNameLogService.findPage(new Page<ChangeNameLog>(request, response, -1), changeNameLog);
    		new ExportExcel("修改昵称记录", ChangeNameLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出修改昵称记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入修改昵称记录")
	@RequiresPermissions("changenamelog:changeNameLog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ChangeNameLog> list = ei.getDataList(ChangeNameLog.class);
			for (ChangeNameLog changeNameLog : list){
				try{
					changeNameLogService.save(changeNameLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条修改昵称记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条修改昵称记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入修改昵称记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入修改昵称记录数据模板
	 */
	@ApiLog("下载修改昵称记录模板")
	@RequiresPermissions("changenamelog:changeNameLog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "修改昵称记录数据导入模板.xlsx";
    		List<ChangeNameLog> list = Lists.newArrayList();
    		new ExportExcel("修改昵称记录数据", ChangeNameLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}