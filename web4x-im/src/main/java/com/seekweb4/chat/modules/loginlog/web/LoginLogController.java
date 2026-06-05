package com.seekweb4.chat.modules.loginlog.web;

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
import com.seekweb4.chat.modules.loginlog.entity.LoginLog;
import com.seekweb4.chat.modules.loginlog.service.LoginLogService;

/**
 * 登录记录Controller
 * @author lixinapp
 * @version 2024-11-15
 */
@RestController
@RequestMapping(value = "/loginlog/loginLog")
public class LoginLogController extends BaseController {

	@Autowired
	private LoginLogService loginLogService;

	@ModelAttribute
	public LoginLog get(@RequestParam(required=false) String id) {
		LoginLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = loginLogService.get(id);
		}
		if (entity == null){
			entity = new LoginLog();
		}
		return entity;
	}

	/**
	 * 登录记录列表数据
	 */
	@ApiLog("查询登录记录列表")
//	@RequiresPermissions("loginlog:loginLog:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    LoginLog loginLog, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(loginLog);
		Page<LoginLog> page = loginLogService.findPage(new Page<LoginLog>(request, response), loginLog);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取登录记录数据
	 */
	@ApiLog("查询登录记录")
//	@RequiresPermissions(value={"loginlog:loginLog:view","loginlog:loginLog:add","loginlog:loginLog:edit"},logical=Logical.OR)
	@RequiresPermissions(value={"social:security:login-audit:view"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    LoginLog loginLog) {
		if (StringUtils.isNotBlank(loginLog.getId())) {
			LoginLog entity = loginLogService.get(loginLog.getId());
			return entity != null ? AjaxJson.success().put("loginLog", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存登录记录
	 */
	@ApiLog("保存登录记录")
//	@RequiresPermissions(value={"loginlog:loginLog:add","loginlog:loginLog:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    LoginLog loginLog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(loginLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		loginLogService.save(loginLog);//保存
		return AjaxJson.success("保存登录记录成功");
	}


	/**
	 * 批量删除登录记录
	 */
	@ApiLog("删除登录记录")
//	@RequiresPermissions("loginlog:loginLog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			loginLogService.delete(new LoginLog(id));
		}
		return AjaxJson.success("删除登录记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出登录记录")
//	@RequiresPermissions("loginlog:loginLog:export")
    @GetMapping("export")
    public AjaxJson exportFile(LoginLog loginLog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "登录记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<LoginLog> page = loginLogService.findPage(new Page<LoginLog>(request, response, -1), loginLog);
    		new ExportExcel("登录记录", LoginLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出登录记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入登录记录")
//	@RequiresPermissions("loginlog:loginLog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<LoginLog> list = ei.getDataList(LoginLog.class);
			for (LoginLog loginLog : list){
				try{
					loginLogService.save(loginLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条登录记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条登录记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入登录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入登录记录数据模板
	 */
	@ApiLog("下载登录记录模板")
//	@RequiresPermissions("loginlog:loginLog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "登录记录数据导入模板.xlsx";
    		List<LoginLog> list = Lists.newArrayList();
    		new ExportExcel("登录记录数据", LoginLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}