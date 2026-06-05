package com.seekweb4.chat.modules.changephonelog.web;

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
import com.seekweb4.chat.modules.changephonelog.entity.ChangePhoneLog;
import com.seekweb4.chat.modules.changephonelog.service.ChangePhoneLogService;

/**
 * 修改手机号记录Controller
 * @author lixinapp
 * @version 2024-11-15
 */
@RestController
@RequestMapping(value = "/changephonelog/changePhoneLog")
public class ChangePhoneLogController extends BaseController {

	@Autowired
	private ChangePhoneLogService changePhoneLogService;

	@ModelAttribute
	public ChangePhoneLog get(@RequestParam(required=false) String id) {
		ChangePhoneLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = changePhoneLogService.get(id);
		}
		if (entity == null){
			entity = new ChangePhoneLog();
		}
		return entity;
	}

	/**
	 * 修改手机号记录列表数据
	 */
	@ApiLog("查询修改手机号记录列表")
	@RequiresPermissions("changephonelog:changePhoneLog:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    ChangePhoneLog changePhoneLog, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(changePhoneLog);
		Page<ChangePhoneLog> page = changePhoneLogService.findPage(new Page<ChangePhoneLog>(request, response), changePhoneLog);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取修改手机号记录数据
	 */
	@ApiLog("查询修改手机号记录")
	@RequiresPermissions(value={"changephonelog:changePhoneLog:view","changephonelog:changePhoneLog:add","changephonelog:changePhoneLog:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    ChangePhoneLog changePhoneLog) {
		if (StringUtils.isNotBlank(changePhoneLog.getId())) {
			ChangePhoneLog entity = changePhoneLogService.get(changePhoneLog.getId());
			return entity != null ? AjaxJson.success().put("changePhoneLog", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存修改手机号记录
	 */
	@ApiLog("保存修改手机号记录")
	@RequiresPermissions(value={"changephonelog:changePhoneLog:add","changephonelog:changePhoneLog:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    ChangePhoneLog changePhoneLog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(changePhoneLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		changePhoneLogService.save(changePhoneLog);//保存
		return AjaxJson.success("保存修改手机号记录成功");
	}


	/**
	 * 批量删除修改手机号记录
	 */
	@ApiLog("删除修改手机号记录")
	@RequiresPermissions("changephonelog:changePhoneLog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			changePhoneLogService.delete(new ChangePhoneLog(id));
		}
		return AjaxJson.success("删除修改手机号记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出修改手机号记录")
	@RequiresPermissions("changephonelog:changePhoneLog:export")
    @GetMapping("export")
    public AjaxJson exportFile(ChangePhoneLog changePhoneLog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "修改手机号记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ChangePhoneLog> page = changePhoneLogService.findPage(new Page<ChangePhoneLog>(request, response, -1), changePhoneLog);
    		new ExportExcel("修改手机号记录", ChangePhoneLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出修改手机号记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入修改手机号记录")
	@RequiresPermissions("changephonelog:changePhoneLog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ChangePhoneLog> list = ei.getDataList(ChangePhoneLog.class);
			for (ChangePhoneLog changePhoneLog : list){
				try{
					changePhoneLogService.save(changePhoneLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条修改手机号记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条修改手机号记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入修改手机号记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入修改手机号记录数据模板
	 */
	@ApiLog("下载修改手机号记录模板")
	@RequiresPermissions("changephonelog:changePhoneLog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "修改手机号记录数据导入模板.xlsx";
    		List<ChangePhoneLog> list = Lists.newArrayList();
    		new ExportExcel("修改手机号记录数据", ChangePhoneLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}