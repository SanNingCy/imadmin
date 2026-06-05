package com.seekweb4.chat.modules.rechagelog.web;

import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.modules.member.service.MemberService;
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
import com.seekweb4.chat.modules.rechagelog.entity.RechageLog;
import com.seekweb4.chat.modules.rechagelog.service.RechageLogService;

/**
 * 充值记录Controller
 * @author lixinapp
 * @version 2024-09-22
 */
@RestController
@RequestMapping(value = "/rechagelog/rechageLog")
public class RechageLogController extends BaseController {

	@Autowired
	private RechageLogService rechageLogService;
	@Autowired
	private MemberService memberService;

	@ModelAttribute
	public RechageLog get(@RequestParam(required=false) String id) {
		RechageLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = rechageLogService.get(id);
		}
		if (entity == null){
			entity = new RechageLog();
		}
		return entity;
	}

	/**
	 * 充值记录列表数据
	 */
	@ApiLog("查询充值记录列表")
//	@RequiresPermissions("rechagelog:rechageLog:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    RechageLog rechageLog, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(rechageLog);
		Page<RechageLog> page = rechageLogService.findPage(new Page<RechageLog>(request, response), rechageLog);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取充值记录数据
	 */
	@ApiLog("查询充值记录")
//	@RequiresPermissions(value={"rechagelog:rechageLog:view","rechagelog:rechageLog:add","rechagelog:rechageLog:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    RechageLog rechageLog) {
		if (StringUtils.isNotBlank(rechageLog.getId())) {
			RechageLog entity = rechageLogService.get(rechageLog.getId());
			return entity != null ? AjaxJson.success().put("rechageLog", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存充值记录
	 */
	@ApiLog("保存充值记录")
//	@RequiresPermissions(value={"rechagelog:rechageLog:add","rechagelog:rechageLog:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody RechageLog rechageLog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(rechageLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		rechageLogService.save(rechageLog);//保存
		return AjaxJson.success("保存充值记录成功");
	}

    /**
     * 审核
     *
     * 同时兼容两种调用方式：
     * 1）application/json：   body 为 RechageLog 对象
     * 2）表单 / query 参数：  id=xxx&state=2&...
     *
     * Spring 会先尝试用 JSON 绑定到 bodyRechageLog，如果没有 body，则用 formRechageLog（表单/URL 参数）。
     */
    @PostMapping(value = "examine", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson examine(@RequestBody(required = false) RechageLog bodyRechageLog,
                            RechageLog formRechageLog,
                            Model model) throws Exception{
        // JSON 优先，没有 JSON 时退回到表单 / 参数
        RechageLog rechageLog = bodyRechageLog != null ? bodyRechageLog : formRechageLog;
		if("2".equals(rechageLog.getState())){
			RechageLog log = rechageLogService.get(rechageLog.getId());
			memberService.updateBalance(log.getU(),new BigDecimal(log.getMoney()),"1","充值");
		}
		rechageLogService.save(rechageLog);//保存
		return AjaxJson.success("保存充值记录成功");
	}


	/**
	 * 批量删除充值记录
	 */
	@ApiLog("删除充值记录")
//	@RequiresPermissions("rechagelog:rechageLog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			rechageLogService.delete(new RechageLog(id));
		}
		return AjaxJson.success("删除充值记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出充值记录")
//	@RequiresPermissions("rechagelog:rechageLog:export")
    @GetMapping("export")
    public AjaxJson exportFile(RechageLog rechageLog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "充值记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<RechageLog> page = rechageLogService.findPage(new Page<RechageLog>(request, response, -1), rechageLog);
    		new ExportExcel("充值记录", RechageLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出充值记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入充值记录")
//	@RequiresPermissions("rechagelog:rechageLog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<RechageLog> list = ei.getDataList(RechageLog.class);
			for (RechageLog rechageLog : list){
				try{
					rechageLogService.save(rechageLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条充值记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条充值记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入充值记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入充值记录数据模板
	 */
	@ApiLog("下载充值记录模板")
//	@RequiresPermissions("rechagelog:rechageLog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "充值记录数据导入模板.xlsx";
    		List<RechageLog> list = Lists.newArrayList();
    		new ExportExcel("充值记录数据", RechageLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}