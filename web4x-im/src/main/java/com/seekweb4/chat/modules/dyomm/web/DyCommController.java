package com.seekweb4.chat.modules.dyomm.web;

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
import com.seekweb4.chat.modules.dyomm.entity.DyComm;
import com.seekweb4.chat.modules.dyomm.service.DyCommService;

/**
 * 动态评论Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/dyomm/dyComm")
public class DyCommController extends BaseController {

	@Autowired
	private DyCommService dyCommService;

	@ModelAttribute
	public DyComm get(@RequestParam(required=false) String id) {
		DyComm entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dyCommService.get(id);
		}
		if (entity == null){
			entity = new DyComm();
		}
		return entity;
	}

	/**
	 * 动态评论列表数据
	 */
	@ApiLog("查询动态评论列表")
//	@RequiresPermissions("dyomm:dyComm:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    DyComm dyComm, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(dyComm);
		Page<DyComm> page = dyCommService.findPage(new Page<DyComm>(request, response), dyComm);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取动态评论数据
	 */
	@ApiLog("查询动态评论")
	@RequiresPermissions(value={"social:content:moment-comments:view","social:content:moment-comments:add","social:content:moment-comments:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"dyomm:dyComm:view","dyomm:dyComm:add","dyomm:dyComm:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    DyComm dyComm) {
		if (StringUtils.isNotBlank(dyComm.getId())) {
			DyComm entity = dyCommService.get(dyComm.getId());
			return entity != null ? AjaxJson.success().put("dyComm", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存动态评论
	 */
	@ApiLog("保存动态评论")
//	@RequiresPermissions(value={"dyomm:dyComm:add","dyomm:dyComm:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(  @RequestBody  DyComm dyComm) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(dyComm);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		dyCommService.save(dyComm);//保存
		return AjaxJson.success("保存动态评论成功");
	}

	/**
	 * 修改动态评论
	 */
	@ApiLog("修改动态评论")
//	@RequiresPermissions(value={"dyomm:dyComm:add","dyomm:dyComm:edit"},logical=Logical.OR)
	@PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson update(    DyComm dyComm) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(dyComm);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		dyCommService.update(dyComm);//保存
		return AjaxJson.success("保存动态评论成功");
	}


	/**
	 * 批量删除动态评论
	 */
	@ApiLog("删除动态评论")
	@RequiresPermissions("social:content:moment-comments:delete")
//	@RequiresPermissions("dyomm:dyComm:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			dyCommService.delete(new DyComm(id));
		}
		return AjaxJson.success("删除动态评论成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出动态评论")
//	@RequiresPermissions("dyomm:dyComm:export")
    @GetMapping("export")
    public AjaxJson exportFile(DyComm dyComm, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "动态评论"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<DyComm> page = dyCommService.findPage(new Page<DyComm>(request, response, -1), dyComm);
    		new ExportExcel("动态评论", DyComm.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出动态评论记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入动态评论")
//	@RequiresPermissions("dyomm:dyComm:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<DyComm> list = ei.getDataList(DyComm.class);
			for (DyComm dyComm : list){
				try{
					dyCommService.save(dyComm);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条动态评论记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条动态评论记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入动态评论失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入动态评论数据模板
	 */
	@ApiLog("下载动态评论模板")
//	@RequiresPermissions("dyomm:dyComm:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "动态评论数据导入模板.xlsx";
    		List<DyComm> list = Lists.newArrayList();
    		new ExportExcel("动态评论数据", DyComm.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}