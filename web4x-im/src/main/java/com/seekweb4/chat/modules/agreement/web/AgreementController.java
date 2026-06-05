package com.seekweb4.chat.modules.agreement.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.common.annotation.ApiLog;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.common.utils.excel.ExportExcel;
import com.seekweb4.chat.common.utils.excel.ImportExcel;
import com.seekweb4.chat.modules.agreement.entity.Agreement;
import com.seekweb4.chat.modules.agreement.service.AgreementService;

/**
 * 说明协议Controller
 * @author lixinapp
 * @version 2021-07-05
 */
@RestController
@RequestMapping(value = "/agreement/agreement")
public class AgreementController extends BaseController {

	@Autowired
	private AgreementService agreementService;

	@ModelAttribute
	public Agreement get(@RequestParam(required=false) String id) {
		Agreement entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = agreementService.get(id);
		}
		if (entity == null){
			entity = new Agreement();
		}
		return entity;
	}

	/**
	 * 说明协议列表数据
	 */
	@ApiLog("查询说明协议列表")
//	@RequiresPermissions("agreement:agreement:list")
	@GetMapping(value = "list")
//	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(Agreement agreement, HttpServletRequest request, HttpServletResponse response) {
		Page<Agreement> page = agreementService.findPage(new Page<Agreement>(request, response), agreement);
		return AjaxJson.success().put("page", page);
	}

	/**
	 * 根据Id获取说明协议数据
	 */
	@ApiLog("查询说明协议")
	@RequiresPermissions(value={"ops:support:agreement:view","ops:support:agreement:add","ops:support:agreement:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"agreement:agreement:view","agreement:agreement:add","agreement:agreement:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(  Agreement agreement) {
		if (StringUtils.isNotBlank(agreement.getId())) {
			Agreement entity = agreementService.get(agreement.getId());
			return entity != null ? AjaxJson.success().put("agreement", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存说明协议
	 */
	@ApiLog("保存说明协议")
	@RequiresPermissions(value={"ops:support:agreement:add","ops:support:agreement:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody  Agreement agreement) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(agreement);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		agreementService.save(agreement);//保存
		return AjaxJson.success("保存说明协议成功");
	}

	/**
	 * 保存说明协议
	 */
	@ApiLog("保存说明协议")
	@RequiresPermissions(value={"ops:support:agreement:add","ops:support:agreement:edit"},logical=Logical.OR)
	@PostMapping(value = "upadte", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson update(  Agreement agreement) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(agreement);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		agreementService.update(agreement);//保存
		return AjaxJson.success("修改说明协议成功");
	}


	/**
	 * 批量删除说明协议
	 */
	@ApiLog("删除说明协议")
//	@RequiresPermissions("agreement:agreement:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(  String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			agreementService.delete(new Agreement(id));
		}
		return AjaxJson.success("删除说明协议成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出说明协议")
//	@RequiresPermissions("agreement:agreement:export")
    @GetMapping("export")
    public AjaxJson exportFile(Agreement agreement, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "说明协议"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Agreement> page = agreementService.findPage(new Page<Agreement>(request, response, -1), agreement);
    		new ExportExcel("说明协议", Agreement.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出说明协议记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	@ApiLog("导入说明协议")
//	@RequiresPermissions("agreement:agreement:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Agreement> list = ei.getDataList(Agreement.class);
			for (Agreement agreement : list){
				try{
					agreementService.save(agreement);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条说明协议记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条说明协议记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入说明协议失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入说明协议数据模板
	 */
	@ApiLog("下载说明协议模版")
//	@RequiresPermissions("agreement:agreement:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "说明协议数据导入模板.xlsx";
    		List<Agreement> list = Lists.newArrayList();
    		new ExportExcel("说明协议数据", Agreement.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}