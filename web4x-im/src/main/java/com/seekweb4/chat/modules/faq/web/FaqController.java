package com.seekweb4.chat.modules.faq.web;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.common.utils.excel.ExportExcel;
import com.seekweb4.chat.common.utils.excel.ImportExcel;
import com.seekweb4.chat.modules.faq.entity.Faq;
import com.seekweb4.chat.modules.faq.service.FaqService;

/**
 * 常见问题Controller
 * @author lixinapp
 * @version 2022-12-19
 */
@RestController
@RequestMapping(value = "/faq/faq")
public class FaqController extends BaseController {

	@Autowired
	private FaqService faqService;

	@ModelAttribute
	public Faq get(@RequestParam(required=false) String id) {
		Faq entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = faqService.get(id);
		}
		if (entity == null){
			entity = new Faq();
		}
		return entity;
	}

	/**
	 * 常见问题列表数据
	 */
	@ApiLog("查询常见问题列表")
//	@RequiresPermissions("faq:faq:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(Faq faq, HttpServletRequest request, HttpServletResponse response) {
		Page<Faq> page = faqService.findPage(new Page<Faq>(request, response), faq);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取常见问题数据
	 */
	@ApiLog("查询常见问题")
	@RequiresPermissions(value={"ops:support:faq:view","ops:support:faq:add","ops:support:faq:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Faq faq) {
		if (StringUtils.isNotBlank(faq.getId())) {
			Faq entity = faqService.get(faq.getId());
			return entity != null ? AjaxJson.success().put("faq", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存常见问题
	 */
	@ApiLog("保存常见问题")
	@RequiresPermissions(value={"ops:support:faq:add","ops:support:faq:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(  @RequestBody  Faq faq) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(faq);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		faqService.save(faq);//保存
		return AjaxJson.success("保存常见问题成功");
	}


	/**
	 * 批量删除常见问题
	 */
	@ApiLog("删除常见问题")
	@RequiresPermissions("ops:support:faq:delete")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			faqService.delete(new Faq(id));
		}
		return AjaxJson.success("删除常见问题成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出常见问题")
	@RequiresPermissions("faq:faq:export")
    @GetMapping("export")
    public AjaxJson exportFile(Faq faq, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "常见问题"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Faq> page = faqService.findPage(new Page<Faq>(request, response, -1), faq);
    		new ExportExcel("常见问题", Faq.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出常见问题记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	@ApiLog("导入常见问题")
	@RequiresPermissions("faq:faq:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Faq> list = ei.getDataList(Faq.class);
			for (Faq faq : list){
				try{
					faqService.save(faq);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条常见问题记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条常见问题记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入常见问题失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入常见问题数据模板
	 */
	@ApiLog("下载常见问题模板")
	@RequiresPermissions("faq:faq:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "常见问题数据导入模板.xlsx";
    		List<Faq> list = Lists.newArrayList();
    		new ExportExcel("常见问题数据", Faq.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}