package com.seekweb4.chat.modules.zhuangzhang.service;

import java.util.List;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
import com.seekweb4.chat.delayedQueue.ZhuanzhangDelayedQueueListener;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.zhuangzhang.entity.Zhuangzhang;
import com.seekweb4.chat.modules.zhuangzhang.mapper.ZhuangzhangMapper;

/**
 * 转账记录Service
 * @author lixinapp
 * @version 2024-09-22
 */
@Service
@Transactional(readOnly = true)
public class ZhuangzhangService extends CrudService<ZhuangzhangMapper, Zhuangzhang> {

	@Autowired
	private MemberService memberService;
	@Autowired
	private RedisDelayedQueue redisDelayedQueue;

	public Zhuangzhang get(String id) {
		return super.get(id);
	}
	
	public List<Zhuangzhang> findList(Zhuangzhang zhuangzhang) {
		return super.findList(zhuangzhang);
	}
	
	public Page<Zhuangzhang> findPage(Page<Zhuangzhang> page, Zhuangzhang zhuangzhang) {
		return super.findPage(page, zhuangzhang);
	}
	
	@Transactional(readOnly = false)
	public void save(Zhuangzhang zhuangzhang) {
		super.save(zhuangzhang);
	}

	@Transactional(readOnly = false)
	public void delete(Zhuangzhang zhuangzhang) {
		super.delete(zhuangzhang);
	}

	/**
	 * 转账
	 * @param zhuangzhang
	 */
	@Transactional(readOnly = false)
	public void zhuanzhang(Zhuangzhang zhuangzhang) {
		super.save(zhuangzhang);
		Member member = memberService.get(zhuangzhang.getUid2().getId());
		memberService.updateBalance(zhuangzhang.getU(),zhuangzhang.getMoney(),"0","转账给 "+member.getNickname());
		redisDelayedQueue.addQueueHours(zhuangzhang.getId(),24, ZhuanzhangDelayedQueueListener.class);
	}
	/**
	 * 领取转账
	 * @param zhuangzhang
	 */
	@Transactional(readOnly = false)
	public void getZz(Zhuangzhang zhuangzhang,String msgId,String payload) {
		super.save(zhuangzhang);
		memberService.updateBalance(zhuangzhang.getUid2(),zhuangzhang.getMoney(),"1",zhuangzhang.getU().getNickname()+" 的转账");

		ImUtils.updateMsg(zhuangzhang.getU().getId(),Long.valueOf(msgId),payload,"2001");
	}
}