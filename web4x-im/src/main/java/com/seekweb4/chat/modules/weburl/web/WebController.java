package com.seekweb4.chat.modules.weburl.web;

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
import com.seekweb4.chat.modules.weburl.entity.Web;
import com.seekweb4.chat.modules.weburl.service.WebService;

/**
 * 发现页外链Controller
 * @author lixinapp
 * @version 2024-09-22
 */
@RestController
@RequestMapping(value = "/weburl/web")
public class WebController extends BaseController {

	@Autowired
	private WebService webService;

	@ModelAttribute
	public Web get(@RequestParam(required=false) String id) {
		Web entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = webService.get(id);
		}
		if (entity == null){
			entity = new Web();
		}
		return entity;
	}

	/**
	 * 发现页外链列表数据
	 */
	@ApiLog("查询发现页外链列表")
	@RequiresPermissions("weburl:web:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Web web, HttpServletRequest request, HttpServletResponse response) {
		Page<Web> page = webService.findPage(new Page<Web>(request, response), web);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取发现页外链数据
	 */
	@ApiLog("查询发现页外链")
	@RequiresPermissions(value={"weburl:web:view","weburl:web:add","weburl:web:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Web web) {
		if (StringUtils.isNotBlank(web.getId())) {
			Web entity = webService.get(web.getId());
			return entity != null ? AjaxJson.success().put("web", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存发现页外链
	 */
	@ApiLog("保存发现页外链")
	@RequiresPermissions(value={"weburl:web:add","weburl:web:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    Web web) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(web);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		webService.save(web);//保存
		return AjaxJson.success("保存发现页外链成功");
	}


	/**
	 * 批量删除发现页外链
	 */
	@ApiLog("删除发现页外链")
	@RequiresPermissions("weburl:web:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			webService.delete(new Web(id));
		}
		return AjaxJson.success("删除发现页外链成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出发现页外链")
	@RequiresPermissions("weburl:web:export")
    @GetMapping("export")
    public AjaxJson exportFile(Web web, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "发现页外链"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Web> page = webService.findPage(new Page<Web>(request, response, -1), web);
    		new ExportExcel("发现页外链", Web.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出发现页外链记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入发现页外链")
	@RequiresPermissions("weburl:web:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Web> list = ei.getDataList(Web.class);
			for (Web web : list){
				try{
					webService.save(web);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条发现页外链记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条发现页外链记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入发现页外链失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入发现页外链数据模板
	 */
	@ApiLog("下载发现页外链模板")
	@RequiresPermissions("weburl:web:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "发现页外链数据导入模板.xlsx";
    		List<Web> list = Lists.newArrayList();
    		new ExportExcel("发现页外链数据", Web.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}