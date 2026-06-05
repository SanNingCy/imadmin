package com.seekweb4.chat.delayedQueue;

import com.seekweb4.chat.modules.balancelog.entity.BalanceLog;
import com.seekweb4.chat.modules.balancelog.service.BalanceLogService;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.zhuangzhang.entity.Zhuangzhang;
import com.seekweb4.chat.modules.zhuangzhang.service.ZhuangzhangService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 转账监听退还
 */
@Component
@Slf4j
public class ZhuanzhangDelayedQueueListener implements RedisDelayedQueueListener<String> {
    @Autowired
    private ZhuangzhangService zhuangzhangService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private BalanceLogService balanceLogService;

    @Override
    public void invoke(String id) {
        Zhuangzhang hongbao = zhuangzhangService.get(id);
        if(hongbao.getShouTime()==null){
            hongbao.setTuiTime(new Date());
            zhuangzhangService.save(hongbao);
            memberService.executeUpdateSql("UPDATE t_member SET balance = balance + "+hongbao.getMoney()+" WHERE id = '"+hongbao.getU().getId()+"'");
            BalanceLog log = new BalanceLog();
            log.setU(hongbao.getU());
            log.setMoney(hongbao.getMoney());
            log.setTitle("转账退还");
            log.setInfo("发给"+hongbao.getUid2().getNickname()+"的转账超时未收款退还");
            log.setType("2");
            log.setState("1");
            balanceLogService.save(log);
        }
    }
}
