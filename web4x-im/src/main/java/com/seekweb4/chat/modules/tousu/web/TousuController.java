package com.seekweb4.chat.modules.tousu.web;

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
import com.seekweb4.chat.modules.tousu.entity.Tousu;
import com.seekweb4.chat.modules.tousu.service.TousuService;

/**
 * 投诉群组记录Controller
 * @author lixinapp
 * @version 2024-09-22
 */
@RestController
@RequestMapping(value = "/tousu/tousu")
public class TousuController extends BaseController {

	@Autowired
	private TousuService tousuService;

	@ModelAttribute
	public Tousu get(@RequestParam(required=false) String id) {
		Tousu entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tousuService.get(id);
		}
		if (entity == null){
			entity = new Tousu();
		}
		return entity;
	}

	/**
	 * 投诉群组记录列表数据
	 */
	@ApiLog("查询投诉群组记录列表")
//	@RequiresPermissions("tousu:tousu:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Tousu tousu, HttpServletRequest request, HttpServletResponse response) {
		Page<Tousu> page = tousuService.findPage(new Page<Tousu>(request, response), tousu);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取投诉群组记录数据
	 */
	@ApiLog("查询投诉群组记录")
//	@RequiresPermissions(value={"group:manage:complaints:view","group:manage:complaints:add","group:manage:complaints:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"tousu:tousu:view","tousu:tousu:add","tousu:tousu:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Tousu tousu) {
		if (StringUtils.isNotBlank(tousu.getId())) {
			Tousu entity = tousuService.get(tousu.getId());
			return entity != null ? AjaxJson.success().put("tousu", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存投诉群组记录
	 */
	@ApiLog("保存投诉群组记录")
//	@RequiresPermissions(value={"tousu:tousu:add","tousu:tousu:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Tousu tousu) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(tousu);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		tousuService.save(tousu);//保存
		return AjaxJson.success("保存投诉群组记录成功");
	}


	/**
	 * 批量删除投诉群组记录
	 */
	@ApiLog("删除投诉群组记录")
//	@RequiresPermissions("tousu:tousu:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			tousuService.delete(new Tousu(id));
		}
		return AjaxJson.success("删除投诉群组记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出投诉群组记录")
//	@RequiresPermissions("tousu:tousu:export")
    @GetMapping("export")
    public AjaxJson exportFile(Tousu tousu, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "投诉群组记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Tousu> page = tousuService.findPage(new Page<Tousu>(request, response, -1), tousu);
    		new ExportExcel("投诉群组记录", Tousu.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出投诉群组记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入投诉群组记录")
//	@RequiresPermissions("tousu:tousu:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Tousu> list = ei.getDataList(Tousu.class);
			for (Tousu tousu : list){
				try{
					tousuService.save(tousu);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条投诉群组记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条投诉群组记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入投诉群组记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入投诉群组记录数据模板
	 */
	@ApiLog("下载投诉群组记录模板")
//	@RequiresPermissions("tousu:tousu:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "投诉群组记录数据导入模板.xlsx";
    		List<Tousu> list = Lists.newArrayList();
    		new ExportExcel("投诉群组记录数据", Tousu.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}