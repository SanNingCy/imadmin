package com.seekweb4.chat.modules.emoji.web;

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
import com.seekweb4.chat.modules.emoji.entity.Emoji;
import com.seekweb4.chat.modules.emoji.service.EmojiService;

/**
 * 表情包Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/emoji/emoji")
public class EmojiController extends BaseController {

	@Autowired
	private EmojiService emojiService;

	@ModelAttribute
	public Emoji get(@RequestParam(required=false) String id) {
		Emoji entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = emojiService.get(id);
		}
		if (entity == null){
			entity = new Emoji();
		}
		return entity;
	}

	/**
	 * 表情包列表数据
	 */
	@ApiLog("查询表情包列表")
//	@RequiresPermissions("emoji:emoji:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Emoji emoji, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(emoji);
		Page<Emoji> page = emojiService.findPage(new Page<Emoji>(request, response), emoji);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取表情包数据
	 */
	@ApiLog("查询表情包")
//	@RequiresPermissions(value={"social:content:emojis:view","social:content:emojis:add","social:content:emojis:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"emoji:emoji:view","emoji:emoji:add","emoji:emoji:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Emoji emoji) {
		if (StringUtils.isNotBlank(emoji.getId())) {
			Emoji entity = emojiService.get(emoji.getId());
			return entity != null ? AjaxJson.success().put("emoji", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存表情包
	 */
	@ApiLog("保存表情包")
//	@RequiresPermissions(value={"emoji:emoji:add","emoji:emoji:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Emoji emoji) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(emoji);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		emojiService.save(emoji);//保存
		return AjaxJson.success("保存表情包成功");
	}


	/**
	 * 批量删除表情包
	 */
	@ApiLog("删除表情包")
//	@RequiresPermissions("emoji:emoji:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			emojiService.delete(new Emoji(id));
		}
		return AjaxJson.success("删除表情包成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出表情包")
//	@RequiresPermissions("emoji:emoji:export")
    @GetMapping("export")
    public AjaxJson exportFile(Emoji emoji, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "表情包"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Emoji> page = emojiService.findPage(new Page<Emoji>(request, response, -1), emoji);
    		new ExportExcel("表情包", Emoji.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出表情包记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入表情包")
//	@RequiresPermissions("emoji:emoji:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Emoji> list = ei.getDataList(Emoji.class);
			for (Emoji emoji : list){
				try{
					emojiService.save(emoji);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条表情包记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条表情包记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入表情包失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入表情包数据模板
	 */
	@ApiLog("下载表情包模板")
//	@RequiresPermissions("emoji:emoji:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "表情包数据导入模板.xlsx";
    		List<Emoji> list = Lists.newArrayList();
    		new ExportExcel("表情包数据", Emoji.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}