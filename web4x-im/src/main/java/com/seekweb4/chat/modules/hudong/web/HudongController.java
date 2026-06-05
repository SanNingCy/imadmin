package com.seekweb4.chat.modules.hudong.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
import com.seekweb4.chat.modules.hudong.entity.Hudong;
import com.seekweb4.chat.modules.hudong.service.HudongService;

/**
 * 互动消息Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/hudong/hudong")
public class HudongController extends BaseController {

	@Autowired
	private HudongService hudongService;

	@ModelAttribute
	public Hudong get(@RequestParam(required=false) String id) {
		Hudong entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = hudongService.get(id);
		}
		if (entity == null){
			entity = new Hudong();
		}
		return entity;
	}

	/**
	 * 互动消息列表数据
	 */
	@ApiLog("查询互动消息列表")
	@RequiresPermissions("hudong:hudong:list")
	@GetMapping("list")
	public AjaxJson list(Hudong hudong, HttpServletRequest request, HttpServletResponse response) {
		Page<Hudong> page = hudongService.findPage(new Page<Hudong>(request, response), hudong);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取互动消息数据
	 */
	@ApiLog("查询互动消息")
	@RequiresPermissions(value={"hudong:hudong:view","hudong:hudong:add","hudong:hudong:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(Hudong hudong) {
		return AjaxJson.success().put("hudong", hudong);
	}

	/**
	 * 保存互动消息
	 */
	@ApiLog("保存互动消息")
	@RequiresPermissions(value={"hudong:hudong:add","hudong:hudong:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(Hudong hudong, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(hudong);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		hudongService.save(hudong);//保存
		return AjaxJson.success("保存互动消息成功");
	}


	/**
	 * 批量删除互动消息
	 */
	@ApiLog("删除互动消息")
	@RequiresPermissions("hudong:hudong:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			hudongService.delete(new Hudong(id));
		}
		return AjaxJson.success("删除互动消息成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出互动消息")
	@RequiresPermissions("hudong:hudong:export")
    @GetMapping("export")
    public AjaxJson exportFile(Hudong hudong, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "互动消息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Hudong> page = hudongService.findPage(new Page<Hudong>(request, response, -1), hudong);
    		new ExportExcel("互动消息", Hudong.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出互动消息记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入互动消息")
	@RequiresPermissions("hudong:hudong:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Hudong> list = ei.getDataList(Hudong.class);
			for (Hudong hudong : list){
				try{
					hudongService.save(hudong);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条互动消息记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条互动消息记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入互动消息失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入互动消息数据模板
	 */
	@ApiLog("下载互动消息模板")
	@RequiresPermissions("hudong:hudong:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "互动消息数据导入模板.xlsx";
    		List<Hudong> list = Lists.newArrayList();
    		new ExportExcel("互动消息数据", Hudong.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}