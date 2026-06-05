package com.seekweb4.chat.modules.buttonConfig.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.seekweb4.chat.common.annotation.ApiLog;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.common.utils.excel.ExportExcel;
import com.seekweb4.chat.common.utils.excel.ImportExcel;
import com.seekweb4.chat.modules.buttonConfig.entity.ButtonConfig;
import com.seekweb4.chat.modules.buttonConfig.service.ButtonConfigService;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolationException;

/**
 * 按钮配置Controller
 * @author system
 * @version 2025-11-28
 */
@RestController
@RequestMapping(value = "/buttonConfig/buttonConfig")
public class ButtonConfigController extends BaseController {

	@Autowired
	private ButtonConfigService buttonConfigService;

	@ModelAttribute
	public ButtonConfig get(@RequestParam(required=false) String id) {
		ButtonConfig entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = buttonConfigService.get(id);
		}
		if (entity == null){
			entity = new ButtonConfig();
		}
		return entity;
	}

	/**
	 * 按钮配置列表数据
	 */
	@ApiLog("查询按钮配置列表")
//	@RequiresPermissions("buttonConfig:buttonConfig:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(ButtonConfig buttonConfig, HttpServletRequest request, HttpServletResponse response) {
		Page<ButtonConfig> page = buttonConfigService.findPage(new Page<ButtonConfig>(request, response), buttonConfig);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取按钮配置数据
	 */
	@ApiLog("查询按钮配置")
	@RequiresPermissions(value={"ops:system:features:view","ops:system:features:add","ops:system:features:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"buttonConfig:buttonConfig:view","buttonConfig:buttonConfig:add","buttonConfig:buttonConfig:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(ButtonConfig buttonConfig) {
		if (StringUtils.isNotBlank(buttonConfig.getId())) {
			ButtonConfig entity = buttonConfigService.get(buttonConfig.getId());
			return entity != null ? AjaxJson.success().put("buttonConfig", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 根据按钮标识获取按钮配置
	 */
	@ApiLog("根据按钮标识查询按钮配置")
	@GetMapping(value = "queryByButtonKey", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryByButtonKey(@RequestParam String buttonKey) {
		if (StringUtils.isNotBlank(buttonKey)) {
			ButtonConfig entity = buttonConfigService.getByButtonKey(buttonKey);
			return entity != null ? AjaxJson.success().put("buttonConfig", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("buttonKey不能为空");
	}

	/**
	 * 保存按钮配置
	 */
	@ApiLog("保存按钮配置")
	@RequiresPermissions(value={"ops:system:features:add","ops:system:features:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody ButtonConfig buttonConfig) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(buttonConfig);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		buttonConfigService.save(buttonConfig);//保存
		return AjaxJson.success("保存按钮配置成功");
	}

	/**
	 * 根据标识修改按钮权限
	 */
	@ApiLog("修改按钮权限")
	@RequiresPermissions("ops:system:features:edit")
	@PostMapping(value = "updateKey", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson updateKey(@RequestBody ButtonConfig buttonConfig) {
		if (StringUtils.isBlank(buttonConfig.getButtonKey())){
			return AjaxJson.error("标识不能为空");
		}
		buttonConfigService.updateButtonKey(buttonConfig);
		return AjaxJson.success("修改按钮成功");
	}

	/**
	 * 批量删除按钮配置
	 */
	@ApiLog("删除按钮配置")
	@RequiresPermissions("ops:system:features:delete")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			buttonConfigService.delete(new ButtonConfig(id));
		}
		return AjaxJson.success("删除按钮配置成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出按钮配置")
//	@RequiresPermissions("buttonConfig:buttonConfig:export")
    @GetMapping("export")
    public AjaxJson exportFile(ButtonConfig buttonConfig, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "按钮配置"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ButtonConfig> page = buttonConfigService.findPage(new Page<ButtonConfig>(request, response, -1), buttonConfig);
    		new ExportExcel("按钮配置", ButtonConfig.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出按钮配置记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入按钮配置")
//	@RequiresPermissions("buttonConfig:buttonConfig:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ButtonConfig> list = ei.getDataList(ButtonConfig.class);
			for (ButtonConfig buttonConfig : list){
				try{
					buttonConfigService.save(buttonConfig);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条按钮配置记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条按钮配置记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入按钮配置失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入按钮配置数据模板
	 */
	@ApiLog("下载按钮配置模板")
//	@RequiresPermissions("buttonConfig:buttonConfig:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "按钮配置数据导入模板.xlsx";
    		List<ButtonConfig> list = Lists.newArrayList();
    		new ExportExcel("按钮配置数据", ButtonConfig.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }

}
