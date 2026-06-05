package com.seekweb4.chat.modules.notification.web;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.repository.AccessoryRepository;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.notification.entity.SysNotification;
import com.seekweb4.chat.modules.notification.service.SysNotificationService;
import lombok.extern.slf4j.Slf4j;
// import org.apache.shiro.authz.annotation.Logical;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/notif/admin/")
//@RequestMapping("/notice/admin/")
public class NotificationAdminController extends BaseController {

	@Autowired
	private SysNotificationService notificationService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private AccessoryRepository accessoryRepository;

	@ApiLog("公告列表")
	//@RequiresPermissions("notification:admin:list")
	@PostMapping("list")
	public AjaxJson list(SysNotification notification, HttpServletRequest request, HttpServletResponse response) {
		Page<SysNotification> page = notificationService.findPage(new Page<>(request, response), notification);
		fillTargetUserIdnos(page.getList());
		return AjaxJson.success().put("page",page);
	}

	@ApiLog("查询公告")
	@RequiresPermissions(value={"ops:content:announcement:view","ops:content:announcement:add","ops:content:announcement:edit"}, logical= Logical.OR)
	@GetMapping("get/{id}")
	public AjaxJson get(@PathVariable("id") String nid) {
		if (StringUtils.isBlank(nid)) { return AjaxJson.error("id不能为空"); }
		SysNotification data = notificationService.get(nid);
		if (data != null) {
			fillTargetUserIdnos(java.util.Collections.singletonList(data));
		}
		return AjaxJson.success().put("data", data);
	}

	private void fillTargetUserIdnos(List<SysNotification> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		Map<String, String> userIdToIdno = new HashMap<>();
		for (SysNotification n : list) {
			if (n == null || StringUtils.isBlank(n.getTargetUserIds())) {
				n.setTargetUserIdnos(new ArrayList<>());
				continue;
			}
			List<String> userIds;
			try {
				userIds = JSONArray.parseArray(n.getTargetUserIds(), String.class);
			} catch (Exception e) {
				n.setTargetUserIdnos(new ArrayList<>());
				continue;
			}
			List<String> idnos = new ArrayList<>();
			if (userIds != null) {
				for (String uid : userIds) {
					if (StringUtils.isBlank(uid)) {
						continue;
					}
					String idno = userIdToIdno.get(uid);
					if (idno == null) {
						Member m = memberService.selectBasicById(uid);
						idno = m == null ? "" : m.getIdno();
						userIdToIdno.put(uid, idno == null ? "" : idno);
					}
					if (StringUtils.isNotBlank(idno)) {
						idnos.add(idno);
					}
				}
			}
			n.setTargetUserIdnos(idnos);
		}
	}

