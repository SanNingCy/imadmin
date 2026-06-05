package com.seekweb4.chat.modules.member.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import com.seekweb4.chat.api.utils.QrCodeUtil;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.utils.IdGen;
import com.seekweb4.chat.common.utils.MD5Util;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.modules.balancelog.entity.BalanceLog;
import com.seekweb4.chat.modules.balancelog.service.BalanceLogService;
import com.seekweb4.chat.modules.customer.entity.Customer;
import com.seekweb4.chat.modules.customer.service.CustomerService;
import com.seekweb4.chat.modules.friend.service.FriendService;
import com.seekweb4.chat.modules.member.entity.MemberTongji;
import com.seekweb4.chat.modules.sign.entity.Sign;
import com.seekweb4.chat.modules.sign.service.SignService;
import com.seekweb4.chat.modules.signlog.entity.SignLog;
import com.seekweb4.chat.modules.signlog.service.SignLogService;
import com.seekweb4.chat.modules.signset.entity.SignSet;
import com.seekweb4.chat.modules.signset.entity.SignSetItem;
import com.seekweb4.chat.modules.signset.service.SignSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.mapper.MemberMapper;

/**
 * 移动端用户Service
 *
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class MemberService extends CrudService<MemberMapper, Member> {

    @Autowired
    private FriendService friendService;
    @Autowired
    private BalanceLogService balanceLogService;
    @Autowired
    private SignLogService signLogService;
    @Autowired
    private SignSetService signSetService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private SignService signService;


    @Value("${user.idno.prefix-m:false}")
    private boolean prefixM;

    public Member get(String id) {
        return super.get(id);
    }

    public List<Member> findList(Member member) {
        return super.findList(member);
    }

    public Page<Member> findPage(Page<Member> page, Member member) {
        return super.findPage(page, member);
    }

    @Transactional(readOnly = false)
    public void save(Member member) {
        super.save(member);
    }

    @Transactional(readOnly = false)
    public void delete(Member member) {
        super.delete(member);
    }

    /**
     * 注册
     *
     * @param member
     */
    @Transactional(readOnly = false)
    public synchronized void regist(Member member) {
        if (StringUtils.isBlank(member.getIcon())) {
            member.setIcon(AppProperites.newInstance().filePath + "/userfiles/icon.png");
        }
        member.setIdno(getIdno());
        member.setQrcode2(QrCodeUtil.getCode("3-" + member.getIdno(), "/memberCode"));
        if (StringUtils.isBlank(member.getNickname())) {
            member.setNickname("用户" + member.getIdno());
        }
        member.setState("0");
        member.setZiti("20");
        member.setShowQuan("1");
        member.setIsAddYz("1");
        member.setIsQuanTx("1");
        member.setIsMsgTx("1");
        member.setAccSerch("1");
        member.setPhoneSerch("0");
        member.setGroAdd("1");
        member.setMpAdd("1");
        member.setQrAdd("1");
        member.setBalance(BigDecimal.ZERO);
        super.save(member);
        SignLog ll = new SignLog();
        ll.setU(member);
        ll.setDate(DateUtils.getDate());
        ll.setIsSign("0");
        ll.setDay(1);
        signLogService.save(ll);
        // IM导入用户
        //本地im导入
        ImUtils.register(member.getId(), member.getAcount(), member.getNickname(), member.getIcon());
//		if(StringUtils.isNotBlank(member.getInvitid())){
//			Member inv = super.get(member.getInvitid());
//			Friend friend = new Friend();
//			friend.setU(member);
//			friend.setUid2(inv);
//			friend.setZimu(PinyinUtils.getFirstLetter(member.getNickname()));
//			friend.setMdr("0");
//			friend.setIsTop("0");
//			friendService.save(friend);
//			Friend friend2 = new Friend();
//			friend2.setU(inv);
//			friend2.setUid2(member);
//			friend2.setZimu("y");
//			friend2.setMdr("0");
//			friend2.setIsTop("0");
//			friendService.save(friend2);
//			//TencentLiveUtils.add_friend_both(member.getId(),inv.getId());
//			ImUtils.addFrinend(member.getId(),inv.getId(),true);
//			List<DefultFriend> list = defultFriendService.findList(new DefultFriend());
//			if(!list.isEmpty()){
//				for(DefultFriend df:list){
//					Friend friend3 = new Friend();
//					friend3.setU(member);
//					friend3.setUid2(df.getU());
//					friend3.setZimu(PinyinUtils.getFirstLetter(member.getNickname()));
//					friend3.setMdr("0");
//					friend3.setIsTop("0");
//					friendService.save(friend3);
//					Friend friend4 = new Friend();
//					friend4.setU(df.getU());
//					friend4.setUid2(member);
//					friend4.setZimu("y");
//					friend4.setMdr("0");
//					friend4.setIsTop("0");
//					friendService.save(friend4);
//					ImUtils.addFrinend(member.getId(),df.getU().getId(),true);
//				}
//			}
//		}
    }

    /**
     * 修改余额
     *
     * @param member 用户
     * @param money  金额
     * @param type   1增加 0:减少
     */
    @Transactional(readOnly = false)
    public synchronized void updateBalance(Member member, BigDecimal money, String type, String title) {
        if ("1".equals(type)) {
            mapper.execUpdateSql("UPDATE t_member SET balance = balance + " + money + " WHERE id = '" + member.getId() + "'");
        } else {
            mapper.execUpdateSql("UPDATE t_member SET balance = balance - " + money + " WHERE id = '" + member.getId() + "'");
        }
        BalanceLog log = new BalanceLog();
        log.setU(member);
        log.setMoney(money);
        log.setTitle(title);
        log.setState(type);
        if (title.contains("红包")) {
            log.setType("1");
        } else if (title.contains("转账")) {
            log.setType("2");
        } else if (title.contains("充值")) {
            log.setType("3");
        } else if (title.contains("签到")) {
            log.setType("4");
        }
        balanceLogService.save(log);
    }

    /**
     * 签到
     *
     * @param member
     */
    @Transactional(readOnly = false)
    public void sign(Member member) {
        SignLog log = new SignLog();
        log.setU(member);
        log.setDate(DateUtils.getDate());
        List<SignLog> list = signLogService.findList(log);
        //如果当天没有签到事件记录
        if (list.isEmpty()) {
            //查询或创建签到业务数据
            Sign sign = signService.get(member.getId());
            if (sign == null) {
                sign = new Sign();
                sign.setIsNewRecord(true);
                sign.setId(member.getId());
                sign.setDay(0);
                sign.setMoney(member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO);
                sign.setAwardMoney(BigDecimal.ZERO);
            }

            //计算连续签到天数
            int continuousDays = calculateContinuousDays(member);
            sign.setDay(continuousDays);

            //计算奖励金额
            BigDecimal awardMoney = calculateSignAward(continuousDays);
            sign.setAwardMoney(awardMoney);

            //设置最后签到时间
            sign.setLastSignDate(DateUtils.getDate());
            sign.setMoney(sign.getMoney().add(awardMoney));

            //保存每天奖励了多少，用于奖励记录接口
            log.setMoney(awardMoney);
            log.setIsSign("1");
            //先保存签到记录
            signLogService.save(log);

            //发放奖励
            updateBalance(member, awardMoney, "1", "签到奖励");

            //保存签到业务表
            signService.save(sign);
        }
    }

    /**
     * 计算连续签到天数
     */
    private int calculateContinuousDays(Member member) {
        //获取用户签到业务数据
        Sign sign = signService.get(member.getId());
        if (sign == null || sign.getLastSignDate() == null) {
            return 1; //首次签到
        }

        //判断是否连续签到(昨天是否签到)
        String yesterday = DateUtils.formatDate(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
        if (yesterday.equals(sign.getLastSignDate())) {
            return sign.getDay() + 1; //连续签到，天数+1
        } else {
            return 1; //断签，重置为1
        }
    }

    /**
     * 计算签到奖励金额
     */
    private BigDecimal calculateSignAward(int continuousDays) {
        SignSet set = signSetService.get("1");
        BigDecimal baseMoney = set.getDaymoney();

        //查找对应连续天数的额外奖励
        for (SignSetItem item : set.getSignSetItemList()) {
            if (continuousDays == item.getDay()) {
                return item.getMoney().add(baseMoney); //返回对应天数的总奖励金额
            }
        }

        return baseMoney; //没有额外奖励，返回基础金额
    }

    public List<MemberTongji> getCityList() {
        return memberMapper.cityList();
    }

    public List<MemberTongji> getEqList() {
        return memberMapper.eqList();
    }


    public Member selectByReceivingAddress(String receivingAddress) {
        return memberMapper.selectByReceivingAddress(receivingAddress);
    }

    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String entryptPassword(String plainPassword) {
        return MD5Util.md5(plainPassword);
    }

    /**
     * 验证密码
     *
     * @param plainPassword 明文密码
     * @param password      密文密码
     * @return 验证成功返回true
     */
    public static boolean validatePassword(String plainPassword, String password) {
        plainPassword = MD5Util.md5(plainPassword);
        return plainPassword.contentEquals(password);
    }

    /**
     * 获取邀请码
     *
     * @return
     */
    private synchronized String getIdno() {
        while (true) {
            String code = IdGen.getNumber(6);
            // 根据配置决定是否添加M前缀
            if (prefixM) {
                code = "M" + code;
            }
            String count = executeGetSql("SELECT COUNT(*) FROM t_member WHERE idno = '" + code + "'").toString();
            if ("0".equals(count)) {
                return code;
            }
        }
    }

    public static void main(String[] args) {
        String psd = MD5Util.md5("dev100");

        System.out.println(psd);
    }


    @Transactional(readOnly = false)
    public int updateBalanceByUserId(BigDecimal amount, String id) {
        return memberMapper.updateBalanceByUserId(amount, id);
    }

    @Transactional(readOnly = false)
    public int updateSubtractionBalanceByUserId(BigDecimal amount, String id) {
        return memberMapper.updateSubtractionBalanceByUserId(amount, id);
    }

    @Transactional(readOnly = false)
    public int updateTwoFactorCode(Member member) {
        return memberMapper.updateTwoFactorCode(member.getId(), member.getTwoFactorCode(), member.getTwoFactorTime());
    }

    @Transactional(readOnly = false)
    public int updateMember(Member member) {
        return memberMapper.updateMember(member);
    }

    public Member getCustomer() {
        Member member = memberMapper.getCustomer();
        return member;
    }

    public Member selectBasicById(String id) {
        return memberMapper.selectBasicById(id);
    }

    /**
     * 按 id 列表查询用户详情，与单条 {@link #get(String)} 使用同一套 SQL 与映射（含密保关联等，与 /member/member/queryById 一致）。
     * 返回顺序与入参首次出现顺序一致（去重、忽略空串），最多 200 条。内部按 id 逐条查询，id 数量较多时 DB 压力会升高。
     */
    public List<Member> listBasicByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String id : ids) {
            if (StringUtils.isBlank(id)) {
                continue;
            }
            unique.add(id.trim());
            if (unique.size() >= 200) {
                break;
            }
        }
        if (unique.isEmpty()) {
            return Collections.emptyList();
        }
        List<Member> ordered = new ArrayList<>(unique.size());
        for (String id : unique) {
            Member m = get(id);
            if (m != null) {
                ordered.add(m);
            }
        }
        return ordered;
    }
}