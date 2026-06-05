package com.seekweb4.chat.modules.grouphuanying.web;

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
import com.seekweb4.chat.modules.grouphuanying.entity.GroupHuanying;
import com.seekweb4.chat.modules.grouphuanying.service.GroupHuanyingService;

/**
 * 群欢迎语Controller
 * @author lixinapp
 * @version 2025-03-24
 */
@RestController
@RequestMapping(value = "/grouphuanying/groupHuanying")
public class GroupHuanyingController extends BaseController {

	@Autowired
	private GroupHuanyingService groupHuanyingService;

	@ModelAttribute
	public GroupHuanying get(@RequestParam(required=false) String id) {
		GroupHuanying entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = groupHuanyingService.get(id);
		}
		if (entity == null){
			entity = new GroupHuanying();
		}
		return entity;
	}

	/**
	 * 群欢迎语列表数据
	 */
	@ApiLog("查询群欢迎语列表")
//	@RequiresPermissions("grouphuanying:groupHuanying:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    GroupHuanying groupHuanying, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(groupHuanying);
		Page<GroupHuanying> page = groupHuanyingService.findPage(new Page<GroupHuanying>(request, response), groupHuanying);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取群欢迎语数据
	 */
	@ApiLog("查询群欢迎语")
//	@RequiresPermissions(value={"grouphuanying:groupHuanying:view","grouphuanying:groupHuanying:add","grouphuanying:groupHuanying:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    GroupHuanying groupHuanying) {
		if (StringUtils.isNotBlank(groupHuanying.getId())) {
			GroupHuanying entity = groupHuanyingService.get(groupHuanying.getId());
			return entity != null ? AjaxJson.success().put("groupHuanying", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存群欢迎语
	 */
	@ApiLog("保存群欢迎语")
//	@RequiresPermissions(value={"grouphuanying:groupHuanying:add","grouphuanying:groupHuanying:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   GroupHuanying groupHuanying) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(groupHuanying);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		groupHuanyingService.save(groupHuanying);//保存
		return AjaxJson.success("保存群欢迎语成功");
	}


	/**
	 * 批量删除群欢迎语
	 */
	@ApiLog("删除群欢迎语")
//	@RequiresPermissions("grouphuanying:groupHuanying:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			groupHuanyingService.delete(new GroupHuanying(id));
		}
		return AjaxJson.success("删除群欢迎语成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出群欢迎语")
//	@RequiresPermissions("grouphuanying:groupHuanying:export")
    @GetMapping("export")
    public AjaxJson exportFile(GroupHuanying groupHuanying, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "群欢迎语"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<GroupHuanying> page = groupHuanyingService.findPage(new Page<GroupHuanying>(request, response, -1), groupHuanying);
    		new ExportExcel("群欢迎语", GroupHuanying.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出群欢迎语记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入群欢迎语")
//	@RequiresPermissions("grouphuanying:groupHuanying:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<GroupHuanying> list = ei.getDataList(GroupHuanying.class);
			for (GroupHuanying groupHuanying : list){
				try{
					groupHuanyingService.save(groupHuanying);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条群欢迎语记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条群欢迎语记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入群欢迎语失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入群欢迎语数据模板
	 */
	@ApiLog("下载群欢迎语模板")
//	@RequiresPermissions("grouphuanying:groupHuanying:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "群欢迎语数据导入模板.xlsx";
    		List<GroupHuanying> list = Lists.newArrayList();
    		new ExportExcel("群欢迎语数据", GroupHuanying.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}