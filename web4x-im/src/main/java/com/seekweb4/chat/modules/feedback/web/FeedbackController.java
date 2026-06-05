package com.seekweb4.chat.modules.feedback.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.common.annotation.ApiLog;
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

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.common.utils.excel.ExportExcel;
import com.seekweb4.chat.common.utils.excel.ImportExcel;
import com.seekweb4.chat.modules.feedback.entity.Feedback;
import com.seekweb4.chat.modules.feedback.service.FeedbackService;

/**
 * 意见反馈Controller
 * @author lixinapp
 * @version 2022-12-19
 */
@RestController
@RequestMapping(value = "/feedback/feedback")
public class FeedbackController extends BaseController {

	@Autowired
	private FeedbackService feedbackService;

	@ModelAttribute
	public Feedback get(@RequestParam(required=false) String id) {
		Feedback entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = feedbackService.get(id);
		}
		if (entity == null){
			entity = new Feedback();
		}
		return entity;
	}

	/**
	 * 意见反馈列表数据
	 */
	@ApiLog("查询意见反馈列表")
//	@RequiresPermissions("feedback:feedback:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(Feedback feedback, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(feedback);
		Page<Feedback> pageReq = new Page<Feedback>(request, response);
		normalizeOrderByForFeedback(pageReq);
		Page<Feedback> page = feedbackService.findPage(pageReq, feedback);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 前端排序字段通常是实体驼峰（如 replyDate），数据库列是下划线（reply_date）。
	 * 统一在控制层转换，避免 ORDER BY 使用不存在的列名。
	 */
	private static void normalizeOrderByForFeedback(Page<Feedback> page) {
		if (page == null || StringUtils.isBlank(page.getOrderBy())) {
			return;
		}
		Map<String, String> columnMap = new HashMap<>();
		columnMap.put("id", "a.id");
		columnMap.put("phone", "a.phone");
		columnMap.put("content", "a.content");
		columnMap.put("status", "a.status");
		columnMap.put("replyDate", "a.reply_date");
		columnMap.put("createDate", "a.create_date");
		columnMap.put("rewardAmount", "a.reward_amount");
		columnMap.put("isReward", "a.is_reward");
		columnMap.put("idno", "member.idno");
		columnMap.put("nickname", "member.nickname");

		String normalized = Arrays.stream(page.getOrderBy().split(","))
				.map(String::trim)
				.filter(StringUtils::isNotBlank)
				.map(item -> {
					String[] parts = item.split("\\s+");
					if (parts.length == 0) {
						return "";
					}
					String rawField = parts[0];
					String field = columnMap.getOrDefault(rawField, rawField);
					String direction = "";
					if (parts.length > 1) {
						String d = parts[1].toLowerCase(Locale.ROOT);
						if ("asc".equals(d) || "desc".equals(d)) {
							direction = " " + d;
						}
					}
					return field + direction;
				})
				.filter(StringUtils::isNotBlank)
				.reduce((a, b) -> a + ", " + b)
				.orElse("");
		page.setOrderBy(normalized);
	}

	/**
	 * 根据Id获取意见反馈数据
	 */
	@ApiLog("查询意见反馈")
	@RequiresPermissions(value={"ops:support:feedback:view","ops:support:feedback:add","ops:support:feedback:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Feedback feedback) {
		if (StringUtils.isNotBlank(feedback.getId())) {
			Feedback entity = feedbackService.get(feedback.getId());
			return entity != null ? AjaxJson.success().put("feedback", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存意见反馈
	 */
	@ApiLog("保存意见反馈")
	@RequiresPermissions(value={"ops:support:feedback:add","ops:support:feedback:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Feedback feedback) throws Exception{
		// 编辑时合并库内数据，避免 JSON 未传字段被置空；并保证校验前 member 等必填引用存在
		feedbackService.mergeFromExistingForUpdate(feedback);
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(feedback);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		try {
			feedbackService.save(feedback);
		} catch (IllegalStateException e) {
			return AjaxJson.error(e.getMessage());
		}
		return AjaxJson.success("保存意见反馈成功");
	}

	/**
	 * 管理员回复：保存回复内容，待处理(0)→已回复(1)；已采纳(2)时仅更新回复不降级状态。
	 */
	@ApiLog("意见反馈-回复")
	@RequiresPermissions(value = {"ops:support:feedback:add", "ops:support:feedback:edit"}, logical = Logical.OR)
	@PostMapping(value = "reply", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson reply(@RequestBody Feedback body) {
		try {
			feedbackService.replyById(body.getId(), body.getReply());
		} catch (IllegalArgumentException e) {
			return AjaxJson.error(e.getMessage());
		} catch (IllegalStateException e) {
			return AjaxJson.error(e.getMessage());
		}
		return AjaxJson.success("回复成功");
	}

	/**
	 * 采纳：仅「已回复」(1) 可采纳→已采纳(2)；按 rewardAmount 自动发放代币（与 save 发奖规则一致）。
	 */
	@ApiLog("意见反馈-采纳")
	@RequiresPermissions(value = {"ops:support:feedback:add", "ops:support:feedback:edit"}, logical = Logical.OR)
	@PostMapping(value = "adopt", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson adopt(@RequestBody Map<String, Object> body) {
		try {
			Object idObj = body == null ? null : body.get("id");
			String id = idObj == null ? null : idObj.toString();
			feedbackService.adoptById(id, parseBigDecimalFlexible(body == null ? null : body.get("rewardAmount")));
		} catch (IllegalArgumentException e) {
			return AjaxJson.error(e.getMessage());
		} catch (IllegalStateException e) {
			return AjaxJson.error(e.getMessage());
		} catch (RuntimeException e) {
			return AjaxJson.error(e.getMessage());
		}
		return AjaxJson.success("采纳成功");
	}

	/** 兼容 JSON 里 rewardAmount 为字符串 "99" 等情况 */
	private static BigDecimal parseBigDecimalFlexible(Object raw) {
		if (raw == null) {
			return null;
		}
		if (raw instanceof BigDecimal) {
			return (BigDecimal) raw;
		}
		if (raw instanceof Number) {
			return BigDecimal.valueOf(((Number) raw).doubleValue());
		}
		String s = raw.toString().trim();
		if (s.isEmpty()) {
			return null;
		}
		return new BigDecimal(s);
	}


	/**
	 * 批量删除意见反馈
	 */
	@ApiLog("删除意见反馈")
	@RequiresPermissions("ops:support:feedback:delete")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			feedbackService.delete(new Feedback(id));
		}
		return AjaxJson.success("删除意见反馈成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出意见反馈")
	@RequiresPermissions("feedback:feedback:export")
    @GetMapping("export")
    public AjaxJson exportFile(Feedback feedback, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "意见反馈"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Feedback> page = feedbackService.findPage(new Page<Feedback>(request, response, -1), feedback);
    		new ExportExcel("意见反馈", Feedback.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出意见反馈记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入意见反馈")
	@RequiresPermissions("feedback:feedback:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Feedback> list = ei.getDataList(Feedback.class);
			for (Feedback feedback : list){
				try{
					feedbackService.save(feedback);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条意见反馈记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条意见反馈记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入意见反馈失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入意见反馈数据模板
	 */
	@ApiLog("下载意见反馈模板")
	@RequiresPermissions("feedback:feedback:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "意见反馈数据导入模板.xlsx";
    		List<Feedback> list = Lists.newArrayList();
    		new ExportExcel("意见反馈数据", Feedback.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}