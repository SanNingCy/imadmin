package com.seekweb4.chat.modules.redPacketTransaction.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.seekweb4.chat.common.annotation.ApiLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.redPacketTransaction.service.RedPacketTransactionService;
import com.seekweb4.chat.modules.redPacketTransaction.vo.RedPacketTransactionVO;

import java.util.Date;

/**
 * 红包交易记录统一Controller
 * 统一查询单聊红包和群聊红包记录
 * @author system
 * @version 2025-12-08
 */
@RestController
@RequestMapping(value = "/redPacketTransaction/redPacketTransaction")
public class RedPacketTransactionController extends BaseController {

	@Autowired
	private RedPacketTransactionService redPacketTransactionService;

	/**
	 * 红包交易记录列表数据（统一查询）
	 * @param packetType 红包类型（可选：single-单聊红包, group-群聊红包）
	 * @param userId 用户ID（可选，用于筛选特定用户的记录）
	 * @param uIdno 发红包用户的IDNO（可选）
	 * @param uNickname 发红包用户的昵称（可选）
	 * @param uid2Idno 接收红包用户的IDNO（可选，仅用于单聊红包）
	 * @param uid2Nickname 接收红包用户的昵称（可选，仅用于单聊红包）
	 * @param beginCreateDate 开始创建时间（可选）
	 * @param endCreateDate 结束创建时间（可选）
	 */
	@ApiLog("查询红包交易记录列表")
//	@RequiresPermissions("redPacketTransaction:redPacketTransaction:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(
			@RequestParam(required = false) String packetType,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) String uIdno,
			@RequestParam(required = false) String uNickname,
			@RequestParam(required = false) String uid2Idno,
			@RequestParam(required = false) String uid2Nickname,
			@RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date beginCreateDate,
			@RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endCreateDate,
			HttpServletRequest request, 
			HttpServletResponse response) {
		Page<RedPacketTransactionVO> page = new Page<RedPacketTransactionVO>(request, response);
		Page<RedPacketTransactionVO> result = redPacketTransactionService.findPage(page, packetType, userId, uIdno, uNickname, uid2Idno, uid2Nickname, beginCreateDate, endCreateDate);
		return AjaxJson.success().put("page", result);
	}

	/**
	 * 根据ID查询红包交易记录
	 * @param id 记录ID
	 * @param packetType 红包类型（必填：single-单聊红包, group-群聊红包）
	 */
	@ApiLog("查询红包交易记录")
//	@RequiresPermissions("asset:trade:packet:view")
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(
			@RequestParam(required = true) String id,
			@RequestParam(required = true) String packetType) {
		if (StringUtils.isBlank(id)) {
			return AjaxJson.error("id不能为空");
		}
		if (StringUtils.isBlank(packetType)) {
			return AjaxJson.error("packetType不能为空");
		}
		RedPacketTransactionVO vo = redPacketTransactionService.get(id, packetType);
		if (vo != null) {
			return AjaxJson.success().put("redPacketTransaction", vo);
		}
		return AjaxJson.error("记录不存在");
	}

}

