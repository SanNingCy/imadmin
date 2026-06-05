package com.seekweb4.chat.modules.vipcode.web;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;

import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
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
import com.seekweb4.chat.modules.vipcode.constant.ChainBridgeResult;
import com.seekweb4.chat.modules.vipcode.dto.SyncFromImItemDTO;
import com.seekweb4.chat.modules.vipcode.dto.SyncFromImRequestDTO;
import com.seekweb4.chat.modules.vipcode.entity.VipCode;
import com.seekweb4.chat.modules.vipcode.service.VipCodeService;

/**
 * 会员码Controller
 * @author lixinapp
 * @version 2025-03-24
 */
@RestController
@RequestMapping(value = "/vipcode/vipCode")
public class VipCodeController extends BaseController {

	@Autowired
	private VipCodeService vipCodeService;
	private static final int MAX_SYNC_BATCH_SIZE = 500;
	private static final int MAX_GENERATE_BATCH_SIZE = 10_000;
	private static final String CHAR_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final Random random = new Random();

	public static String generateRandomString(int size) {
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int index = random.nextInt(CHAR_SET.length());
			sb.append(CHAR_SET.charAt(index));
		}
		return sb.toString();
	}

	@ModelAttribute
	public VipCode get(@RequestParam(required=false) String id) {
		VipCode entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = vipCodeService.get(id);
		}
		if (entity == null){
			entity = new VipCode();
		}
		return entity;
	}

	/**
	 * 会员码列表数据
	 */
	@ApiLog("查询会员码列表")
