package com.seekweb4.chat.task;

import com.seekweb4.chat.api.utils.QrCodeUtil;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.monitor.entity.Task;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 刷新登录码
 */
@DisallowConcurrentExecution
public class MemberQrcode2Task extends Task {
    @Autowired
    private MemberService memberService;

    @Override
    public void run() {
        Member m = new Member();
        m.setDataScope("and a.qrcode is null OR a.qrcode2 is null");
        List<Member> list = memberService.findList(m);
        for(Member member:list){
            if(StringUtils.isBlank(member.getQrcode())){
                member.setQrcode(QrCodeUtil.getCode("1-"+member.getId(),"/memberCode"));
            }
            if(StringUtils.isBlank(member.getQrcode2())){
                member.setQrcode2(QrCodeUtil.getCode("3-"+member.getIdno(),"/memberCode"));
            }
            memberService.save(member);
        }
    }
}
