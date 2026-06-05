package com.seekweb4.chat.modules.kejianzu.web;

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
import com.seekweb4.chat.modules.kejianzu.entity.KejianZu;
import com.seekweb4.chat.modules.kejianzu.service.KejianZuService;

/**
 * 课件分组Controller
 * @author lixinapp
 * @version 2025-05-24
 */
@RestController
@RequestMapping(value = "/kejianzu/kejianZu")
public class KejianZuController extends BaseController {

	@Autowired
	private KejianZuService kejianZuService;

	@ModelAttribute
	public KejianZu get(@RequestParam(required=false) String id) {
		KejianZu entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = kejianZuService.get(id);
		}
		if (entity == null){
			entity = new KejianZu();
		}
		return entity;
	}

	/**
	 * 课件分组列表数据
	 */
	@ApiLog("查询课件分组列表")
	@RequiresPermissions("kejianzu:kejianZu:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    KejianZu kejianZu, HttpServletRequest request, HttpServletResponse response) {
		Page<KejianZu> page = kejianZuService.findPage(new Page<KejianZu>(request, response), kejianZu);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取课件分组数据
	 */
	@ApiLog("查询课件分组")
	@RequiresPermissions(value={"kejianzu:kejianZu:view","kejianzu:kejianZu:add","kejianzu:kejianZu:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    KejianZu kejianZu) {
		if (StringUtils.isNotBlank(kejianZu.getId())) {
			KejianZu entity = kejianZuService.get(kejianZu.getId());
			return entity != null ? AjaxJson.success().put("kejianZu", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存课件分组
	 */
	@ApiLog("保存课件分组")
	@RequiresPermissions(value={"kejianzu:kejianZu:add","kejianzu:kejianZu:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    KejianZu kejianZu) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(kejianZu);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		kejianZuService.save(kejianZu);//保存
		return AjaxJson.success("保存课件分组成功");
	}


	/**
	 * 批量删除课件分组
	 */
	@ApiLog("删除课件分组")
	@RequiresPermissions("kejianzu:kejianZu:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			kejianZuService.delete(new KejianZu(id));
		}
		return AjaxJson.success("删除课件分组成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出课件分组")
	@RequiresPermissions("kejianzu:kejianZu:export")
    @GetMapping("export")
    public AjaxJson exportFile(KejianZu kejianZu, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "课件分组"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<KejianZu> page = kejianZuService.findPage(new Page<KejianZu>(request, response, -1), kejianZu);
    		new ExportExcel("课件分组", KejianZu.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出课件分组记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入课件分组")
	@RequiresPermissions("kejianzu:kejianZu:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<KejianZu> list = ei.getDataList(KejianZu.class);
			for (KejianZu kejianZu : list){
				try{
					kejianZuService.save(kejianZu);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条课件分组记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条课件分组记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入课件分组失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入课件分组数据模板
	 */
	@ApiLog("下载课件分组模板")
	@RequiresPermissions("kejianzu:kejianZu:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "课件分组数据导入模板.xlsx";
    		List<KejianZu> list = Lists.newArrayList();
    		new ExportExcel("课件分组数据", KejianZu.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}