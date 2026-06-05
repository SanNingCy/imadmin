package com.seekweb4.chat.modules.member.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.seekweb4.chat.modules.mibaofaq.entity.MibaoFaq;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

/**
 * 移动端用户Entity
 *
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Member extends DataEntity<Member> {

    private static final long serialVersionUID = 1L;
    @ExcelField(title = "账号类型（1：手机号 2：账户名 3：设备号）", dictType = "acc_type", align = 2, sort = 1)
    private String accType;        // 账号类型
    @ExcelField(title = "手机号", align = 2, sort = 1)
    private String phone;        // 账号
    @ExcelField(title = "账户名", align = 2, sort = 2)
    private String acount;        // 账户名
    @ExcelField(title = "设备号", align = 2, sort = 3)
    private String eqno;        // 设备号
    private String idno;        // id号
    private String qrcode;        // 名片二维码
    private String qrcode2;        // 登录二维码
    @ExcelField(title = "昵称", align = 2, sort = 6)
    private String nickname;        // 昵称
    private String icon;        // 头像
    @ExcelField(title = "性别（1：男 2：女）", dictType = "sex", align = 2, sort = 8)
    private String sex;        // 性别
    private String img;        // 朋友圈背景图
    private String chatImg;        // 聊天背景
    private String sign;        // 签名
    private String state;        // 状态 0正常 1禁用
    private BigDecimal balance;        // 余额
    @ExcelField(title = "登录密码", align = 2, sort = 9)
    private String password;        // 密码
    private String paypwd;        // 支付密码
    private MibaoFaq mb;        // 密宝问题
    private String mbid;
    private String mbname;    //问题
    private String mbda;        // 密保答案
    private String ziti;        // 字体大小
    private String showQuan;        // 朋友圈是否别人可见
    private String isAddYz;        // 加我为好友时是否需要验证
    private String isQuanTx;        // 朋友圈更新提醒是否开启
    private String isMsgTx;        // 消息提醒是否开启
    private String accSerch;        // 是否可以通过用户名搜到我
    private String phoneSerch;        // 是否可以通过ID号搜到我
    private String groAdd;        // 是否可以通过群聊添加我
    private String mpAdd;        // 是否可以通过名片添加我
    private String qrAdd;        // 是否可以通过二维码添加我
    private String xcxurl;        // 小程序地址链接
    private String eqid;        // 设备id
    private String kfid;        // 客服id
    private Date beginCreateDate;        // 开始 注册时间
    private Date endCreateDate;        // 结束 注册时间
    private String invitid;    //邀请人id

    /** 最近一次登录成功时间 */
    @Getter(AccessLevel.NONE)
    private Date lastLoginDate;

    /**
     * 长期未登录用户
     */
    @Getter(AccessLevel.NONE)
    private Integer inactiveDays;

    private String key;

    private String btype;    //变更余额类型 1：增加 0：扣除
    private BigDecimal money;

    private String idnos;
    private String ftype;    //1：封禁 2：解禁
    private String huanying;    //欢迎语

    private String isneibu;    //是否内部号
    private String ipwhite;    //ip白名单

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String city;    //ip归属地
    private String model;    //手机型号
    private String regip;    //注册ip

    private String noadd;    //是否禁止添加我为好友

    private String isvip;    //是否会员
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date viptime;    //会员到期时间

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String lianghao;    //靓号
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;    //靓号到期时间

    private String autofanyi;    //是否开启自动翻译
    private String biandabianyi;    //是否边打边译


    /**
     * 付款地址
     */
    private String paymentAddress;


    /**
     * 收款地址
     */
    private String receivingAddress;


    /***
     * 谷歌验证码
     */
    private String twoFactorCode;


    /***
     * 积分总额
     */
    private BigDecimal pointTotal;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date twoFactorTime;


    public Member() {
        super();
    }

    public Member(String id) {
        super(id);
    }

    /** 手写访问器：保证 @JsonInclude 作用在 getter 上，覆盖全局 NON_NULL（见 WebConfig）；Lombok @Data 不会把字段注解带到 getter */
    @ExcelField(title = "最近登录时间", align = 2, sort = 30)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @JsonIgnore
    public Integer getInactiveDays() {
        return inactiveDays;
    }

    public void setInactiveDays(Integer inactiveDays) {
        this.inactiveDays = inactiveDays;
    }
}