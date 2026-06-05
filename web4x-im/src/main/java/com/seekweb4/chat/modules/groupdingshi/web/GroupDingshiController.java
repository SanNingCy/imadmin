package com.seekweb4.chat.modules.groupdingshi.web;

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
import com.seekweb4.chat.modules.groupdingshi.entity.GroupDingshi;
import com.seekweb4.chat.modules.groupdingshi.service.GroupDingshiService;

/**
 * 群定时消息Controller
 * @author lixinapp
 * @version 2025-03-24
 */
@RestController
@RequestMapping(value = "/groupdingshi/groupDingshi")
public class GroupDingshiController extends BaseController {

	@Autowired
	private GroupDingshiService groupDingshiService;

	@ModelAttribute
	public GroupDingshi get(@RequestParam(required=false) String id) {
		GroupDingshi entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = groupDingshiService.get(id);
		}
		if (entity == null){
			entity = new GroupDingshi();
		}
		return entity;
	}

	/**
	 * 群定时消息列表数据
	 */
	@ApiLog("查询群定时消息列表")
//	@RequiresPermissions("groupdingshi:groupDingshi:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    GroupDingshi groupDingshi, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(groupDingshi);
		Page<GroupDingshi> page = groupDingshiService.findPage(new Page<GroupDingshi>(request, response), groupDingshi);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取群定时消息数据
	 */
	@ApiLog("查询群定时消息")
//	@RequiresPermissions(value={"groupdingshi:groupDingshi:view","groupdingshi:groupDingshi:add","groupdingshi:groupDingshi:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    GroupDingshi groupDingshi) {
		if (StringUtils.isNotBlank(groupDingshi.getId())) {
			GroupDingshi entity = groupDingshiService.get(groupDingshi.getId());
			return entity != null ? AjaxJson.success().put("groupDingshi", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存群定时消息
	 */
	@ApiLog("保存群定时消息")
//	@RequiresPermissions(value={"groupdingshi:groupDingshi:add","groupdingshi:groupDingshi:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody    GroupDingshi groupDingshi) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(groupDingshi);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		groupDingshiService.save(groupDingshi);//保存
		return AjaxJson.success("保存群定时消息成功");
	}


	/**
	 * 批量删除群定时消息
	 */
	@ApiLog("删除群定时消息")
//	@RequiresPermissions("groupdingshi:groupDingshi:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			groupDingshiService.delete(new GroupDingshi(id));
		}
		return AjaxJson.success("删除群定时消息成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出群定时消息")
//	@RequiresPermissions("groupdingshi:groupDingshi:export")
    @GetMapping("export")
    public AjaxJson exportFile(GroupDingshi groupDingshi, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "群定时消息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<GroupDingshi> page = groupDingshiService.findPage(new Page<GroupDingshi>(request, response, -1), groupDingshi);
    		new ExportExcel("群定时消息", GroupDingshi.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出群定时消息记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入群定时消息")
//	@RequiresPermissions("groupdingshi:groupDingshi:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<GroupDingshi> list = ei.getDataList(GroupDingshi.class);
			for (GroupDingshi groupDingshi : list){
				try{
					groupDingshiService.save(groupDingshi);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条群定时消息记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条群定时消息记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入群定时消息失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入群定时消息数据模板
	 */
	@ApiLog("下载群定时消息模板")
//	@RequiresPermissions("groupdingshi:groupDingshi:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "群定时消息数据导入模板.xlsx";
    		List<GroupDingshi> list = Lists.newArrayList();
    		new ExportExcel("群定时消息数据", GroupDingshi.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}