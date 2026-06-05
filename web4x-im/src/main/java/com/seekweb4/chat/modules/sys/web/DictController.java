package com.seekweb4.chat.modules.sys.web;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.sys.entity.DictType;
import com.seekweb4.chat.modules.sys.entity.DictValue;
import com.seekweb4.chat.modules.sys.service.DictTypeService;
import com.seekweb4.chat.modules.sys.utils.DictUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典Controller
 * @author lixinapp
 * @version 2017-05-16
 */
@RestController
@RequestMapping("/sys/dict")
public class DictController extends BaseController {

	@Autowired
	private DictTypeService dictTypeService;

	@ModelAttribute
	public DictType get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return dictTypeService.get(id);
		}else{
			return new DictType();
		}
	}
	@ApiLog("查询字典值")
//	@RequiresPermissions("system:dictionary:list")
//	@RequiresPermissions("sys:dict:list")
	@GetMapping("getDictValue")
	public AjaxJson getDictValue(String dictTypeId) {
		Map<String, Object> page = new HashMap<String, Object>();
		if(dictTypeId == null || "".equals(dictTypeId)){
			page.put("list","[]");
			page.put("count",0);
		}else{
			List<DictValue> list = dictTypeService.get(dictTypeId).getDictValueList();
			page.put("list",list);
			page.put("count", list.size());
		}
		return AjaxJson.success().put("page", page);
	}

	@ApiLog("查询字典列表")
//	@RequiresPermissions("system:dictionary:list")
//	@RequiresPermissions("sys:dict:list")
	@GetMapping("type/list")
	public AjaxJson data(DictType dictType, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<DictType> page = dictTypeService.findPage(new Page<DictType>(request, response), dictType);
		return AjaxJson.success().put("page",page);
	}

	@ApiLog("查询字典")
//	@RequiresPermissions(value={"sys:dict:view","sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@RequiresPermissions(value={"system:dictionary:view","system:dictionary:add","system:dictionary:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(DictType dictType) {
		return AjaxJson.success().put("dictType", dictType);
	}

	@ApiLog("查询字典值")
//	@RequiresPermissions(value={"sys:dict:view","sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@GetMapping("queryDictValue")
	public AjaxJson queryDictValue(String dictValueId) {
		DictValue dictValue;
		if(dictValueId == null || "".equals(dictValueId)){
			dictValue =  new DictValue();
		}else{
			dictValue = dictTypeService.getDictValue(dictValueId);
		}
		return AjaxJson.success().put("dictValue", dictValue);
	}

	@ApiLog("保存字典")
//	@RequiresPermissions(value={"sys:dict:add","sys:dict:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"system:dictionary:add","system:dictionary:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(@RequestBody DictType dictType, Model model) {
		if(appProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(dictType);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		dictTypeService.save(dictType);
		return AjaxJson.success("保存字典类型'" + dictType.getDescription() + "'成功！");
	}

	@ApiLog("保存字典值")
//	@RequiresPermissions(value={"sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@PostMapping("saveDictValue")
	public AjaxJson saveDictValue(@RequestBody Map<String, Object> params) {
		if(appProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		
		DictValue dictValue = new DictValue();
		
		// 处理字典值ID（如果有，说明是更新操作）
		if (params.containsKey("id") && params.get("id") != null) {
			dictValue.setId(params.get("id").toString());
		}
		
		// 处理标签名
		if (params.containsKey("label")) {
			dictValue.setLabel(params.get("label").toString());
		}
		
		// 处理键值
		if (params.containsKey("value")) {
			dictValue.setValue(params.get("value").toString());
		}
		
		// 处理排序（前端可能传数字，需要转换为字符串）
		if (params.containsKey("sort")) {
			Object sortObj = params.get("sort");
			if (sortObj != null) {
				dictValue.setSort(sortObj.toString());
			}
		}
		
		// 处理字典类型ID（前端传的是 dictType.id）
		String dictTypeId = null;
		if (params.containsKey("dictType.id")) {
			dictTypeId = params.get("dictType.id").toString();
		} else if (params.containsKey("dictTypeId")) {
			dictTypeId = params.get("dictTypeId").toString();
		}
		
		if (StringUtils.isNotBlank(dictTypeId)) {
			DictType dictType = new DictType();
			dictType.setId(dictTypeId);
			dictValue.setDictType(dictType);
		}
		
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(dictValue);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		
		dictTypeService.saveDictValue(dictValue);
		return AjaxJson.success("保存键值'" + dictValue.getLabel() + "'成功！");
	}

	@ApiLog("删除字典值")
//	@RequiresPermissions("sys:dict:edit")
	@RequiresPermissions("system:dictionary:delete")
	@DeleteMapping("deleteDictValue")
	public AjaxJson deleteDictValue(String ids, Model model) {
		AjaxJson j = new AjaxJson();
		if(appProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		dictTypeService.batchDeleteDictValue(ids.split(","));
		return AjaxJson.success("删除键值成功！");
	}

	/**
	 * 批量删除
	 */
	@ApiLog("保存字典")
//	@RequiresPermissions("sys:dict:del")
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		AjaxJson j = new AjaxJson();
		if(appProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		String idArray[] =ids.split(",");
		dictTypeService.batchDelete(idArray);
		return AjaxJson.success("删除字典成功！");
	}



	@GetMapping("listData")
	public AjaxJson listData(@RequestParam(required=false) String type) {
		DictType dictType = new DictType();
		dictType.setType(type);
		return AjaxJson.success().put("list", dictTypeService.findList(dictType));
	}

	@GetMapping("getDictMap")
	public AjaxJson getDictMap() {
		AjaxJson j = new AjaxJson();
		j.put("dictList", DictUtils.getDictMap());
		return j;
	}


}
