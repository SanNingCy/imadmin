package com.seekweb4.chat.modules.black.web;

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
import com.seekweb4.chat.modules.black.entity.Black;
import com.seekweb4.chat.modules.black.service.BlackService;

/**
 * 拉黑表Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/black/black")
public class BlackController extends BaseController {

	@Autowired
	private BlackService blackService;

	@ModelAttribute
	public Black get(@RequestParam(required=false) String id) {
		Black entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = blackService.get(id);
		}
		if (entity == null){
			entity = new Black();
		}
		return entity;
	}

	/**
	 * 拉黑表列表数据
	 */
	@ApiLog("查询拉黑表列表")
//	@RequiresPermissions("black:black:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Black black, HttpServletRequest request, HttpServletResponse response) {
		Page<Black> page = blackService.findPage(new Page<Black>(request, response), black);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取拉黑表数据
	 */
	@ApiLog("查询拉黑表")
//	@RequiresPermissions(value={"black:black:view","black:black:add","black:black:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Black black) {
		if (StringUtils.isNotBlank(black.getId())) {
			Black entity = blackService.get(black.getId());
			return entity != null ? AjaxJson.success().put("black", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存拉黑表
	 */
	@ApiLog("保存拉黑表")
//	@RequiresPermissions(value={"black:black:add","black:black:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody  Black black) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(black);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		blackService.save(black);//保存
		return AjaxJson.success("保存拉黑表成功");
	}

	/**
	 * 修改拉黑表
	 */
	@ApiLog("修改拉黑表")
//	@RequiresPermissions(value={"black:black:add","black:black:edit"},logical=Logical.OR)
	@PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson update(    Black black) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(black);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		blackService.update(black);//保存
		return AjaxJson.success("保存拉黑表成功");
	}


	/**
	 * 批量删除拉黑表
	 */
	@ApiLog("删除拉黑表")
//	@RequiresPermissions("black:black:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			blackService.delete(new Black(id));
		}
		return AjaxJson.success("删除拉黑表成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出拉黑表")
//	@RequiresPermissions("black:black:export")
    @GetMapping("export")
    public AjaxJson exportFile(Black black, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "拉黑表"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Black> page = blackService.findPage(new Page<Black>(request, response, -1), black);
    		new ExportExcel("拉黑表", Black.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出拉黑表记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入拉黑表")
//	@RequiresPermissions("black:black:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Black> list = ei.getDataList(Black.class);
			for (Black black : list){
				try{
					blackService.save(black);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条拉黑表记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条拉黑表记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入拉黑表失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入拉黑表数据模板
	 */
	@ApiLog("下载拉黑表模板")
//	@RequiresPermissions("black:black:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "拉黑表数据导入模板.xlsx";
    		List<Black> list = Lists.newArrayList();
    		new ExportExcel("拉黑表数据", Black.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}