package com.seekweb4.chat.modules.kejianzugroup.web;

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
import com.seekweb4.chat.modules.kejianzugroup.entity.KejianZuGroup;
import com.seekweb4.chat.modules.kejianzugroup.service.KejianZuGroupService;

/**
 * 课件分组群组关联Controller
 * @author lixinapp
 * @version 2025-05-24
 */
@RestController
@RequestMapping(value = "/kejianzugroup/kejianZuGroup")
public class KejianZuGroupController extends BaseController {

	@Autowired
	private KejianZuGroupService kejianZuGroupService;

	@ModelAttribute
	public KejianZuGroup get(@RequestParam(required=false) String id) {
		KejianZuGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = kejianZuGroupService.get(id);
		}
		if (entity == null){
			entity = new KejianZuGroup();
		}
		return entity;
	}

	/**
	 * 课件分组群组关联列表数据
	 */
	@ApiLog("查询课件分组群组关联列表")
	@RequiresPermissions("kejianzugroup:kejianZuGroup:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    KejianZuGroup kejianZuGroup, HttpServletRequest request, HttpServletResponse response) {
		Page<KejianZuGroup> page = kejianZuGroupService.findPage(new Page<KejianZuGroup>(request, response), kejianZuGroup);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取课件分组群组关联数据
	 */
	@ApiLog("查询课件分组群组关联")
	@RequiresPermissions(value={"kejianzugroup:kejianZuGroup:view","kejianzugroup:kejianZuGroup:add","kejianzugroup:kejianZuGroup:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    KejianZuGroup kejianZuGroup) {
		if (StringUtils.isNotBlank(kejianZuGroup.getId())) {
			KejianZuGroup entity = kejianZuGroupService.get(kejianZuGroup.getId());
			return entity != null ? AjaxJson.success().put("kejianZuGroup", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存课件分组群组关联
	 */
	@ApiLog("保存课件分组群组关联")
	@RequiresPermissions(value={"kejianzugroup:kejianZuGroup:add","kejianzugroup:kejianZuGroup:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    KejianZuGroup kejianZuGroup) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(kejianZuGroup);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		kejianZuGroupService.save(kejianZuGroup);//保存
		return AjaxJson.success("保存课件分组群组关联成功");
	}


	/**
	 * 批量删除课件分组群组关联
	 */
	@ApiLog("删除课件分组群组关联")
	@RequiresPermissions("kejianzugroup:kejianZuGroup:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			kejianZuGroupService.delete(new KejianZuGroup(id));
		}
		return AjaxJson.success("删除课件分组群组关联成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出课件分组群组关联")
	@RequiresPermissions("kejianzugroup:kejianZuGroup:export")
    @GetMapping("export")
    public AjaxJson exportFile(KejianZuGroup kejianZuGroup, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "课件分组群组关联"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<KejianZuGroup> page = kejianZuGroupService.findPage(new Page<KejianZuGroup>(request, response, -1), kejianZuGroup);
    		new ExportExcel("课件分组群组关联", KejianZuGroup.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出课件分组群组关联记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入课件分组群组关联")
	@RequiresPermissions("kejianzugroup:kejianZuGroup:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<KejianZuGroup> list = ei.getDataList(KejianZuGroup.class);
			for (KejianZuGroup kejianZuGroup : list){
				try{
					kejianZuGroupService.save(kejianZuGroup);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条课件分组群组关联记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条课件分组群组关联记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入课件分组群组关联失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入课件分组群组关联数据模板
	 */
	@ApiLog("下载课件分组群组关联模板")
	@RequiresPermissions("kejianzugroup:kejianZuGroup:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "课件分组群组关联数据导入模板.xlsx";
    		List<KejianZuGroup> list = Lists.newArrayList();
    		new ExportExcel("课件分组群组关联数据", KejianZuGroup.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}