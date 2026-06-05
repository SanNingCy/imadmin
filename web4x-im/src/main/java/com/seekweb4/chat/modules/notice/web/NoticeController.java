package com.seekweb4.chat.modules.notice.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.membernotice.entity.MemberNotice;
import com.seekweb4.chat.modules.membernotice.service.MemberNoticeService;
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
import com.seekweb4.chat.modules.notice.entity.Notice;
import com.seekweb4.chat.modules.notice.service.NoticeService;

/**
 * 系统通知Controller
 * @author lixinapp
 * @version 2024-12-23
 */
@RestController
@RequestMapping(value = "/notice/notice")
public class NoticeController extends BaseController {

	@Autowired
	private NoticeService noticeService;
	@Autowired
	private MemberNoticeService memberNoticeService;
	@Autowired
	private MemberService memberService;

	@ModelAttribute
	public Notice get(@RequestParam(required=false) String id) {
		Notice entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = noticeService.get(id);
		}
		if (entity == null){
			entity = new Notice();
		}
		return entity;
	}

	/**
	 * 系统通知列表数据
	 */
	@ApiLog("查询系统通知列表")
//	@RequiresPermissions("notice:notice:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Notice notice, HttpServletRequest request, HttpServletResponse response) {
		Page<Notice> page = noticeService.findPage(new Page<Notice>(request, response), notice);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取系统通知数据
	 */
	@ApiLog("查询系统通知")
	@RequiresPermissions(value={"ops:content:notify:view","ops:content:notify:add","ops:content:notify:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Notice notice) {
		if (StringUtils.isNotBlank(notice.getId())) {
			Notice entity = noticeService.get(notice.getId());
			return entity != null ? AjaxJson.success().put("notice", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存系统通知
	 */
	@ApiLog("保存系统通知")
	@RequiresPermissions(value={"ops:content:notify:add","ops:content:notify:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody Notice notice) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(notice);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		boolean flag = false;
		if(notice.getIsNewRecord()){
			flag = true;
		}
		//新增或编辑表单保存
		noticeService.save(notice);//保存
		if(flag){
			List<Member> list = memberService.findList(new Member());
			for(Member m:list){
				MemberNotice no = new MemberNotice();
				no.setU(m);
				no.setNid(notice.getId());
				no.setInfo(notice.getInfo());
				no.setIsdu("0");
				memberNoticeService.save(no);
			}
		}
		return AjaxJson.success("保存系统通知成功");
	}


	/**
	 * 批量删除系统通知
	 */
	@ApiLog("删除系统通知")
	@RequiresPermissions("ops:content:notify:delete")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			memberNoticeService.executeDeleteSql("delete from t_member_notice where nid = '"+id+"'");
			noticeService.delete(new Notice(id));
		}
		return AjaxJson.success("删除系统通知成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出系统通知")
	@RequiresPermissions("notice:notice:export")
    @GetMapping("export")
    public AjaxJson exportFile(Notice notice, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "系统通知"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Notice> page = noticeService.findPage(new Page<Notice>(request, response, -1), notice);
    		new ExportExcel("系统通知", Notice.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出系统通知记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入系统通知")
	@RequiresPermissions("notice:notice:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Notice> list = ei.getDataList(Notice.class);
			for (Notice notice : list){
				try{
					noticeService.save(notice);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条系统通知记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条系统通知记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入系统通知失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入系统通知数据模板
	 */
	@ApiLog("下载系统通知模板")
	@RequiresPermissions("notice:notice:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "系统通知数据导入模板.xlsx";
    		List<Notice> list = Lists.newArrayList();
    		new ExportExcel("系统通知数据", Notice.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}