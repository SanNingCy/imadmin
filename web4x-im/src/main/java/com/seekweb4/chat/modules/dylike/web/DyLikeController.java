package com.seekweb4.chat.modules.dylike.web;

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
import com.seekweb4.chat.modules.dylike.entity.DyLike;
import com.seekweb4.chat.modules.dylike.service.DyLikeService;

/**
 * 动态点赞Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/dylike/dyLike")
public class DyLikeController extends BaseController {

	@Autowired
	private DyLikeService dyLikeService;

	@ModelAttribute
	public DyLike get(@RequestParam(required=false) String id) {
		DyLike entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dyLikeService.get(id);
		}
		if (entity == null){
			entity = new DyLike();
		}
		return entity;
	}

	/**
	 * 动态点赞列表数据
	 */
	@ApiLog("查询动态点赞列表")
	@RequiresPermissions("dylike:dyLike:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    DyLike dyLike, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(dyLike);
		Page<DyLike> page = dyLikeService.findPage(new Page<DyLike>(request, response), dyLike);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取动态点赞数据
	 */
	@ApiLog("查询动态点赞")
	@RequiresPermissions(value={"dylike:dyLike:view","dylike:dyLike:add","dylike:dyLike:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    DyLike dyLike) {
		if (StringUtils.isNotBlank(dyLike.getId())) {
			DyLike entity = dyLikeService.get(dyLike.getId());
			return entity != null ? AjaxJson.success().put("dyLike", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存动态点赞
	 */
	@ApiLog("保存动态点赞")
	@RequiresPermissions(value={"dylike:dyLike:add","dylike:dyLike:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(  @RequestBody  DyLike dyLike) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(dyLike);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		dyLikeService.save(dyLike);//保存
		return AjaxJson.success("保存动态点赞成功");
	}

	/**
	 * 修改动态点赞
	 */
	@ApiLog("修改动态点赞")
	@RequiresPermissions(value={"dylike:dyLike:add","dylike:dyLike:edit"},logical=Logical.OR)
	@PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson update(    DyLike dyLike) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(dyLike);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		dyLikeService.update(dyLike);//保存
		return AjaxJson.success("保存动态点赞成功");
	}


	/**
	 * 批量删除动态点赞
	 */
	@ApiLog("删除动态点赞")
	@RequiresPermissions("dylike:dyLike:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			dyLikeService.delete(new DyLike(id));
		}
		return AjaxJson.success("删除动态点赞成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出动态点赞")
	@RequiresPermissions("dylike:dyLike:export")
    @GetMapping("export")
    public AjaxJson exportFile(DyLike dyLike, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "动态点赞"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<DyLike> page = dyLikeService.findPage(new Page<DyLike>(request, response, -1), dyLike);
    		new ExportExcel("动态点赞", DyLike.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出动态点赞记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入动态点赞")
	@RequiresPermissions("dylike:dyLike:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<DyLike> list = ei.getDataList(DyLike.class);
			for (DyLike dyLike : list){
				try{
					dyLikeService.save(dyLike);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条动态点赞记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条动态点赞记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入动态点赞失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入动态点赞数据模板
	 */
	@ApiLog("下载动态点赞模板")
	@RequiresPermissions("dylike:dyLike:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "动态点赞数据导入模板.xlsx";
    		List<DyLike> list = Lists.newArrayList();
    		new ExportExcel("动态点赞数据", DyLike.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}