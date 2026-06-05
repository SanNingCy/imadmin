package com.seekweb4.chat.modules.signlog.web;

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
import com.seekweb4.chat.modules.signlog.entity.SignLog;
import com.seekweb4.chat.modules.signlog.service.SignLogService;

/**
 * 签到记录Controller
 * @author lixinapp
 * @version 2024-09-22
 */
@RestController
@RequestMapping(value = "/signlog/signLog")
public class SignLogController extends BaseController {

	@Autowired
	private SignLogService signLogService;

	@ModelAttribute
	public SignLog get(@RequestParam(required=false) String id) {
		SignLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = signLogService.get(id);
		}
		if (entity == null){
			entity = new SignLog();
		}
		return entity;
	}

	/**
	 * 签到记录列表数据
	 */
	@ApiLog("查询签到记录列表")
//	@RequiresPermissions("signlog:signLog:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    SignLog signLog, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(signLog);
		Page<SignLog> page = signLogService.findPage(new Page<SignLog>(request, response), signLog);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取签到记录数据
	 */
	@ApiLog("查询签到记录")
//	@RequiresPermissions(value={"social:security:sign-logs:view","social:security:sign-logs:add","social:security:sign-logs:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"signlog:signLog:view","signlog:signLog:add","signlog:signLog:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    SignLog signLog) {
		if (StringUtils.isNotBlank(signLog.getId())) {
			SignLog entity = signLogService.get(signLog.getId());
			return entity != null ? AjaxJson.success().put("signLog", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存签到记录
	 */
	@ApiLog("保存签到记录")
//	@RequiresPermissions(value={"signlog:signLog:add","signlog:signLog:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    SignLog signLog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(signLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		signLogService.save(signLog);//保存
		return AjaxJson.success("保存签到记录成功");
	}


	/**
	 * 批量删除签到记录
	 */
	@ApiLog("删除签到记录")
//	@RequiresPermissions("signlog:signLog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			signLogService.delete(new SignLog(id));
		}
		return AjaxJson.success("删除签到记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出签到记录")
//	@RequiresPermissions("signlog:signLog:export")
    @GetMapping("export")
    public AjaxJson exportFile(SignLog signLog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "签到记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SignLog> page = signLogService.findPage(new Page<SignLog>(request, response, -1), signLog);
    		new ExportExcel("签到记录", SignLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出签到记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入签到记录")
//	@RequiresPermissions("signlog:signLog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SignLog> list = ei.getDataList(SignLog.class);
			for (SignLog signLog : list){
				try{
					signLogService.save(signLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条签到记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条签到记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入签到记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入签到记录数据模板
	 */
	@ApiLog("下载签到记录模板")
//	@RequiresPermissions("signlog:signLog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "签到记录数据导入模板.xlsx";
    		List<SignLog> list = Lists.newArrayList();
    		new ExportExcel("签到记录数据", SignLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}