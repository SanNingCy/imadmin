package com.seekweb4.chat.modules.upgrade.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.common.annotation.ApiLog;
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

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.common.utils.excel.ExportExcel;
import com.seekweb4.chat.common.utils.excel.ImportExcel;
import com.seekweb4.chat.modules.upgrade.entity.Upgrade;
import com.seekweb4.chat.modules.upgrade.service.UpgradeService;

/**
 * 版本更新Controller
 * @author lixinapp
 * @version 2022-12-19
 */
@RestController
@RequestMapping(value = "/upgrade/upgrade")
public class UpgradeController extends BaseController {

	@Autowired
	private UpgradeService upgradeService;

	@ModelAttribute
	public Upgrade get(@RequestParam(required=false) String id) {
		Upgrade entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = upgradeService.get(id);
		}
		if (entity == null){
			entity = new Upgrade();
		}
		return entity;
	}

	/**
	 * 版本更新列表数据
	 */
	@ApiLog("查询版本更新列表")
//	@RequiresPermissions("upgrade:upgrade:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Upgrade upgrade, HttpServletRequest request, HttpServletResponse response) {
		Page<Upgrade> page = upgradeService.findPage(new Page<Upgrade>(request, response), upgrade);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取版本更新数据
	 */
	@ApiLog("查询版本更新")
	@RequiresPermissions(value={"ops:content:version:view","ops:content:version:add","ops:content:version:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Upgrade upgrade) {
		if (StringUtils.isNotBlank(upgrade.getId())) {
			Upgrade entity = upgradeService.get(upgrade.getId());
			return entity != null ? AjaxJson.success().put("upgrade", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存版本更新
	 */
	@ApiLog("保存版本更新")
	@RequiresPermissions(value={"ops:content:version:add","ops:content:version:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody Upgrade upgrade) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(upgrade);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		upgradeService.save(upgrade);//保存
		return AjaxJson.success("保存版本更新成功");
	}


	/**
	 * 批量删除版本更新
	 */
	@ApiLog("删除版本更新")
	@RequiresPermissions("ops:content:version:delete")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			upgradeService.delete(new Upgrade(id));
		}
		return AjaxJson.success("删除版本更新成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出版本更新")
	@RequiresPermissions("upgrade:upgrade:export")
    @GetMapping("export")
    public AjaxJson exportFile(Upgrade upgrade, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "版本更新"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Upgrade> page = upgradeService.findPage(new Page<Upgrade>(request, response, -1), upgrade);
    		new ExportExcel("版本更新", Upgrade.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出版本更新记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	@ApiLog("导入版本更新")
	@RequiresPermissions("upgrade:upgrade:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Upgrade> list = ei.getDataList(Upgrade.class);
			for (Upgrade upgrade : list){
				try{
					upgradeService.save(upgrade);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条版本更新记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条版本更新记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入版本更新失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入版本更新数据模板
	 */
	@ApiLog("下载版本更新模板")
	@RequiresPermissions("upgrade:upgrade:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "版本更新数据导入模板.xlsx";
    		List<Upgrade> list = Lists.newArrayList();
    		new ExportExcel("版本更新数据", Upgrade.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}