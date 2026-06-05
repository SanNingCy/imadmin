package com.seekweb4.chat.modules.zhuangzhang.web;

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
import com.seekweb4.chat.modules.zhuangzhang.entity.Zhuangzhang;
import com.seekweb4.chat.modules.zhuangzhang.service.ZhuangzhangService;

/**
 * 转账记录Controller
 * @author lixinapp
 * @version 2024-09-22
 */
@RestController
@RequestMapping(value = "/zhuangzhang/zhuangzhang")
public class ZhuangzhangController extends BaseController {

	@Autowired
	private ZhuangzhangService zhuangzhangService;

	@ModelAttribute
	public Zhuangzhang get(@RequestParam(required=false) String id) {
		Zhuangzhang entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = zhuangzhangService.get(id);
		}
		if (entity == null){
			entity = new Zhuangzhang();
		}
		return entity;
	}

	/**
	 * 转账记录列表数据
	 */
	@ApiLog("查询转账记录列表")
//	@RequiresPermissions("zhuangzhang:zhuangzhang:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Zhuangzhang zhuangzhang, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(zhuangzhang);
		Page<Zhuangzhang> page = zhuangzhangService.findPage(new Page<Zhuangzhang>(request, response), zhuangzhang);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取转账记录数据
	 */
	@ApiLog("查询转账记录")
//	@RequiresPermissions(value={"zhuangzhang:zhuangzhang:view","zhuangzhang:zhuangzhang:add","zhuangzhang:zhuangzhang:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"asset:trade:transfer:view"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Zhuangzhang zhuangzhang) {
		if (StringUtils.isNotBlank(zhuangzhang.getId())) {
			Zhuangzhang entity = zhuangzhangService.get(zhuangzhang.getId());
			return entity != null ? AjaxJson.success().put("zhuangzhang", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存转账记录
	 */
	@ApiLog("保存转账记录")
//	@RequiresPermissions(value={"zhuangzhang:zhuangzhang:add","zhuangzhang:zhuangzhang:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Zhuangzhang zhuangzhang) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(zhuangzhang);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		zhuangzhangService.save(zhuangzhang);//保存
		return AjaxJson.success("保存转账记录成功");
	}


	/**
	 * 批量删除转账记录
	 */
	@ApiLog("删除转账记录")
//	@RequiresPermissions("zhuangzhang:zhuangzhang:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			zhuangzhangService.delete(new Zhuangzhang(id));
		}
		return AjaxJson.success("删除转账记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出转账记录")
//	@RequiresPermissions("zhuangzhang:zhuangzhang:export")
    @GetMapping("export")
    public AjaxJson exportFile(Zhuangzhang zhuangzhang, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "转账记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Zhuangzhang> page = zhuangzhangService.findPage(new Page<Zhuangzhang>(request, response, -1), zhuangzhang);
    		new ExportExcel("转账记录", Zhuangzhang.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出转账记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入转账记录")
//	@RequiresPermissions("zhuangzhang:zhuangzhang:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Zhuangzhang> list = ei.getDataList(Zhuangzhang.class);
			for (Zhuangzhang zhuangzhang : list){
				try{
					zhuangzhangService.save(zhuangzhang);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条转账记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条转账记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入转账记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入转账记录数据模板
	 */
	@ApiLog("下载转账记录模板")
//	@RequiresPermissions("zhuangzhang:zhuangzhang:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "转账记录数据导入模板.xlsx";
    		List<Zhuangzhang> list = Lists.newArrayList();
    		new ExportExcel("转账记录数据", Zhuangzhang.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}