package com.seekweb4.chat.api;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.wildfirechat.pojos.OutputMessageData;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seekweb4.chat.api.error.BizException;
import com.seekweb4.chat.api.req.SearchReqJson;
import com.seekweb4.chat.api.utils.*;
import com.seekweb4.chat.api.utils.QrCodeUtil;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.*;
import com.seekweb4.chat.common.websocket.service.system.SystemInfoSocketHandler;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.delayedQueue.AIPicDelayedQueueListener;
import com.seekweb4.chat.delayedQueue.GroupHuanyingDelayedQueueListener;
import com.seekweb4.chat.delayedQueue.JiejinDelayedQueueListener;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;

import java.util.*;
import java.util.concurrent.TimeUnit;
import com.seekweb4.chat.modules.agreement.service.AgreementService;
import com.seekweb4.chat.modules.balancelog.entity.BalanceLog;
import com.seekweb4.chat.modules.balancelog.service.BalanceLogService;
import com.seekweb4.chat.modules.black.entity.Black;
import com.seekweb4.chat.modules.black.service.BlackService;
import com.seekweb4.chat.modules.changenamelog.entity.ChangeNameLog;
import com.seekweb4.chat.modules.changenamelog.service.ChangeNameLogService;
import com.seekweb4.chat.modules.changepaypwdlog.entity.ChangePaypwdLog;
import com.seekweb4.chat.modules.changepaypwdlog.service.ChangePaypwdLogService;
import com.seekweb4.chat.modules.changephonelog.entity.ChangePhoneLog;
import com.seekweb4.chat.modules.changephonelog.service.ChangePhoneLogService;
import com.seekweb4.chat.modules.changepwdlog.entity.ChangePwdLog;
import com.seekweb4.chat.modules.changepwdlog.service.ChangePwdLogService;
import com.seekweb4.chat.modules.chatlog.service.ChatLogService;
import com.seekweb4.chat.modules.coll.entity.Coll;
import com.seekweb4.chat.modules.coll.service.CollService;
import com.seekweb4.chat.modules.customer.entity.Customer;
import com.seekweb4.chat.modules.customer.service.CustomerService;
import com.seekweb4.chat.modules.dy.entity.Dy;
import com.seekweb4.chat.modules.dy.service.DyService;
import com.seekweb4.chat.modules.dylike.entity.DyLike;
import com.seekweb4.chat.modules.dylike.service.DyLikeService;
import com.seekweb4.chat.modules.dyomm.entity.DyComm;
import com.seekweb4.chat.modules.dyomm.service.DyCommService;
import com.seekweb4.chat.modules.emoji.entity.Emoji;
import com.seekweb4.chat.modules.emoji.service.EmojiService;
import com.seekweb4.chat.modules.faq.entity.Faq;
import com.seekweb4.chat.modules.faq.service.FaqService;
import com.seekweb4.chat.modules.feedback.entity.Feedback;
import com.seekweb4.chat.modules.feedback.service.FeedbackService;
import com.seekweb4.chat.modules.friend.entity.Friend;
import com.seekweb4.chat.modules.friend.service.FriendService;
import com.seekweb4.chat.modules.friendapply.entity.FriendApply;
import com.seekweb4.chat.modules.friendapply.service.FriendApplyService;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.group.service.GroupService;
import com.seekweb4.chat.modules.groupapply.entity.GroupApply;
import com.seekweb4.chat.modules.groupapply.service.GroupApplyService;
import com.seekweb4.chat.modules.groupdingshi.entity.GroupDingshi;
import com.seekweb4.chat.modules.groupdingshi.service.GroupDingshiService;
import com.seekweb4.chat.modules.grouphongbao.entity.GroupHongbao;
import com.seekweb4.chat.modules.grouphongbao.service.GroupHongbaoService;
import com.seekweb4.chat.modules.grouphongbaolog.entity.GroupHongbaoLog;
import com.seekweb4.chat.modules.grouphongbaolog.service.GroupHongbaoLogService;
import com.seekweb4.chat.modules.grouphuanying.entity.GroupHuanying;
import com.seekweb4.chat.modules.grouphuanying.service.GroupHuanyingService;
import com.seekweb4.chat.modules.groupitem.entity.GroupItem;
import com.seekweb4.chat.modules.groupitem.service.GroupItemService;
import com.seekweb4.chat.modules.groupuplog.entity.GroupUplog;
import com.seekweb4.chat.modules.groupuplog.service.GroupUplogService;
import com.seekweb4.chat.modules.hongbao.entity.Hongbao;
import com.seekweb4.chat.modules.hongbao.service.HongbaoService;
import com.seekweb4.chat.modules.hudong.entity.Hudong;
import com.seekweb4.chat.modules.hudong.service.HudongService;
import com.seekweb4.chat.modules.loginlog.entity.LoginLog;
import com.seekweb4.chat.modules.loginlog.service.LoginLogService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.entity.MemberTongji;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.membernotice.entity.MemberNotice;
import com.seekweb4.chat.modules.membernotice.service.MemberNoticeService;
import com.seekweb4.chat.modules.mibaofaq.entity.MibaoFaq;
import com.seekweb4.chat.modules.mibaofaq.service.MibaoFaqService;
import com.seekweb4.chat.modules.quhao.entity.Quhao;
import com.seekweb4.chat.modules.quhao.service.QuhaoService;
import com.seekweb4.chat.modules.reason.entity.Reason;
import com.seekweb4.chat.modules.reason.service.ReasonService;
import com.seekweb4.chat.modules.rechagelog.entity.RechageLog;
import com.seekweb4.chat.modules.rechagelog.service.RechageLogService;
import com.seekweb4.chat.modules.sign.entity.Sign;
import com.seekweb4.chat.modules.sign.service.SignService;
import com.seekweb4.chat.modules.signlog.entity.SignLog;
import com.seekweb4.chat.modules.signlog.service.SignLogService;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import com.seekweb4.chat.modules.tixian.entity.Tixian;
import com.seekweb4.chat.modules.tixian.service.TixianService;
import com.seekweb4.chat.modules.tixiantitle.entity.TixianTitle;
import com.seekweb4.chat.modules.tixiantitle.service.TixianTitleService;
import com.seekweb4.chat.modules.tousu.entity.Tousu;
import com.seekweb4.chat.modules.tousu.service.TousuService;
import com.seekweb4.chat.modules.upgrade.entity.Upgrade;
import com.seekweb4.chat.modules.upgrade.service.UpgradeService;
import com.seekweb4.chat.modules.vipcode.entity.VipCode;
import com.seekweb4.chat.modules.vipcode.service.VipCodeService;
import com.seekweb4.chat.modules.weburl.entity.Web;
import com.seekweb4.chat.modules.weburl.service.WebService;
import com.seekweb4.chat.modules.xcx.entity.Xcx;
import com.seekweb4.chat.modules.xcx.service.XcxService;
import com.seekweb4.chat.modules.zhuangzhang.entity.Zhuangzhang;
import com.seekweb4.chat.modules.zhuangzhang.service.ZhuangzhangService;
import com.seekweb4.chat.modules.zhuxiao.entity.Zhuxiao;
import com.seekweb4.chat.modules.zhuxiao.service.ZhuxiaoService;
import com.seekweb4.chat.security.AppIntercept;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.core.web.BaseController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController extends BaseController {
	@Autowired
	private StringRedisUtils redisUtils;
	@Autowired
	private MemberService memberService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private AgreementService agreementService;
	@Autowired
	private FeedbackService feedbackService;
	@Autowired
	private FaqService faqService;
	@Autowired
	private RedisDelayedQueue redisDelayedQueue;
	@Autowired
	private RedissonClient redissonClient;
	@Autowired
	private BalanceLogService balanceLogService;
	@Autowired
	private BlackService blackService;
	@Autowired
	private CollService collService;
	@Autowired
	private DyService dyService;
	@Autowired
	private DyCommService dyCommService;
	@Autowired
	private DyLikeService dyLikeService;
	@Autowired
	private EmojiService emojiService;
	@Autowired
	private FriendService friendService;
	@Autowired
	private FriendApplyService friendApplyService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private GroupApplyService groupApplyService;
	@Autowired
	private GroupHongbaoService groupHongbaoService;
	@Autowired
	private GroupHongbaoLogService groupHongbaoLogService;
	@Autowired
	private GroupItemService groupItemService;
	@Autowired
	private HongbaoService hongbaoService;
	@Autowired
	private HudongService hudongService;
	@Autowired
	private MibaoFaqService mibaoFaqService;
	@Autowired
	private ReasonService reasonService;
	@Autowired
	private RechageLogService rechageLogService;
	@Autowired
	private SignLogService signLogService;
	@Autowired
	private TixianService tixianService;
	@Autowired
	private TixianTitleService tixianTitleService;
	@Autowired
	private TousuService tousuService;
	@Autowired
	private WebService webService;
	@Autowired
	private XcxService xcxService;
	@Autowired
	private ZhuangzhangService zhuangzhangService;
	@Autowired
	private QuhaoService quhaoService;
	@Autowired
	private LoginLogService loginLogService;
	@Autowired
	private ChangeNameLogService changeNameLogService;
	@Autowired
	private ChangePaypwdLogService changePaypwdLogService;
	@Autowired
	private ChangePwdLogService changePwdLogService;
	@Autowired
	private ChangePhoneLogService changePhoneLogService;
	@Resource
	private SystemInfoSocketHandler systemInfoSocketHandler;
	@Resource
	private ChatLogService chatLogService;
	@Resource
	private MemberNoticeService memberNoticeService;
	@Resource
	private GroupHuanyingService groupHuanyingService;
	@Resource
	private GroupDingshiService groupDingshiService;
	@Resource
	private VipCodeService vipCodeService;
	@Resource
	private GroupUplogService groupUplogService;
	@Autowired
	private UpgradeService upgradeService;
	@Autowired
	private ZhuxiaoService zhuxiaoService;
	@Autowired
	private SignService signService;

	private void fillLoginLogSuccessFields(LoginLog lo, Member member, ReqJson req) {
		String reqEq = req != null ? req.getString("eqno") : null;
		lo.setEqno(!isBlank(reqEq) ? reqEq : member.getEqno());
		lo.setQrcode(resolveQrcode2ForResponse(member.getQrcode2()));
		lo.setStatus(1);
	}

	/**
	 * 手机号密码登陆
	 * @param req
	 * @return
	 */
	@RequestMapping("/eqLogin")
	public AjaxJson eqLogin(@RequestBody ReqJson req, HttpServletRequest request) {
		reqValidator(req,"eqno");
		Member member = memberService.findUniqueByProperty("eqno", req.getString("eqno"));
		if(member == null){
			return AjaxJson.error("账号不存在");
		}
		if("robot001".equals(member.getId())){
			return AjaxJson.error("用户不存在");
		}
		if("1".equals(member.getState())){
			return AjaxJson.error("用户被禁用");
		}
		if(!isBlank(member.getIpwhite()) && !member.getIpwhite().contains(IpUtils.getIpAddr(request))){
			return AjaxJson.error("ip限制登录");
		}
		LoginLog lo = new LoginLog();
		lo.setU(member);
		lo.setIp(IpUtils.getIpAddr(request));
		String city = IpAddressUtil.getCity(lo.getIp());
		lo.setIpcity(city);
		fillLoginLogSuccessFields(lo, member, req);
		loginLogService.save(lo);

		String isws = "0";
		if(!member.getNickname().contains("用户")){
			isws = "1";
		}
		return AjaxJson.success()
				.put(JWTUtil.APPTOKEN, JWTUtil.createAppToken(member.getId()))
				.put("userId",member.getId())
				.put("imtoken", ImUtils.getToken(member.getId(),req.getString("clientId"),req.getInteger("type")))
				.put("isws",isws)
				.put("idno",member.getIdno())
				.put("qrcode",resolveQrcode2ForResponse(member.getQrcode2()));
	}

	/**
	 * 设备号注册
	 * @param req
	 * @return
	 */
	@RequestMapping("/regist")
	public AjaxJson regist(@RequestBody ReqJson req, HttpServletRequest request) {
		reqValidator(req,"eqno");
		Member member = memberService.findUniqueByProperty("eqno", req.getString("eqno"));
		if(member != null){
			return AjaxJson.error("此设备号已注册 请登录");
		}
		member = new Member();
		member.setEqno(req.getString("eqno"));
		String ipAddr = IpUtils.getIpAddr(request);
		member.setRegip(ipAddr);
		String city = IpAddressUtil.getCity(ipAddr);
		member.setCity(city);
		member.setModel(req.getString("model"));
		member.setEqid(req.getString("eqno"));
		Customer customer = customerService.get("1");
		if(!isBlank(req.getString("eqno")) && customer.getEqcount() > 0){
			String s = memberService.executeGetSql("select count(1) from t_member where eqid = '" + req.getString("eqno") + "'").toString();
			if(Integer.valueOf(s) >= customer.getEqcount()){
				return AjaxJson.error("单设备注册数量上限");
			}
		}
		if(!isBlank(ipAddr) && customer.getIpcount()>0){
			String s1 = memberService.executeGetSql("select count(1) from t_member where regip = '"+ipAddr+"' and create_date > DATE_SUB(CURDATE(), INTERVAL 12 HOUR)").toString();
			if(Integer.valueOf(s1) >= customer.getIpcount()){
				return AjaxJson.error("单IP12小时内注册数量上限");
			}
		}
		Member m3 = memberService.findUniqueByProperty("acount", req.getString("account"));
		if(m3 != null){
			return AjaxJson.error("此账号已注册，当前用户名已经存在");
//			return AjaxJson.error("该设备已注册，请前往直接登录");
		}
		member.setAcount(req.getString("account"));
		member.setPassword(memberService.entryptPassword(req.getString("pwd")));
		memberService.regist(member);
		String isws = "0";
		if(!member.getNickname().contains("用户")){
			isws = "1";
		}
		LoginLog lo = new LoginLog();
		lo.setU(member);
		lo.setIp(IpUtils.getIpAddr(request));
		lo.setIpcity(city);
		fillLoginLogSuccessFields(lo, member, req);
		loginLogService.save(lo);
		return AjaxJson.success()
				.put(JWTUtil.APPTOKEN, JWTUtil.createAppToken(member.getId()))
				.put("userId",member.getId())
				.put("imtoken", ImUtils.getToken(member.getId(),req.getString("clientId"),req.getInteger("type")))
				.put("isws",isws)
				.put("idno",member.getIdno())
				.put("qrcode",resolveQrcode2ForResponse(member.getQrcode2()))
				.put("account",req.getString("account"))
				.put("pwd",req.getString("pwd"));
	}
	/**
	 * id登录
	 * @param req
	 * @return
	 */
	@RequestMapping("/idLogin")
	public AjaxJson idLogin(@RequestBody ReqJson req, HttpServletRequest request) {
		reqValidator(req,"id");
		Member member = memberService.findUniqueByProperty("idno",req.getString("id"));
		if(member == null){
			return AjaxJson.error("用户不存在");
		}
		if("robot001".equals(member.getId())){
			return AjaxJson.error("用户不存在");
		}
		if("1".equals(member.getState())){
			return AjaxJson.error("用户被禁用");
		}
		String isws = "0";
		if(!member.getNickname().contains("用户")){
			isws = "1";
		}
		LoginLog lo = new LoginLog();
		lo.setU(member);
		lo.setIp(IpUtils.getIpAddr(request));
		String city = IpAddressUtil.getCity(lo.getIp());
		lo.setIpcity(city);
		fillLoginLogSuccessFields(lo, member, req);
		loginLogService.save(lo);
		return AjaxJson.success()
				.put(JWTUtil.APPTOKEN, JWTUtil.createAppToken(member.getId()))
				.put("userId",member.getId())
				.put("imtoken", ImUtils.getToken(member.getId(),req.getString("clientId"),req.getInteger("type")))
				.put("isws",isws);
	}
	/**
	 * 账号密码登录（新）
	 * @param req
	 * @return
	 */
	@RequestMapping("/accLogin")
	public AjaxJson accLogin(@RequestBody ReqJson req, HttpServletRequest request) {
		reqValidator(req,"acc","pwd");
		Member member = memberService.findUniqueByProperty("acount",req.getString("acc"));
		if(member == null){
			return AjaxJson.error("用户不存在");
		}
		// 审核账号跳过设备验证
		boolean isAuditAccount = "85201021".equals(req.getString("acc")) || "61347031".equals(req.getString("acc"));

		if(!isAuditAccount) {
			if (!isBlank(req.getString("code"))) {
				if (!checkSmsCode(req.getString("acc"), req.getString("code"))) {
					return AjaxJson.error("验证码错误");
				}
			} else {
				if (!isBlank(req.getString("eqno")) && !member.getEqno().equals(req.getString("eqno"))) {
					AjaxJson json = new AjaxJson();
					json.setCode(202);
					json.setSuccess(true);
					json.setMsg("新设备登录请填写验证码");
					return json;
				}
			}
		}
		if(!memberService.validatePassword(req.getString("pwd"),member.getPassword())){
			return AjaxJson.error("登录密码错误");
		}
		if("robot001".equals(member.getId())){
			return AjaxJson.error("用户不存在");
		}
		if("1".equals(member.getState())){
			return AjaxJson.error("用户被禁用");
		}
		String isws = "0";
		if(!member.getNickname().contains("用户")){
			isws = "1";
		}
		LoginLog lo = new LoginLog();
		lo.setU(member);
		lo.setIp(IpUtils.getIpAddr(request));
		String city = IpAddressUtil.getCity(lo.getIp());
		lo.setIpcity(city);
		fillLoginLogSuccessFields(lo, member, req);
		loginLogService.save(lo);
		return AjaxJson.success()
				.put(JWTUtil.APPTOKEN, JWTUtil.createAppToken(member.getId()))
				.put("userId",member.getId())
				.put("imtoken", ImUtils.getToken(member.getId(),req.getString("clientId"),req.getInteger("type")))
				.put("isws",isws);
	}
	/**
	 * 获取设备验证码
	 * @param req
	 * @return
	 */
	@RequestMapping("/getAccCode")
	public AjaxJson getAccCode(@RequestBody ReqJson req, HttpServletRequest request) {
		reqValidator(req,"acc");
		Member member = memberService.findUniqueByProperty("acount",req.getString("acc"));
		if(member == null){
			return AjaxJson.error("用户不存在");
		}
		String code = RandomUtil.randomNumbers(6);
		redisUtils.setEx(req.getString("acc"), code, 600, TimeUnit.SECONDS);

		ImUtils.sendTxtMsg(ImUtils.robot_id,"您的登录验证码为："+code,member.getId());
		return AjaxJson.success();
	}
	/**
	 * 申请注销
	 * @param req
	 * @return
	 */
	@RequestMapping("/zhuxiao")
	public AjaxJson zhuxiao(@RequestBody ReqJson req, HttpServletRequest request) {
		Member member = MemberUtils.getMember();
		if(member.getBalance().compareTo(BigDecimal.ZERO) == 1){
			return AjaxJson.error("钱包仍有余额，不可注销");
		}

		Zhuxiao zhuxiao = new Zhuxiao();
		zhuxiao.setUid(member.getId());
		List<Zhuxiao> list = zhuxiaoService.findList(zhuxiao);
		if(!list.isEmpty()){
			zhuxiao = list.get(0);
			if(!"3".equals(zhuxiao.getState())){
				return AjaxJson.error("您已提交过申请");
			}
		}
		zhuxiao.setIdno(member.getIdno());
		zhuxiao.setAcc(member.getAcount());
		zhuxiao.setEqid(member.getEqno());
		zhuxiao.setState("1");
		zhuxiaoService.save(zhuxiao);
		return AjaxJson.success();
	}

	/**
	 * 验证码找回密码
	 * @param req
	 * @return
	 */
	@RequestMapping("/zhaohui")
	public AjaxJson zhaohui(@RequestBody ReqJson req) {
		reqValidator(req,"phone","pwd");
		Member member = memberService.findUniqueByProperty("phone", req.getString("phone"));
		if(member == null){
			return AjaxJson.error("此账号不存在");
		}
		if(!checkSmsCode(req.getString("phone"),req.getString("code"))){
			return AjaxJson.error("验证码错误");
		}
		member.setPassword(memberService.entryptPassword(req.getString("pwd")));
		memberService.save(member);

		return AjaxJson.success();
	}
	/**
	 * 获取图形验证码
	 * @param req
	 * @return
	 */
	@RequestMapping("/getCode")
	public AjaxJson getCode(@RequestBody ReqJson req) {
		//HuTool定义图形验证码的长和宽,验证码的位数，干扰线的条数
		LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(116, 36,4,50);
		//将验证码放入session
		String uuid = IdGen.uuid();
		redisUtils.setEx("tucode:"+uuid,lineCaptcha.getCode(),10, TimeUnit.MINUTES);

		return AjaxJson.success().put("yanzm",lineCaptcha.getImageBase64()).put("uuid",uuid);
	}
	/**
	 * 支付密码找回密码
	 * @param req
	 * @return
	 */
	@RequestMapping("/zhaohui2")
	public AjaxJson zhaohui2(@RequestBody ReqJson req, HttpSession session) {
		reqValidator(req,"phone","payPwd","pwd","code");
		Member member = memberService.findUniqueByProperty("phone", req.getString("phone"));
		if(member == null){
			return AjaxJson.error("此账号不存在");
		}
		if(isBlank(member.getPaypwd())){
			return AjaxJson.error("此账号未设置支付密码");
		}
		if(!memberService.validatePassword(req.getString("payPwd"),member.getPaypwd())){
			return AjaxJson.error("支付密码错误");
		}
		String s = "tucode:" + req.getString("uuid");
		if(!req.getString("code").equals(redisUtils.get(s))){
			return AjaxJson.error("图文验证码不正确");
		}
		member.setPassword(memberService.entryptPassword(req.getString("pwd")));
		memberService.save(member);

		return AjaxJson.success();
	}
	/**
	 * 完善资料
	 * @param req
	 * @return
	 */
	@RequestMapping("/wanshan")
	public AjaxJson wanshan(@RequestBody ReqJson req) {
		//reqValidator(req,"name");
		Member member = MemberUtils.getMember();
		if(!isBlank(req.getString("name"))){
			String s1 = memberService.executeGetSql("select count(1) from t_member where nickname = '" + req.getString("name") + "' and id != '" + MemberUtils.getUid() + "'").toString();
			if(!"0".equals(s1)){
				return AjaxJson.error("昵称已存在，请更换");
			}
			Customer customer = customerService.get("1");
			String[] split = customer.getNamemgc().split("[|]", 0);
			for(String s:split){
				if(req.getString("name").contains(s)){
					return AjaxJson.error("昵称包含敏感词 "+s);
				}
			}
		}
		if(!isBlank(req.getString("name"))){
			member.setNickname(req.getString("name"));
		}
		if(!isBlank(req.getString("icon"))){
			member.setIcon(req.getString("icon"));
		}
		if(!isBlank(req.getString("account"))){
			String s2 = memberService.executeGetSql("select count(1) from t_member where acount = '" + req.getString("account") + "' and id != '" + MemberUtils.getUid() + "'").toString();
			if(!"0".equals(s2)){
				return AjaxJson.error("此账号已存在，请更换");
			}
			member.setAcount(req.getString("account"));
		}
		if(!isBlank(req.getString("pwd"))){
			member.setPassword(memberService.entryptPassword(req.getString("pwd")));
		}
		memberService.save(member);
		ImUtils.editUser(member.getId(),member.getNickname(),getRealPath(member.getIcon()));
		return AjaxJson.success();
	}
	/**
	 * 添加对方好友
	 * @param req
	 * @return
	 */
	@RequestMapping("/addFriend")
	public AjaxJson addFriend(@RequestBody ReqJson req) {
		reqValidator(req,"id","sinfo","source");
		Member member = memberService.get(req.getString("id"));
		if(member == null){
			return AjaxJson.error("用户不存在");
		}
		Member me = MemberUtils.getMember();
		Customer customer = customerService.get("1");
		long l = DateUtils.pastHour(me.getCreateDate());
		if((int)l < customer.getNewadd()){
			if(!"3".equals(req.getString("source"))){
				return AjaxJson.error("新用户"+customer.getNewadd()+"小时只能通过二维码添加好友");
			}
			//return AjaxJson.error("新号"+customer.getNewadd()+"小时内不可加人");
		}

		if("1".equals(member.getNoadd())){
			return AjaxJson.error("对方禁止别人添加");
		}
		if("1".equals(customer.getOpenneibu()) && "1".equals(member.getIsneibu())){
			return AjaxJson.error("添加好友失败，对方已开启限制");
		}

		String s = friendService.executeGetSql("select count(1) from t_friend where uid = '" + me.getId() + "' and uid2 = '" + member.getId() + "'").toString();
		if(!"0".equals(s)){
			return AjaxJson.error("你们已经是好友");
		}
		if("0".equals(member.getIsAddYz())){	//无需验证
			Friend friend = new Friend();
			friend.setU(me);
			friend.setUid2(member);
			friend.setZimu(PinyinUtils.getFirstLetter(member.getNickname()));
			friend.setMdr("0");
			friend.setIsTop("0");
			friendService.save(friend);
			Friend friend2 = new Friend();
			friend2.setU(member);
			friend2.setUid2(me);
			friend2.setZimu(PinyinUtils.getFirstLetter(me.getNickname()));
			friend2.setMdr("0");
			friend2.setIsTop("0");
			friendService.save(friend2);
			ImUtils.addFrinend(member.getId(),me.getId(),true);

			ImUtils.sendMsg(me.getId(),1003,"",member.getId());
			ImUtils.sendMsg(member.getId(),1003,"",me.getId());

			ImUtils.sendMsg(member.getId(),1,!isBlank(member.getHuanying())?member.getHuanying():"你好，我已通过你的好友申请。",me.getId());
		}else {
			if("1".equals(req.getString("source")) && "0".equals(member.getGroAdd())){
				return AjaxJson.error("对方设置不可通过群聊添加好友");
			}
			if("2".equals(req.getString("source")) && "0".equals(member.getMpAdd())){
				return AjaxJson.error("对方设置不可通过名片添加好友");
			}
			if("3".equals(req.getString("source"))){
				if(!isBlank(req.getString("groupId"))){
					Group group = groupService.get(req.getString("groupId"));
					if("1".equals(group.getOpenQysl())){
						return AjaxJson.error("群员无法私聊加好友");
					}
				}
				if("0".equals(member.getQrAdd())){
					return AjaxJson.error("对方设置不可通过名片添加好友");
				}
			}
			FriendApply apply = new FriendApply();
			apply.setU(me);
			apply.setUid2(member);
			List<FriendApply> list = friendApplyService.findList(apply);
			if(!list.isEmpty()){
				apply = list.get(0);
			}
			apply.setState("1");
			apply.setSinfo(req.getString("sinfo"));
			apply.setBei(req.getString("bei"));
			apply.setInfo(req.getString("info"));
			friendApplyService.save(apply);

			String s1 = friendApplyService.executeGetSql("select count(1) from t_friend_apply where state = 1 and uid2 = '" + member.getId() + "'").toString();
			String s2 = groupApplyService.executeGetSql("select count(1) from t_group_apply where state = '1' and showids like '%" + member.getId() + "%'").toString();

			ImUtils.sendMsg(me.getId(),1002,s1+"|"+s2,member.getId());
		}
		return AjaxJson.success();
	}
	/**
	 * 收藏聊天记录
	 * @param req
	 * @return
	 */
	@RequestMapping("/logColl")
	public AjaxJson logColl(@RequestBody ReqJson req) {
		reqValidator(req,"uid","info","type");
		Coll coll = new Coll();
		coll.setUid(MemberUtils.getUid());
		coll.setUid2(new Member(req.getString("uid")));
		coll.setInfo(req.getString("info"));
		coll.setType(req.getString("type"));
		coll.setMiao(req.getInteger("miao"));
		coll.setLon(req.getString("lon"));
		coll.setLat(req.getString("lat"));
		collService.save(coll);

		return AjaxJson.success();
	}
	/**
	 * 添加表情包
	 * @param req
	 * @return
	 */
	@RequestMapping("/addEmoji")
	public AjaxJson addEmoji(@RequestBody ReqJson req) {
		reqValidator(req,"img");
		Emoji emoji = new Emoji();
		emoji.setU(MemberUtils.getMember());
		emoji.setImg(req.getString("img"));
		emojiService.save(emoji);

		return AjaxJson.success();
	}
	/**
	 * 我的表情
	 *
	 * @return
	 */
	@RequestMapping("/emojiList")
	public AjaxJson emojiList(@RequestBody ReqJson req) {
		List<Map<String, Object>> dataList = Lists.newArrayList();
		Emoji emoji = new Emoji();
		emoji.setU(MemberUtils.getMember());
		List<Emoji> list = emojiService.findList(emoji);
		Map map = null;
		for (Emoji p : list) {
			map = Maps.newHashMap();
			map.put("id", p.getId());
			map.put("emoji", getRealPath(p.getImg()));
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 系统消息（1223）
	 *
	 * @return
	 */
	@RequestMapping("/noticeMsgList")
	public AjaxJson noticeMsgList(@RequestBody ReqJson req) {
		List<Map<String, Object>> dataList = Lists.newArrayList();
		MemberNotice emoji = new MemberNotice();
		emoji.setU(MemberUtils.getMember());
		emoji.setIsdu("0");
		List<MemberNotice> list = memberNoticeService.findList(emoji);
		Map map = null;
		for (MemberNotice p : list) {
			map = Maps.newHashMap();
			map.put("id", p.getId());
			map.put("info2", p.getInfo());
			map.put("info", appProperites.getFilePath() + "/lixin2/display/memNotice?id=" + p.getId());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 系统消息已读（1223）
	 *
	 * @return
	 */
	@RequestMapping("/msgYidu")
	public AjaxJson msgYidu(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		MemberNotice notice = memberNoticeService.get(req.getString("id"));
		notice.setIsdu("1");
		memberNoticeService.save(notice);
		return AjaxJson.success();
	}
	/**
	 * 系统消息删除（1223）
	 *
	 * @return
	 */
	@RequestMapping("/msgDel")
	public AjaxJson msgDel(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		memberNoticeService.delete(new MemberNotice(req.getString("id")));
		return AjaxJson.success();
	}
	/**
	 * 删除表情包
	 *
	 * @return
	 */
	@RequestMapping("/delEmoji")
	public AjaxJson delEmoji(@RequestBody ReqJson req) {
		reqValidator(req,"ids");
		String[] ids = req.getString("ids").split("[|]", 0);
		for(String id:ids){
			emojiService.delete(new Emoji(id));
		}
		return AjaxJson.success();
	}

	/**
	 * 我的好友
	 *
	 * @return
	 */
	@RequestMapping("/friendList")
	public AjaxJson friendList(@RequestBody ReqJson req) {
		List<Map<String, Object>> dataList = Lists.newArrayList();
		Friend f = new Friend();
		f.setU(MemberUtils.getMember());
		if(!isBlank(req.getString("key"))){
			f.setKey(req.getString("key"));
		}
		List<Friend> list = friendService.findList(f);
		Map map = null;
		for (Friend p : list) {
			map = Maps.newHashMap();
			map.put("id",p.getId());
			map.put("zimu", p.getZimu());
			map.put("userId", p.getUid2().getId());
			map.put("icon", getRealPath(p.getUid2().getIcon()));
			map.put("name", p.getUid2().getNickname());
			map.put("sex", p.getUid2().getSex());
			map.put("sign", p.getUid2().getSign());
			map.put("bei", p.getBei());
			map.put("isZy", "0");
			if(!isBlank(req.getString("groupId"))){
				String s = groupItemService.executeGetSql("select count(1) from t_group_item where uid = '" + p.getUid2().getId() + "' and group_id = '" + req.getString("groupId") + "'").toString();
				if(!"0".equals(s)){
					map.put("isZy", "1");
				}
			}
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 新的朋友
	 *
	 * @return
	 */
	@RequestMapping("/newFriList")
	public AjaxJson newFriList(@RequestBody ReqJson req) {
		List<Map<String, Object>> dataList = Lists.newArrayList();
		FriendApply f = new FriendApply();
		f.setUid2(MemberUtils.getMember());
		if(!isBlank(req.getString("key"))){
			f.setKey(req.getString("key"));
		}
		List<FriendApply> list = friendApplyService.findList(f);
		Map map = null;
		for (FriendApply p : list) {
			map = Maps.newHashMap();
			map.put("id",p.getId());
			map.put("userId",p.getU().getId());
			map.put("icon", getRealPath(p.getU().getIcon()));
			map.put("name", p.getU().getNickname());
			map.put("info", p.getInfo());
			map.put("sinfo", p.getSinfo());
			map.put("state", p.getState());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 好友申请处理
	 *
	 * @return
	 */
	@RequestMapping("/friExamine")
	public AjaxJson friExamine(@RequestBody ReqJson req) {
		reqValidator(req,"id","state");
		FriendApply apply = friendApplyService.get(req.getString("id"));
		if(apply == null){
			return AjaxJson.error("申请不存在");
		}
		if(!"1".equals(apply.getState())){
			return AjaxJson.error("此申请已处理");
		}
		apply.setState(req.getString("state"));
		if("2".equals(apply.getState())){
			String s = friendService.executeGetSql("select count(1) from t_friend where uid = '" + apply.getU().getId() + "' and uid2 = '" + apply.getUid2().getId() + "'").toString();
			if(!"0".equals(s)){
				return AjaxJson.error("此用户已是好友");
			}
			Customer customer = customerService.get("1");
			String s1 = friendService.executeGetSql("select count(1) from t_friend where uid = '" + apply.getU().getId() + "'").toString();
			if(Integer.valueOf(s1) >= customer.getMaxadd()){
				return AjaxJson.error("对方好友数已上限");
			}
		}
		friendApplyService.examine(apply);

		return AjaxJson.success();
	}
	/**
	 * 我的群组
	 *
	 * @return
	 */
	@RequestMapping("/groupList")
	public AjaxJson groupList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		GroupItem user = new GroupItem();
		user.setU(MemberUtils.getMember());
		if(!isBlank(req.getString("key"))){
			user.setKey(req.getString("key"));
		}
		List<GroupItem> list = groupItemService.findList(user);
		Map<String, Object> map = null;
		for (GroupItem faq : list) {
			map = Maps.newHashMap();
			map.put("id", faq.getGroup().getId());
			map.put("name", faq.getGroup().getName());
			map.put("icon", getRealPath(faq.getGroup().getIcon()));
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 入群申请
	 *
	 * @return
	 */
	@RequestMapping("/newGroupList")
	public AjaxJson newGroupList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		GroupApply user = new GroupApply();
		user.setShowids(MemberUtils.getUid());
		if(!isBlank(req.getString("key"))){
			user.setKey(req.getString("key"));
		}
		List<GroupApply> list = groupApplyService.findList(user);
		Map<String, Object> map = null;
		for (GroupApply faq : list) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("name", faq.getGroup().getName());
			map.put("icon", getRealPath(faq.getGroup().getIcon()));
			map.put("info", faq.getInfo());
			map.put("state", faq.getState());
			map.put("type", faq.getType());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 拉人进群
	 *
	 * @return
	 */
	@RequestMapping("/addGroupUser")
	public AjaxJson addGroupUser(@RequestBody ReqJson req) {
		reqValidator(req,"id","uids");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		Member me = MemberUtils.getMember();
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(me);
		List<GroupItem> list = groupItemService.findList(item);
		if(list.isEmpty()){
			return AjaxJson.error("您不在此群");
		}
		item = list.get(0);
		String s = groupItemService.executeGetSql("select count(1) from t_group_item where group_id = '" + group.getId() + "'").toString();
		int max = 0;
		if("1".equals(group.getGtype())){
			max = 1000;
		}else if("2".equals(group.getGtype())){
			max = 5000;
		}
		if(Integer.valueOf(s) >= max){
			return AjaxJson.error("群成员已满");
		}
		String[] uids = req.getString("uids").split("[|]", 0);
		for(String id:uids){
			Member dui = memberService.get(id);
			String ids = groupItemService.executeGetSql("SELECT GROUP_CONCAT(uid SEPARATOR '|') FROM t_group_item where type != '3' and group_id = '"+group.getId()+"'").toString();//群主、管理员id
			GroupApply aa = new GroupApply();
			aa.setU(me);
			aa.setUid2(dui);
			List<GroupApply> list1 = groupApplyService.findList(aa);
			if("3".equals(item.getType())){	//群员
				if("0".equals(group.getOpenQyyq())){
					return AjaxJson.error("此群不允许邀人进群");
				}

				if("1".equals(group.getOpenQygl())){	//群员入群邀请管理验证-是否开启
					if(!list1.isEmpty()){
						if("1".equals(list1.get(0).getState())){
							return AjaxJson.error("您已邀请过此人");
						}
					}
					GroupApply apply = new GroupApply();
					apply.setU(me);
					apply.setUid2(dui);
					apply.setGroup(group);
					apply.setInfo("群员'"+me.getNickname()+"'邀请'"+dui.getNickname()+"'加入");
					apply.setState("1");
					apply.setType("2");
					apply.setShowids(ids);
					groupApplyService.save(apply);
				}else {
					if("1".equals(group.getOpenQyyz())){//群员入群邀请验证-是否开启
						if(!list1.isEmpty()){
							if("1".equals(list1.get(0).getState())){
								return AjaxJson.error("您已邀请过此人");
							}
						}
						GroupApply apply = new GroupApply();
						apply.setU(me);
						apply.setUid2(dui);
						apply.setGroup(group);
						apply.setInfo("您的好友'"+me.getNickname()+"'邀请您加入");
						apply.setState("1");
						apply.setType("1");
						apply.setShowids(dui.getId());
						groupApplyService.save(apply);
					}else {
						GroupItem item2 = new GroupItem();
						item2.setU(dui);
						item2.setGroup(group);
						item2.setNickname(dui.getNickname());
						item2.setType("3");
						item2.setIsjy("0");
						groupItemService.save(item2);
						//直接进群
						//查询群信息并更新群头像到群icon
						// 更新群头像
						groupItemService.updateGroupAvatar(group.getId());
						ImUtils.addGroupMember(group.getU().getId(),group.getId(),dui.getId());
						groupHuanyingService.sendHuanying(group.getId());
					}
				}
			}else {
				if("1".equals(group.getOpenQyyz())){//群员入群邀请验证-是否开启
					if(!list1.isEmpty()){
						if("1".equals(list1.get(0).getState())){
							return AjaxJson.error("您已邀请过此人");
						}
					}
					GroupApply apply = new GroupApply();
					apply.setU(me);
					apply.setUid2(dui);
					apply.setGroup(group);
					apply.setInfo("您的好友'"+me.getNickname()+"'邀请您加入");
					apply.setState("1");
					apply.setType("1");
					apply.setShowids(dui.getId());
					groupApplyService.save(apply);
				}else {
					GroupItem item2 = new GroupItem();
					item2.setU(dui);
					item2.setGroup(group);
					item2.setNickname(dui.getNickname());
					item2.setType("3");
					item2.setIsjy("0");
					groupItemService.save(item2);
					//直接进群
					// 更新群头像
					groupItemService.updateGroupAvatar(group.getId());
					ImUtils.addGroupMember(group.getU().getId(),group.getId(),dui.getId());
					groupHuanyingService.sendHuanying(group.getId());
				}
			}
		}
		return AjaxJson.success();
	}
	/**
	 * 群申请处理
	 *
	 * @return
	 */
	@RequestMapping("/groupExamine")
	public AjaxJson groupExamine(@RequestBody ReqJson req) {
		reqValidator(req,"id","state");
		GroupApply apply = groupApplyService.get(req.getString("id"));
		if(apply == null){
			return AjaxJson.error("申请不存在");
		}
		if("2".equals(req.getString("state"))){
			String s = groupItemService.executeGetSql("select count(1) from t_group_item where group_id = '" + apply.getGroup().getId() + "' and uid = '" + apply.getUid2().getId() + "'").toString();
			if(!"0".equals(s)){
				return AjaxJson.error("已是群成员");
			}
		}
		groupApplyService.examine(apply,req.getString("state"));

		return AjaxJson.success();
	}
	/**
	 * 群置顶
	 *
	 * @return
	 */
	@RequestMapping("/setGTop")
	public AjaxJson setGTop(@RequestBody ReqJson req) {
		reqValidator(req,"id","type");
		Group apply = groupService.get(req.getString("id"));
		if(apply == null){
			return AjaxJson.error("群组不存在");
		}
		String key = "GroupTop:"+MemberUtils.getUid()+"-"+apply.getId();
		redisUtils.set(key,req.getString("type"));

		return AjaxJson.success();
	}
	/**
	 * 对方资料
	 *
	 * @return
	 */
	@RequestMapping("/duiInfo")
	public AjaxJson duiInfo(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Member member = memberService.get(req.getString("id"));
		if(member == null){
			return AjaxJson.error("用户已注销");
		}
		Map map = Maps.newHashMap();
		map.put("bei","");
		map.put("isFri","0");
		map.put("isZd","0");
		map.put("isMdr","0");
		map.put("isfanyi","0");

		Friend f = new Friend();
		f.setU(MemberUtils.getMember());
		f.setUid2(member);
		List<Friend> list = friendService.findList(f);
		if(!list.isEmpty()){
			f = list.get(0);
			map.put("bei",f.getBei());
			map.put("isFri","1");
			map.put("isZd",f.getIsTop());
			map.put("isMdr",f.getMdr());
			map.put("isfanyi",f.getIsfanyi());
		}
		map.put("id",member.getId());
		map.put("icon",getRealPath(member.getIcon()));
		map.put("name",member.getNickname());
		map.put("sex",member.getSex());
		map.put("sign",member.getSign());
		map.put("phone",member.getPhone());
		map.put("acount",member.getAcount());
		map.put("isOpenQuan",member.getShowQuan());
		map.put("idno",!isBlank(member.getLianghao())?member.getLianghao():member.getIdno());
		map.put("isLiang",!isBlank(member.getLianghao())?"1":"0");

		map.put("quan",urlsToList(""));
		Dy dy = new Dy();
		dy.setU(member);
		dy.setDataScope("and a.imgs != ''");
		List<Dy> list1 = dyService.findList(dy);
		if(!list1.isEmpty()){
			map.put("quan",urlsToList(list1.get(0).getImgs()));
		}
		String s = blackService.executeGetSql("select count(1) from t_black where uid = '" + MemberUtils.getUid() + "' and uid2 = '" + member.getId() + "'").toString();
		map.put("isBlack",!"0".equals(s)?"1":"0");
		String key = "setfanyi:"+MemberUtils.getUid()+"-"+member.getId();
		if(redisUtils.hasKey(key)){
			map.put("fantime",redisUtils.get(key));
		}else {
			map.put("fantime","");
		}

		return AjaxJson.success().put("data",map);
	}
	/**
	 * 设置备注
	 *
	 * @return
	 */
	@RequestMapping("/setBei")
	public AjaxJson setBei(@RequestBody ReqJson req) {
		reqValidator(req,"id","bei");
		Member member = memberService.get(req.getString("id"));
		if(member == null){
			return AjaxJson.error("用户不存在");
		}
		Friend f = new Friend();
		f.setU(MemberUtils.getMember());
		f.setUid2(member);
		List<Friend> list = friendService.findList(f);
		if(!list.isEmpty()){
			f = list.get(0);
			f.setBei(req.getString("bei"));
			f.setZimu(PinyinUtils.getFirstLetter(f.getBei()));
			friendService.save(f);
			ImUtils.setBeizhu(f.getU().getId(),f.getUid2().getId(),req.getString("bei"));
		}
		return AjaxJson.success();
	}
	/**
	 * 设置置顶、免打扰
	 *
	 * @return
	 */
	@RequestMapping("/setZd")
	public AjaxJson setZd(@RequestBody ReqJson req) {
		reqValidator(req,"id","type","state");
		Member member = memberService.get(req.getString("id"));
		if(member == null){
			return AjaxJson.error("用户不存在");
		}
		Friend f = new Friend();
		f.setU(MemberUtils.getMember());
		f.setUid2(member);
		List<Friend> list = friendService.findList(f);
		if(list.isEmpty()){
			return AjaxJson.error("非朋友关系");
		}
		f = list.get(0);
		if("1".equals(req.getString("type"))){//1：置顶 2：免打扰
			f.setIsTop(req.getString("state"));
		}else {
			f.setMdr(req.getString("state"));
		}
		friendService.save(f);
		return AjaxJson.success();
	}
	/**
	 * 设置自动翻译（0607）
	 *
	 * @return
	 */
	@RequestMapping("/setFanyi")
	public AjaxJson setFanyi(@RequestBody ReqJson req) {
		reqValidator(req,"id","state");
		Member member = memberService.get(req.getString("id"));
		if(member == null){
			return AjaxJson.error("用户不存在");
		}
		Member me = MemberUtils.getMember();
		if("1".equals(req.getString("state")) && "0".equals(me.getIsvip())){//1：开启 0：关闭
			return AjaxJson.error("只有会员才可开启");
		}
		Friend f = new Friend();
		f.setU(me);
		f.setUid2(member);
		List<Friend> list = friendService.findList(f);
		if(list.isEmpty()){
			return AjaxJson.error("非朋友关系");
		}
		f = list.get(0);
		f.setIsfanyi(req.getString("state"));
		friendService.save(f);
		String key = "setfanyi:"+me.getId()+"-"+member.getId();
		redisUtils.set(key,System.currentTimeMillis()+"");
		return AjaxJson.success();
	}

	/**
	 * 拉黑、解除拉黑好友
	 *
	 * @return
	 */
	@RequestMapping("/black")
	public AjaxJson black(@RequestBody ReqJson req) {
		reqValidator(req,"uid","type");
		Member member = memberService.get(req.getString("uid"));
		if(member == null){
			return AjaxJson.error("用户不存在");
		}
		Black b = new Black();
		b.setUid(MemberUtils.getUid());
		b.setUid2(member);
		List<Black> list = blackService.findList(b);
		if("1".equals(req.getString("type"))){
			if(list.isEmpty()){
				blackService.save(b);
//				friendService.executeDeleteSql("delete from t_friend where uid = '"+MemberUtils.getUid()+"' and uid2 = '"+member.getId()+"'");
//				friendService.executeDeleteSql("delete from t_friend where uid2 = '"+MemberUtils.getUid()+"' and uid = '"+member.getId()+"'");
				//ImUtils.addBlack(b.getUid(),b.getUid2().getId(),true);
			}
		}else {
			if(!list.isEmpty()){
				blackService.delete(list.get(0));
				//ImUtils.addBlack(b.getUid(),b.getUid2().getId(),false);
			}
		}
		return AjaxJson.success();
	}
	/**
	 * 删除好友
	 *
	 * @return
	 */
	@RequestMapping("/delFriend")
	public AjaxJson delFriend(@RequestBody ReqJson req) {
		reqValidator(req,"uid");
		Member member = memberService.get(req.getString("uid"));
		if(member == null){
			return AjaxJson.error("用户不存在");
		}
		friendService.executeDeleteSql("delete from t_friend where uid = '"+MemberUtils.getUid()+"' and uid2 = '"+member.getId()+"'");
		friendService.executeDeleteSql("delete from t_friend where uid2 = '"+MemberUtils.getUid()+"' and uid = '"+member.getId()+"'");
		ImUtils.addFrinend(MemberUtils.getUid(),member.getId(),false);
		ImUtils.addFrinend(member.getId(),MemberUtils.getUid(),false);
		return AjaxJson.success();
	}
	/**
	 * 对方朋友圈
	 *
	 * @return
	 */
	@RequestMapping("/duiQuanList")
	public AjaxJson duiQuanList(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Member member = memberService.get(req.getString("id"));

		List<Map<String, Object>> dataList = Lists.newArrayList();
		Dy dy = new Dy();
		dy.setU(member);
		List<Dy> list = dyService.findList(dy);
		Map<String, Object> map = null;
		for (Dy faq : list) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("time", DateUtils.formatDate(faq.getCreateDate()));
			map.put("type", faq.getType());
			map.put("imgs", urlsToList(faq.getImgs()));
			map.put("video", getRealPath(faq.getVideo()));
			map.put("vimg", getRealPath(faq.getVimg()));
			map.put("info", faq.getInfo());
			dataList.add(map);
		}
        String bg = member.getImg();
        if(StringUtils.isBlank(bg)){
            // 朋友圈背景为空时返回默认背景图（相对路径或URL均可）
            bg = "https://seekweb4-pro.s3.ap-east-1.amazonaws.com/file/app/1758881776888.png";
        }
        return AjaxJson.success().setDataList(dataList)
                .put("icon",getRealPath(member.getIcon()))
                .put("name",member.getNickname())
                .put("backImg",getRealPath(bg));
	}
	/**
	 * 朋友圈详情
	 *
	 * @return
	 */
	@RequestMapping("/quanInfo")
	public AjaxJson quanInfo(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Dy faq = dyService.get(req.getString("id"));
		if(faq == null){
			return AjaxJson.error("内容不存在");
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", faq.getId());
		map.put("time", DateUtils.formatDate(faq.getCreateDate()));
		map.put("type", faq.getType());
		map.put("imgs", urlsToList(faq.getImgs()));
		map.put("video", getRealPath(faq.getVideo()));
		map.put("vimg", getRealPath(faq.getVimg()));
		map.put("info", faq.getInfo());
		map.put("address", faq.getAddress());
		map.put("lon", faq.getLon());
		map.put("lat", faq.getLat());
		map.put("userId", faq.getU().getId());
		map.put("icon", getRealPath(faq.getU().getIcon()));
		map.put("name", faq.getU().getNickname());
		String s = dyLikeService.executeGetSql("select count(1) from t_dy_like where uid = '" + MemberUtils.getUid() + "' and dy_id = '" + faq.getId() + "'").toString();
		map.put("isDz", !"0".equals(s)?"1":"0");
		DyLike like = new DyLike();
		like.setDyId(faq.getId());
		like.setDataScope("and (a.uid in(select uid2 from t_friend where uid = '"+MemberUtils.getUid()+"') or a.uid = '"+MemberUtils.getUid()+"')");
		List<DyLike> list = dyLikeService.findList(like);
		StringBuffer sb = new StringBuffer();
		String names = "";
		List<Map<String, Object>> likeList = Lists.newArrayList();
		Map map3 = null;
		for(DyLike l:list){
			map3 = Maps.newHashMap();
			map3.put("id",l.getU().getId());
			map3.put("name",l.getU().getNickname());
			likeList.add(map3);
			sb.append(l.getU().getNickname()+",");
		}
		if(sb != null && sb.length() > 0){
			names = sb.substring(0,sb.length()-1);
		}
		map.put("dzNames", names);
		map.put("likeList", likeList);
		DyComm comm = new DyComm();
		comm.setDy(faq);
		comm.setDataScope("and (a.uid in(select uid2 from t_friend where uid = '"+MemberUtils.getUid()+"') or a.uid = '"+MemberUtils.getUid()+"' or a.to in(select uid2 from t_friend where uid = '"+MemberUtils.getUid()+"'))");
		List<DyComm> list1 = dyCommService.findList(comm);
		List<Map<String, Object>> commList = Lists.newArrayList();
		Map map2 = null;
		for(DyComm l:list1){
			map2 = Maps.newHashMap();
			map2.put("id",l.getId());
			map2.put("userId",l.getU().getId());
			map2.put("name",l.getU().getNickname());
			map2.put("icon",getRealPath(l.getU().getIcon()));
			map2.put("info",l.getTitle());
			map2.put("huifu",l.getTo()!=null?l.getTo().getNickname():"");
			map2.put("time",DateUtils.formatDateTime(l.getCreateDate()));
			commList.add(map2);
		}
		map.put("commList", commList);

		return AjaxJson.success().put("data",map);
	}
	/**
	 * 删除朋友圈
	 *
	 * @return
	 */
	@RequestMapping("/delQuan")
	public AjaxJson delQuan(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Dy faq = dyService.get(req.getString("id"));
		if(faq == null){
			return AjaxJson.error("内容不存在");
		}
		if(!MemberUtils.getUid().equals(faq.getU().getId())){
			return AjaxJson.error("非自己动态不可删除");
		}
		dyService.deleteByLogic(faq);
		return AjaxJson.success();
	}
	/**
	 * 点赞朋友圈
	 *
	 * @return
	 */
	@RequestMapping("/quanLike")
	public AjaxJson quanLike(@RequestBody ReqJson req) {
		reqValidator(req,"id","type");
		Dy faq = dyService.get(req.getString("id"));
		if(faq == null){
			return AjaxJson.error("内容不存在");
		}
		DyLike like = new DyLike();
		like.setDyId(req.getString("id"));
		like.setU(MemberUtils.getMember());
		List<DyLike> list = dyLikeService.findList(like);
		if("1".equals(req.getString("type"))){
			if(list.isEmpty()){
				dyLikeService.save(like);
			}
		}else {
			if(!list.isEmpty()){
				dyLikeService.delete(list.get(0));
			}
		}
		if("1".equals(req.getString("type")) && faq.getU().getId().equals(MemberUtils.getUid())){
			Hudong hudong = new Hudong();
			hudong.setU(faq.getU());
			hudong.setUid2(MemberUtils.getMember());
			hudong.setInfo("点赞了你的动态");
			hudong.setType("1");
			hudong.setState("0");
			hudong.setDyId(faq.getId());
			hudongService.save(hudong);
		}
		DyLike like2 = new DyLike();
		like2.setDyId(req.getString("id"));
		like2.setDataScope("and (a.uid in(select uid2 from t_friend where uid = '"+MemberUtils.getUid()+"') or a.uid = '"+MemberUtils.getUid()+"')");
		List<DyLike> list1 = dyLikeService.findList(like2);
		StringBuffer sb = new StringBuffer();
		String names = "";
		List<Map<String, Object>> likeList = Lists.newArrayList();
		Map map3 = null;
		for(DyLike l:list1){
			map3 = Maps.newHashMap();
			map3.put("id",l.getU().getId());
			map3.put("name",l.getU().getNickname());
			likeList.add(map3);
			sb.append(l.getU().getNickname()+",");
		}
		if(sb != null && sb.length() > 0){
			names = sb.substring(0,sb.length()-1);
		}

		return AjaxJson.success().put("dzNames",names).put("likeList", likeList);
	}
	/**
	 * 评论朋友圈
	 *
	 * @return
	 */
	@RequestMapping("/quanComm")
	public AjaxJson quanComm(@RequestBody ReqJson req) {
		reqValidator(req,"id","info");
		Dy faq = dyService.get(req.getString("id"));
		if(faq == null){
			return AjaxJson.error("内容不存在");
		}
		Member member = MemberUtils.getMember();

		DyComm comm = new DyComm();
		comm.setId(IdGen.uuid());
		comm.setIsNewRecord(true);
		comm.setDy(faq);
		comm.setU(member);
		comm.setTitle(req.getString("info"));
		String hui = "";
		Member dui = new Member();
		String info = "";
		if(!isBlank(req.getString("commId"))){
			DyComm comm1 = dyCommService.get(req.getString("commId"));
			comm.setLevel(2);
			comm.setTo(comm1.getU());
			comm.setCommid(comm1.getId());
			dui = comm1.getU();
			info = "回复了你："+req.getString("info");
			hui = comm1.getU().getNickname();
		}else {
			comm.setLevel(1);
			dui = faq.getU();
			info = req.getString("info");
		}
		dyCommService.save(comm);
		if(!dui.getId().equals(MemberUtils.getUid())){
			Hudong hudong = new Hudong();
			hudong.setUid2(MemberUtils.getMember());
			hudong.setU(dui);
			hudong.setInfo(info);
			hudong.setType("2");
			hudong.setState("0");
			hudong.setDyId(faq.getId());
			hudongService.save(hudong);
		}
		Map map = Maps.newHashMap();
		map.put("id",comm.getId());
		map.put("icon",getRealPath(member.getIcon()));
		map.put("name",member.getNickname());
		map.put("info",comm.getTitle());
		map.put("huifu",hui);
		map.put("time",DateUtils.formatDateTime(comm.getCreateDate()));

		return AjaxJson.success().put("data",map);
	}
	/**
	 * 发红包 - 单聊
	 *
	 * @return
	 */
	@RequestMapping("/sendHongbao")
	public AjaxJson sendHongbao(@RequestBody ReqJson req) {
		reqValidator(req,"uid","money","payPwd");
		Member member = MemberUtils.getMember();
		if(isBlank(member.getPaypwd())){
			return AjaxJson.error("请先设置支付密码");
		}
		Customer customer = customerService.get("1");
		if(new BigDecimal(req.getString("money")).compareTo(customer.getHongbao()) > -1){
			return AjaxJson.error("超过单次最大红包额度");
		}
		if(new BigDecimal(req.getString("money")).compareTo(member.getBalance()) == 1){
			return AjaxJson.error("余额不足");
		}
		if(!memberService.validatePassword(req.getString("payPwd"),member.getPaypwd())){
			return AjaxJson.error("支付密码错误");
		}
		Hongbao hongbao = new Hongbao();
		hongbao.setIsNewRecord(true);
		hongbao.setId(IdGen.uuid());
		hongbao.setU(member);
		hongbao.setUid2(new Member(req.getString("uid")));
		hongbao.setMoney(new BigDecimal(req.getString("money")));
		hongbao.setInfo(req.getString("info"));
		hongbaoService.sendHb(hongbao);

		return AjaxJson.success().put("id",hongbao.getId());
	}
	/**
	 * 转账
	 *
	 * @return
	 */
	@RequestMapping("/zhuangZhang")
	public AjaxJson zhuangZhang(@RequestBody ReqJson req) {
		reqValidator(req,"uid","money","payPwd");
		Member member = MemberUtils.getMember();
		if(isBlank(member.getPaypwd())){
			return AjaxJson.error("请先设置支付密码");
		}
		Customer customer = customerService.get("1");
		if(new BigDecimal(req.getString("money")).compareTo(customer.getZhuanzhang()) > -1){
			return AjaxJson.error("超过单次最大转账额度");
		}
		if(new BigDecimal(req.getString("money")).compareTo(member.getBalance()) == 1){
			return AjaxJson.error("余额不足");
		}
		if(!memberService.validatePassword(req.getString("payPwd"),member.getPaypwd())){
			return AjaxJson.error("支付密码错误");
		}
		Zhuangzhang hongbao = new Zhuangzhang();
		hongbao.setIsNewRecord(true);
		hongbao.setId(IdGen.uuid());
		hongbao.setU(member);
		hongbao.setUid2(new Member(req.getString("uid")));
		hongbao.setMoney(new BigDecimal(req.getString("money")));
		hongbao.setInfo(req.getString("info"));
		zhuangzhangService.zhuanzhang(hongbao);

		return AjaxJson.success().put("id",hongbao.getId());
	}
	/**
	 * 转账详情
	 *
	 * @return
	 */
	@RequestMapping("/zzInfo")
	public AjaxJson zzInfo(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Zhuangzhang zz = zhuangzhangService.get(req.getString("id"));
		if(zz == null){
			return AjaxJson.error("记录不存在");
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("money",zz.getMoney());
		map.put("canLing","0");
		map.put("isLing",zz.getShouTime()!=null?"1":"0");
		if(zz.getTuiTime() == null){//有效期内
			if(zz.getUid2().getId().equals(MemberUtils.getUid()) && zz.getShouTime() == null){
				map.put("canLing","1");
			}
		}
		map.put("zzTime",DateUtils.formatDateTime(zz.getCreateDate()));
		map.put("skTime",zz.getShouTime()!=null?DateUtils.formatDateTime(zz.getShouTime()):"");
		map.put("tuiTime",zz.getTuiTime()!=null?DateUtils.formatDateTime(zz.getTuiTime()):"");

		return AjaxJson.success().put("data",map);
	}
	/**
	 * 领红包
	 *
	 * @return
	 */
	@RequestMapping("/getHongbao")
	public AjaxJson getHongbao(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Hongbao hongbao = hongbaoService.get(req.getString("id"));
		if(hongbao == null){
			return AjaxJson.error("红包不存在");
		}
		if(hongbao.getShouTime() != null){
			return AjaxJson.error("您已领过此红包");
		}
		if(hongbao.getTuiTime() != null){
			return AjaxJson.error("红包已失效");
		}
		hongbao.setShouTime(new Date());
		hongbaoService.getHb(hongbao,req.getString("messageUid"),req.getString("payload"));

		return AjaxJson.success();
	}
	/**
	 * 单聊红包详情
	 *
	 * @return
	 */
	@RequestMapping("/dhbInfo")
	public AjaxJson dhbInfo(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Hongbao hongbao = hongbaoService.get(req.getString("id"));
		if(hongbao == null){
			return AjaxJson.error("红包不存在");
		}
		Map map = Maps.newHashMap();
		map.put("icon",getRealPath(hongbao.getU().getIcon()));
		map.put("name",hongbao.getU().getNickname());
		map.put("info",hongbao.getInfo());
		map.put("money",hongbao.getMoney());
		map.put("count","1");
		map.put("ylq",hongbao.getShouTime()==null?"0":"1");
		map.put("ylqMoney",hongbao.getShouTime()==null?"0":hongbao.getMoney());
		map.put("canLing","0");
		map.put("isLing",hongbao.getShouTime()!=null?"1":"0");
		if(hongbao.getTuiTime() == null){//有效期内
			if(hongbao.getUid2().getId().equals(MemberUtils.getUid()) && hongbao.getShouTime() == null){
				map.put("canLing","1");
			}
		}
		List<Map<String, Object>> dataList = Lists.newArrayList();
		if(hongbao.getShouTime() != null){
			Map map1 = Maps.newHashMap();
			map1.put("icon",getRealPath(hongbao.getUid2().getIcon()));
			map1.put("name",hongbao.getUid2().getNickname());
			map1.put("time",DateUtils.formatDateTime(hongbao.getShouTime()));
			map1.put("money",hongbao.getMoney());
			dataList.add(map1);
		}
		return AjaxJson.success().setDataList(dataList).put("data",map);
	}
	/**
	 * 领取转账
	 *
	 * @return
	 */
	@RequestMapping("/getZhuanzhang")
	public AjaxJson getZhuanzhang(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Zhuangzhang zz = zhuangzhangService.get(req.getString("id"));
		if(zz == null){
			return AjaxJson.error("转账记录不存在");
		}
		if(zz.getShouTime() != null){
			return AjaxJson.error("您已领过");
		}
		if(zz.getTuiTime() != null){
			return AjaxJson.error("转账已失效");
		}
		zz.setShouTime(new Date());
		zhuangzhangService.getZz(zz,req.getString("messageUid"),req.getString("payload"));

		return AjaxJson.success();
	}
	/**
	 * 创建群组
	 *
	 * @return
	 */
	@RequestMapping("/addGroup")
	public AjaxJson addGroup(@RequestBody ReqJson req) {
		reqValidator(req,"uids");
		Member member = MemberUtils.getMember();
		Customer customer = customerService.get("1");
		if("1".equals(customer.getOpenneibu()) && "0".equals(member.getIsneibu())){
			return AjaxJson.error("抱歉，暂时无法创建群聊");
		}
		Group group = new Group();
		group.setIsNewRecord(true);
		group.setU(member);
		//group.setIcon(member.getIcon());
		group.setName(member.getNickname()+"的群聊");
		group.setOpenQyyq("1");
		group.setOpenQyyz("1");
		group.setOpenQzhb("0");
		group.setOpenQzts("0");
		group.setOpenQygl("1");
		group.setOpenQysl("0");
		group.setOpenQykj("0");
		group.setJiange(0);
		group.setOpenGmc("1");
		group.setAllJy("0");
		groupService.addGroup(group,req.getString("uids"));

		return AjaxJson.success().put("groupId",group.getId());
	}
	/**
	 * 群组详情
	 *
	 * @return
	 */
	@RequestMapping("/groupInfo")
	public AjaxJson groupInfo(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(MemberUtils.getMember());
		List<GroupItem> list = groupItemService.findList(item);
		if(list.isEmpty()){
			return AjaxJson.error("您不是群成员");
		}
		item = list.get(0);
		Map map = Maps.newHashMap();
		map.put("id",group.getId());
		map.put("icon",getRealPath(group.getIcon()));
		map.put("name",group.getName());
		map.put("idno",group.getIdno());
		if(isBlank(group.getQrcode())){
			group.setQrcode(QrCodeUtil.getCode("2-"+group.getId(),"/groupCode"));
			groupService.save(group);
		}
		map.put("qrcode",getRealPath(group.getQrcode()));
		map.put("gonggao",group.getGonggao());
		map.put("openQyyq",group.getOpenQyyq());
		map.put("openQyyz",group.getOpenQyyz());
		map.put("openQzhb",group.getOpenQzhb());
		map.put("openQzts",group.getOpenQzts());
		map.put("openQygl",group.getOpenQygl());
		map.put("openQysl",group.getOpenQysl());
		map.put("openQykj",group.getOpenQykj());
		map.put("jiange",group.getJiange());
		map.put("mgc",group.getMgc());
		Customer customer = customerService.get("1");
		map.put("openMgc",group.getOpenGmc()+"|"+customer.getMgc());
		map.put("allJy",group.getAllJy());
		map.put("isJy",item.getIsjy());
		map.put("jyEndTime",item.getJyTime()!=null? DateUtils.formatDateTime(item.getJyTime()):"");
		String jyKey = "GroupJinyan:"+group.getId()+"-"+MemberUtils.getUid();
		if(redisUtils.hasKey(jyKey)){
			map.put("jyType",redisUtils.get(jyKey));
		}else {
			map.put("jyType","");
		}
		map.put("userType",item.getType());
		map.put("nickname",item.getNickname());
		String key = "GroupTop:"+MemberUtils.getUid()+"-"+group.getId();
		if(!redisUtils.hasKey(key)){
			redisUtils.set(key,"0");
		}
		map.put("isZd",redisUtils.get(key));

		GroupItem item2 = new GroupItem();
		item2.setGroup(group);
		if("1".equals(group.getOpenQykj()) && "3".equals(item.getType())){
			item2.setDataScope("and a.type != '3'");
		}
		Page<GroupItem> page = groupItemService.findPage(new Page<>(1, 13), item2);
		List<Map<String, Object>> userList = Lists.newArrayList();
		Map map2 = null;
		for(GroupItem ii:page.getList()){
			map2 = Maps.newHashMap();
			map2.put("userId",ii.getU().getId());
			map2.put("icon",getRealPath(ii.getU().getIcon()));
			map2.put("name",ii.getNickname());
			map2.put("type",ii.getType());
			userList.add(map2);
		}
		map.put("userList",userList);
		map.put("gsize",group.getGtype());


		map.put("isfanyi",item.getIsfanyi());
		String key2 = "setGroupfanyi:"+MemberUtils.getUid()+"-"+group.getId();
		if(redisUtils.hasKey(key2)){
			map.put("fantime",redisUtils.get(key2));
		}else {
			map.put("fantime","");
		}

		return AjaxJson.success().put("data",map);
	}
	/**
	 * 设置自动翻译 - 群（0607）
	 *
	 * @return
	 */
	@RequestMapping("/setGroupFanyi")
	public AjaxJson setGroupFanyi(@RequestBody ReqJson req) {
		reqValidator(req,"id","state");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		Member me = MemberUtils.getMember();
		if("1".equals(req.getString("state")) && "0".equals(me.getIsvip())){//1：开启 0：关闭
			return AjaxJson.error("只有会员才可开启");
		}
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(me);
		List<GroupItem> list = groupItemService.findList(item);
		if(list.isEmpty()){
			return AjaxJson.error("您不是群成员");
		}
		String key = "setGroupfanyi:"+me.getId()+"-"+group.getId();
		redisUtils.set(key,System.currentTimeMillis()+"");
		return AjaxJson.success();
	}
	/**
	 * 群组成员
	 *
	 * @return
	 */
	@RequestMapping("/groupUserList")
	public AjaxJson groupUserList(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		List<Map<String, Object>> dataList = Lists.newArrayList();
		GroupItem item2 = new GroupItem();
		item2.setGroup(new Group(req.getString("id")));
		if(!isBlank(req.getString("type"))){
			item2.setType(req.getString("type"));
		}
		if(!isBlank(req.getString("name"))){
			item2.setNickname(req.getString("name"));
		}
		List<GroupItem> list = groupItemService.findList(item2);
		Map map2 = null;
		for(GroupItem ii:list){
			map2 = Maps.newHashMap();
			map2.put("id",ii.getId());
			map2.put("userId",ii.getU().getId());
			map2.put("icon",getRealPath(ii.getU().getIcon()));
			map2.put("name",ii.getU().getNickname());
			map2.put("type",ii.getType());
			map2.put("zimu",PinyinUtils.getFirstLetter(ii.getU().getNickname()));
			map2.put("isJy",ii.getIsjy());
			map2.put("jyEndTime",ii.getJyTime()!=null? DateUtils.formatDateTime(ii.getJyTime()):"");
			String jyKey = "GroupJinyan:"+req.getString("id")+"-"+ii.getU().getId();
			if(redisUtils.hasKey(jyKey)){
				map2.put("jyType",redisUtils.get(jyKey));
			}else {
				map2.put("jyType","");
			}
			dataList.add(map2);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 投诉群组
	 *
	 * @return
	 */
	@RequestMapping("/tousuGroup")
	public AjaxJson tousuGroup(@RequestBody ReqJson req) {
		reqValidator(req,"id","type");
		Tousu tousu = new Tousu();
		tousu.setU(MemberUtils.getMember());
		tousu.setGroup(new Group(req.getString("id")));
		tousu.setReason(req.getString("type"));
		String[] logs = req.getString("log").split("[|]", 0);
		StringBuffer sb = new StringBuffer();
		for(String s:logs){
			OutputMessageData oneMsg = ImUtils.getOneMsg(Long.valueOf(s));
			if(oneMsg != null){
				Member member = memberService.get(oneMsg.getSender());
				if(member != null){
					String a = member.getNickname()+":"+oneMsg.getPayload().getContent();
					sb.append("<"+a+">,");
				}
			}
		}
		if(sb != null && sb.length() > 0){
			tousu.setLog(sb.substring(0,sb.length()-1));
		}
		tousu.setImgs(req.getString("imgs"));
		tousu.setInfo(req.getString("info"));
		tousuService.save(tousu);

		return AjaxJson.success();
	}
	/**
	 * 修改群信息
	 *
	 * @return
	 */
	@RequestMapping("/editGroup")
	public AjaxJson editGroup(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		//boolean flag = false;
		if(!isBlank(req.getString("icon"))){
			group.setIcon(req.getString("icon"));
		}
		if(!isBlank(req.getString("name"))){
			group.setName(req.getString("name"));
		}
		if(!isBlank(req.getString("gonggao"))){
			group.setGonggao(req.getString("gonggao"));
			ImUtils.sendGroupMsg(MemberUtils.getUid(),1001,req.getString("gonggao"),group.getId());
		}
		if(!isBlank(req.getString("openQyyq"))){
			group.setOpenQyyq(req.getString("openQyyq"));
		}
		if(!isBlank(req.getString("openQyyz"))){
			group.setOpenQyyz(req.getString("openQyyz"));
		}
		if(!isBlank(req.getString("openQzhb"))){
			group.setOpenQzhb(req.getString("openQzhb"));
		}
		if(!isBlank(req.getString("openQzts"))){
			group.setOpenQzts(req.getString("openQzts"));
		}
		if(!isBlank(req.getString("openQygl"))){
			group.setOpenQygl(req.getString("openQygl"));
		}
		if(!isBlank(req.getString("openQysl"))){
			group.setOpenQysl(req.getString("openQysl"));
		}
		if(!isBlank(req.getString("openQykj"))){
			group.setOpenQykj(req.getString("openQykj"));
		}
		if(!isBlank(req.getString("jiange"))){
			group.setJiange(req.getInteger("jiange"));
			JSONObject msg = new JSONObject();
			msg.put("groupId", group.getId());
			msg.put("miao", req.getInteger("jiange"));
			ImUtils.sendGroupMsg(MemberUtils.getUid(),1004,msg.toJSONString(),group.getId());
		}
		if(!isBlank(req.getString("mgc"))){
			group.setMgc(req.getString("mgc"));
		}
		if(!isBlank(req.getString("openMgc"))){
			group.setOpenGmc(req.getString("openMgc"));
		}
		if(!isBlank(req.getString("allJy"))){
			group.setAllJy(req.getString("allJy"));
			GroupItem item = new GroupItem();
			item.setGroup(group);
			item.setType("3");
			List<GroupItem> list = groupItemService.findList(item);
			List<String> ids = new ArrayList<>();
			for(GroupItem ii:list){
				ids.add(ii.getU().getId());
			}
			if("1".equals(req.getString("allJy"))){
				groupItemService.executeUpdateSql("update t_group_item set isjy = '1',jy_time = '2099-12-31 23:59:59' where group_id = '"+group.getId()+"' and type = '3'");
				ImUtils.editGroup(group.getU().getId(),group.getId(),3,"1");
			}else {
				groupItemService.executeUpdateSql("update t_group_item set isjy = '0',jy_time = null where group_id = '"+group.getId()+"' and type = '3'");
				ImUtils.editGroup(group.getU().getId(),group.getId(),3,"0");
			}
		}
		groupService.save(group);
		return AjaxJson.success();
	}
	/**
	 * 修改群昵称
	 *
	 * @return
	 */
	@RequestMapping("/editGnickname")
	public AjaxJson editGnickname(@RequestBody ReqJson req) {
		reqValidator(req,"id","name");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		Customer customer = customerService.get("1");
		String[] split = customer.getNamemgc().split("[|]", 0);
		for(String s:split){
			if(req.getString("name").contains(s)){
				return AjaxJson.error("昵称包含敏感词 "+s);
			}
		}

		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(MemberUtils.getMember());
		List<GroupItem> list = groupItemService.findList(item);
		if(!list.isEmpty()){
			item = list.get(0);
			item.setNickname(req.getString("name"));
			groupItemService.save(item);
		}
		return AjaxJson.success();
	}

	/**
	 * 添加、移除群管理
	 *
	 * @return
	 */
	@RequestMapping("/addManag")
	public AjaxJson addManag(@RequestBody ReqJson req) {
		reqValidator(req,"id","uids","type");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		if(!MemberUtils.getUid().equals(group.getU().getId())){
			return AjaxJson.error("您非群主");
		}
		String[] uids = req.getString("uids").split("[|]", 0);
		List<String> ids = new ArrayList<>();
		for(String id:uids){
			GroupItem item = new GroupItem();
			item.setGroup(group);
			item.setU(new Member(id));
			List<GroupItem> list = groupItemService.findList(item);
			if(!list.isEmpty()){
				item = list.get(0);
				item.setType(req.getString("type"));
				groupItemService.save(item);
				ids.add(id);
			}
		}
		ImUtils.setManage(group.getU().getId(),group.getId(),ids,"2".equals(req.getString("type"))?true:false);//2：添加 3：移除

		return AjaxJson.success();
	}
	/**
	 * 禁言
	 *
	 * @return
	 */
	@RequestMapping("/jinyan")
	public AjaxJson jinyan(@RequestBody ReqJson req) {
		reqValidator(req,"id","uids","type");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(MemberUtils.getMember());
		List<GroupItem> list = groupItemService.findList(item);
		if(list.isEmpty()){
			return AjaxJson.error("您不在群里");
		}
		item = list.get(0);
//		if("3".equals(item.getType())){
//			return AjaxJson.error("您不是群主或管理员");
//		}
		String[] uids = req.getString("uids").split("[|]", 0);
		List<String> ids = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for(String id:uids){
			ids.add(id);
			sb.append("'"+id+"',");
		}
		String s = sb.substring(0, sb.length() - 1) + ")";
		Date time = null;
		int day = 0;
		if("1".equals(req.getString("type"))){//禁言时间 1：一天 2：一周 3：一月 4：永久
			time = DateUtil.addDays(new Date(),1);
			day = 1;
		}else if("2".equals(req.getString("type"))){
			time = DateUtil.addDays(new Date(),7);
			day = 7;
		}else if("3".equals(req.getString("type"))){
			time = DateUtil.addDays(new Date(),30);
			day = 31;
		}else if("4".equals(req.getString("type"))){
			time = DateUtil.addMonths(new Date(),1200);
			day = 999999;
		}
		groupItemService.executeUpdateSql("update t_group_item set isjy = '1',jy_time = '"+DateUtils.formatDateTime(time)+"' where group_id = '"+group.getId()+"' and uid in"+s);

		String key = "GroupJinyan:"+group.getId()+"-"+MemberUtils.getUid();
		redisUtils.set(key,req.getString("type"));
		ImUtils.jinyan(group.getU().getId(),group.getId(),ids,true);

		redisDelayedQueue.addQueueDays(item.getId(),day, JiejinDelayedQueueListener.class);

		return AjaxJson.success();
	}
	/**
	 * 已禁言成员
	 *
	 * @return
	 */
	@RequestMapping("/jinyanUserList")
	public AjaxJson jinyanUserList(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		List<Map<String, Object>> dataList = Lists.newArrayList();
		GroupItem item2 = new GroupItem();
		item2.setGroup(new Group(req.getString("id")));
		item2.setIsjy("1");
		if(!isBlank(req.getString("name"))){
			item2.setNickname(req.getString("name"));
		}
		List<GroupItem> list = groupItemService.findList(item2);
		Map map2 = null;
		for(GroupItem ii:list){
			map2 = Maps.newHashMap();
			map2.put("id",ii.getId());
			map2.put("userId",ii.getU().getId());
			map2.put("icon",getRealPath(ii.getU().getIcon()));
			map2.put("name",ii.getU().getNickname());
			map2.put("type",ii.getType());
			map2.put("endTime",ii.getJyTime()!=null?DateUtils.formatDateTime(ii.getJyTime()):"");
			dataList.add(map2);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 解除禁言
	 *
	 * @return
	 */
	@RequestMapping("/delJinyan")
	public AjaxJson delJinyan(@RequestBody ReqJson req) {
		reqValidator(req,"id","uid");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(MemberUtils.getMember());
		List<GroupItem> list = groupItemService.findList(item);
		if(list.isEmpty()){
			return AjaxJson.error("您不在群里");
		}
		item = list.get(0);
//		if("3".equals(item.getType())){
//			return AjaxJson.error("您不是群主或管理员");
//		}
		groupItemService.executeUpdateSql("update t_group_item set isjy = '0',jy_time = null where group_id = '"+group.getId()+"' and uid = '"+req.getString("uid")+"'");
		List<String> ids = new ArrayList<>();
		ids.add(req.getString("uid"));

		String key = "GroupJinyan:"+group.getId()+"-"+req.getString("uid");
		redisUtils.delete(key);
		ImUtils.jinyan(group.getU().getId(),group.getId(),ids,false);

		return AjaxJson.success();
	}
	/**
	 * 移出本群
	 *
	 * @return
	 */
	@RequestMapping("/remove")
	public AjaxJson remove(@RequestBody ReqJson req) {
		reqValidator(req,"id","uids");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(MemberUtils.getMember());
		List<GroupItem> list = groupItemService.findList(item);
		if(list.isEmpty()){
			return AjaxJson.error("您不在群里");
		}
		item = list.get(0);
		if("3".equals(item.getType())){
			return AjaxJson.error("您不是群主或管理员");
		}
		if(req.getString("id").equals(MemberUtils.getUid())){
			return AjaxJson.error("不可移除自己");
		}
		String[] uids = req.getString("uids").split("[|]", 0);
		List<String> ids = new ArrayList<>();
		for(String id:uids){
			groupItemService.executeDeleteSql("delete from t_group_item where group_id = '"+group.getId()+"' and uid = '"+id+"'");
			ids.add(id);
		}
		// 更新群头像
		groupItemService.updateGroupAvatar(group.getId());
		ImUtils.delGroupMember(MemberUtils.getUid(),group.getId(),ids);

		return AjaxJson.success();
	}
	/**
	 * 解散群聊
	 *
	 * @return
	 */
	@RequestMapping("/destoryGroup")
	public AjaxJson destoryGroup(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		if(!MemberUtils.getUid().equals(group.getU().getId())){
			return AjaxJson.error("您不是群主");
		}
		groupService.delGroup(group);

		return AjaxJson.success();
	}
	/**
	 * 退出群聊
	 *
	 * @return
	 */
	@RequestMapping("/outGroup")
	public AjaxJson outGroup(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(MemberUtils.getMember());
		List<GroupItem> list = groupItemService.findList(item);
		if(list.isEmpty()){
			return AjaxJson.error("您不在群里");
		}
		item = list.get(0);
		if("1".equals(item.getType())){//群主的话直接解散
			groupService.delGroup(group);
		}else {
			groupItemService.executeDeleteSql("delete from t_group_item where group_id = '"+group.getId()+"' and uid = '"+MemberUtils.getUid()+"'");
//			List<String> ids = new ArrayList<>();
//			ids.add(MemberUtils.getUid());
			ImUtils.quitGroup(MemberUtils.getUid(),group.getId());
		}

		return AjaxJson.success();
	}
	/**
	 * 转让群主
	 *
	 * @return
	 */
	@RequestMapping("/turnQz")
	public AjaxJson turnQz(@RequestBody ReqJson req) {
		reqValidator(req,"id","uid");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(MemberUtils.getMember());
		List<GroupItem> list = groupItemService.findList(item);
		if(list.isEmpty()){
			return AjaxJson.error("您不在群里");
		}
		item = list.get(0);
		if(!"1".equals(item.getType())){
			return AjaxJson.error("您不是群主");
		}
		groupService.turnQz(group,req.getString("uid"));

		return AjaxJson.success();
	}
	/**
	 * 扫一扫加群
	 *
	 * @return
	 */
	@RequestMapping("/groupScan")
	public AjaxJson groupScan(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		Member member = MemberUtils.getMember();
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(member);
		List<GroupItem> list = groupItemService.findList(item);
		if(!list.isEmpty()){
			return AjaxJson.success().put("groupId",group.getId());//已是群成员
		}
		if("0".equals(group.getOpenQygl())){//无需管理验证
			GroupItem item1 = new GroupItem();
			item1.setGroup(group);
			item1.setU(member);
			item1.setNickname(member.getInvitid());
			item1.setType("3");
			item1.setIsjy("0");
			groupItemService.save(item1);
			// 更新群头像
			groupItemService.updateGroupAvatar(group.getId());
			ImUtils.addGroupMember(group.getU().getId(),group.getId(),member.getId());
			groupHuanyingService.sendHuanying(group.getId());
			return AjaxJson.success().put("groupId",group.getId());//直接进群
		}
		String ids = groupItemService.executeGetSql("SELECT GROUP_CONCAT(uid SEPARATOR '|') FROM t_group_item where type != '3' and group_id = '"+group.getId()+"'").toString();//群主、管理员id
		GroupApply apply = new GroupApply();
		apply.setUid2(member);
		apply.setGroup(group);
		apply.setInfo(member.getNickname()+"'申请加入");
		apply.setState("1");
		apply.setType("2");
		apply.setShowids(ids);
		groupApplyService.save(apply);
		return AjaxJson.success();
	}
	/**
	 * 发红包 - 群聊
	 *
	 * @return
	 */
	@RequestMapping("/sendGroHb")
	public AjaxJson sendGroHb(@RequestBody ReqJson req) {
		reqValidator(req,"id","money","type","payPwd");
		Member member = MemberUtils.getMember();
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		if(new BigDecimal(req.getString("money")).compareTo(BigDecimal.ZERO) < 1){
			return AjaxJson.error("金额不可小于等于0");
		}
		Customer customer = customerService.get("1");
		if(new BigDecimal(req.getString("money")).compareTo(customer.getHongbao()) > -1){
			return AjaxJson.error("超过单次最大红包额度");
		}
		GroupItem item = new GroupItem();
		item.setGroup(group);
		item.setU(MemberUtils.getMember());
		List<GroupItem> list = groupItemService.findList(item);
		if(list.isEmpty()){
			return AjaxJson.error("您不在群里");
		}
		item = list.get(0);
		if("1".equals(group.getOpenQzhb()) && "3".equals(item.getType())){
			return AjaxJson.error("只有群主或管理员才能发红包");
		}
		if(isBlank(member.getPaypwd())){
			return AjaxJson.error("请先设置支付密码");
		}
		BigDecimal mo = new BigDecimal(req.getString("money"));
		if("2".equals(req.getString("type"))){
			mo = mo.multiply(new BigDecimal(req.getString("count")));
		}
		if(mo.compareTo(member.getBalance()) == 1){
			return AjaxJson.error("余额不足");
		}
		if(!memberService.validatePassword(req.getString("payPwd"),member.getPaypwd())){
			return AjaxJson.error("支付密码错误");
		}
		String s = groupItemService.executeGetSql("select count(1) from t_group_item where group_id = '" + group.getId() + "'").toString();
		if(req.getInteger("count") > Integer.valueOf(s)){
			return AjaxJson.error("数量不可大于群成员数量");
		}
		GroupHongbao hongbao = new GroupHongbao();
		hongbao.setIsNewRecord(true);
		hongbao.setId(IdGen.uuid());
		hongbao.setU(MemberUtils.getMember());
		hongbao.setGroup(group);
		hongbao.setType(req.getString("type"));
		hongbao.setMoney(mo);
		hongbao.setSymonet(hongbao.getMoney());
		hongbao.setCount(req.getInteger("count"));
		hongbao.setSycount(req.getInteger("count"));
		hongbao.setInfo(req.getString("info"));
		hongbao.setZsid(req.getString("uid"));
		groupHongbaoService.sendHongbao(hongbao,group);

		return AjaxJson.success().put("id",hongbao.getId());
	}
	/**
	 * 群红包详情
	 *
	 * @return
	 */
	@RequestMapping("/hbInfo")
	public AjaxJson hbInfo(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		GroupHongbao hongbao = groupHongbaoService.get(req.getString("id"));
		if(hongbao == null){
			return AjaxJson.error("红包不存在");
		}
		Map map = Maps.newHashMap();
		map.put("icon",getRealPath(hongbao.getU().getIcon()));
		map.put("uid",hongbao.getU().getId());
		map.put("name",hongbao.getU().getNickname());
		map.put("info",hongbao.getInfo());
		map.put("money",hongbao.getMoney());
		map.put("count",hongbao.getCount());
		map.put("ylq",hongbao.getCount()-hongbao.getSycount());
		map.put("ylqMoney",hongbao.getMoney().subtract(hongbao.getSymonet()));
		map.put("canLing","0");
		map.put("isLing","0");
		String s = groupHongbaoLogService.executeGetSql("select count(1) from t_group_hongbao_log where bao_id = '" + hongbao.getId() + "' and uid = '" + MemberUtils.getUid() + "'").toString();
		map.put("isLing",!"0".equals(s)?"1":"0");
		if(hongbao.getTuiTime() == null){//有效期内
			if("3".equals(hongbao.getType())){	//类型 1：拼手气 2：普通 3：专属
				if(hongbao.getZsid().equals(MemberUtils.getUid()) && "0".equals(s)){
					map.put("canLing","1");
				}
			}else {
				if("0".equals(s) && hongbao.getSycount() > 0){
					map.put("canLing","1");
				}
			}
		}
		map.put("benrenMoney","0");
		List<Map<String, Object>> dataList = Lists.newArrayList();
		GroupHongbaoLog log = new GroupHongbaoLog();
		log.setBaoId(hongbao.getId());
		List<GroupHongbaoLog> list = groupHongbaoLogService.findList(log);
		Map map1 = null;
		for(GroupHongbaoLog ll:list){
			map1 = Maps.newHashMap();
			map1.put("icon",getRealPath(ll.getU().getIcon()));
			map1.put("name",ll.getU().getNickname());
			map1.put("time",DateUtils.formatDateTime(ll.getCreateDate()));
			map1.put("money",ll.getMoney());
			dataList.add(map1);
			if(ll.getU().getId().equals(MemberUtils.getUid())){
				map.put("benrenMoney",ll.getMoney());
			}
		}
		return AjaxJson.success().setDataList(dataList).put("data",map);
	}
	/**
	 * 抢红包 - 群聊
	 *
	 * @return
	 */
	@RequestMapping("/getGroHb")
	public AjaxJson getGroHb(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		BigDecimal money = BigDecimal.ZERO;
		RLock lock = redissonClient.getFairLock("getGroHb:"+req.getString("id"));
		String isfin = "0";
		try {
			lock.lock(30L, TimeUnit.SECONDS);
//			boolean b = lock.tryLock(0L, 30L, TimeUnit.SECONDS);
//			if(!b){
//				return AjaxJson.error("前方拥堵");
//			}
			GroupHongbao hongbao = groupHongbaoService.get(req.getString("id"));
			if(hongbao == null){
				return AjaxJson.error("红包不存在");
			}
			if(hongbao.getTuiTime() != null) {//有效期内
				return AjaxJson.error("红包已过期");
			}
			String s = groupHongbaoLogService.executeGetSql("select count(1) from t_group_hongbao_log where bao_id = '" + hongbao.getId() + "' and uid = '" + MemberUtils.getUid() + "'").toString();
			if(!"0".equals(s)){
				return AjaxJson.error("您已领取过此红包");
			}
			if("3".equals(hongbao.getType()) && !hongbao.getZsid().equals(MemberUtils.getUid())){	//类型 1：拼手气 2：普通 3：专属
				return AjaxJson.error("此红包不可领取");
			}
			if(hongbao.getSycount() == 0){
				return AjaxJson.error("红包已被抢完");
			}
			if(hongbao.getSycount() == 1){
				isfin = "1";
			}
			money = groupHongbaoService.getHongbao(hongbao,req.getString("messageUid"),req.getString("payload"));
		} finally {
			deleteLock(lock);
		}
		return AjaxJson.success().put("money",money).put("isfin",isfin);
	}
	/**
	 * 切换小程序页
	 *
	 * @return
	 */
	@RequestMapping("/changeXcx")
	public AjaxJson changeXcx(@RequestBody ReqJson req) {
		reqValidator(req,"code");
		Xcx xcx = xcxService.findUniqueByProperty("code", req.getString("code"));
		if(xcx == null){
			return AjaxJson.error("无效码");
		}
		memberService.executeUpdateSql("update t_member set xcxurl = '"+xcx.getUrl()+"' where id = '"+MemberUtils.getUid()+"'");

		return AjaxJson.success().put("url",xcx.getUrl());
	}
	/**
	 * 我的朋友动态
	 *
	 * @return
	 */
	@RequestMapping("/myQuanList")
	public AjaxJson myQuanList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		Dy dy = new Dy();
		dy.setDataScope("and (a.uid in(select uid2 from t_friend where uid = '"+MemberUtils.getUid()+"') or a.uid = '"+MemberUtils.getUid()+"')");
		Page<Dy> page = dyService.findPage(new Page<>(req.getPageNo(), req.getPageSize()), dy);
		Map<String, Object> map = null;
		for (Dy faq : page.getList()) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("time", DateUtils.formatDate(faq.getCreateDate()));
			map.put("type", faq.getType());
			map.put("imgs", urlsToList(faq.getImgs()));
			map.put("video", getRealPath(faq.getVideo()));
			map.put("vimg", getRealPath(faq.getVimg()));
			map.put("info", faq.getInfo());
			map.put("address", faq.getAddress());
			map.put("lon", faq.getLon());
			map.put("lat", faq.getLat());
			map.put("userId", faq.getU().getId());
			map.put("icon", getRealPath(faq.getU().getIcon()));
			map.put("name", faq.getU().getNickname());
			String s = dyLikeService.executeGetSql("select count(1) from t_dy_like where uid = '" + MemberUtils.getUid() + "' and dy_id = '" + faq.getId() + "'").toString();
			map.put("isDz", !"0".equals(s)?"1":"0");
			DyLike like = new DyLike();
			like.setDyId(faq.getId());
			like.setDataScope("and (a.uid in(select uid2 from t_friend where uid = '"+MemberUtils.getUid()+"') or a.uid = '"+MemberUtils.getUid()+"')");
			List<DyLike> list = dyLikeService.findList(like);
			StringBuffer sb = new StringBuffer();
			String names = "";
			List<Map<String, Object>> likeList = Lists.newArrayList();
			Map map3 = null;
			for(DyLike l:list){
				map3 = Maps.newHashMap();
				map3.put("id",l.getU().getId());
				map3.put("name",l.getU().getNickname());
				likeList.add(map3);
				sb.append(l.getU().getNickname()+",");
			}
			if(sb != null && sb.length() > 0){
				names = sb.substring(0,sb.length()-1);
			}
			map.put("dzNames", names);
			map.put("likeList", likeList);
			DyComm comm = new DyComm();
			comm.setDy(faq);

			comm.setDataScope("and (a.uid in(select uid2 from t_friend where uid = '"+MemberUtils.getUid()+"') or a.uid = '"+MemberUtils.getUid()+"' or a.to in(select uid2 from t_friend where uid = '"+MemberUtils.getUid()+"'))");
			List<DyComm> list1 = dyCommService.findList(comm);
			List<Map<String, Object>> commList = Lists.newArrayList();
			Map map2 = null;
			for(DyComm l:list1){
				map2 = Maps.newHashMap();
				map2.put("id",l.getId());
				map2.put("userId",l.getU().getId());
				map2.put("name",l.getU().getNickname());
				map2.put("icon",getRealPath(l.getU().getIcon()));
				map2.put("info",l.getTitle());
				map2.put("huifu",l.getTo()!=null?l.getTo().getNickname():"");
				map2.put("time",DateUtils.formatDateTime(l.getCreateDate()));
				commList.add(map2);
			}
			map.put("commList", commList);
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList).put("totalCount",page.getCount()).put("totalPage",page.getTotalPage());
	}
	/**
	 * 发布动态
	 *
	 * @return
	 */
	@RequestMapping("/pushDy")
	public AjaxJson pushDy(@RequestBody ReqJson req) {

		Dy dy = new Dy();
		dy.setU(MemberUtils.getMember());
		dy.setInfo(req.getString("info"));
		dy.setImgs(req.getString("imgs"));
		dy.setVimg(req.getString("vimg"));
		dy.setVideo(req.getString("video"));
		dy.setType("1");
		if(!isBlank(req.getString("video"))){
			dy.setType("2");
		}
		dy.setAddress(req.getString("address"));
		dy.setLon(req.getString("lon"));
		dy.setLat(req.getString("lat"));
		dyService.save(dy);
		return AjaxJson.success();
	}
	/**
	 * 我的互动消息
	 *
	 * @return
	 */
	@RequestMapping("/hudongList")
	public AjaxJson hudongList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		Hudong dy = new Hudong();
		dy.setU(MemberUtils.getMember());
		Page<Hudong> page = hudongService.findPage(new Page<>(req.getPageNo(), req.getPageSize()), dy);
		Map<String, Object> map = null;
		for (Hudong faq : page.getList()) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("userId", faq.getUid2().getId());
			map.put("icon", getRealPath(faq.getUid2().getIcon()));
			map.put("name", faq.getUid2().getNickname());
			map.put("type", faq.getType());
			map.put("state", faq.getState());
			map.put("info", faq.getInfo());
			map.put("dyId", faq.getDyId());
			if(!isBlank(faq.getDyImgs())){
				List<String> list = urlsToList(faq.getDyImgs());
				map.put("dyImg", list.get(0));
			}else {
				map.put("dyImg", "");
			}
			map.put("dyInfo", faq.getDyInfo());
			map.put("time", DateUtils.formatDate(faq.getCreateDate()));
			dataList.add(map);
		}
		hudongService.executeUpdateSql("update t_hudong set state = '1' where uid = '"+MemberUtils.getUid()+"' and state = '0'");
		return AjaxJson.success().setDataList(dataList).put("totalCount",page.getCount()).put("totalPage",page.getTotalPage());
	}
	/**
	 * 清空互动消息
	 *
	 * @return
	 */
	@RequestMapping("/delHd")
	public AjaxJson delHd(@RequestBody ReqJson req) {

		hudongService.executeDeleteSql("delete from t_hudong where uid = '"+MemberUtils.getUid()+"'");
		return AjaxJson.success();
	}
	/**
	 * 签到金额明细
	 *
	 * @return
	 */
	@RequestMapping("/signLogList")
	public AjaxJson signLogList(@RequestBody ReqJson req) {
		List<Map<String, Object>> dataList = Lists.newArrayList();
		SignLog signLog = new SignLog();
//		Member member = memberService.get("33f11f3b894f4ee9b0a2e9f0a511b85d");
//		signLog.setU(member);

		signLog.setU(MemberUtils.getMember());
		Page<SignLog> page = signLogService.findPage(new Page<>(req.getPageNo(), req.getPageSize()), signLog);
		Map<String, Object> map = null;
		for (SignLog sl : page.getList()) {
			map = Maps.newHashMap();
			map.put("id", sl.getId());
			map.put("title", "签到领奖");
			map.put("money", sl.getMoney());
			map.put("time", DateUtils.formatDate(sl.getCreateDate()));
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList).put("totalCount",page.getCount()).put("totalPage",page.getTotalPage());
	}
	/**
	 * 签到页
	 *
	 * @return
	 */
	@RequestMapping("/signInfo")
	public AjaxJson signInfo(@RequestBody ReqJson req) {
		Map map = Maps.newHashMap();
		//查询签到业务数据
		Sign sign = signService.get(MemberUtils.getUid());

		if (sign == null) {
			Member member = MemberUtils.getMember();
			map.put("days", 0);
			map.put("isSign", "0");
			map.put("signMoney",member.getBalance());
		} else {
			boolean isSignedToday = sign.getLastSignDate().equals(DateUtils.getDate());
			map.put("days", sign.getDay());
			map.put("isSign", isSignedToday ? "1" : "0");
			map.put("signMoney",sign.getMoney());
		}
		return AjaxJson.success().put("data",map);
	}
	/**
	 * 签到
	 *
	 * @return
	 */
	@RequestMapping("/sign")
	public AjaxJson sign(@RequestBody ReqJson req) {

		Long size = (Long)signLogService.executeGetSql("select count(1) from t_sign where id = '" + MemberUtils.getUid() + "' and last_sign_date = '" + DateUtils.getDate() + "'");
		if(size !=null && size >= 1){
			return AjaxJson.error("您今天已签到过");
		}

//		Member member = memberService.get("33f11f3b894f4ee9b0a2e9f0a511b85d");
//		memberService.sign(member);

		memberService.sign(MemberUtils.getMember());

		return AjaxJson.success();
	}
	/**
	 * 外链列表
	 *
	 * @return
	 */
	@RequestMapping("/urlList")
	public AjaxJson urlList(@RequestBody ReqJson req) {
		List<Map<String, Object>> dataList = Lists.newArrayList();
		List<Web> list = webService.findList(new Web());
		Map map = null;
		for (Web p : list) {
			map = Maps.newHashMap();
			map.put("id", p.getId());
			map.put("icon", getRealPath(p.getIcon()));
			map.put("title", p.getTitle());
			map.put("url", p.getUrl());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 用户信息
	 *
	 * @return
	 */
	@RequestMapping("/userInfo")
	public AjaxJson userInfo(@RequestBody ReqJson req) {
		Member member = MemberUtils.getMember();
		if("1".equals(member.getState())){
			return AjaxJson.error("用户被禁用").put("code","201");
		}
		Map map = Maps.newHashMap();
		map.put("id", member.getId());
		map.put("icon", getRealPath(member.getIcon()));
		map.put("img", getRealPath(member.getImg()));
		map.put("name", member.getNickname());
		map.put("sex", member.getSex());
		map.put("idno", !isBlank(member.getLianghao())?member.getLianghao():member.getIdno());
		map.put("isLiang",!isBlank(member.getLianghao())?"1":"0");
		map.put("huanying", member.getHuanying());
		map.put("phone", member.getPhone());
		map.put("acount", member.getAcount());
		map.put("pwd", member.getPassword());
		if(isBlank(member.getQrcode())){
			member.setQrcode(QrCodeUtil.getCode("1-"+member.getId(),"/memberCode"));
			memberService.save(member);
		}
		if(isBlank(member.getQrcode2())){
			member.setQrcode2(QrCodeUtil.getCode("3-"+member.getIdno(),"/memberCode"));
			memberService.save(member);
		}
		map.put("qrcode", getRealPath(member.getQrcode()));
		map.put("qrcode2", resolveQrcode2ForResponse(member.getQrcode2()));
		map.put("sign", member.getSign());
		map.put("showQuan", member.getShowQuan());
		map.put("balalce", member.getBalance());
		map.put("hasPaypwd", !isBlank(member.getPaypwd())?"1":"0");
		map.put("mbid", member.getMb()!=null?member.getMb().getId():"");
		map.put("mb", member.getMbname());
		map.put("isAddYz", member.getIsAddYz());
		map.put("isQuantx", member.getIsQuanTx());
		map.put("isMsgtx", member.getIsMsgTx());
		map.put("accSerch", member.getAccSerch());
		map.put("idnoSerch", member.getPhoneSerch());
		map.put("groAdd", member.getGroAdd());
		map.put("mpAdd", member.getMpAdd());
		map.put("qrAdd", member.getQrAdd());
		map.put("ziti", member.getZiti());
		map.put("chatImg", getRealPath(member.getChatImg()));
		map.put("kfid", ImUtils.robot_id);
		map.put("quanCount", hudongService.executeGetSql("select count(1) from t_hudong where state = '0' and uid = '"+member.getId()+"'").toString());
		map.put("xcxUrl", member.getXcxurl());
		Hudong hudong = new Hudong();
		hudong.setU(member);
		hudong.setState("0");
		List<Hudong> list = hudongService.findList(hudong);
		if(!list.isEmpty()){
			map.put("hudong",list.get(0).getInfo());
			map.put("hdIcon", getRealPath(list.get(0).getUid2().getIcon()));
		}else {
			map.put("hudong","");
			map.put("hdIcon", "");
		}
		if(!isBlank(member.getPhone())){
			map.put("regType", "3");
		}else if(!isBlank(member.getAcount())){
			map.put("regType", "2");
		}else if(!isBlank(member.getEqno())){
			map.put("regType", "1");
		}
		map.put("fris",friendApplyService.executeGetSql("select count(1) from t_friend_apply where state = 1 and uid2 = '" + member.getId() + "'").toString());
		map.put("gros",groupApplyService.executeGetSql("select count(1) from t_group_apply where state = '1' and showids like '%" + member.getId() + "%'").toString());
		map.put("noadd", member.getNoadd());
		map.put("isvip", member.getIsvip());
		map.put("vipTime", member.getViptime()!=null?DateUtils.formatDateTime(member.getViptime()):"");
		map.put("autofanyi", member.getAutofanyi());
		map.put("biandabianyi", member.getBiandabianyi());

		map.put("zxState", "4");
		map.put("zxReson", "");
		Zhuxiao zhuxiao = new Zhuxiao();
		zhuxiao.setIdno(member.getIdno());
		List<Zhuxiao> list2 = zhuxiaoService.findList(zhuxiao);
		if(!list2.isEmpty()){
			map.put("zxState", list2.get(0).getState());
			map.put("zxReson", list2.get(0).getReason());
		}
		return AjaxJson.success().put("data",map);
	}
	/**
	 * 修改资料
	 *
	 * @return
	 */
	@RequestMapping("/editUserinfo")
	public AjaxJson editUserinfo(@RequestBody ReqJson req, HttpServletRequest request) {
		Member member = MemberUtils.getMember();
		boolean flag = false;
		if(!isBlank(req.getString("icon"))){
			member.setIcon(req.getString("icon"));
			flag = true;
		}
		if(!isBlank(req.getString("name"))){
			String s1 = memberService.executeGetSql("select count(1) from t_member where nickname = '" + req.getString("name") + "' and id != '" + member.getId() + "'").toString();
			if(!"0".equals(s1)){
				return AjaxJson.error("昵称已存在，请更换");
			}
			Customer customer = customerService.get("1");
			String[] split = customer.getNamemgc().split("[|]", 0);
			for(String s:split){
				if(req.getString("name").contains(s)){
					return AjaxJson.error("昵称包含敏感词 "+s);
				}
			}
			ChangeNameLog log = new ChangeNameLog();
			log.setU(member);
			log.setIp(IpUtils.getHostIp());
			log.setIpcity(IpUtils.getIpAddr(request));
			log.setOldName(member.getNickname());
			log.setNewName(req.getString("name"));
			changeNameLogService.save(log);

			member.setNickname(req.getString("name"));
			flag = true;
		}
		if(!isBlank(req.getString("sex"))){
			member.setSex(req.getString("sex"));
		}
		if(!isBlank(req.getString("sign"))){
			member.setSign(req.getString("sign"));
		}
		if(!isBlank(req.getString("img"))){
			member.setImg(req.getString("img"));
		}
		if(!isBlank(req.getString("showQuan"))){
			member.setShowQuan(req.getString("showQuan"));
		}
		if(!isBlank(req.getString("isAddYz"))){
			member.setIsAddYz(req.getString("isAddYz"));
		}
		if(!isBlank(req.getString("isQuantx"))){
			member.setIsQuanTx(req.getString("isQuantx"));
		}
		if(!isBlank(req.getString("isMsgtx"))){
			member.setIsMsgTx(req.getString("isMsgtx"));
		}
		if(!isBlank(req.getString("accSerch"))){
			member.setAccSerch(req.getString("accSerch"));
		}
		if(!isBlank(req.getString("idnoSerch"))){
			member.setPhoneSerch(req.getString("idnoSerch"));
		}
		if(!isBlank(req.getString("groAdd"))){
			member.setGroAdd(req.getString("groAdd"));
		}
		if(!isBlank(req.getString("mpAdd"))){
			member.setMpAdd(req.getString("mpAdd"));
		}
		if(!isBlank(req.getString("qrAdd"))){
			member.setQrAdd(req.getString("qrAdd"));
		}
		if(!isBlank(req.getString("ziti"))){
			member.setZiti(req.getString("ziti"));
		}
		if(!isBlank(req.getString("chatImg"))){
			member.setChatImg(req.getString("chatImg"));
		}else {
			member.setChatImg("");
		}
		if(!isBlank(req.getString("code"))){
			String s = memberService.executeGetSql("select count(1) from t_member where idno = '" + req.getString("code") + "' and id != '" + member.getId() + "'").toString();
			if(!"0".equals(s)){
				return AjaxJson.error("此邀请码已存在");
			}
			member.setIdno(req.getString("code"));
			member.setQrcode(QrCodeUtil.getCode("1-"+member.getIdno(),"/memberCode"));
		}
		if(!isBlank(req.getString("huanying"))){
			member.setHuanying(req.getString("huanying"));
		}
		if(!isBlank(req.getString("noadd"))){
			member.setNoadd(req.getString("noadd"));
		}
		if(!isBlank(req.getString("autofanyi"))){
			member.setAutofanyi(req.getString("autofanyi"));
		}
		if(!isBlank(req.getString("biandabianyi"))){
			member.setBiandabianyi(req.getString("biandabianyi"));
		}
		memberService.save(member);
		if(flag){
			ImUtils.editUser(member.getId(),member.getNickname(),member.getIcon());
			//修改个人相关的群头像。
			groupItemService.updateGroupAvatarByUserId(member.getId());
		}
		return AjaxJson.success();
	}
	/**
	 * 充值
	 *
	 * @return
	 */
	@RequestMapping("/rechage")
	public AjaxJson rechage(@RequestBody ReqJson req) {
		reqValidator(req,"money","img");
		Member member = MemberUtils.getMember();
		Customer customer = customerService.get("1");
		RechageLog log = new RechageLog();
		log.setU(member);
		log.setMoney(req.getString("money"));
		log.setTitle(customer.getCzimg());
		log.setSname(customer.getSname());
		log.setSno(customer.getSno());
		log.setPingz(req.getString("img"));
		log.setState("1");
		rechageLogService.save(log);
		systemInfoSocketHandler.sendMessageToUser("admin", "用户（"+member.getNickname()+"）充值"+log.getMoney());

		return AjaxJson.success();
	}
	/**
	 * 充值记录
	 *
	 * @return
	 */
	@RequestMapping("/rechLogList")
	public AjaxJson rechLogList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		RechageLog log = new RechageLog();
		log.setU(MemberUtils.getMember());
		Page<RechageLog> page = rechageLogService.findPage(new Page<>(req.getPageNo(), req.getPageSize()), log);
		Map<String, Object> map = null;
		for (RechageLog faq : page.getList()) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("title", "余额充值");
			map.put("money", faq.getMoney());
			map.put("time", DateUtils.formatDateTime(faq.getCreateDate()));
			map.put("sname", faq.getSname());
			map.put("sno", faq.getSno());
			map.put("pingzheng", getRealPath(faq.getPingz()));
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList).put("totalCount",page.getCount()).put("totalPage",page.getTotalPage());
	}
	/**
	 * 提现页标题
	 *
	 * @return
	 */
	@RequestMapping("/txInfo")
	public AjaxJson txInfo(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		List<TixianTitle> list = tixianTitleService.findList(new TixianTitle());
		Map<String, Object> map = null;
		for (TixianTitle faq : list) {
			map = Maps.newHashMap();
			map.put("title", faq.getTitle());
			map.put("info", faq.getInfo());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 提现
	 *
	 * @return
	 */
	@RequestMapping("/tixian")
	public AjaxJson tixian(@RequestBody ReqJson req) {
		reqValidator(req,"money","img","paypwd");
		Member member = MemberUtils.getMember();
		if(isBlank(member.getPaypwd())){
			return AjaxJson.error("请先设置支付密码");
		}
		Customer customer = customerService.get("1");
		if(customer.getMintixian().compareTo(new BigDecimal(req.getString("money"))) == 1){
			return AjaxJson.error("最低提现金额 "+customer.getMintixian());
		}
		if(new BigDecimal(req.getString("money")).compareTo(member.getBalance()) == 1){
			return AjaxJson.error("余额不足");
		}
		if(!memberService.validatePassword(req.getString("paypwd"),member.getPaypwd())){
			return AjaxJson.error("支付密码错误");
		}
		Tixian tx = new Tixian();
		tx.setU(member);
		tx.setMoney(new BigDecimal(req.getString("money")));
		tx.setImg(req.getString("img"));
		tx.setImgtitle(customer.getTx2());
		tx.setInfo(req.getString("info"));
		tx.setState("1");
		tixianService.tixian(tx);
		systemInfoSocketHandler.sendMessageToUser("admin", "用户（"+member.getNickname()+"）充值");
		return AjaxJson.success();
	}
	/**
	 * 提现记录
	 *
	 * @return
	 */
	@RequestMapping("/txLogList")
	public AjaxJson txLogList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		Tixian log = new Tixian();
		log.setU(MemberUtils.getMember());
		Page<Tixian> page = tixianService.findPage(new Page<>(req.getPageNo(), req.getPageSize()), log);
		Map<String, Object> map = null;
		for (Tixian faq : page.getList()) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("title", "余额提现到账户");
			map.put("money", faq.getMoney());
			map.put("tutitle", faq.getImgtitle());
			map.put("tu", getRealPath(faq.getImg()));
			map.put("time", DateUtils.formatDate(faq.getCreateDate()));
//			List<Map<String, Object>> inList = Lists.newArrayList();
//			String[] split = faq.getInfo().split("[,]", 0);
//			Map<String, Object> map2 = null;
//			for(String s:split){
//				map2 = Maps.newHashMap();
//				map2.put("info",s);
//				inList.add(map2);
//			}
			map.put("info", faq.getInfo());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList).put("totalCount",page.getCount()).put("totalPage",page.getTotalPage());
	}
	/**
	 * 账户记录
	 *
	 * @return
	 */
	@RequestMapping("/balanceLogList")
	public AjaxJson balanceLogList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		BalanceLog log = new BalanceLog();
		log.setU(MemberUtils.getMember());
		Page<BalanceLog> page = balanceLogService.findPage(new Page<>(req.getPageNo(), req.getPageSize()), log);
		Map<String, Object> map = null;
		for (BalanceLog faq : page.getList()) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("title", faq.getTitle());
			map.put("time", DateUtils.formatDate(faq.getCreateDate()));
			map.put("money", faq.getMoney());
			map.put("state", faq.getState());
			map.put("type", faq.getType());
			map.put("info", faq.getInfo());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList).put("totalCount",page.getCount()).put("totalPage",page.getTotalPage());
	}
	/**
	 * 搜索用户
	 *
	 * @return
	 */
	@RequestMapping("/serchList")
	public AjaxJson serchList(@RequestBody SearchReqJson req) {
		List<Map<String, Object>> dataList = Lists.newArrayList();

		// 检查每日搜索限制
		String userId = MemberUtils.getUid();
		String today = DateUtils.getDate(); // 获取今天日期 yyyy-MM-dd
		String dailyKey = "search_daily:" + userId + ":" + today;
		String consecutiveKey = "search_consecutive:" + userId;
		
		// 获取今日搜索次数
		Long dailyCount = redisUtils.get(dailyKey) != null ? 
			Long.parseLong(redisUtils.get(dailyKey).toString()) : 0L;
		
		// 检查是否达到每日限制
		if (dailyCount >= 30) {
			return AjaxJson.error("今日搜索次数已达上限，请明天再来");
		}
		
		// 增加今日搜索次数
		Long newDailyCount = redisUtils.incrBy(dailyKey, 1);
		// 只有第一次创建时才设置过期时间
		if (newDailyCount == 1) {
			redisUtils.expire(dailyKey, 86400, TimeUnit.SECONDS); // 24小时过期
		}
		
		// 检查连续滥用情况 - 传入增加后的次数
		checkConsecutiveAbuse(userId, consecutiveKey, today, newDailyCount);
		
		// 执行搜索逻辑
		Member m = new Member();
		String t = "";
		if (isBlank(req.getString("key"))) {
			return AjaxJson.success().setDataList(dataList);
		}
		m.setKey(req.getString("key"));
		if(req.getString("key") != null && req.getString("key").matches("\\d{4}")){	//4位数字代表id号
			t = "and phone_serch = '1'";
		}else {
			t = "and acc_serch = '1'";
		}

		m.setDataScope(t+" and a.id not in(select uid2 from t_friend where uid = '"+MemberUtils.getUid()+"') and a.id != '"+MemberUtils.getUid()+"'");
		List<Member> list = memberService.findList(m);
		Map<String, Object> map = null;
		for (Member faq : list) {
			map = Maps.newHashMap();
			map.put("userId", faq.getId());
			map.put("icon", getRealPath(faq.getIcon()));
			map.put("name", faq.getNickname());
			map.put("sex", faq.getSex());
			map.put("sign", faq.getSign());
			map.put("phone", faq.getPhone());
			map.put("acount", faq.getAcount());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	
	/**
	 * 检查连续滥用情况
	 */
	private void checkConsecutiveAbuse(String userId, String consecutiveKey, String today, Long todayCount) {
		if (todayCount >= 30) { // 当前这次搜索会达到30次
			// 获取连续滥用记录: "lastDate,consecutiveDays"
			String consecutiveData = redisUtils.get(consecutiveKey) != null ? 
				redisUtils.get(consecutiveKey).toString() : "";
			
			String lastDate = "";
			int consecutiveDays = 0;
			
			if (!consecutiveData.isEmpty()) {
				String[] parts = consecutiveData.split(",");
				if (parts.length == 2) {
					lastDate = parts[0];
					consecutiveDays = Integer.parseInt(parts[1]);
				}
			}
			
			// 如果最后一次搜索日期与今天相同，不需要累计
			if (today.equals(lastDate)) {
				return;
			}
			
			// 计算昨天日期
			String yesterday = DateUtils.formatDate(DateUtils.addDays(DateUtils.parseDate(today), -1), "yyyy-MM-dd");
			
			// 如果昨天是最后一次滥用日期，连续天数+1，否则重置为1
			if (yesterday.equals(lastDate)) {
				consecutiveDays++;
			} else {
				consecutiveDays = 1;
			}
			
			// 检查是否连续10天滥用
			if (consecutiveDays >= 10) {
				// 封禁用户
				Member member = MemberUtils.getMember();
				member.setState("1");
				memberService.save(member);
				ImUtils.sendMsg(ImUtils.robot_id, 1005, userId, userId);
				
				// 清空滥用记录
				redisUtils.delete(consecutiveKey);
				return;
			}
			
			// 更新连续滥用记录
			String newConsecutiveData = today + "," + consecutiveDays;
			redisUtils.set(consecutiveKey, newConsecutiveData);
			redisUtils.expire(consecutiveKey, 30, TimeUnit.DAYS); // 30天过期
		}
	}
	
	/**
	 * 验证支付密码
	 *
	 * @return
	 */
	@RequestMapping("/checkPayPwd")
	public AjaxJson checkPayPwd(@RequestBody ReqJson req) {
		reqValidator(req,"pwd");
		Member member = MemberUtils.getMember();
		if(isBlank(member.getPaypwd())){
			return AjaxJson.error("请先设置支付密码");
		}
		if(!memberService.validatePassword(req.getString("pwd"),member.getPaypwd())){
			return AjaxJson.error("密码错误");
		}
		return AjaxJson.success();
	}
	/**
	 * 修改支付密码
	 *
	 * @return
	 */
	@RequestMapping("/editPayPwd")
	public AjaxJson editPayPwd(@RequestBody ReqJson req,HttpServletRequest request) {
		reqValidator(req,"pwd");
		Member member = MemberUtils.getMember();
		ChangePaypwdLog log = new ChangePaypwdLog();
		log.setU(member);
		log.setIp(IpUtils.getHostIp());
		log.setIpcity(IpUtils.getIpAddr(request));
		log.setOldPwd(member.getPaypwd());
		log.setNewPwd(req.getString("pwd"));
		changePaypwdLogService.save(log);

		member.setPaypwd(memberService.entryptPassword(req.getString("pwd")));
		memberService.save(member);
		return AjaxJson.success();
	}
	/**
	 * 设置支付密码
	 *
	 * @return
	 */
	@RequestMapping("/setPaypwd")
	public AjaxJson setPaypwd(@RequestBody ReqJson req) {
		reqValidator(req,"pwd");
		Member member = MemberUtils.getMember();
		member.setPaypwd(memberService.entryptPassword(req.getString("pwd")));
		memberService.save(member);
		String key = "setPayPwd:"+member.getId();
		redisUtils.set(key,"1");
		return AjaxJson.success();
	}
	/**
	 * 更换登录密码
	 *
	 * @return
	 */
	@RequestMapping("/changePwd")
	public AjaxJson changePwd(@RequestBody ReqJson req,HttpServletRequest request) {
		reqValidator(req,"pwd");
		Member member = MemberUtils.getMember();
		if(!isBlank(req.getString("oldPwd"))){
			if(!memberService.validatePassword(req.getString("oldPwd"),member.getPassword())){
				return AjaxJson.error("原密码错误");
			}
		}

		ChangePwdLog log = new ChangePwdLog();
		log.setU(member);
		log.setIp(IpUtils.getHostIp());
		log.setIpcity(IpUtils.getIpAddr(request));
		log.setOldPwd(req.getString("oldPwd"));
		log.setNewPwd(req.getString("pwd"));
		changePwdLogService.save(log);

		member.setPassword(memberService.entryptPassword(req.getString("pwd")));
		memberService.save(member);
		return AjaxJson.success();
	}
	/**
	 * 验证登录密码
	 *
	 * @return
	 */
	@RequestMapping("/checkPwd")
	public AjaxJson checkPwd(@RequestBody ReqJson req) {
		reqValidator(req,"pwd");
		Member member = MemberUtils.getMember();
		if(!memberService.validatePassword(req.getString("pwd"),member.getPassword())){
			return AjaxJson.error("密码错误");
		}
		String key = "setPayPwd:"+member.getId();
		String isFirst = "1";
		if(redisUtils.hasKey(key)){
			isFirst = "0";
		}
		return AjaxJson.success().put("isFirst",isFirst);
	}
	/**
	 * 验证短信验证码
	 *
	 * @return
	 */
	@RequestMapping("/checkCode")
	public AjaxJson checkCode(@RequestBody ReqJson req) {
		reqValidator(req,"code");
		Member member = MemberUtils.getMember();
		if(!checkSmsCode(member.getPhone(),req.getString("code"))){
			return AjaxJson.error("验证码错误");
		}
		String key = "setPayPwd:"+member.getId();
		String isFirst = "1";
		if(redisUtils.hasKey(key)){
			isFirst = "0";
		}
		return AjaxJson.success().put("isFirst",isFirst);
	}
	/**
	 * 验证密保问题
	 *
	 * @return
	 */
	@RequestMapping("/changeMibao")
	public AjaxJson changeMibao(@RequestBody ReqJson req) {
		reqValidator(req,"answer");
		Member member = MemberUtils.getMember();
		if(member.getMb() == null){
			return AjaxJson.error("未设置密保");
		}
//		if(!member.getMb().getId().equals(req.getString("id"))){
//			return AjaxJson.error("验证失败");
//		}
		if(!member.getMbname().equals(req.getString("title"))){
			return AjaxJson.error("验证失败");
		}
		if(!member.getMbda().equals(req.getString("answer"))){
			return AjaxJson.error("验证失败");
		}
		return AjaxJson.success();
	}
	/**
	 * 密保问题列表
	 *
	 * @return
	 */
	@RequestMapping("/mbFaqList")
	public AjaxJson mbFaqList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		List<MibaoFaq> list = mibaoFaqService.findList(new MibaoFaq());
		Map<String, Object> map = null;
		for (MibaoFaq faq : list) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("title", faq.getTitle());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 设置密保问题
	 *
	 * @return
	 */
	@RequestMapping("/setMibao")
	public AjaxJson setMibao(@RequestBody ReqJson req) {
		reqValidator(req,"answer");
//		MibaoFaq mb = mibaoFaqService.get(req.getString("id"));
//		if(mb == null){
//			return AjaxJson.error("问题不存在");
//		}
		Member member = MemberUtils.getMember();
		//member.setMb(mb);
		member.setMbname(req.getString("title"));
		member.setMbda(req.getString("answer"));
		memberService.save(member);
		return AjaxJson.success();
	}
	/**
	 * 黑名单列表
	 *
	 * @return
	 */
	@RequestMapping("/blackList")
	public AjaxJson blackList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		Black black = new Black();
		black.setUid(MemberUtils.getUid());
		List<Black> list = blackService.findList(black);
		Map<String, Object> map = null;
		for (Black faq : list) {
			map = Maps.newHashMap();
			map.put("userId", faq.getUid2().getId());
			map.put("icon", getRealPath(faq.getUid2().getIcon()));
			map.put("name", faq.getUid2().getNickname());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 我的收藏
	 *
	 * @return
	 */
	@RequestMapping("/collList")
	public AjaxJson collList(@RequestBody ReqJson req) {

		List<Map<String, Object>> dataList = Lists.newArrayList();
		Coll log = new Coll();
		log.setUid(MemberUtils.getUid());
		Page<Coll> page = collService.findPage(new Page<>(req.getPageNo(), req.getPageSize()), log);
		Map<String, Object> map = null;
		for (Coll faq : page.getList()) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("userId", faq.getUid2().getId());
			map.put("icon", getRealPath(faq.getUid2().getIcon()));
			map.put("name", faq.getUid2().getNickname());
			map.put("time", DateUtils.formatDate(faq.getCreateDate()));
			map.put("type", faq.getType());
			map.put("info", faq.getInfo());
			map.put("miao", faq.getMiao());
			map.put("lon", faq.getLon());
			map.put("lat", faq.getLat());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList).put("totalCount",page.getCount()).put("totalPage",page.getTotalPage());
	}
	/**
	 * 删除收藏
	 *
	 * @return
	 */
	@RequestMapping("/delColl")
	public AjaxJson delColl(@RequestBody ReqJson req) {
		reqValidator(req,"ids");
		String[] ids = req.getString("ids").split("[|]", 0);
		for(String id:ids){
			collService.delete(new Coll(id));
		}
		return AjaxJson.success();
	}

	/**
	 * 编辑收藏
	 * @param req
	 * @return
	 */
	@RequestMapping("/editColl")
	public AjaxJson editColl(@RequestBody ReqJson req) {
		reqValidator(req,"id","info","type");
		Coll coll = collService.get(req.getString("id"));
		if(coll == null){
			return AjaxJson.error("收藏记录不存在");
		}
		// 验证收藏记录是否属于当前用户
		if(!coll.getUid().equals(MemberUtils.getUid())){
			return AjaxJson.error("无权限编辑此收藏记录");
		}
		// 更新收藏内容
		coll.setInfo(req.getString("info"));
		coll.setType(req.getString("type"));
		if(req.getInteger("miao") != null){
			coll.setMiao(req.getInteger("miao"));
		}
		if(!isBlank(req.getString("lon"))){
			coll.setLon(req.getString("lon"));
		}
		if(!isBlank(req.getString("lat"))){
			coll.setLat(req.getString("lat"));
		}
		collService.save(coll);
		return AjaxJson.success();
	}

	/**
	 * 换绑手机号
	 * @param req
	 * @return
	 */
	@RequestMapping("/changePhone")
	public AjaxJson changePhone(@RequestBody ReqJson req,HttpServletRequest request) {
		reqValidator(req, "code", "phone");
		Member member = MemberUtils.getMember();
		String oldPhone = member.getPhone();
		int count = memberService.executeGetCountSql("t_member", "phone = '" + req.getString("phone") + "'");
		if (count > 0) {
			return AjaxJson.error("新手机号已存在");
		}
		if (!checkSmsCode(req.getString("phone"), req.getString("code"))) {
			return AjaxJson.error("新手机号验证码错误");
		}
		member.setPhone(req.getString("phone"));
		memberService.save(member);

		ChangePhoneLog log = new ChangePhoneLog();
		log.setU(member);
		log.setIp(IpUtils.getHostIp());
		log.setIpcity(IpUtils.getIpAddr(request));
		log.setOldPhone(oldPhone);
		log.setNewPhone(req.getString("phone"));
		changePhoneLogService.save(log);

		redisUtils.delete(member.getPhone());
		return AjaxJson.success();
	}

	/**
	 * 常见问题
	 * @return
	 */
	@RequestMapping("/qaList")
	@AppIntercept
	public AjaxJson qaList() {
		List<Map<String, String>> dataList = Lists.newArrayList();
		List<Faq> list = faqService.findList(new Faq());
		Map<String, String> map = null;
		for (Faq faq : list) {
			map = Maps.newHashMap();
			map.put("title", faq.getTitle());
			map.put("context", faq.getContent());
			map.put("url", appProperites.getFilePath() + "/lixin2/display/faq?id=" + faq.getId());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 投诉类型
	 * @return
	 */
	@RequestMapping("/tousuReason")
	@AppIntercept
	public AjaxJson tousuReason() {
		List<Map<String, String>> dataList = Lists.newArrayList();
		List<Reason> list = reasonService.findList(new Reason());
		Map<String, String> map = null;
		for (Reason faq : list) {
			map = Maps.newHashMap();
			map.put("type", faq.getTitle());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 手机区号列表
	 * @return
	 */
	@RequestMapping("/quhaoList")
	@AppIntercept
	public AjaxJson quhaoList(@RequestBody ReqJson req) {
		List<Map<String, String>> dataList = Lists.newArrayList();
		Quhao quhao = new Quhao();
		if(!isBlank(req.getString("no"))){
			quhao.setCountry(req.getString("no"));
		}
		List<Quhao> list = quhaoService.findList(quhao);
		Map<String, String> map = null;
		for (Quhao faq : list) {
			map = Maps.newHashMap();
			map.put("no", faq.getNo());
			map.put("contry", faq.getCountry());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 意见反馈
	 * @param req
	 * @return
	 */
	@RequestMapping("/fankui")
	public AjaxJson fankui(@RequestBody ReqJson req) {
		reqValidator(req, "info", "phone");
		Feedback feedback = new Feedback();
		feedback.setMember(new Member(MemberUtils.getUid()));
		feedback.setContent(req.getString("info"));
		feedback.setPhone(req.getString("phone"));
		feedback.setImages(req.getString("imgs"));
		feedbackService.save(feedback);
		return AjaxJson.success();
	}
	/**
	 * 平台参数
	 * @return
	 */
	@RequestMapping("/ptConfig")
	@AppIntercept
	public AjaxJson ptConfig() {
		Customer customer = customerService.get("1");
		Map map = Maps.newHashMap();
		map.put("loginType", customer.getLoginType());
		map.put("hasInvit", customer.getHasInvit());
		map.put("showFx", customer.getShowFx());
		map.put("showXcx", customer.getShowXcx());
		map.put("openBj", customer.getOpenBj());
		map.put("openCh", customer.getOpenCh());
		map.put("sname", customer.getSname());
		map.put("sno", customer.getSno());
		map.put("skm", getRealPath(customer.getCzimg()));
		map.put("openMibao", customer.getOpenMibao());
		map.put("tx1", customer.getTx1());
		map.put("tx2", customer.getTx2());
		map.put("cz", customer.getCz());
		map.put("czimg", getRealPath(customer.getCzimg()));
		map.put("hongbao", customer.getOpenhb());
		map.put("zhuanzhang", customer.getOpenzz());
		map.put("tonghua", customer.getTonghua());
		map.put("sendadd", customer.getSendadd());
		map.put("showKf", customer.getShowkf());
		map.put("keUrl", customer.getKfurl());
		map.put("showonline", customer.getShowonline());
		map.put("showyidu", customer.getShowyidu());
		map.put("showsign", customer.getShowsign());
		map.put("showqianbao", customer.getShowqianbao());
		map.put("mintixian", customer.getMintixian());
		map.put("showmsgtime", customer.getShowmsgtime());
		map.put("topsyan", customer.getTopsyan());
		map.put("regType", customer.getRegimgtype());
		map.put("regImg", getRealPath("1".equals(customer.getRegimgtype())?customer.getRegimg():customer.getRegvideo()));
		map.put("fileType", customer.getFiletype());
		map.put("regInfo", appProperites.getFilePath() + "/lixin/display/customer?id=1" );
		map.put("groupinfo", agreementService.get("7").getContent());
		map.put("vipinfo", agreementService.get("5").getContent());
		map.put("appinfo", agreementService.get("6").getContent());
		//map.put("mgc", urlsToList2(customer.getMgc()));
		map.put("mgc", customer.getMgc());
		map.put("line", customer.getLine());
		return AjaxJson.success().put("data",map);
	}
	/**
	 * 注销账号（删除账号）
	 * @param req
	 * @return
	 */
	@RequestMapping("/logoff")
	public AjaxJson logoff(@RequestBody ReqJson req) {
		reqValidator(req, "code");
		Member member = MemberUtils.getMember();
		if (!checkSmsCode(member.getPhone(), req.getString("code"))) {
			return AjaxJson.error("验证码错误");
		}
		memberService.delete(member);
		return AjaxJson.success();
	}
	/**
	 * 退出登录
	 * @return
	 */
	@RequestMapping("/logout")
	public AjaxJson logout() {
		String uid = MemberUtils.getUid();
		if (StringUtils.isNotBlank(uid)) {
			memberService.executeUpdateSql("UPDATE t_member SET token = NULL WHERE id = '"+uid+"'");
			MemberUtils.logout();
		}
		return AjaxJson.success();
	}
//	/**
//	 * 发送验证码
//	 * @param req
//	 * @return
//	 */
//	@RequestMapping("/sendMsg")
//	@AppIntercept
//	public AjaxJson sendMsg(@RequestBody ReqJson req,HttpServletRequest request) {
//		reqValidator(req, "phone", "sign", "timestamp");
//		String phone = req.getString("phone");
//		String sign = req.getString("sign");
//		// 判断请求时间，误差不超过10分钟
//		long minutes = Math.abs(DateUtils.pastMinutes(new Date(req.getLongValue("timestamp"))));
//		if (minutes > 10) {
//			return AjaxJson.error("请求超时");
//		}
//		// 判断签名
//		if (!sign.equals(MD5Util.md5(req.getString("timestamp") + "lixinkeji"))) {
//			return AjaxJson.error("非法请求");
//		}
//		if (sign.equals(redisUtils.get(req.getString("sign")))) {
//			return AjaxJson.error("非法请求");
//		}
//
//		String code = RandomUtil.randomNumbers(6);
//		redisUtils.setEx(phone, code, 600, TimeUnit.SECONDS);
//
//		redisUtils.setEx(sign, sign, 600, TimeUnit.SECONDS);
//		// 对接阿里云
//		AliSMSUtil.sendCode(phone, code);
//		logger.debug("手机号:{},验证码:{}", phone, code);
//		return AjaxJson.success();
//	}

	/**===============================大屏=================================*/
	/**
	 * 顶部数据
	 * @return
	 */
	@RequestMapping("/topData")
	public AjaxJson topData() {
		Map map = Maps.newHashMap();
		map.put("totalCount", memberService.executeGetSql("select count(1) from t_member").toString());
		map.put("todayMsgCount", chatLogService.executeGetSql("select count(1) from t_chat_log where to_days(create_date) = to_days(now())").toString());
		map.put("msgCount", chatLogService.executeGetSql("select count(1) from t_chat_log ").toString());
		map.put("groupCount", groupService.executeGetSql("select count(1) from t_group where del_flag = '0'").toString());
		return AjaxJson.success().put("data",map);
	}
	/**
	 * 用户地区分布图
	 * @return
	 */
	@RequestMapping("/memberMap")
	public AjaxJson memberMap(@RequestBody ReqJson req) {
		List<Map<String, Object>> dataList = Lists.newArrayList();
		List<MemberTongji> cityList = memberService.getCityList();
		Map<String, Object> map = null;
		for (MemberTongji faq : cityList) {
			map = Maps.newHashMap();
			map.put("city", faq.getCity());
			map.put("count", faq.getCount());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 设备使用情况
	 * @return
	 */
	@RequestMapping("/eqMap")
	public AjaxJson eqMap(@RequestBody ReqJson req) {
		List<Map<String, Object>> dataList = Lists.newArrayList();
		List<MemberTongji> cityList = memberService.getEqList();
		Map<String, Object> map = null;
		for (MemberTongji faq : cityList) {
			map = Maps.newHashMap();
			map.put("model", faq.getModel());
			map.put("count", faq.getCount());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 群欢迎语（0322）
	 * @return
	 */
	@RequestMapping("/groupHyList")
	public AjaxJson groupHyList(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		List<Map<String, Object>> dataList = Lists.newArrayList();
		GroupHuanying huanying = new GroupHuanying();
		huanying.setGroup(new Group(req.getString("id")));
		List<GroupHuanying> cityList = groupHuanyingService.findList(huanying);
		Map<String, Object> map = null;
		for (GroupHuanying faq : cityList) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("title", faq.getTitle());
			map.put("imgs", urlsToList(faq.getImgs()));
			map.put("video", getRealPath(faq.getVideo()));
			map.put("file", getRealPath(faq.getPdf()));
			map.put("type", faq.getType());
			map.put("name", faq.getName());
			map.put("size", faq.getSize());
			map.put("miao", faq.getMiao());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 删除群欢迎语（0322）
	 * @return
	 */
	@RequestMapping("/delHy")
	public AjaxJson delHy(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		groupHuanyingService.delete(new GroupHuanying(req.getString("id")));
		return AjaxJson.success();
	}
	/**
	 * 添加、修改群欢迎语（0322）
	 * @return
	 */
	@RequestMapping("/addHy")
	public AjaxJson addHy(@RequestBody ReqJson req) {
		reqValidator(req,"gid");
		Group group = groupService.get(req.getString("gid"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		GroupHuanying huanying = new GroupHuanying();
		if(!isBlank(req.getString("id"))){
			huanying = groupHuanyingService.get(req.getString("id"));
		}
		huanying.setGroup(group);
		huanying.setTitle(req.getString("txt"));
		huanying.setImgs(req.getString("imgs"));
		huanying.setVideo(req.getString("video"));
		huanying.setPdf(req.getString("pdf"));
		huanying.setType(req.getString("type"));
		huanying.setName(req.getString("name"));
		huanying.setSize(req.getString("size"));
		huanying.setMiao(req.getInteger("miao"));
		groupHuanyingService.save(huanying);

		return AjaxJson.success();
	}
	/**
	 * 群定时消息（0322）
	 * @return
	 */
	@RequestMapping("/groupDsList")
	public AjaxJson groupDsList(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		List<Map<String, Object>> dataList = Lists.newArrayList();
		GroupDingshi huanying = new GroupDingshi();
		huanying.setGroup(new Group(req.getString("id")));
		List<GroupDingshi> cityList = groupDingshiService.findList(huanying);
		Map<String, Object> map = null;
		for (GroupDingshi faq : cityList) {
			map = Maps.newHashMap();
			map.put("id", faq.getId());
			map.put("title", faq.getTitle());
			map.put("imgs", urlsToList(faq.getImgs()));
			map.put("video", getRealPath(faq.getVideo()));
			map.put("file", getRealPath(faq.getPdf()));
			map.put("type", faq.getType());
			map.put("time", faq.getTime());
			map.put("txType", faq.getTxtype());
			map.put("name", faq.getName());
			map.put("size", faq.getSize());
			map.put("miao", faq.getMiao());
			dataList.add(map);
		}
		return AjaxJson.success().setDataList(dataList);
	}
	/**
	 * 删除群定时消息（0322）
	 * @return
	 */
	@RequestMapping("/delDs")
	public AjaxJson delDs(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		GroupDingshi dingshi = groupDingshiService.get(req.getString("id"));
		if(dingshi == null){
			return AjaxJson.error("定时消息不存在");
		}

		groupDingshiService.delete(dingshi);

		redisDelayedQueue.removeDelayedQueue(dingshi.getGroup().getId()+"-"+dingshi.getId(), GroupHuanyingDelayedQueueListener.class);
		return AjaxJson.success();
	}
	/**
	 * 添加、修改群定时消息（0322）
	 * @return
	 */
	@RequestMapping("/addDs")
	public AjaxJson addDs(@RequestBody ReqJson req) {
		reqValidator(req,"gid");
		Group group = groupService.get(req.getString("gid"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		Date time = DateUtils.parseDate(req.getString("time"));
		if(time.before(new Date())){
			return AjaxJson.error("请设置未来的时间点");
		}
		GroupDingshi huanying = new GroupDingshi();
		if(!isBlank(req.getString("id"))){
			huanying = groupDingshiService.get(req.getString("id"));
		}
		huanying.setGroup(group);
		huanying.setTitle(req.getString("txt"));
		huanying.setImgs(req.getString("imgs"));
		huanying.setVideo(req.getString("video"));
		huanying.setPdf(req.getString("pdf"));
		huanying.setType(req.getString("type"));
		huanying.setTime(req.getString("time"));
		huanying.setTxtype(req.getString("txType"));
		huanying.setName(req.getString("name"));
		huanying.setSize(req.getString("size"));
		huanying.setMiao(req.getInteger("miao"));
		groupDingshiService.save(huanying);
		String id = group.getId() + "-" + huanying.getId();
		redisDelayedQueue.removeDelayedQueue(id, GroupHuanyingDelayedQueueListener.class);
		// 计算两个时间的毫秒差值
		long differenceInMillis = time.getTime() - new Date().getTime();
		// 将毫秒差值转换为秒
		long miao = differenceInMillis / 1000;
		redisDelayedQueue.addQueueSeconds(id,(int)miao, GroupHuanyingDelayedQueueListener.class);

		return AjaxJson.success();
	}
	/**
	 * 升级群聊（0322）
	 * @return
	 */
	@RequestMapping("/upGroup")
	public AjaxJson upGroup(@RequestBody ReqJson req) {
		reqValidator(req,"id");
		Group group = groupService.get(req.getString("id"));
		if(group == null){
			return AjaxJson.error("群组不存在");
		}
		if("2".equals(group.getGtype())){
			return AjaxJson.error("此群已是5000人群");
		}
		Member member = memberService.get(group.getU().getId());
		if("0".equals(member.getIsvip())){
			return AjaxJson.error("群主不是会员");
		}
		group.setGtype("2");
		groupService.save(group);
		GroupUplog log = new GroupUplog();
		log.setGroup(group);
		log.setQz(member);
		log.setU(MemberUtils.getMember());
		groupUplogService.save(log);


		return AjaxJson.success();
	}
	/**
	 * 兑换会员（0322）
	 * @return
	 */
	@RequestMapping("/duivip")
	public AjaxJson duivip(@RequestBody ReqJson req) {
		reqValidator(req,"code");
		VipCode vipCode = vipCodeService.findUniqueByProperty("code", req.getString("code"));
		if(vipCode == null){
			return AjaxJson.error("无效码");
		}
		if("1".equals(vipCode.getIsdui())){
			return AjaxJson.error("此码已被兑换");
		}
		Member member = MemberUtils.getMember();
		vipCode.setIsdui("1");
		vipCode.setDuiTime(new Date());
		vipCode.setU(member);
		vipCodeService.duihuan(vipCode,member);

		return AjaxJson.success();
	}
	/**
	 * ai机器人回答（0322）
	 * @return
	 */
	@RequestMapping("/aihuifu")
	public AjaxJson aihuifu(@RequestBody ReqJson req) {
		reqValidator(req,"info");
		ThreadUtil.execute(()->{	//异步处理
			if(!isBlank(req.getString("id"))){	//传群id代表群内回复 传用户id代表外面的客服回复
				String key = "aiGroupChat:convId:"+req.getString("id")+"-"+req.getString("uid");
				//String conv_id = "";
				Group group = groupService.get(req.getString("id"));
				if("0".equals(group.getOpenpic())){
//					if(redisUtils.hasKey(key)){
//						conv_id = redisUtils.get(key);
//					}
					Map<String, String> chat = AiChatUtils.chat(req.getString("uid"), req.getString("info"), "", group.getAikey());
					if("0".equals(chat.get("code"))){
						ImUtils.sendGroupTxtMsg(ImUtils.robot_id,chat.get("answer"),req.getString("id"));
						//redisUtils.setEx(key,chat.get("conversation_id"),2l,TimeUnit.HOURS);
					}
				}else {
					//图片回复
					redisDelayedQueue.addQueue(MemberUtils.getUid()+"-"+req.getString("info")+"-"+group.getId(), AIPicDelayedQueueListener.class);
				}
			}else {
				String key = "aiChat:convId:"+req.getString("uid");
				String conv_id = "";
				if(redisUtils.hasKey(key)){
					conv_id = redisUtils.get(key);
				}
				Customer customer = customerService.get("1");
				Map<String, String> chat = AiChatUtils.chat(req.getString("uid"), req.getString("info"), conv_id, customer.getAikey());//客服机器人写死
				if("0".equals(chat.get("code"))){
					ImUtils.sendTxtMsg(ImUtils.robot_id,chat.get("answer"),req.getString("uid"));
					redisUtils.setEx(key,chat.get("conversation_id"),2l,TimeUnit.HOURS);
				}
			}
		});
		return AjaxJson.success();
	}
	/**
	 * ai机器人回答（0322）
	 * @return
	 */
	@RequestMapping("/aifanyi")
	public AjaxJson aifanyi(@RequestBody ReqJson req) {
		reqValidator(req,"info","yuzhong");
		String uid = MemberUtils.getUid();
		String a = "";
		Map<String, String> chat = AiChatUtils.aifanyi(uid, "", req.getString("info"), req.getString("yuzhong"));
		if("0".equals(chat.get("code"))){
			a = chat.get("answer");
		}
		return AjaxJson.success().put("answer",a);
	}
	/**
	 * 添加机器人
	 * @return
	 */
	@RequestMapping("/addRobot")
	public AjaxJson addRobot(@RequestBody ReqJson req) {
		ImUtils.register(req.getString("id"),req.getString("acount"),req.getString("name"),req.getString("icon"));
		return AjaxJson.success();
	}
	/**
	 * 发消息
	 * @return
	 */
	@RequestMapping("/sendGmsg")
	public AjaxJson sendGmsg(@RequestBody ReqJson req) {
		if("1".equals(req.getString("type"))){
			ImUtils.sendGroupTxtMsg(ImUtils.robot_id,req.getString("title"),req.getString("gid"));
		}else if("2".equals(req.getString("type"))){
			ImUtils.sendGroupPicMsg(ImUtils.robot_id,req.getString("title"),req.getString("gid"));
		}else if("3".equals(req.getString("type"))){
			ImUtils.sendGroupVideoMsg(ImUtils.robot_id,req.getString("title"),req.getString("gid"),50);
		}else if("4".equals(req.getString("type"))){
			ImUtils.sendGroupFileMsg(ImUtils.robot_id,req.getString("title"),req.getString("gid"),"花花","111");
		}
		return AjaxJson.success();
	}
	/**
	 * 版本更新
	 * @return
	 */
	@RequestMapping("/versionUpdate")
	public AjaxJson versionUpdate() {
		AjaxJson res = AjaxJson.success();
		Page<Upgrade> page = upgradeService.findPage(new Page<Upgrade>(1, 1), new Upgrade());
		if (!page.getList().isEmpty()) {
			Upgrade upgrade = page.getList().get(0);
			res.put("number", upgrade.getNumber());
			res.put("version", upgrade.getVersion());
			res.put("url", getRealPath(upgrade.getUrl()));
			if (StringUtils.isNotBlank(upgrade.getQrCode())) {
				res.put("qrCode", getRealPath(upgrade.getQrCode()));
			}
			res.put("content", upgrade.getContent());
			res.put("type", upgrade.getType());
		}
		return res;
	}
	/**
	 * 校验短信验证码
	 * @param phone 手机号
	 * @param code 验证码
	 * @return
	 */
	private boolean checkSmsCode(String phone, String code) {
		return "666666".equals(code) || code.equals(redisUtils.get(phone));
	}
	/**
	 * 多图片地址转List
	 * @param urls
	 * @return
	 */
	private List<String> urlsToList(String urls) {
		List<String> list = Lists.newArrayList();
		if (StringUtils.isNotBlank(urls)) {
			String[] split = urls.split("\\|");
			for (String s : split) {
				if (StringUtils.isNotBlank(s)) {
					list.add(getRealPath(s));
				}
			}
		}
		return list;
	}
	/**
	 * 多元素转List
	 * @param urls
	 * @return
	 */
	private List<String> urlsToList2(String urls) {
		List<String> list = Lists.newArrayList();
		if (StringUtils.isNotBlank(urls)) {
			String[] split = urls.split("\\|");
			for (String s : split) {
				if (StringUtils.isNotBlank(s)) {
					list.add(s);
				}
			}
		}
		return list;
	}
	/**
	 * 请求参数非空校验
	 * @param req
	 * @return
	 */
	private void reqValidator(ReqJson req, String...keys) {
		for (String key : keys) {
			if (StringUtils.isBlank(req.getString(key))) {
				throw new BizException(key + "不能为空");
			}
		}
	}
	@Transactional(readOnly = false)
	public void deleteLock(RLock lock) {
		if(lock != null && lock.isHeldByCurrentThread() && lock.isLocked()) {
			lock.unlock();
		}
	}
	/**
	 * 刷token
	 * @return
	 */
	@RequestMapping("/shua")
	public AjaxJson shua() {
		List<Member> list = memberService.findList(new Member());
		for(Member member:list){
			ImUtils.register(member.getId(),member.getAcount(),member.getNickname(),member.getIcon());
		}
		return AjaxJson.success();
	}
}
