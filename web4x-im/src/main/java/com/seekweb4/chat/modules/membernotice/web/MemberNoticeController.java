package com.seekweb4.chat.modules.membernotice.web;

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
import com.seekweb4.chat.modules.membernotice.entity.MemberNotice;
import com.seekweb4.chat.modules.membernotice.service.MemberNoticeService;

/**
 * 用户系统消息Controller
 * @author lixinapp
 * @version 2024-12-23
 */
@RestController
@RequestMapping(value = "/membernotice/memberNotice")
public class MemberNoticeController extends BaseController {

	@Autowired
	private MemberNoticeService memberNoticeService;

	@ModelAttribute
	public MemberNotice get(@RequestParam(required=false) String id) {
		MemberNotice entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = memberNoticeService.get(id);
		}
		if (entity == null){
			entity = new MemberNotice();
		}
		return entity;
	}

	/**
	 * 用户系统消息列表数据
	 */
	@ApiLog("查询用户系统消息列表")
	@RequiresPermissions("membernotice:memberNotice:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    MemberNotice memberNotice, HttpServletRequest request, HttpServletResponse response) {
		Page<MemberNotice> page = memberNoticeService.findPage(new Page<MemberNotice>(request, response), memberNotice);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取用户系统消息数据
	 */
	@ApiLog("查询用户系统消息")
	@RequiresPermissions(value={"membernotice:memberNotice:view","membernotice:memberNotice:add","membernotice:memberNotice:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    MemberNotice memberNotice) {
		if (StringUtils.isNotBlank(memberNotice.getId())) {
			MemberNotice entity = memberNoticeService.get(memberNotice.getId());
			return entity != null ? AjaxJson.success().put("memberNotice", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存用户系统消息
	 */
	@ApiLog("保存用户系统消息")
	@RequiresPermissions(value={"membernotice:memberNotice:add","membernotice:memberNotice:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   MemberNotice memberNotice) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(memberNotice);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		memberNoticeService.save(memberNotice);//保存
		return AjaxJson.success("保存用户系统消息成功");
	}


	/**
	 * 批量删除用户系统消息
	 */
	@ApiLog("删除用户系统消息")
	@RequiresPermissions("membernotice:memberNotice:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			memberNoticeService.delete(new MemberNotice(id));
		}
		return AjaxJson.success("删除用户系统消息成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出用户系统消息")
	@RequiresPermissions("membernotice:memberNotice:export")
    @GetMapping("export")
    public AjaxJson exportFile(MemberNotice memberNotice, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "用户系统消息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<MemberNotice> page = memberNoticeService.findPage(new Page<MemberNotice>(request, response, -1), memberNotice);
    		new ExportExcel("用户系统消息", MemberNotice.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出用户系统消息记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入用户系统消息")
	@RequiresPermissions("membernotice:memberNotice:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<MemberNotice> list = ei.getDataList(MemberNotice.class);
			for (MemberNotice memberNotice : list){
				try{
					memberNoticeService.save(memberNotice);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条用户系统消息记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条用户系统消息记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入用户系统消息失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入用户系统消息数据模板
	 */
	@ApiLog("下载用户系统消息模板")
	@RequiresPermissions("membernotice:memberNotice:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "用户系统消息数据导入模板.xlsx";
    		List<MemberNotice> list = Lists.newArrayList();
    		new ExportExcel("用户系统消息数据", MemberNotice.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}