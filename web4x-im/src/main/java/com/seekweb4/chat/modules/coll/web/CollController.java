package com.seekweb4.chat.modules.coll.web;

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
import com.seekweb4.chat.modules.coll.entity.Coll;
import com.seekweb4.chat.modules.coll.service.CollService;

/**
 * 收藏记录Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/coll/coll")
public class CollController extends BaseController {

	@Autowired
	private CollService collService;

	@ModelAttribute
	public Coll get(@RequestParam(required=false) String id) {
		Coll entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = collService.get(id);
		}
		if (entity == null){
			entity = new Coll();
		}
		return entity;
	}

	/**
	 * 收藏记录列表数据
	 */
	@ApiLog("查询收藏记录列表")
	@RequiresPermissions("coll:coll:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Coll coll, HttpServletRequest request, HttpServletResponse response) {
		Page<Coll> page = collService.findPage(new Page<Coll>(request, response), coll);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取收藏记录数据
	 */
	@ApiLog("查询收藏记录")
	@RequiresPermissions(value={"coll:coll:view","coll:coll:add","coll:coll:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Coll coll) {
		if (StringUtils.isNotBlank(coll.getId())) {
			Coll entity = collService.get(coll.getId());
			return entity != null ? AjaxJson.success().put("coll", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存收藏记录
	 */
	@ApiLog("保存收藏记录")
	@RequiresPermissions(value={"coll:coll:add","coll:coll:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Coll coll) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(coll);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		collService.save(coll);//保存
		return AjaxJson.success("保存收藏记录成功");
	}

	/**
	 * 修改收藏记录
	 */
	@ApiLog("修改收藏记录")
	@RequiresPermissions(value={"coll:coll:add","coll:coll:edit"},logical=Logical.OR)
	@PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson update(    Coll coll) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(coll);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		collService.update(coll);//保存
		return AjaxJson.success("保存收藏记录成功");
	}


	/**
	 * 批量删除收藏记录
	 */
	@ApiLog("删除收藏记录")
	@RequiresPermissions("coll:coll:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			collService.delete(new Coll(id));
		}
		return AjaxJson.success("删除收藏记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出收藏记录")
	@RequiresPermissions("coll:coll:export")
    @GetMapping("export")
    public AjaxJson exportFile(Coll coll, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "收藏记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Coll> page = collService.findPage(new Page<Coll>(request, response, -1), coll);
    		new ExportExcel("收藏记录", Coll.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出收藏记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入收藏记录")
	@RequiresPermissions("coll:coll:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Coll> list = ei.getDataList(Coll.class);
			for (Coll coll : list){
				try{
					collService.save(coll);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条收藏记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条收藏记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入收藏记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入收藏记录数据模板
	 */
	@ApiLog("下载收藏记录模板")
	@RequiresPermissions("coll:coll:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "收藏记录数据导入模板.xlsx";
    		List<Coll> list = Lists.newArrayList();
    		new ExportExcel("收藏记录数据", Coll.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}