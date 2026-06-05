package com.seekweb4.chat.modules.balancelog.web;

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
import com.seekweb4.chat.modules.balancelog.entity.BalanceLog;
import com.seekweb4.chat.modules.balancelog.service.BalanceLogService;

/**
 * 余额明细Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/balancelog/balanceLog")
public class BalanceLogController extends BaseController {

	@Autowired
	private BalanceLogService balanceLogService;

	@ModelAttribute
	public BalanceLog get(@RequestParam(required=false) String id) {
		BalanceLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = balanceLogService.get(id);
		}
		if (entity == null){
			entity = new BalanceLog();
		}
		return entity;
	}

	/**
	 * 余额明细列表数据
	 */
	@ApiLog("查询余额明细列表")
//	@RequiresPermissions("balancelog:balanceLog:list")
	@GetMapping(value = "list")
//	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(BalanceLog balanceLog, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(balanceLog);
		Page<BalanceLog> page = balanceLogService.findPage(new Page<BalanceLog>(request, response), balanceLog);
		// 计算每条记录的操作前后余额
		if (page != null && page.getList() != null && !page.getList().isEmpty()) {
			balanceLogService.calculateBalanceInfo(page.getList());
		}
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取余额明细数据
	 */
	@ApiLog("查询余额明细")
//	@RequiresPermissions(value={"asset:fund:balance:view","asset:fund:balance:add","asset:fund:balance:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(   BalanceLog balanceLog) {
		if (StringUtils.isNotBlank(balanceLog.getId())){
			BalanceLog entity = balanceLogService.get(balanceLog.getId());
			return entity != null ? AjaxJson.success().put("balanceLog", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存余额明细
	 */
	@ApiLog("保存余额明细")
	@RequiresPermissions(value={"asset:fund:balance:add","asset:fund:balance:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(   BalanceLog balanceLog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(balanceLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		balanceLogService.save(balanceLog);//保存
		return AjaxJson.success("保存余额明细成功");
	}

	/**
	 * 保存余额明细
	 */
	@ApiLog("修改余额明细")
	@RequiresPermissions(value={"asset:fund:balance:add","asset:fund:balance:edit"},logical=Logical.OR)
	@PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson update(   BalanceLog balanceLog) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(balanceLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		balanceLogService.update(balanceLog);//保存
		return AjaxJson.success("保存余额明细成功");
	}


	/**
	 * 批量删除余额明细
	 */
	@ApiLog("删除余额明细")
//	@RequiresPermissions("balancelog:balanceLog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(   String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			balanceLogService.delete(new BalanceLog(id));
		}
		return AjaxJson.success("删除余额明细成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出余额明细")
//	@RequiresPermissions("balancelog:balanceLog:export")
    @GetMapping("export")
    public AjaxJson exportFile(BalanceLog balanceLog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "余额明细"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<BalanceLog> page = balanceLogService.findPage(new Page<BalanceLog>(request, response, -1), balanceLog);
    		new ExportExcel("余额明细", BalanceLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出余额明细记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入余额明细")
//	@RequiresPermissions("balancelog:balanceLog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<BalanceLog> list = ei.getDataList(BalanceLog.class);
			for (BalanceLog balanceLog : list){
				try{
					balanceLogService.save(balanceLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条余额明细记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条余额明细记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入余额明细失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入余额明细数据模板
	 */
	@ApiLog("下载余额明细模板")
//	@RequiresPermissions("balancelog:balanceLog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "余额明细数据导入模板.xlsx";
    		List<BalanceLog> list = Lists.newArrayList();
    		new ExportExcel("余额明细数据", BalanceLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}