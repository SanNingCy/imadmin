package com.seekweb4.chat.modules.changepwdlog.web;

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
import com.seekweb4.chat.modules.changepwdlog.entity.ChangePwdLog;
import com.seekweb4.chat.modules.changepwdlog.service.ChangePwdLogService;

/**
 * 修改登录密码记录Controller
 * @author lixinapp
 * @version 2024-11-15
 */
@RestController
@RequestMapping(value = "/changepwdlog/changePwdLog")
public class ChangePwdLogController extends BaseController {

	@Autowired
	private ChangePwdLogService changePwdLogService;

	@ModelAttribute
	public ChangePwdLog get(@RequestParam(required=false) String id) {
		ChangePwdLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = changePwdLogService.get(id);
		}
		if (entity == null){
			entity = new ChangePwdLog();
		}
		return entity;
	}

	/**
	 * 修改登录密码记录列表数据
	 */
	@ApiLog("查询修改登录密码记录列表")
	@RequiresPermissions("changepwdlog:changePwdLog:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    ChangePwdLog changePwdLog, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(changePwdLog);
		Page<ChangePwdLog> page = changePwdLogService.findPage(new Page<ChangePwdLog>(request, response), changePwdLog);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取修改登录密码记录数据
	 */
	@ApiLog("查询修改登录密码记录")
	@RequiresPermissions(value={"changepwdlog:changePwdLog:view","changepwdlog:changePwdLog:add","changepwdlog:changePwdLog:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    ChangePwdLog changePwdLog) {
		if (StringUtils.isNotBlank(changePwdLog.getId())) {
			ChangePwdLog entity = changePwdLogService.get(changePwdLog.getId());
			return entity != null ? AjaxJson.success().put("changePwdLog", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存修改登录密码记录
	 */
	@ApiLog("保存修改登录密码记录")
	@RequiresPermissions(value={"changepwdlog:changePwdLog:add","changepwdlog:changePwdLog:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    ChangePwdLog changePwdLog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(changePwdLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		changePwdLogService.save(changePwdLog);//保存
		return AjaxJson.success("保存修改登录密码记录成功");
	}


	/**
	 * 批量删除修改登录密码记录
	 */
	@ApiLog("删除修改登录密码记录")
	@RequiresPermissions("changepwdlog:changePwdLog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			changePwdLogService.delete(new ChangePwdLog(id));
		}
		return AjaxJson.success("删除修改登录密码记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出修改登录密码记录")
	@RequiresPermissions("changepwdlog:changePwdLog:export")
    @GetMapping("export")
    public AjaxJson exportFile(ChangePwdLog changePwdLog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "修改登录密码记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ChangePwdLog> page = changePwdLogService.findPage(new Page<ChangePwdLog>(request, response, -1), changePwdLog);
    		new ExportExcel("修改登录密码记录", ChangePwdLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出修改登录密码记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入修改登录密码记录")
	@RequiresPermissions("changepwdlog:changePwdLog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ChangePwdLog> list = ei.getDataList(ChangePwdLog.class);
			for (ChangePwdLog changePwdLog : list){
				try{
					changePwdLogService.save(changePwdLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条修改登录密码记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条修改登录密码记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入修改登录密码记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入修改登录密码记录数据模板
	 */
	@ApiLog("下载修改登录密码记录模板")
	@RequiresPermissions("changepwdlog:changePwdLog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "修改登录密码记录数据导入模板.xlsx";
    		List<ChangePwdLog> list = Lists.newArrayList();
    		new ExportExcel("修改登录密码记录数据", ChangePwdLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}