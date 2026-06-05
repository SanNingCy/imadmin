package com.seekweb4.chat.modules.tixiantitle.web;

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
import com.seekweb4.chat.modules.tixiantitle.entity.TixianTitle;
import com.seekweb4.chat.modules.tixiantitle.service.TixianTitleService;

/**
 * 提现页标题Controller
 * @author lixinapp
 * @version 2024-09-22
 */
@RestController
@RequestMapping(value = "/tixiantitle/tixianTitle")
public class TixianTitleController extends BaseController {

	@Autowired
	private TixianTitleService tixianTitleService;

	@ModelAttribute
	public TixianTitle get(@RequestParam(required=false) String id) {
		TixianTitle entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tixianTitleService.get(id);
		}
		if (entity == null){
			entity = new TixianTitle();
		}
		return entity;
	}

	/**
	 * 提现页标题列表数据
	 */
	@ApiLog("查询提现页标题列表")
//	@RequiresPermissions("tixiantitle:tixianTitle:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    TixianTitle tixianTitle, HttpServletRequest request, HttpServletResponse response) {
		Page<TixianTitle> page = tixianTitleService.findPage(new Page<TixianTitle>(request, response), tixianTitle);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取提现页标题数据
	 */
	@ApiLog("查询提现页标题")
	@RequiresPermissions(value={"ops:content:withdraw-title:view","ops:content:withdraw-title:add","ops:content:withdraw-title:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    TixianTitle tixianTitle) {
		if (StringUtils.isNotBlank(tixianTitle.getId())) {
			TixianTitle entity = tixianTitleService.get(tixianTitle.getId());
			return entity != null ? AjaxJson.success().put("tixianTitle", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存提现页标题
	 */
	@ApiLog("保存提现页标题")
	@RequiresPermissions(value={"ops:content:withdraw-title:add","ops:content:withdraw-title:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody    TixianTitle tixianTitle) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(tixianTitle);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		tixianTitleService.save(tixianTitle);//保存
		return AjaxJson.success("保存提现页标题成功");
	}


	/**
	 * 批量删除提现页标题
	 */
	@ApiLog("删除提现页标题")
	@RequiresPermissions("ops:content:withdraw-title:delete")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			tixianTitleService.delete(new TixianTitle(id));
		}
		return AjaxJson.success("删除提现页标题成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出提现页标题")
	@RequiresPermissions("tixiantitle:tixianTitle:export")
    @GetMapping("export")
    public AjaxJson exportFile(TixianTitle tixianTitle, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "提现页标题"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<TixianTitle> page = tixianTitleService.findPage(new Page<TixianTitle>(request, response, -1), tixianTitle);
    		new ExportExcel("提现页标题", TixianTitle.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出提现页标题记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入提现页标题")
	@RequiresPermissions("tixiantitle:tixianTitle:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TixianTitle> list = ei.getDataList(TixianTitle.class);
			for (TixianTitle tixianTitle : list){
				try{
					tixianTitleService.save(tixianTitle);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条提现页标题记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条提现页标题记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入提现页标题失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入提现页标题数据模板
	 */
	@ApiLog("下载提现页标题模板")
	@RequiresPermissions("tixiantitle:tixianTitle:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "提现页标题数据导入模板.xlsx";
    		List<TixianTitle> list = Lists.newArrayList();
    		new ExportExcel("提现页标题数据", TixianTitle.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}