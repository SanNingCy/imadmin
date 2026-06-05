package com.seekweb4.chat.modules.zhuxiao.web;

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
import com.seekweb4.chat.modules.zhuxiao.entity.Zhuxiao;
import com.seekweb4.chat.modules.zhuxiao.service.ZhuxiaoService;

/**
 * 注销申请Controller
 * @author lixinapp
 * @version 2025-07-01
 */
@RestController
@RequestMapping(value = "/zhuxiao/zhuxiao")
public class ZhuxiaoController extends BaseController {

	@Autowired
	private ZhuxiaoService zhuxiaoService;

	@ModelAttribute
	public Zhuxiao get(@RequestParam(required=false) String id) {
		Zhuxiao entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = zhuxiaoService.get(id);
		}
		if (entity == null){
			entity = new Zhuxiao();
		}
		return entity;
	}

	/**
	 * 注销申请列表数据
	 */
	@ApiLog("查询注销申请列表")
//	@RequiresPermissions("zhuxiao:zhuxiao:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Zhuxiao zhuxiao, HttpServletRequest request, HttpServletResponse response) {
		Page<Zhuxiao> page = zhuxiaoService.findPage(new Page<Zhuxiao>(request, response), zhuxiao);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取注销申请数据
	 */
	@ApiLog("查询注销申请")
//	@RequiresPermissions(value={"zhuxiao:zhuxiao:view","zhuxiao:zhuxiao:add","zhuxiao:zhuxiao:edit"},logical=Logical.OR)
	@RequiresPermissions(value={"social:user:cancel-apply:view"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Zhuxiao zhuxiao) {
		if (StringUtils.isNotBlank(zhuxiao.getId())) {
			Zhuxiao entity = zhuxiaoService.get(zhuxiao.getId());
			return entity != null ? AjaxJson.success().put("zhuxiao", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存注销申请
	 */
	@ApiLog("保存注销申请")
//	@RequiresPermissions(value={"zhuxiao:zhuxiao:add","zhuxiao:zhuxiao:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Zhuxiao zhuxiao) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(zhuxiao);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		zhuxiaoService.save(zhuxiao);//保存
		return AjaxJson.success("保存注销申请成功");
	}

	/**
	 * 审核
	 * @param zhuxiao
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "examine", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson examine(    Zhuxiao zhuxiao) throws Exception{
		//新增或编辑表单保存
		zhuxiaoService.examine(zhuxiao);//保存
		return AjaxJson.success("审核成功");
	}


	/**
	 * 批量删除注销申请
	 */
	@ApiLog("删除注销申请")
//	@RequiresPermissions("zhuxiao:zhuxiao:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			zhuxiaoService.delete(new Zhuxiao(id));
		}
		return AjaxJson.success("删除注销申请成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出注销申请")
//	@RequiresPermissions("zhuxiao:zhuxiao:export")
    @GetMapping("export")
    public AjaxJson exportFile(Zhuxiao zhuxiao, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "注销申请"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Zhuxiao> page = zhuxiaoService.findPage(new Page<Zhuxiao>(request, response, -1), zhuxiao);
    		new ExportExcel("注销申请", Zhuxiao.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出注销申请记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入注销申请")
//	@RequiresPermissions("zhuxiao:zhuxiao:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Zhuxiao> list = ei.getDataList(Zhuxiao.class);
			for (Zhuxiao zhuxiao : list){
				try{
					zhuxiaoService.save(zhuxiao);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条注销申请记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条注销申请记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入注销申请失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入注销申请数据模板
	 */
	@ApiLog("下载注销申请模板")
//	@RequiresPermissions("zhuxiao:zhuxiao:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "注销申请数据导入模板.xlsx";
    		List<Zhuxiao> list = Lists.newArrayList();
    		new ExportExcel("注销申请数据", Zhuxiao.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}