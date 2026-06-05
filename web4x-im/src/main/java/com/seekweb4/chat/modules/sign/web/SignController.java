package com.seekweb4.chat.modules.sign.web;

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
import com.seekweb4.chat.modules.sign.entity.Sign;
import com.seekweb4.chat.modules.sign.service.SignService;

/**
 * 签到奖励配置Controller
 * @author lixinapp
 * @version 2024-09-22
 */
@RestController
@RequestMapping(value = "/sign/sign")
public class SignController extends BaseController {

	@Autowired
	private SignService signService;

	@ModelAttribute
	public Sign get(@RequestParam(required=false) String id) {
		Sign entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = signService.get(id);
		}
		if (entity == null){
			entity = new Sign();
		}
		return entity;
	}

	/**
	 * 签到奖励配置列表数据
	 */
	@ApiLog("查询签到奖励配置列表")
//	@RequiresPermissions("sign:sign:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Sign sign, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(sign);
		Page<Sign> page = signService.findPage(new Page<Sign>(request, response), sign);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取签到奖励配置数据
	 */
	@ApiLog("查询签到奖励配置")
//	@RequiresPermissions(value={"sign:sign:view","sign:sign:add","sign:sign:edit"},logical=Logical.OR)
	@RequiresPermissions(value={"asset:rate:signin-reward:view","asset:rate:signin-reward:add","asset:rate:signin-reward:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Sign sign) {
		if (StringUtils.isNotBlank(sign.getId())) {
			Sign entity = signService.get(sign.getId());
			return entity != null ? AjaxJson.success().put("sign", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存签到奖励配置
	 */
	@ApiLog("保存签到奖励配置")
//	@RequiresPermissions(value={"sign:sign:add","sign:sign:edit"},logical=Logical.OR)
	@RequiresPermissions(value={"asset:rate:signin-reward:add","asset:rate:signin-reward:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Sign sign) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(sign);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		signService.save(sign);//保存
		return AjaxJson.success("保存签到奖励配置成功");
	}


	/**
	 * 批量删除签到奖励配置
	 */
	@ApiLog("删除签到奖励配置")
//	@RequiresPermissions("sign:sign:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			signService.delete(new Sign(id));
		}
		return AjaxJson.success("删除签到奖励配置成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出签到奖励配置")
//	@RequiresPermissions("sign:sign:export")
    @GetMapping("export")
    public AjaxJson exportFile(Sign sign, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "签到奖励配置"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Sign> page = signService.findPage(new Page<Sign>(request, response, -1), sign);
    		new ExportExcel("签到奖励配置", Sign.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出签到奖励配置记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入签到奖励配置")
//	@RequiresPermissions("sign:sign:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Sign> list = ei.getDataList(Sign.class);
			for (Sign sign : list){
				try{
					signService.save(sign);
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
//	@RequiresPermissions("sign:sign:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "签到奖励配置数据导入模板.xlsx";
    		List<Sign> list = Lists.newArrayList();
    		new ExportExcel("签到奖励配置数据", Sign.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}