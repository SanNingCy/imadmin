package com.seekweb4.chat.delayedQueue;

import com.seekweb4.chat.modules.balancelog.entity.BalanceLog;
import com.seekweb4.chat.modules.balancelog.service.BalanceLogService;
import com.seekweb4.chat.modules.grouphongbao.entity.GroupHongbao;
import com.seekweb4.chat.modules.grouphongbao.service.GroupHongbaoService;
import com.seekweb4.chat.modules.hongbao.entity.Hongbao;
import com.seekweb4.chat.modules.hongbao.service.HongbaoService;
import com.seekweb4.chat.modules.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包监听退还
 */
@Component
@Slf4j
public class HongbaoDelayedQueueListener implements RedisDelayedQueueListener<String> {
    @Autowired
    private HongbaoService hongbaoService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private GroupHongbaoService groupHongbaoService;
    @Autowired
    private BalanceLogService balanceLogService;

    @Override
    public void invoke(String id) {
        String[] split = id.split("[-]", 0);
        String hid = split[1];
        if("1".equals(split[0])){//单聊红包
            Hongbao hongbao = hongbaoService.get(hid);
            if(hongbao.getShouTime()==null){
                hongbao.setTuiTime(new Date());
                hongbaoService.save(hongbao);
                memberService.executeUpdateSql("UPDATE t_member SET balance = balance + "+hongbao.getMoney()+" WHERE id = '"+hongbao.getU().getId()+"'");
                BalanceLog log = new BalanceLog();
                log.setU(hongbao.getU());
                log.setMoney(hongbao.getMoney());
                log.setTitle("红包退还");
                log.setInfo("发给"+hongbao.getUid2().getNickname()+"的红包超时未领取退还");
                log.setType("1");
                log.setState("1");
                balanceLogService.save(log);
            }
        }else { //群聊红包
            GroupHongbao hongbao = groupHongbaoService.get(hid);
            if(hongbao.getSymonet().compareTo(BigDecimal.ZERO) == 1){
                hongbao.setTuiMoney(hongbao.getSymonet());
                hongbao.setTuiTime(new Date());
                groupHongbaoService.save(hongbao);
                memberService.executeUpdateSql("UPDATE t_member SET balance = balance + "+hongbao.getSymonet()+" WHERE id = '"+hongbao.getU().getId()+"'");
                BalanceLog log = new BalanceLog();
                log.setU(hongbao.getU());
                log.setMoney(hongbao.getSymonet());
                log.setTitle("红包退还");
                log.setInfo("群聊（"+hongbao.getGroup().getName()+"）红包超时未领取完退还");
                log.setType("1");
                log.setState("1");
                balanceLogService.save(log);
            }
        }
    }
}