	@ApiLog("保存/更新公告")
	@RequiresPermissions(value={"ops:content:announcement:add","ops:content:announcement:edit"}, logical= Logical.OR)
	@PostMapping("save")
	public AjaxJson save(@RequestBody SysNotification notice) {
		log.info("保存/更新公告,{}", JSON.toJSONString( notice));

		if (StringUtils.isBlank(notice.getPreviewContent())) {
			return AjaxJson.error("预览内容不能为空");
		}
		if (StringUtils.isBlank(notice.getContentUrl())) {
			return AjaxJson.error("公告富文本URL不能为空");
		}
		if (StringUtils.isBlank(notice.getContent())) {
			return AjaxJson.error("公告富文本不能为空");
		}
		if (StringUtils.isBlank(notice.getNoticeTitle())) {
			return AjaxJson.error("公告标题不能为空");
		}
		if (notice.getForcePopup() == null) {
			return AjaxJson.error("是否强制弹出不能为空");
		}
		if (notice.getStatus() == null) {
			return AjaxJson.error("公告状态不能为空");
		}
		if (notice.getStartTime() == null) {
			return AjaxJson.error("开始时间不能为空");
		}
		if (notice.getEndTime() == null) {
			return AjaxJson.error("结束时间不能为空");
		}
		if (notice.getIntervalNumber() == null) {
			return AjaxJson.error("弹窗时间间隔不能为空");
		}
		if (notice.getIntervalUnit() == null) {
			return AjaxJson.error("时间单位不能为空");
		}
		if (notice.getNumber() == null) {
			return AjaxJson.error("通知次数不能为空");
		}
		String errMsg = beanValidator(notice);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		
		// 保存前记录ID和旧配置，用于判断是新增还是更新，以及关键配置是否变化
		boolean isUpdate = !notice.getIsNewRecord() && StringUtils.isNotBlank(notice.getId());
		String notificationId = notice.getId();
		SysNotification oldNotice = null;
		boolean needClearCache = false;
		
		if (isUpdate && StringUtils.isNotBlank(notificationId)) {
			oldNotice = notificationService.get(notificationId);
			if (oldNotice != null) {
				// 检查影响推送逻辑的关键配置是否发生变化
				// 1. 公告状态变化
				boolean statusChanged = !notice.getStatus().equals(oldNotice.getStatus());
				// 2. 时间间隔变化（间隔时间变化时，需要清理时间窗口缓存，因为时间窗口是基于间隔时间计算的）
				boolean intervalChanged = !notice.getIntervalNumber().equals(oldNotice.getIntervalNumber()) 
					|| !notice.getIntervalUnit().equals(oldNotice.getIntervalUnit());
				// 3. 推送次数变化（推送次数变化时，需要清理推送次数和时间窗口缓存，重新开始计算）
				boolean numberChanged = !notice.getNumber().equals(oldNotice.getNumber());
				// 4. 开始时间变化
				boolean startTimeChanged = (notice.getStartTime() == null && oldNotice.getStartTime() != null)
					|| (notice.getStartTime() != null && oldNotice.getStartTime() == null)
					|| (notice.getStartTime() != null && oldNotice.getStartTime() != null 
						&& !notice.getStartTime().equals(oldNotice.getStartTime()));
				// 5. 结束时间变化
				boolean endTimeChanged = (notice.getEndTime() == null && oldNotice.getEndTime() != null)
					|| (notice.getEndTime() != null && oldNotice.getEndTime() == null)
					|| (notice.getEndTime() != null && oldNotice.getEndTime() != null 
						&& !notice.getEndTime().equals(oldNotice.getEndTime()));
				// 6. 目标用户列表变化（影响客户端是否命中该公告）
				boolean targetUsersChanged = !Objects.equals(
						normalizeTargetUserIdsForCompare(notice.getTargetUserIds()),
						normalizeTargetUserIdsForCompare(oldNotice.getTargetUserIds()));
				
				// 如果任何关键配置发生变化，需要清理缓存
				needClearCache = statusChanged || intervalChanged || numberChanged || startTimeChanged || endTimeChanged || targetUsersChanged;
				
				if (needClearCache) {
					StringBuilder changes = new StringBuilder();
					if (statusChanged) changes.append("状态、");
					if (intervalChanged) changes.append("时间间隔/单位、");
					if (numberChanged) changes.append("推送次数、");
					if (startTimeChanged) changes.append("开始时间、");
					if (endTimeChanged) changes.append("结束时间、");
					if (targetUsersChanged) changes.append("目标用户、");
					if (changes.length() > 0) {
						changes.setLength(changes.length() - 1); // 移除最后的逗号
					}
					log.info("公告关键配置已更新（{}），将清理Redis缓存并重置推送记录。公告ID: {}", changes.toString(), notificationId);
				}
			}
		}
		
		notificationService.save(notice);
		
		// 检查公告是否在有效期内（用于判断是否支持在有效期内关闭或开启）
		Date now = new Date();
		boolean isInValidPeriod = true;
		if (notice.getStartTime() != null && now.before(notice.getStartTime())) {
			// 还未到开始时间
			isInValidPeriod = false;
		}
		if (notice.getEndTime() != null && now.after(notice.getEndTime())) {
			// 已超过结束时间
			isInValidPeriod = false;
		}
		
		// 如果是更新操作且关键配置发生变化，清理Redis缓存，使新配置立即生效
		if (isUpdate && StringUtils.isNotBlank(notificationId) && needClearCache) {
			clearNotificationCache(notificationId);
			log.info("已清理Redis缓存，新配置将立即生效。公告ID: {}", notificationId);
		}
		
		// 处理公告状态变更：在有效期内支持关闭或开启公告
		if (StringUtils.isNotBlank(notificationId) && isUpdate && oldNotice != null) {
			boolean statusChanged = !notice.getStatus().equals(oldNotice.getStatus());
			
			if (statusChanged) {
				// 状态发生变化
				if ("0".equals(notice.getStatus())) {
					// 状态变为关闭：立即停止推送
					if (!needClearCache) {
						// 如果其他配置没变，需要单独清理缓存
						clearNotificationCache(notificationId);
					}
					if (isInValidPeriod) {
						log.info("公告在有效期内被关闭，已清理Redis缓存并停止推送。公告ID: {}, 标题: {}", 
							notificationId, notice.getNoticeTitle());
					} else {
						log.info("公告状态变为关闭，已清理Redis缓存并停止推送。公告ID: {}, 标题: {}", 
							notificationId, notice.getNoticeTitle());
					}
				} else if ("1".equals(notice.getStatus())) {
					// 状态变为开启：立即开始推送（重置推送记录）
					if (!needClearCache) {
						// 如果其他配置没变，需要单独清理缓存
						clearNotificationCache(notificationId);
					}
					if (isInValidPeriod) {
						log.info("公告在有效期内被开启，已清理Redis缓存并重置推送记录，公告将立即开始推送。公告ID: {}, 标题: {}", 
							notificationId, notice.getNoticeTitle());
					} else {
						log.info("公告状态变为开启，已清理Redis缓存并重置推送记录。公告ID: {}, 标题: {}", 
							notificationId, notice.getNoticeTitle());
					}
				}
			} else {
				// 状态没有变化，但如果是关闭状态，确保清理缓存
				if ("0".equals(notice.getStatus()) && !needClearCache) {
					clearNotificationCache(notificationId);
					log.debug("公告状态为关闭，已清理Redis缓存。公告ID: {}", notificationId);
				}
			}
		} else if (!isUpdate) {
			// 新增公告：根据状态清理缓存
			if ("0".equals(notice.getStatus())) {
				clearNotificationCache(notificationId);
				log.debug("新增公告状态为关闭，已清理Redis缓存。公告ID: {}", notificationId);
			} else if ("1".equals(notice.getStatus())) {
				clearNotificationCache(notificationId);
				log.debug("新增公告状态为开启，已清理Redis缓存。公告ID: {}", notificationId);
			}
		}
		
		return AjaxJson.success("保存成功");
	}

