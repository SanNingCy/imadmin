package com.seekweb4.chat.modules.quhao.web;

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
import com.seekweb4.chat.modules.quhao.entity.Quhao;
import com.seekweb4.chat.modules.quhao.service.QuhaoService;

/**
 * 手机区号Controller
 * @author lixinapp
 * @version 2024-09-24
 */
@RestController
@RequestMapping(value = "/quhao/quhao")
public class QuhaoController extends BaseController {

	@Autowired
	private QuhaoService quhaoService;

	@ModelAttribute
	public Quhao get(@RequestParam(required=false) String id) {
		Quhao entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = quhaoService.get(id);
		}
		if (entity == null){
			entity = new Quhao();
		}
		return entity;
	}

	/**
	 * 手机区号列表数据
	 */
	@ApiLog("查询手机区号列表")
	@RequiresPermissions("quhao:quhao:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Quhao quhao, HttpServletRequest request, HttpServletResponse response) {
		Page<Quhao> page = quhaoService.findPage(new Page<Quhao>(request, response), quhao);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取手机区号数据
	 */
	@ApiLog("查询手机区号")
	@RequiresPermissions(value={"quhao:quhao:view","quhao:quhao:add","quhao:quhao:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Quhao quhao) {
		if (StringUtils.isNotBlank(quhao.getId())) {
			Quhao entity = quhaoService.get(quhao.getId());
			return entity != null ? AjaxJson.success().put("quhao", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存手机区号
	 */
	@ApiLog("保存手机区号")
	@RequiresPermissions(value={"quhao:quhao:add","quhao:quhao:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(  @RequestBody  Quhao quhao) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(quhao);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		quhaoService.save(quhao);//保存
		return AjaxJson.success("保存手机区号成功");
	}


	/**
	 * 批量删除手机区号
	 */
	@ApiLog("删除手机区号")
	@RequiresPermissions("quhao:quhao:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			quhaoService.delete(new Quhao(id));
		}
		return AjaxJson.success("删除手机区号成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出手机区号")
	@RequiresPermissions("quhao:quhao:export")
    @GetMapping("export")
    public AjaxJson exportFile(Quhao quhao, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "手机区号"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Quhao> page = quhaoService.findPage(new Page<Quhao>(request, response, -1), quhao);
    		new ExportExcel("手机区号", Quhao.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出手机区号记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入手机区号")
	@RequiresPermissions("quhao:quhao:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Quhao> list = ei.getDataList(Quhao.class);
			for (Quhao quhao : list){
				try{
					quhaoService.save(quhao);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条手机区号记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条手机区号记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入手机区号失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入手机区号数据模板
	 */
	@ApiLog("下载手机区号模板")
	@RequiresPermissions("quhao:quhao:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "手机区号数据导入模板.xlsx";
    		List<Quhao> list = Lists.newArrayList();
    		new ExportExcel("手机区号数据", Quhao.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}