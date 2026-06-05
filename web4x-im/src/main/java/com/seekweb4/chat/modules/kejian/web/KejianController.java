package com.seekweb4.chat.modules.kejian.web;

import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.delayedQueue.KejianDelayedQueueListener;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
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
import com.seekweb4.chat.modules.kejian.entity.Kejian;
import com.seekweb4.chat.modules.kejian.service.KejianService;

/**
 * 课件Controller
 * @author lixinapp
 * @version 2025-05-24
 */
@RestController
@RequestMapping(value = "/kejian/kejian")
public class KejianController extends BaseController {

	@Autowired
	private KejianService kejianService;
	@Autowired
	private RedisDelayedQueue redisDelayedQueue;

	@ModelAttribute
	public Kejian get(@RequestParam(required=false) String id) {
		Kejian entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = kejianService.get(id);
		}
		if (entity == null){
			entity = new Kejian();
		}
		return entity;
	}

	/**
	 * 课件列表数据
	 */
	@ApiLog("查询课件列表")
	@RequiresPermissions("kejian:kejian:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Kejian kejian, HttpServletRequest request, HttpServletResponse response) {
		Page<Kejian> page = kejianService.findPage(new Page<Kejian>(request, response), kejian);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取课件数据
	 */
	@ApiLog("查询课件")
	@RequiresPermissions(value={"kejian:kejian:view","kejian:kejian:add","kejian:kejian:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Kejian kejian) {
		if (StringUtils.isNotBlank(kejian.getId())) {
			Kejian entity = kejianService.get(kejian.getId());
			return entity != null ? AjaxJson.success().put("kejian", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存课件
	 */
	@ApiLog("保存课件")
	@RequiresPermissions(value={"kejian:kejian:add","kejian:kejian:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(    Kejian kejian) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(kejian);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		if(kejian.getSendTime().before(new Date())){
			return AjaxJson.error("请选择当前之后的时间发送");
		}
		//新增或编辑表单保存
		kejianService.save(kejian);//保存
		// 计算两个时间的毫秒差值
		long differenceInMillis = kejian.getSendTime().getTime() - new Date().getTime();
		// 将毫秒差值转换为秒
		long miao = differenceInMillis / 1000;
		redisDelayedQueue.addQueueSeconds(kejian.getId(),(int)miao, KejianDelayedQueueListener.class);

		return AjaxJson.success("保存课件成功");
	}


	/**
	 * 批量删除课件
	 */
	@ApiLog("删除课件")
	@RequiresPermissions("kejian:kejian:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			kejianService.delete(new Kejian(id));
		}
		return AjaxJson.success("删除课件成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出课件")
	@RequiresPermissions("kejian:kejian:export")
    @GetMapping("export")
    public AjaxJson exportFile(Kejian kejian, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "课件"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Kejian> page = kejianService.findPage(new Page<Kejian>(request, response, -1), kejian);
    		new ExportExcel("课件", Kejian.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出课件记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入课件")
	@RequiresPermissions("kejian:kejian:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Kejian> list = ei.getDataList(Kejian.class);
			for (Kejian kejian : list){
				try{
					kejianService.save(kejian);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条课件记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条课件记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入课件失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入课件数据模板
	 */
	@ApiLog("下载课件模板")
	@RequiresPermissions("kejian:kejian:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "课件数据导入模板.xlsx";
    		List<Kejian> list = Lists.newArrayList();
    		new ExportExcel("课件数据", Kejian.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}