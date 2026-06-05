package com.seekweb4.chat.modules.tixian.web;

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
import com.seekweb4.chat.modules.tixian.entity.Tixian;
import com.seekweb4.chat.modules.tixian.service.TixianService;

/**
 * 提现申请Controller
 * @author lixinapp
 * @version 2024-09-22
 */
@RestController
@RequestMapping(value = "/tixian/tixian")
public class TixianController extends BaseController {

	@Autowired
	private TixianService tixianService;
	@Autowired
	private MemberService memberService;

	@ModelAttribute
	public Tixian get(@RequestParam(required=false) String id) {
		Tixian entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tixianService.get(id);
		}
		if (entity == null){
			entity = new Tixian();
		}
		return entity;
	}

	/**
	 * 提现申请列表数据
	 */
	@ApiLog("查询提现申请列表")
	@RequiresPermissions("tixian:tixian:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Tixian tixian, HttpServletRequest request, HttpServletResponse response) {
		Page<Tixian> page = tixianService.findPage(new Page<Tixian>(request, response), tixian);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取提现申请数据
	 */
	@ApiLog("查询提现申请")
	@RequiresPermissions(value={"tixian:tixian:view","tixian:tixian:add","tixian:tixian:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Tixian tixian) {
		if (StringUtils.isNotBlank(tixian.getId())) {
			Tixian entity = tixianService.get(tixian.getId());
			return entity != null ? AjaxJson.success().put("tixian", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存提现申请
	 */
	@ApiLog("保存提现申请")
	@RequiresPermissions(value={"tixian:tixian:add","tixian:tixian:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Tixian tixian) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(tixian);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		tixianService.save(tixian);//保存
		return AjaxJson.success("保存提现申请成功");
	}

	/**
	 * 审核
	 * @param tixian
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("examine")
	public AjaxJson examine(Tixian tixian, Model model) throws Exception{
		if("3".equals(tixian.getState())){
			Tixian tx = tixianService.get(tixian.getId());
			memberService.updateBalance(tx.getU(),tx.getMoney(),"1","提现被驳回");
		}
		tixianService.save(tixian);//保存
		return AjaxJson.success("审核成功");
	}


	/**
	 * 批量删除提现申请
	 */
	@ApiLog("删除提现申请")
	@RequiresPermissions("tixian:tixian:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			tixianService.delete(new Tixian(id));
		}
		return AjaxJson.success("删除提现申请成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出提现申请")
	@RequiresPermissions("tixian:tixian:export")
    @GetMapping("export")
    public AjaxJson exportFile(Tixian tixian, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "提现申请"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Tixian> page = tixianService.findPage(new Page<Tixian>(request, response, -1), tixian);
    		new ExportExcel("提现申请", Tixian.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出提现申请记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入提现申请")
	@RequiresPermissions("tixian:tixian:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Tixian> list = ei.getDataList(Tixian.class);
			for (Tixian tixian : list){
				try{
					tixianService.save(tixian);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条提现申请记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条提现申请记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入提现申请失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入提现申请数据模板
	 */
	@ApiLog("下载提现申请模板")
	@RequiresPermissions("tixian:tixian:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "提现申请数据导入模板.xlsx";
    		List<Tixian> list = Lists.newArrayList();
    		new ExportExcel("提现申请数据", Tixian.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}