	@ApiLog("删除公告")
	//@RequiresPermissions("notification:admin:del")
	@RequiresPermissions("ops:content:announcement:delete")
	@DeleteMapping("delete/{ids}")
	public AjaxJson delete(@PathVariable("ids") String ids) {
		String[] idArray = ids.split(",");
		for (String id : idArray) {
			// 删除前清理Redis缓存
			clearNotificationCache(id);
			
			SysNotification n = new SysNotification();
			n.setId(id);
			notificationService.delete(n);
		}
		return AjaxJson.success("删除成功");
	}
	
	/**
	 * 清理公告相关的Redis缓存
	 * 当公告配置更新时，需要清理推送相关的缓存，使新配置立即生效
	 * 
	 * <p><strong>说明：</strong></p>
	 * <ul>
	 *   <li>此方法清理的缓存与 {@link  “ api端的 NotificationPushScheduler”} 使用的缓存是同一个</li>
	 *   <li>虽然使用了不同的工具类（StringRedisUtils vs RedisUtil），但它们都使用同一个 StringRedisTemplate bean</li>
	 *   <li>因此可以正常清理 NotificationPushScheduler 写入的缓存数据</li>
	 * </ul>
	 * 
	 * <p><strong>清理的缓存包括：</strong></p>
	 * <ul>
	 *   <li>上次推送时间：用于判断是否达到推送间隔</li>
	 *   <li>推送次数：当前时间窗口内的推送次数（按时间间隔计算）</li>
	 *   <li>时间窗口开始时间：推送次数计算的时间窗口起始时间（当时间间隔变化时，旧的时间窗口需要清理）</li>
	 * </ul>
	 * 
	 * @param notificationId 公告ID
	 */
	private void clearNotificationCache(String notificationId) {
		if (StringUtils.isBlank(notificationId)) {
			return;
		}
		
		try {
			StringRedisUtils redisUtils = StringRedisUtils.getInstance();
			
			// 清理上次推送时间（与 NotificationPushScheduler 使用的 key 格式一致）
			String lastPushKey = "sys:notification:push:last:" + notificationId;
			redisUtils.delete(lastPushKey);
			
			// 清理推送次数（与 NotificationPushScheduler 使用的 key 格式一致）
			// 注意：推送次数是按时间间隔计算的，每个时间间隔内重新计算
			String pushCountKey = "sys:notification:push:count:" + notificationId;
			redisUtils.delete(pushCountKey);
			
			// 清理推送次数的时间窗口开始时间（与 NotificationPushScheduler 使用的 key 格式一致）
			// 重要：当时间间隔（intervalNumber 或 intervalUnit）变化时，必须清理时间窗口缓存
			// 因为时间窗口是基于间隔时间计算的，间隔时间变化后，旧的时间窗口就没有意义了
			String pushCountWindowKey = "sys:notification:push:count:window:" + notificationId;
			redisUtils.delete(pushCountWindowKey);
			
			log.debug("已清理公告Redis缓存（包括上次推送时间、推送次数、时间窗口）。公告ID: {}", notificationId);
		} catch (Exception e) {
			log.error("清理公告Redis缓存失败。公告ID: {}", notificationId, e);
		}
	}

	/** null、空白、[] 视为「未限定用户」，与反序列化规则一致，便于比较是否变更 */
	private static String normalizeTargetUserIdsForCompare(String raw) {
		if (StringUtils.isBlank(raw)) {
			return null;
		}
		String t = raw.trim();
		if ("[]".equals(t)) {
			return null;
		}
		return t;
	}

	@ApiLog("上传公告HTML")
	//@RequiresPermissions(value={"notification:admin:upset"}, logical= Logical.OR)
	@PostMapping("notice/upload")
	public AjaxJson uploadContent(@RequestParam("file") MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return AjaxJson.error("文件为空");
		}
		String fileName = UUID.randomUUID().toString()+ "/" + file.getOriginalFilename();
		String url = accessoryRepository.save(file, "notification", fileName);
		if (url == null) {
			return AjaxJson.error("上传失败");
		}
		return AjaxJson.success().put("url", url);
	}
}
