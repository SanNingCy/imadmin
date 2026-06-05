package com.seekweb4.chat.modules.signset.web;

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
import com.seekweb4.chat.modules.signset.entity.SignSet;
import com.seekweb4.chat.modules.signset.service.SignSetService;

/**
 * 签到奖励配置Controller
 * @author lixinapp
 * @version 2024-11-26
 */
@RestController
@RequestMapping(value = "/signset/signSet")
public class SignSetController extends BaseController {

	@Autowired
	private SignSetService signSetService;

	@ModelAttribute
	public SignSet get(@RequestParam(required=false) String id) {
		SignSet entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = signSetService.get(id);
		}
		if (entity == null){
			entity = new SignSet();
		}
		return entity;
	}

	/**
	 * 签到奖励配置列表数据
	 */
	@ApiLog("查询签到奖励配置列表")
	@RequiresPermissions("signset:signSet:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    SignSet signSet, HttpServletRequest request, HttpServletResponse response) {
		Page<SignSet> page = signSetService.findPage(new Page<SignSet>(request, response), signSet);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取签到奖励配置数据
	 */
	@ApiLog("查询签到奖励配置")
	@RequiresPermissions(value={"signset:signSet:view","signset:signSet:add","signset:signSet:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    SignSet signSet) {
		if (StringUtils.isNotBlank(signSet.getId())) {
			SignSet entity = signSetService.get(signSet.getId());
			return entity != null ? AjaxJson.success().put("signSet", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存签到奖励配置
	 */
	@ApiLog("保存签到奖励配置")
	@RequiresPermissions(value={"signset:signSet:add","signset:signSet:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   SignSet signSet) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(signSet);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		signSetService.save(signSet);//保存
		return AjaxJson.success("保存签到奖励配置成功");
	}


	/**
	 * 批量删除签到奖励配置
	 */
	@ApiLog("删除签到奖励配置")
	@RequiresPermissions("signset:signSet:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			signSetService.delete(new SignSet(id));
		}
		return AjaxJson.success("删除签到奖励配置成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出签到奖励配置")
	@RequiresPermissions("signset:signSet:export")
    @GetMapping("export")
    public AjaxJson exportFile(SignSet signSet, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "签到奖励配置"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SignSet> page = signSetService.findPage(new Page<SignSet>(request, response, -1), signSet);
    		new ExportExcel("签到奖励配置", SignSet.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出签到奖励配置记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入签到奖励配置")
	@RequiresPermissions("signset:signSet:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SignSet> list = ei.getDataList(SignSet.class);
			for (SignSet signSet : list){
				try{
					signSetService.save(signSet);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条签到奖励配置记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条签到奖励配置记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入签到奖励配置失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入签到奖励配置数据模板
	 */
	@ApiLog("下载签到奖励配置模板")
	@RequiresPermissions("signset:signSet:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "签到奖励配置数据导入模板.xlsx";
    		List<SignSet> list = Lists.newArrayList();
    		new ExportExcel("签到奖励配置数据", SignSet.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}