package com.seekweb4.chat.modules.memberChangeLog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.seekweb4.chat.common.annotation.ApiLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.memberChangeLog.service.MemberChangeLogService;
import com.seekweb4.chat.modules.memberChangeLog.vo.MemberChangeLogVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户信息修改记录统一Controller
 * 统一查询修改昵称、密码、手机号、支付密码等记录
 * @author system
 * @version 2025-12-05
 */
@RestController
@RequestMapping(value = "/memberChangeLog/memberChangeLog")
public class MemberChangeLogController extends BaseController {

	@Autowired
	private MemberChangeLogService memberChangeLogService;

	/**
	 * 用户信息修改记录列表数据（统一查询）
	 * @param changeType 修改类型（可选：nickname-修改昵称, password-修改密码, phone-修改手机号, paypwd-修改支付密码）
	 * @param userId 用户ID（可选，用于筛选特定用户的记录）
	 * @param uId 用户ID（可选，与userId功能相同，用于兼容）
	 * @param uIdno 用户IDNO（可选）
	 * @param uNickname 用户昵称（可选）
	 * @param beginCreateDate 开始创建时间（可选）
	 * @param endCreateDate 结束创建时间（可选）
	 */
	@ApiLog("查询用户信息修改记录列表")
//	@RequiresPermissions("memberChangeLog:memberChangeLog:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(
			@RequestParam(required = false) String changeType,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) String uId,
			@RequestParam(required = false) String uIdno,
			@RequestParam(required = false) String uNickname,
			@RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date beginCreateDate,
			@RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endCreateDate,
			HttpServletRequest request, 
			HttpServletResponse response) {
		Page<MemberChangeLogVO> page = new Page<MemberChangeLogVO>(request, response);
		Page<MemberChangeLogVO> result = memberChangeLogService.findPage(page, changeType, userId, uId, uIdno, uNickname, beginCreateDate, endCreateDate);
		return AjaxJson.success().put("page", result);
	}

	/**
	 * 根据ID查询用户信息修改记录
	 * @param id 记录ID
	 * @param changeType 修改类型（必填：nickname-修改昵称, password-修改密码, phone-修改手机号, paypwd-修改支付密码）
	 */
	@ApiLog("查询用户信息修改记录")
//	@RequiresPermissions("social:security:account-change-logs:view")
//	@RequiresPermissions("memberChangeLog:memberChangeLog:view")
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(
			@RequestParam(required = true) String id,
			@RequestParam(required = true) String changeType) {
		if (StringUtils.isBlank(id)) {
			return AjaxJson.error("id不能为空");
		}
		if (StringUtils.isBlank(changeType)) {
			return AjaxJson.error("changeType不能为空");
		}
		MemberChangeLogVO vo = memberChangeLogService.get(id, changeType);
		if (vo != null) {
			return AjaxJson.success().put("memberChangeLog", vo);
		}
		return AjaxJson.error("记录不存在");
	}

}

