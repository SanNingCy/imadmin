package com.seekweb4.chat.modules.grouphuanying.service;

import java.util.List;
import java.util.Random;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.group.entity.Group;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.grouphuanying.entity.GroupHuanying;
import com.seekweb4.chat.modules.grouphuanying.mapper.GroupHuanyingMapper;

/**
 * 群欢迎语Service
 * @author lixinapp
 * @version 2025-03-24
 */
@Service
@Transactional(readOnly = true)
public class GroupHuanyingService extends CrudService<GroupHuanyingMapper, GroupHuanying> {

	public GroupHuanying get(String id) {
		return super.get(id);
	}
	
	public List<GroupHuanying> findList(GroupHuanying groupHuanying) {
		return super.findList(groupHuanying);
	}
	
	public Page<GroupHuanying> findPage(Page<GroupHuanying> page, GroupHuanying groupHuanying) {
		return super.findPage(page, groupHuanying);
	}
	
	@Transactional(readOnly = false)
	public void save(GroupHuanying groupHuanying) {
		super.save(groupHuanying);
	}
	
	@Transactional(readOnly = false)
	public void delete(GroupHuanying groupHuanying) {
		super.delete(groupHuanying);
	}

	/**
	 * 发送群欢迎语
	 * @param id
	 */
	public void sendHuanying(String id){
		GroupHuanying huanying = new GroupHuanying();
		huanying.setGroup(new Group(id));
		List<GroupHuanying> list = super.findList(huanying);
		if(!list.isEmpty()){
			Random random = new Random();
			huanying = list.get(random.nextInt(list.size()));
			if(StringUtils.isNotBlank(huanying.getTitle())){
				ImUtils.sendGroupTxtMsg(ImUtils.robot_id,huanying.getTitle(),id);
			}
			if("2".equals(huanying.getType())){	//类型 1：纯文本 2：文本+图片 3：文本+视频 4：文本+文件
				if(StringUtils.isNotBlank(huanying.getImgs())){
					String[] split = huanying.getImgs().split("[|]", 0);
					for(String pic:split){
						ImUtils.sendGroupPicMsg(ImUtils.robot_id,pic,id);
					}
				}
			}else if("3".equals(huanying.getType())){
				if(StringUtils.isNotBlank(huanying.getVideo())){
					ImUtils.sendGroupVideoMsg(ImUtils.robot_id,huanying.getVideo(),id,huanying.getMiao());
				}
			}else if("4".equals(huanying.getType())){
				if(StringUtils.isNotBlank(huanying.getPdf())){
					ImUtils.sendGroupFileMsg(ImUtils.robot_id,huanying.getPdf(),id,huanying.getName(),huanying.getSize());
				}
			}
		}
	}

	
}