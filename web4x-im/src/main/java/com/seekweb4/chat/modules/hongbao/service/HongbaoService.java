package com.seekweb4.chat.modules.hongbao.service;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSONObject;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.delayedQueue.HongbaoDelayedQueueListener;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.hongbao.entity.Hongbao;
import com.seekweb4.chat.modules.hongbao.mapper.HongbaoMapper;

/**
 * 单聊红包记录Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class HongbaoService extends CrudService<HongbaoMapper, Hongbao> {

	@Autowired
	private MemberService memberService;
	@Autowired
	private RedisDelayedQueue redisDelayedQueue;

	public Hongbao get(String id) {
		return super.get(id);
	}
	
	public List<Hongbao> findList(Hongbao hongbao) {
		return super.findList(hongbao);
	}
	
	public Page<Hongbao> findPage(Page<Hongbao> page, Hongbao hongbao) {
		return super.findPage(page, hongbao);
	}
	
	@Transactional(readOnly = false)
	public void save(Hongbao hongbao) {
		super.save(hongbao);
	}
	
	@Transactional(readOnly = false)
	public void delete(Hongbao hongbao) {
		super.delete(hongbao);
	}

	/**
	 * 发红包
	 * @param hongbao
	 */
	@Transactional(readOnly = false)
	public void sendHb(Hongbao hongbao) {
		super.save(hongbao);
		Member member = memberService.get(hongbao.getUid2().getId());
		memberService.updateBalance(hongbao.getU(),hongbao.getMoney(),"0","发红包给 "+member.getNickname());
		redisDelayedQueue.addQueueHours("1-"+hongbao.getId(),24, HongbaoDelayedQueueListener.class);
	}
	/**
	 * 领红包
	 * @param hongbao
	 */
	@Transactional(readOnly = false)
	public void getHb(Hongbao hongbao,String msgId,String payload) {
		super.save(hongbao);
		memberService.updateBalance(hongbao.getUid2(),hongbao.getMoney(),"1",hongbao.getU().getNickname()+" 的红包");
		List<String> ids = new ArrayList<>();
		ids.add(hongbao.getUid2().getId());
		JSONObject jsonObject = JSONObject.parseObject(payload);
		jsonObject.put("lingquStatus","1");
		jsonObject.put("lqUsers",ids);
		ImUtils.updateMsg(hongbao.getU().getId(),Long.valueOf(msgId),jsonObject.toJSONString(),"2000");

	}
}