//	@RequiresPermissions("vipcode:vipCode:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    VipCode vipCode, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(vipCode);
		Page<VipCode> page = vipCodeService.findPage(new Page<VipCode>(request, response), vipCode);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取会员码数据
	 */
	@ApiLog("查询会员码")
	@RequiresPermissions(value={"social:security:vip-code:view","social:security:vip-code:add","social:security:vip-code:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"vipcode:vipCode:view","vipcode:vipCode:add","vipcode:vipCode:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    VipCode vipCode) {
		if (StringUtils.isNotBlank(vipCode.getId())) {
			VipCode entity = vipCodeService.get(vipCode.getId());
			return entity != null ? AjaxJson.success().put("vipCode", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	@GetMapping("getcode")
	public AjaxJson getcode(VipCode vipCode) {
		return AjaxJson.success().put("code", generateRandomString(6));
	}

	/**
	 * 获取链桥卡密类型（保持原样）
	 */
	@ApiLog("获取链桥卡密类型")
	@GetMapping(value = "getKeyCardType", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson getKeyCardType() {
		try {
			Object data = vipCodeService.getKeyCardTypeFromChainBridge();
			// 如果data是一个Map，且包含data字段，则提取data字段的值，避免嵌套
			if (data instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> dataMap = (Map<String, Object>) data;
				if (dataMap.containsKey("data")) {
					// 外部服务器返回的是 {data: [...], success: true} 格式，提取data字段
					return AjaxJson.success().put("data", dataMap.get("data"));
				}
			}
			return AjaxJson.success().put("data", data);
		} catch (Exception e) {
			return AjaxJson.error(e.getMessage() != null ? e.getMessage() : "获取卡密类型失败");
		}
	}

	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	/**
	 * 会员码同步到链桥（syncFromIm）。
	 * 1）不传参数或请求体为空时：自动将「未同步且已填写类型」的会员码提交到链桥。
	 * 2）传参格式不变：支持 {\"data\": [...]} 或 [...]，与原有前端勾选后同步方式兼容。
	 */
	@ApiLog("会员码同步到链桥")
	@PostMapping(value = "syncFromIm", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson syncFromIm(@RequestBody(required = false) String body) {
		SyncFromImRequestDTO request;
		if (body == null || body.trim().isEmpty()) {
			// 无参数：使用未同步且已填类型的会员码
			request = vipCodeService.buildSyncRequestFromUnsynced();
			if (request.getData() == null || request.getData().isEmpty()) {
				return AjaxJson.error("没有可同步的会员码，请先批量生成或添加会员码并填写类型");
			}
		} else {
			try {
				String trimmed = body.trim();
				if (trimmed.startsWith("[")) {
					List<SyncFromImItemDTO> list = JSON_MAPPER.readValue(body, new TypeReference<List<SyncFromImItemDTO>>() {});
					request = new SyncFromImRequestDTO();
					request.setData(list != null ? list : Lists.newArrayList());
				} else {
					request = JSON_MAPPER.readValue(body, SyncFromImRequestDTO.class);
					if (request != null && request.getData() == null) {
						request.setData(Lists.newArrayList());
					}
				}
				if (request.getData() == null || request.getData().isEmpty()) {
					return AjaxJson.error("请求体 data 为空");
				}
			} catch (Exception e) {
				return AjaxJson.error("请求体格式错误，需要 {\"data\": [...]} 或 [...]");
			}
		}
		if (request.getData() != null && request.getData().size() > MAX_SYNC_BATCH_SIZE) {
			return AjaxJson.error("单次最多同步" + MAX_SYNC_BATCH_SIZE + "条数据，请分批同步");
		}
		ChainBridgeResult<Object> result = vipCodeService.syncToChainBridgeSafe(request);
		if (result.isSuccess()) {
			int count = request.getData() != null ? request.getData().size() : 0;
			String msg = "同步成功，共提交" + count + "条数据";
			return AjaxJson.success(msg).put("data", result.getData());
		}
		return AjaxJson.error(result.getErrorMessage());
	}

	/**
	 * 保存会员码
	 */
	@ApiLog("保存会员码")
	@RequiresPermissions(value={"social:security:vip-code:add","social:security:vip-code:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody    VipCode vipCode) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(vipCode);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		vipCodeService.save(vipCode);//保存
		return AjaxJson.success("保存会员码成功");
	}
	/**
	 * 批量生成会员码
	 */
	@ApiLog("批量生成会员码")
	@PostMapping(value = "batchSheng", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson batchSheng(@RequestBody VipCode vipCode, Model model) throws Exception{
		if (vipCode.getCount() == null || vipCode.getCount() <= 0) {
			return AjaxJson.error("生成数量不能为空且必须大于0");
		}
		if (vipCode.getCount() > MAX_GENERATE_BATCH_SIZE) {
			return AjaxJson.error("单次最多生成" + MAX_GENERATE_BATCH_SIZE + "条会员码，请分批生成");
		}
		if (vipCode.getDay() == null || vipCode.getDay() <= 0) {
			return AjaxJson.error("会员天数不能为空且必须大于0");
		}
		int count = vipCode.getCount();
		List<VipCode> toInsert = new ArrayList<>(count);
		for(int i=0;i<count;i++){
			VipCode code = new VipCode();
			code.setCode(generateRandomString(15));
			code.setDay(vipCode.getDay());
			code.setIsdui("0");
			code.setSyncStatus(vipCode.getSyncStatus() != null ? vipCode.getSyncStatus() : "0");
			code.setType(vipCode.getType());
			code.setTypeName(vipCode.getTypeName());
			toInsert.add(code);
		}
		int inserted = vipCodeService.insertBatchForGenerate(toInsert);
		return AjaxJson.success("生成成功，共生成" + inserted + "条会员码");
	}


	/**
	 * 批量删除会员码
	 */
	@ApiLog("删除会员码")
	@RequiresPermissions("social:security:vip-code:delete")
//	@RequiresPermissions("vipcode:vipCode:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			vipCodeService.delete(new VipCode(id));
		}
		return AjaxJson.success("删除会员码成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出会员码")
//	@RequiresPermissions("vipcode:vipCode:export")
    @GetMapping("export")
    public AjaxJson exportFile(VipCode vipCode, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "会员码"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<VipCode> page = vipCodeService.findPage(new Page<VipCode>(request, response, -1), vipCode);
    		new ExportExcel("会员码", VipCode.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出会员码记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入会员码")
//	@RequiresPermissions("vipcode:vipCode:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<VipCode> list = ei.getDataList(VipCode.class);
			for (VipCode vipCode : list){
				try{
					vipCodeService.save(vipCode);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条会员码记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条会员码记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入会员码失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入会员码数据模板
	 */
	@ApiLog("下载会员码模板")
//	@RequiresPermissions("vipcode:vipCode:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "会员码数据导入模板.xlsx";
    		List<VipCode> list = Lists.newArrayList();
    		new ExportExcel("会员码数据", VipCode.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}