package com.seekweb4.chat.modules.groupitem.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.sdk.GroupAdmin;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.modules.member.entity.Member;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.groupitem.entity.GroupItem;
import com.seekweb4.chat.modules.groupitem.mapper.GroupItemMapper;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.group.service.GroupService;
import com.seekweb4.chat.image.service.WechatGroupAvatarService;
import com.seekweb4.chat.image.dto.WechatAvatarS3Response;
import java.util.ArrayList;

/**
 * 群成员Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class GroupItemService extends CrudService<GroupItemMapper, GroupItem> {
	
	@Autowired
	@Lazy
	private GroupService groupService;

	@Autowired
	@Lazy
	private GroupItemService groupItemService;
	
	@Autowired
	private WechatGroupAvatarService wechatGroupAvatarService;

	@Value("${config.accessory.aws.cdnDomain}")
	private String cdnDomain;

	public GroupItem get(String id) {
		return super.get(id);
	}
	
	public List<GroupItem> findList(GroupItem groupItem) {
		return super.findList(groupItem);
	}
	
	public Page<GroupItem> findPage(Page<GroupItem> page, GroupItem groupItem) {
		return super.findPage(page, groupItem);
	}
	
	@Transactional(readOnly = false)
	public void save(GroupItem groupItem) {
		super.save(groupItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(GroupItem groupItem) {
		super.delete(groupItem);
	}
	/**
	 * 根据用户ID更新相关群头像
	 * 查询相关群信息，调用群头像更新
	 *
	 * @param userId 用户ID
	 */
	@Transactional(readOnly = false)
	public void updateGroupAvatarByUserId(String userId) {
		GroupItem item = new GroupItem();
		Member member = new Member();
		member.setId(userId);
		item.setU(member);
		List<GroupItem> groups = groupItemService.findList(item);
		for (GroupItem groupItem : groups ) {
			if (groupItem.getGroup() != null) {
				this.updateGroupAvatar(groupItem.getGroup().getId());
			}
		}
	}


	/**
	 * 根据群ID更新群头像
	 * 查询群成员信息，筛选正常状态的用户，生成群头像并更新到群组
	 * 
	 * @param groupId 群组ID
	 */
	@Transactional(readOnly = false)
	public void updateGroupAvatar(String groupId) {
		long startTime = System.currentTimeMillis();
		logger.info("开始更新群头像:{}，开始时间",groupId,startTime);
		try {
			// 查询群组信息
			Group group = groupService.get(groupId);
			if (group == null) {
				return;
			}
			//如果群主自定义头像头像存在，则不执行生成；
			if (StringUtils.isNotBlank(group.getIcon())) {
				return;
			}

			// 查询群成员列表
			GroupItem queryItem = new GroupItem();
			queryItem.setGroup(group);
			List<GroupItem> groupItems = findList(queryItem);
			
			// 筛选状态为正常的用户，获取头像列表
			List<String> avatarUrls = groupItems.stream()
				.filter(item -> item.getU() != null)
				.map(item -> item.getU().getIcon()) // 获取用户头像
				.filter(icon -> icon != null && !icon.trim().isEmpty()) // 过滤空头像
				.limit(9) // 最多9个头像
				.collect(Collectors.toList());
			
			// 如果头像数量少于2个，不更新群头像
			if (avatarUrls.size() < 2) {
				return;
			}
			// 生成微信群组头像，等待异步任务完成
			CompletableFuture<WechatAvatarS3Response> future = wechatGroupAvatarService.generateWechatGroupAvatarWithS3(groupId,avatarUrls);
			
			try {
				// 等待异步任务完成，设置60秒超时
				WechatAvatarS3Response response = future.get(30, TimeUnit.SECONDS);
				if (response != null && response.getS3Url() != null) {
					String cdnUrl = response.getS3Url();
					//通知IM更新头像
					ImUtils.editGroup(group.getU().getId(), group.getId(), ProtoConstants.ModifyGroupInfoType.Modify_Group_Portrait,cdnUrl);
				} else {
					System.err.println("群头像生成失败，群ID: " + groupId + ", 响应为空或S3 URL为空");
				}
			} catch (TimeoutException e) {
				System.err.println("群头像生成超时，群ID: " + groupId + ", 错误: " + e.getMessage());
				future.cancel(true); // 取消超时的任务
			} catch (Exception e) {
				System.err.println("群头像生成异常，群ID: " + groupId + ", 错误: " + e.getMessage());
			}
		} catch (Exception e) {
			// 记录错误日志，但不抛出异常，避免影响主流程
			System.err.println("更新群头像失败，群ID: " + groupId + ", 错误: " + e.getMessage());
		}
		logger.info("完成更新群头像:{}，执行时间:{}",groupId,System.currentTimeMillis()-startTime);
	}
	
	/**
	 * 构建更新群组头像的SQL语句
	 * 使用安全的字符串转义，防止SQL注入
	 * 
	 * @param groupId 群组ID
	 * @param iconUrl 头像URL
	 * @return 安全的SQL语句
	 */
	private String buildUpdateGroupIconSql(String groupId, String iconUrl) {
		// 转义单引号，防止SQL注入
		String escapedGroupId = groupId.replace("'", "''");
		String escapedIconUrl = iconUrl.replace("'", "''");
		
		// 构建SQL语句
		return "UPDATE t_group SET icon = '" + escapedIconUrl + "' WHERE id = '" + escapedGroupId + "'";
	}

	/**
	 * 解除群成员禁言
	 * 
	 * @param id 群成员ID
	 * @return 是否成功
	 */
	@Transactional(readOnly = false)
	public boolean unbanMember(String id) {
		// 获取群成员信息
		GroupItem groupItem = this.get(id);
		if (groupItem == null) {
			return false;
		}
		
		// 检查是否被禁言
		if (!"1".equals(groupItem.getIsjy())) {
			// 未被禁言，直接返回成功
			return true;
		}
		
		// 获取群组信息
		if (groupItem.getGroup() == null || StringUtils.isBlank(groupItem.getGroup().getId())) {
			return false;
		}
		
		Group group = groupService.get(groupItem.getGroup().getId());
		if (group == null) {
			return false;
		}
		
		// 更新数据库：解除禁言
		int result = mapper.unbanMember(id);
		if (result <= 0) {
			return false;
		}
		
		// 通知IM解除禁言
		if (groupItem.getU() != null && StringUtils.isNotBlank(groupItem.getU().getId())) {
			List<String> userIds = new ArrayList<>();
			userIds.add(groupItem.getU().getId());
			ImUtils.jinyan(group.getU().getId(), group.getId(), userIds, false);
		}
		
		return true;
	}
	
}