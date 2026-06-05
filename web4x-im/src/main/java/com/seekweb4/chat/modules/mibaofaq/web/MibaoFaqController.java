package com.seekweb4.chat.modules.mibaofaq.web;

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
import com.seekweb4.chat.modules.mibaofaq.entity.MibaoFaq;
import com.seekweb4.chat.modules.mibaofaq.service.MibaoFaqService;

/**
 * 密保问题Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/mibaofaq/mibaoFaq")
public class MibaoFaqController extends BaseController {

	@Autowired
	private MibaoFaqService mibaoFaqService;

	@ModelAttribute
	public MibaoFaq get(@RequestParam(required=false) String id) {
		MibaoFaq entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = mibaoFaqService.get(id);
		}
		if (entity == null){
			entity = new MibaoFaq();
		}
		return entity;
	}

	/**
	 * 密保问题列表数据
	 */
	@ApiLog("查询密保问题列表")
//	@RequiresPermissions("mibaofaq:mibaoFaq:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    MibaoFaq mibaoFaq, HttpServletRequest request, HttpServletResponse response) {
		Page<MibaoFaq> page = mibaoFaqService.findPage(new Page<MibaoFaq>(request, response), mibaoFaq);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取密保问题数据
	 */
	@ApiLog("查询密保问题")
	@RequiresPermissions(value={"social:security:qa:view","social:security:qa:add","social:security:qa:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"mibaofaq:mibaoFaq:view","mibaofaq:mibaoFaq:add","mibaofaq:mibaoFaq:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    MibaoFaq mibaoFaq) {
		if (StringUtils.isNotBlank(mibaoFaq.getId())) {
			MibaoFaq entity = mibaoFaqService.get(mibaoFaq.getId());
			return entity != null ? AjaxJson.success().put("mibaoFaq", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存密保问题
	 */
	@ApiLog("保存密保问题")
	@RequiresPermissions(value={"social:security:qa:add","social:security:qa:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"mibaofaq:mibaoFaq:add","mibaofaq:mibaoFaq:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody MibaoFaq mibaoFaq) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(mibaoFaq);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		mibaoFaqService.save(mibaoFaq);//保存
		return AjaxJson.success("保存密保问题成功");
	}


	/**
	 * 批量删除密保问题
	 */
	@ApiLog("删除密保问题")
	@RequiresPermissions("social:security:qa:delete")
//	@RequiresPermissions("mibaofaq:mibaoFaq:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			mibaoFaqService.delete(new MibaoFaq(id));
		}
		return AjaxJson.success("删除密保问题成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出密保问题")
	@RequiresPermissions("mibaofaq:mibaoFaq:export")
    @GetMapping("export")
    public AjaxJson exportFile(MibaoFaq mibaoFaq, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "密保问题"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<MibaoFaq> page = mibaoFaqService.findPage(new Page<MibaoFaq>(request, response, -1), mibaoFaq);
    		new ExportExcel("密保问题", MibaoFaq.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出密保问题记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入密保问题")
	@RequiresPermissions("mibaofaq:mibaoFaq:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<MibaoFaq> list = ei.getDataList(MibaoFaq.class);
			for (MibaoFaq mibaoFaq : list){
				try{
					mibaoFaqService.save(mibaoFaq);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条密保问题记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条密保问题记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入密保问题失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入密保问题数据模板
	 */
	@ApiLog("下载密保问题模板")
	@RequiresPermissions("mibaofaq:mibaoFaq:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "密保问题数据导入模板.xlsx";
    		List<MibaoFaq> list = Lists.newArrayList();
    		new ExportExcel("密保问题数据", MibaoFaq.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}