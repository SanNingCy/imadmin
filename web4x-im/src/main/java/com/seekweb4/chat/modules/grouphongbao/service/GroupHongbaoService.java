package com.seekweb4.chat.modules.grouphongbao.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson2.JSONObject;
import com.seekweb4.chat.api.utils.MemberUtils;
import com.seekweb4.chat.api.utils.RedPacketUtils;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.delayedQueue.HongbaoDelayedQueueListener;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.grouphongbaolog.entity.GroupHongbaoLog;
import com.seekweb4.chat.modules.grouphongbaolog.service.GroupHongbaoLogService;
import com.seekweb4.chat.modules.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.grouphongbao.entity.GroupHongbao;
import com.seekweb4.chat.modules.grouphongbao.mapper.GroupHongbaoMapper;

/**
 * 群红包Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class GroupHongbaoService extends CrudService<GroupHongbaoMapper, GroupHongbao> {

	@Autowired
	private MemberService memberService;
	@Autowired
	private RedisDelayedQueue redisDelayedQueue;
	@Autowired
	private GroupHongbaoLogService groupHongbaoLogService;

	public GroupHongbao get(String id) {
		return super.get(id);
	}
	
	public List<GroupHongbao> findList(GroupHongbao groupHongbao) {
		return super.findList(groupHongbao);
	}
	
	public Page<GroupHongbao> findPage(Page<GroupHongbao> page, GroupHongbao groupHongbao) {
		return super.findPage(page, groupHongbao);
	}
	
	@Transactional(readOnly = false)
	public void save(GroupHongbao groupHongbao) {
		super.save(groupHongbao);
	}
	
	@Transactional(readOnly = false)
	public void delete(GroupHongbao groupHongbao) {
		super.delete(groupHongbao);
	}

	/**
	 * 发红包
	 * @param groupHongbao
	 */
	@Transactional(readOnly = false)
	public void sendHongbao(GroupHongbao groupHongbao, Group group) {
		super.save(groupHongbao);
		memberService.updateBalance(groupHongbao.getU(),groupHongbao.getMoney(),"0","发放群（"+group.getName()+"）红包");
		redisDelayedQueue.addQueueHours("2-"+groupHongbao.getId(),24, HongbaoDelayedQueueListener.class);
	}
	/**
	 * 抢红包
	 * @param hongbao
	 */
	@Transactional(readOnly = false)
	public BigDecimal getHongbao(GroupHongbao hongbao,String msgId,String payload) {
		BigDecimal money = BigDecimal.ZERO;
		if("3".equals(hongbao.getType())){	//类型 1：拼手气 2：普通 3：专属
			money = hongbao.getMoney();
		}else if("1".equals(hongbao.getType())){
			String s = RedPacketUtils.unpackRedPacket(hongbao.getSymonet() + "", hongbao.getSycount());
			money = new BigDecimal(s);
//			if(hongbao.getSycount() > 1){
//				int i = hongbao.getSymonet().multiply(new BigDecimal(100)).intValue();
//				int suijiHongbao = getSuijiHongbao(1, i);
//				money = new BigDecimal(suijiHongbao).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
//			}else {
//				money = hongbao.getSymonet();
//			}
		}else if("2".equals(hongbao.getType())){
			money = hongbao.getMoney().divide(new BigDecimal(hongbao.getCount()),2,BigDecimal.ROUND_UP);
		}
		super.executeUpdateSql("update t_group_hongbao set symonet = symonet-"+money+",sycount = sycount-1 where id = '"+hongbao.getId()+"'");
		GroupHongbaoLog log = new GroupHongbaoLog();
		log.setU(MemberUtils.getMember());
		log.setBaoId(hongbao.getId());
		log.setGroup(hongbao.getGroup());
		log.setMoney(money);
		groupHongbaoLogService.save(log);
		memberService.updateBalance(MemberUtils.getMember(),money,"1","群聊（"+hongbao.getGroup().getName()+"）红包");

		GroupHongbao hb = super.get(hongbao.getId());
		GroupHongbaoLog log2 = new GroupHongbaoLog();
		log2.setBaoId(hongbao.getId());
		List<GroupHongbaoLog> list = groupHongbaoLogService.findList(log2);
		List<String> ids = new ArrayList<>();
		for(GroupHongbaoLog ll:list){
			ids.add(ll.getU().getId());
		}
		JSONObject jsonObject = JSONObject.parseObject(payload);
		jsonObject.put("lingquStatus",hb.getSycount()==0?"1":"0");
		jsonObject.put("lqUsers",ids);
		ImUtils.updateMsg(hongbao.getU().getId(),Long.valueOf(msgId),jsonObject.toJSONString(),"2000");

		return money;
	}

	public int getSuijiHongbao(int start,int end) {
		Random random = new Random();
		int randomNum = random.nextInt((end - start + 1)) + start;
		return randomNum;
	}
}