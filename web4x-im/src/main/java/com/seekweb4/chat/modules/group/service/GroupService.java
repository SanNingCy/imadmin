package com.seekweb4.chat.modules.group.service;

import java.util.ArrayList;
import java.util.List;

import com.seekweb4.chat.api.utils.MemberUtils;
import com.seekweb4.chat.api.utils.QrCodeUtil;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.common.utils.IdGen;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.groupitem.entity.GroupItem;
import com.seekweb4.chat.modules.groupitem.service.GroupItemService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.group.mapper.GroupMapper;

/**
 * 群组信息Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class GroupService extends CrudService<GroupMapper, Group> {
	@Autowired
	@Lazy
	private GroupItemService groupItemService;
	@Autowired
	private MemberService memberService;

	public Group get(String id) {
		return super.get(id);
	}
	
	public List<Group> findList(Group group) {
		return super.findList(group);
	}
	
	public Page<Group> findPage(Page<Group> page, Group group) {
		return super.findPage(page, group);
	}
	
	@Transactional(readOnly = false)
	public void save(Group group) {
		super.save(group);
	}


	@Transactional(readOnly = false)
	public void delete(Group group) {
		super.delete(group);
	}

	/**
	 * 创建群聊
	 * @param group
	 */
	@Transactional(readOnly = false)
	public void addGroup(Group group,String uids) {
		String[] split = uids.split("[|]", 0);
		List<String> ids = new ArrayList<>();
		for(String id:split){
			ids.add(id);
		}
		ids.add(ImUtils.robot_id);
		String s = ImUtils.creatrGroup(group.getU().getId(), group.getName(), ids);
		if(StringUtils.isBlank(s)){
			throw new RuntimeException("im群组创建失败");
		}
		group.setId(s);
		group.setQrcode(QrCodeUtil.getCode("2-"+group.getId(),"/groupCode"));
		group.setIdno(IdGen.getNumber(7));
		super.save(group);
		Member member = MemberUtils.getMember();
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(group.getU());
		item.setNickname(member.getNickname());
		item.setType("1");
		item.setIsjy("0");
		groupItemService.save(item);
		for(String id:split) {
			Member mem = memberService.get(id);
			GroupItem item2 = new GroupItem();
			item2.setGroup(group);
			item2.setU(mem);
			item2.setNickname(mem.getNickname());
			item2.setType("3");
			item2.setIsjy("0");
			groupItemService.save(item2);
		}
//		if(!"OK".equals(res.getString("ActionStatus"))){
//			throw new RuntimeException("im群组创建失败");
//		}
	}

	/**
	 * 解散群聊
	 * @param group
	 */
	@Transactional(readOnly = false)
	public void delGroup(Group group) {
		groupItemService.executeDeleteSql("delete from t_group_item where group_id = '"+group.getId()+"'");
		super.deleteByLogic(group);
		ImUtils.delGroup(group.getU().getId(),group.getId());
	}
	/**
	 * 转让群主
	 * @param group
	 */
	@Transactional(readOnly = false)
	public void turnQz(Group group,String uid2) {
		super.executeUpdateSql("update t_group set uid = '"+uid2+"' where id = '"+group.getId()+"'");
		groupItemService.executeUpdateSql("update t_group_item set type = '3' where group_id = '"+group.getId()+"' and uid = '"+MemberUtils.getUid()+"'");
		groupItemService.executeUpdateSql("update t_group_item set type = '1' where group_id = '"+group.getId()+"' and uid = '"+uid2+"'");
		ImUtils.transferGroup(group.getU().getId(),group.getId(),uid2);
	}
}