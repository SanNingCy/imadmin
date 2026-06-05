package com.seekweb4.chat.modules.grouphongbaolog.web;

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
import com.seekweb4.chat.modules.grouphongbaolog.entity.GroupHongbaoLog;
import com.seekweb4.chat.modules.grouphongbaolog.service.GroupHongbaoLogService;

/**
 * 群红包领取记录Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/grouphongbaolog/groupHongbaoLog")
public class GroupHongbaoLogController extends BaseController {

	@Autowired
	private GroupHongbaoLogService groupHongbaoLogService;

	@ModelAttribute
	public GroupHongbaoLog get(@RequestParam(required=false) String id) {
		GroupHongbaoLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = groupHongbaoLogService.get(id);
		}
		if (entity == null){
			entity = new GroupHongbaoLog();
		}
		return entity;
	}

	/**
	 * 群红包领取记录列表数据
	 */
	@ApiLog("查询群红包领取记录列表")
//	@RequiresPermissions("grouphongbaolog:groupHongbaoLog:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    GroupHongbaoLog groupHongbaoLog, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(groupHongbaoLog);
		Page<GroupHongbaoLog> page = groupHongbaoLogService.findPage(new Page<GroupHongbaoLog>(request, response), groupHongbaoLog);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取群红包领取记录数据
	 */
	@ApiLog("查询群红包领取记录")
//	@RequiresPermissions(value={"asset:trade:claim-logs:view"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    GroupHongbaoLog groupHongbaoLog) {
		if (StringUtils.isNotBlank(groupHongbaoLog.getId())) {
			GroupHongbaoLog entity = groupHongbaoLogService.get(groupHongbaoLog.getId());
			return entity != null ? AjaxJson.success().put("groupHongbaoLog", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存群红包领取记录
	 */
	@ApiLog("保存群红包领取记录")
//	@RequiresPermissions(value={"grouphongbaolog:groupHongbaoLog:add","grouphongbaolog:groupHongbaoLog:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody    GroupHongbaoLog groupHongbaoLog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(groupHongbaoLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		groupHongbaoLogService.save(groupHongbaoLog);//保存
		return AjaxJson.success("保存群红包领取记录成功");
	}


	/**
	 * 批量删除群红包领取记录
	 */
	@ApiLog("删除群红包领取记录")
//	@RequiresPermissions("grouphongbaolog:groupHongbaoLog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			groupHongbaoLogService.delete(new GroupHongbaoLog(id));
		}
		return AjaxJson.success("删除群红包领取记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出群红包领取记录")
//	@RequiresPermissions("grouphongbaolog:groupHongbaoLog:export")
    @GetMapping("export")
    public AjaxJson exportFile(GroupHongbaoLog groupHongbaoLog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "群红包领取记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<GroupHongbaoLog> page = groupHongbaoLogService.findPage(new Page<GroupHongbaoLog>(request, response, -1), groupHongbaoLog);
    		new ExportExcel("群红包领取记录", GroupHongbaoLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出群红包领取记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入群红包领取记录")
//	@RequiresPermissions("grouphongbaolog:groupHongbaoLog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<GroupHongbaoLog> list = ei.getDataList(GroupHongbaoLog.class);
			for (GroupHongbaoLog groupHongbaoLog : list){
				try{
					groupHongbaoLogService.save(groupHongbaoLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条群红包领取记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条群红包领取记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入群红包领取记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入群红包领取记录数据模板
	 */
	@ApiLog("下载群红包领取记录模板")
//	@RequiresPermissions("grouphongbaolog:groupHongbaoLog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "群红包领取记录数据导入模板.xlsx";
    		List<GroupHongbaoLog> list = Lists.newArrayList();
    		new ExportExcel("群红包领取记录数据", GroupHongbaoLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}