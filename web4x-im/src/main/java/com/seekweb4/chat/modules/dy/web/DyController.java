package com.seekweb4.chat.modules.dy.web;

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
import com.seekweb4.chat.modules.dy.entity.Dy;
import com.seekweb4.chat.modules.dy.service.DyService;

/**
 * 朋友圈动态Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/dy/dy")
public class DyController extends BaseController {

	@Autowired
	private DyService dyService;

	@ModelAttribute
	public Dy get(@RequestParam(required=false) String id) {
		Dy entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dyService.get(id);
		}
		if (entity == null){
			entity = new Dy();
		}
		return entity;
	}

	/**
	 * 朋友圈动态列表数据
	 */
	@ApiLog("查询朋友圈动态列表")
//	@RequiresPermissions("dy:dy:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Dy dy, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(dy);
		Page<Dy> page = dyService.findPage(new Page<Dy>(request, response), dy);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取朋友圈动态数据
	 */
	@ApiLog("查询朋友圈动态")
	@RequiresPermissions(value={"social:content:moments:view","social:content:moments:add","social:content:moments:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"dy:dy:view","dy:dy:add","dy:dy:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Dy dy) {
		if (StringUtils.isNotBlank(dy.getId())) {
			Dy entity = dyService.get(dy.getId());
			return entity != null ? AjaxJson.success().put("dy", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存朋友圈动态
	 */
	@ApiLog("保存朋友圈动态")
//	@RequiresPermissions(value={"dy:dy:add","dy:dy:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Dy dy) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(dy);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		dyService.save(dy);//保存
		return AjaxJson.success("保存朋友圈动态成功");
	}

	/**
	 * 修改朋友圈动态
	 */
	@ApiLog("修改朋友圈动态")
//	@RequiresPermissions(value={"dy:dy:add","dy:dy:edit"},logical=Logical.OR)
	@PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson update(    Dy dy) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(dy);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		dyService.update(dy);//保存
		return AjaxJson.success("修改朋友圈动态成功");
	}


	/**
	 * 批量删除朋友圈动态
	 */
	@ApiLog("删除朋友圈动态")
	@RequiresPermissions("social:content:moments:delete")
//	@RequiresPermissions("dy:dy:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			dyService.delete(new Dy(id));
		}
		return AjaxJson.success("删除朋友圈动态成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出朋友圈动态")
//	@RequiresPermissions("dy:dy:export")
    @GetMapping("export")
    public AjaxJson exportFile(Dy dy, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "朋友圈动态"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Dy> page = dyService.findPage(new Page<Dy>(request, response, -1), dy);
    		new ExportExcel("朋友圈动态", Dy.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出朋友圈动态记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入朋友圈动态")
//	@RequiresPermissions("dy:dy:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Dy> list = ei.getDataList(Dy.class);
			for (Dy dy : list){
				try{
					dyService.save(dy);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条朋友圈动态记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条朋友圈动态记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入朋友圈动态失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入朋友圈动态数据模板
	 */
	@ApiLog("下载朋友圈动态模板")
//	@RequiresPermissions("dy:dy:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "朋友圈动态数据导入模板.xlsx";
    		List<Dy> list = Lists.newArrayList();
    		new ExportExcel("朋友圈动态数据", Dy.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}