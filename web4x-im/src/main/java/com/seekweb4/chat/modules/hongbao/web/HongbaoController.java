package com.seekweb4.chat.modules.hongbao.web;

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
import com.seekweb4.chat.modules.hongbao.entity.Hongbao;
import com.seekweb4.chat.modules.hongbao.service.HongbaoService;

/**
 * 单聊红包记录Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/hongbao/hongbao")
public class HongbaoController extends BaseController {

	@Autowired
	private HongbaoService hongbaoService;

	@ModelAttribute
	public Hongbao get(@RequestParam(required=false) String id) {
		Hongbao entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = hongbaoService.get(id);
		}
		if (entity == null){
			entity = new Hongbao();
		}
		return entity;
	}

	/**
	 * 单聊红包记录列表数据
	 */
	@ApiLog("查询单聊红包记录列表")
	@RequiresPermissions("hongbao:hongbao:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Hongbao hongbao, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(hongbao);
		Page<Hongbao> page = hongbaoService.findPage(new Page<Hongbao>(request, response), hongbao);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取单聊红包记录数据
	 */
	@ApiLog("查询单聊红包记录")
	@RequiresPermissions(value={"hongbao:hongbao:view","hongbao:hongbao:add","hongbao:hongbao:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Hongbao hongbao) {
		if (StringUtils.isNotBlank(hongbao.getId())) {
			Hongbao entity = hongbaoService.get(hongbao.getId());
			return entity != null ? AjaxJson.success().put("hongbao", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存单聊红包记录
	 */
	@ApiLog("保存单聊红包记录")
	@RequiresPermissions(value={"hongbao:hongbao:add","hongbao:hongbao:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody    Hongbao hongbao) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(hongbao);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		hongbaoService.save(hongbao);//保存
		return AjaxJson.success("保存单聊红包记录成功");
	}


	/**
	 * 批量删除单聊红包记录
	 */
	@ApiLog("删除单聊红包记录")
	@RequiresPermissions("hongbao:hongbao:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			hongbaoService.delete(new Hongbao(id));
		}
		return AjaxJson.success("删除单聊红包记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出单聊红包记录")
	@RequiresPermissions("hongbao:hongbao:export")
    @GetMapping("export")
    public AjaxJson exportFile(Hongbao hongbao, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "单聊红包记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Hongbao> page = hongbaoService.findPage(new Page<Hongbao>(request, response, -1), hongbao);
    		new ExportExcel("单聊红包记录", Hongbao.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出单聊红包记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入单聊红包记录")
	@RequiresPermissions("hongbao:hongbao:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Hongbao> list = ei.getDataList(Hongbao.class);
			for (Hongbao hongbao : list){
				try{
					hongbaoService.save(hongbao);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条单聊红包记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条单聊红包记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入单聊红包记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入单聊红包记录数据模板
	 */
	@ApiLog("下载单聊红包记录模板")
	@RequiresPermissions("hongbao:hongbao:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "单聊红包记录数据导入模板.xlsx";
    		List<Hongbao> list = Lists.newArrayList();
    		new ExportExcel("单聊红包记录数据", Hongbao.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}