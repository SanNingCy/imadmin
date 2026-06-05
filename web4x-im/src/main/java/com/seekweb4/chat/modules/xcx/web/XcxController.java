package com.seekweb4.chat.modules.xcx.web;

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
import com.seekweb4.chat.modules.xcx.entity.Xcx;
import com.seekweb4.chat.modules.xcx.service.XcxService;

/**
 * 小程序链接管理Controller
 * @author lixinapp
 * @version 2024-09-22
 */
@RestController
@RequestMapping(value = "/xcx/xcx")
public class XcxController extends BaseController {

	@Autowired
	private XcxService xcxService;

	@ModelAttribute
	public Xcx get(@RequestParam(required=false) String id) {
		Xcx entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = xcxService.get(id);
		}
		if (entity == null){
			entity = new Xcx();
		}
		return entity;
	}

	/**
	 * 小程序链接管理列表数据
	 */
	@ApiLog("查询小程序链接管理列表")
	@RequiresPermissions("xcx:xcx:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Xcx xcx, HttpServletRequest request, HttpServletResponse response) {
		Page<Xcx> page = xcxService.findPage(new Page<Xcx>(request, response), xcx);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取小程序链接管理数据
	 */
	@ApiLog("查询小程序链接管理")
	@RequiresPermissions(value={"xcx:xcx:view","xcx:xcx:add","xcx:xcx:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Xcx xcx) {
		if (StringUtils.isNotBlank(xcx.getId())) {
			Xcx entity = xcxService.get(xcx.getId());
			return entity != null ? AjaxJson.success().put("xcx", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存小程序链接管理
	 */
	@ApiLog("保存小程序链接管理")
	@RequiresPermissions(value={"xcx:xcx:add","xcx:xcx:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    Xcx xcx) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(xcx);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		xcxService.save(xcx);//保存
		return AjaxJson.success("保存小程序链接管理成功");
	}


	/**
	 * 批量删除小程序链接管理
	 */
	@ApiLog("删除小程序链接管理")
	@RequiresPermissions("xcx:xcx:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			xcxService.delete(new Xcx(id));
		}
		return AjaxJson.success("删除小程序链接管理成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出小程序链接管理")
	@RequiresPermissions("xcx:xcx:export")
    @GetMapping("export")
    public AjaxJson exportFile(Xcx xcx, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "小程序链接管理"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Xcx> page = xcxService.findPage(new Page<Xcx>(request, response, -1), xcx);
    		new ExportExcel("小程序链接管理", Xcx.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出小程序链接管理记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入小程序链接管理")
	@RequiresPermissions("xcx:xcx:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Xcx> list = ei.getDataList(Xcx.class);
			for (Xcx xcx : list){
				try{
					xcxService.save(xcx);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条小程序链接管理记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条小程序链接管理记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入小程序链接管理失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入小程序链接管理数据模板
	 */
	@ApiLog("下载小程序链接管理模板")
	@RequiresPermissions("xcx:xcx:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "小程序链接管理数据导入模板.xlsx";
    		List<Xcx> list = Lists.newArrayList();
    		new ExportExcel("小程序链接管理数据", Xcx.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}