package com.seekweb4.chat.modules.reason.web;

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
import com.seekweb4.chat.modules.reason.entity.Reason;
import com.seekweb4.chat.modules.reason.service.ReasonService;

/**
 * 投诉原因Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/reason/reason")
public class ReasonController extends BaseController {

	@Autowired
	private ReasonService reasonService;

	@ModelAttribute
	public Reason get(@RequestParam(required=false) String id) {
		Reason entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = reasonService.get(id);
		}
		if (entity == null){
			entity = new Reason();
		}
		return entity;
	}

	/**
	 * 投诉原因列表数据
	 */
	@ApiLog("查询投诉原因列表")
//	@RequiresPermissions("reason:reason:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Reason reason, HttpServletRequest request, HttpServletResponse response) {
		Page<Reason> page = reasonService.findPage(new Page<Reason>(request, response), reason);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取投诉原因数据
	 */
	@ApiLog("查询投诉原因")
	@RequiresPermissions(value={"ops:support:complaint-reasons:view","ops:support:complaint-reasons:add","ops:support:complaint-reasons:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Reason reason) {
		if (StringUtils.isNotBlank(reason.getId())) {
			Reason entity = reasonService.get(reason.getId());
			return entity != null ? AjaxJson.success().put("reason", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存投诉原因
	 */
	@ApiLog("保存投诉原因")
	@RequiresPermissions(value={"ops:support:complaint-reasons:add","ops:support:complaint-reasons:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody  Reason reason) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(reason);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		reasonService.save(reason);//保存
		return AjaxJson.success("保存投诉原因成功");
	}


	/**
	 * 批量删除投诉原因
	 */
	@ApiLog("删除投诉原因")
	@RequiresPermissions("ops:support:complaint-reasons:delete")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			reasonService.delete(new Reason(id));
		}
		return AjaxJson.success("删除投诉原因成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出投诉原因")
	@RequiresPermissions("reason:reason:export")
    @GetMapping("export")
    public AjaxJson exportFile(Reason reason, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "投诉原因"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Reason> page = reasonService.findPage(new Page<Reason>(request, response, -1), reason);
    		new ExportExcel("投诉原因", Reason.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出投诉原因记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入投诉原因")
	@RequiresPermissions("reason:reason:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Reason> list = ei.getDataList(Reason.class);
			for (Reason reason : list){
				try{
					reasonService.save(reason);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条投诉原因记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条投诉原因记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入投诉原因失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入投诉原因数据模板
	 */
	@ApiLog("下载投诉原因模板")
	@RequiresPermissions("reason:reason:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "投诉原因数据导入模板.xlsx";
    		List<Reason> list = Lists.newArrayList();
    		new ExportExcel("投诉原因数据", Reason.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}