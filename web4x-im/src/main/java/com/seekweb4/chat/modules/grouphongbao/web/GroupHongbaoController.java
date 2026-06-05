package com.seekweb4.chat.modules.grouphongbao.web;

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
import com.seekweb4.chat.modules.grouphongbao.entity.GroupHongbao;
import com.seekweb4.chat.modules.grouphongbao.service.GroupHongbaoService;

/**
 * 群红包Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/grouphongbao/groupHongbao")
public class GroupHongbaoController extends BaseController {

	@Autowired
	private GroupHongbaoService groupHongbaoService;

	@ModelAttribute
	public GroupHongbao get(@RequestParam(required=false) String id) {
		GroupHongbao entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = groupHongbaoService.get(id);
		}
		if (entity == null){
			entity = new GroupHongbao();
		}
		return entity;
	}

	/**
	 * 群红包列表数据
	 */
	@ApiLog("查询群红包列表")
	@RequiresPermissions("grouphongbao:groupHongbao:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    GroupHongbao groupHongbao, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(groupHongbao);
		Page<GroupHongbao> page = groupHongbaoService.findPage(new Page<GroupHongbao>(request, response), groupHongbao);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取群红包数据
	 */
	@ApiLog("查询群红包")
	@RequiresPermissions(value={"grouphongbao:groupHongbao:view","grouphongbao:groupHongbao:add","grouphongbao:groupHongbao:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    GroupHongbao groupHongbao) {
		if (StringUtils.isNotBlank(groupHongbao.getId())) {
			GroupHongbao entity = groupHongbaoService.get(groupHongbao.getId());
			return entity != null ? AjaxJson.success().put("groupHongbao", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存群红包
	 */
	@ApiLog("保存群红包")
	@RequiresPermissions(value={"grouphongbao:groupHongbao:add","grouphongbao:groupHongbao:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   GroupHongbao groupHongbao) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(groupHongbao);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		groupHongbaoService.save(groupHongbao);//保存
		return AjaxJson.success("保存群红包成功");
	}


	/**
	 * 批量删除群红包
	 */
	@ApiLog("删除群红包")
	@RequiresPermissions("grouphongbao:groupHongbao:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			groupHongbaoService.delete(new GroupHongbao(id));
		}
		return AjaxJson.success("删除群红包成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出群红包")
	@RequiresPermissions("grouphongbao:groupHongbao:export")
    @GetMapping("export")
    public AjaxJson exportFile(GroupHongbao groupHongbao, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "群红包"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<GroupHongbao> page = groupHongbaoService.findPage(new Page<GroupHongbao>(request, response, -1), groupHongbao);
    		new ExportExcel("群红包", GroupHongbao.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出群红包记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入群红包")
	@RequiresPermissions("grouphongbao:groupHongbao:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<GroupHongbao> list = ei.getDataList(GroupHongbao.class);
			for (GroupHongbao groupHongbao : list){
				try{
					groupHongbaoService.save(groupHongbao);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条群红包记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条群红包记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入群红包失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入群红包数据模板
	 */
	@ApiLog("下载群红包模板")
	@RequiresPermissions("grouphongbao:groupHongbao:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "群红包数据导入模板.xlsx";
    		List<GroupHongbao> list = Lists.newArrayList();
    		new ExportExcel("群红包数据", GroupHongbao.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}