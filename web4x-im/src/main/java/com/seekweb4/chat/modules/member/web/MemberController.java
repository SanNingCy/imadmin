package com.seekweb4.chat.modules.member.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import cn.wildfirechat.sdk.UserAdmin;
import com.web4x.common.annotation.RepeatSubmit;
import com.seekweb4.chat.api.utils.MemberUtils;
import com.seekweb4.chat.api.utils.QrCodeUtil;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.common.utils.IdGen;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.delayedQueue.LianghaoDelayedQueueListener;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
import com.seekweb4.chat.modules.friend.service.FriendService;
import com.seekweb4.chat.modules.friendapply.service.FriendApplyService;
import com.seekweb4.chat.modules.groupitem.entity.GroupItem;
import com.seekweb4.chat.modules.groupitem.service.GroupItemService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.seekweb4.chat.common.annotation.ApiLog;
import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.common.utils.excel.ExportExcel;
import com.seekweb4.chat.common.utils.excel.ImportExcel;
import com.seekweb4.chat.modules.loginlog.service.LoginLogService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;

/**
 * 移动端用户Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/member/member")
public class MemberController extends BaseController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private GroupItemService groupItemService;
	@Autowired
	private FriendApplyService friendApplyService;
	@Autowired
	private FriendService friendService;
	@Autowired
	private LoginLogService loginLogService;
	@Autowired
	private RedisDelayedQueue redisDelayedQueue;

	@Value("${user.idno.prefix-m:false}")
	private boolean prefixM;

	@ModelAttribute
	public Member get(@RequestParam(required=false) String id) {
		Member entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = memberService.get(id);
		}
		if (entity == null){
			entity = new Member();
		}
		return entity;
	}

	/**
	 * 移动端用户列表数据
	 */
	@ApiLog("查询移动端用户列表")
//	@RequiresPermissions("member:member:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(Member member,
			@RequestParam(required = false) Integer inactiveDays,
			HttpServletRequest request, HttpServletResponse response) {
		applyInactiveDays(member, inactiveDays, request);
		Page<Member> page = memberService.findPage(new Page<Member>(request, response), member);
		fillLastLoginForMembers(page.getList());
		// TODO
		// 将qrcode和qrcode2的相对路径转换为绝对路径
		if (page.getList() != null) {
			for (Member m : page.getList()) {
				if (StringUtils.isNotBlank(m.getQrcode())) {
					m.setQrcode(getRealPath(m.getQrcode()));
				}
				if (StringUtils.isNotBlank(m.getQrcode2())) {
					m.setQrcode2(resolveQrcode2ForResponse(m.getQrcode2()));
				}
			}
		}
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 群封禁记录查询（state=1），分页
	 */
	@ApiLog("查询群封禁记录")
//	@RequiresPermissions("member:member:list")
	@GetMapping(value = "banList", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson banList(Member member,
			@RequestParam(required = false) Integer inactiveDays,
			HttpServletRequest request, HttpServletResponse response) {
		// 仅查询封禁状态
		member.setState("1");
		applyInactiveDays(member, inactiveDays, request);
		Page<Member> page = memberService.findPage(new Page<Member>(request, response), member);
		fillLastLoginForMembers(page.getList());
		// 转换二维码路径与默认列表保持一致
		if (page.getList() != null) {
			for (Member m : page.getList()) {
				if (StringUtils.isNotBlank(m.getQrcode())) {
					m.setQrcode(getRealPath(m.getQrcode()));
				}
				if (StringUtils.isNotBlank(m.getQrcode2())) {
					m.setQrcode2(resolveQrcode2ForResponse(m.getQrcode2()));
				}
			}
		}
		return AjaxJson.success().put("page", page);
	}

	/**
	 * 按用户 id 批量查询用户详情（字段与单条 {@link #queryById} 使用的 {@code get} 一致）。请求体字段 {@code ids}；
	 * 可为 JSON 数组、单个 id 字符串、逗号分隔多个 id、或字符串形式的 JSON 数组（如 {@code "[\"a\",\"b\"]"}）。
	 */
	@ApiLog("批量按用户id查询基础信息")
//	@RequiresPermissions(value = {"social:user:list:view", "social:user:list:add", "social:user:list:edit"}, logical = Logical.OR)
	@PostMapping(value = "basicByIds", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson basicByIds(@RequestBody(required = false) Map<String, Object> body) {
		List<String> ids = parseIdsFromRequestBody(body);
		if (ids.isEmpty()) {
			return AjaxJson.error("ids不能为空");
		}
		List<Member> list = memberService.listBasicByIds(ids);
		if (list != null) {
			for (Member m : list) {
				if (StringUtils.isNotBlank(m.getQrcode())) {
					m.setQrcode(getRealPath(m.getQrcode()));
				}
				if (StringUtils.isNotBlank(m.getQrcode2())) {
					m.setQrcode2(resolveQrcode2ForResponse(m.getQrcode2()));
				}
			}
		}
		return AjaxJson.success().put("list", list);
	}

	private static List<String> parseIdsFromRequestBody(Map<String, Object> body) {
		if (body == null) {
			return Collections.emptyList();
		}
		return parseIdsValue(body.get("ids"));
	}

	@SuppressWarnings("unchecked")
	private static List<String> parseIdsValue(Object raw) {
		if (raw == null) {
			return Collections.emptyList();
		}
		if (raw instanceof List) {
			List<?> list = (List<?>) raw;
			List<String> out = new ArrayList<>();
			for (Object o : list) {
				if (o == null) {
					continue;
				}
				String s = String.valueOf(o).trim();
				if (StringUtils.isNotBlank(s)) {
					out.add(s);
				}
			}
			return out;
		}
		if (raw instanceof String) {
			String s = ((String) raw).trim();
			if (StringUtils.isBlank(s)) {
				return Collections.emptyList();
			}
			if (s.charAt(0) == '[') {
				try {
					List<String> parsed = JSON.parseArray(s, String.class);
					if (parsed == null) {
						return Collections.emptyList();
					}
					return parsed.stream().filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.toList());
				} catch (Exception e) {
					return Collections.singletonList(s);
				}
			}
			if (s.contains(",")) {
				List<String> out = new ArrayList<>();
				for (String p : s.split(",")) {
					String t = p.trim();
					if (StringUtils.isNotBlank(t)) {
						out.add(t);
					}
				}
				return out;
			}
			return Collections.singletonList(s);
		}
		String s = String.valueOf(raw).trim();
		return StringUtils.isBlank(s) ? Collections.emptyList() : Collections.singletonList(s);
	}

	/**
	 * 根据Id获取移动端用户数据
	 */
	@ApiLog("查询移动端用户")
	@RequiresPermissions(value={"social:user:list:view","social:user:list:add","social:user:list:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"member:member:view","member:member:add","member:member:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Member member) {
		if (StringUtils.isNotBlank(member.getId())) {
			Member entity = memberService.get(member.getId());
			// TODO
			if (entity != null) {
				// 将qrcode和qrcode2的相对路径转换为绝对路径
				if (StringUtils.isNotBlank(entity.getQrcode())) {
					entity.setQrcode(getRealPath(entity.getQrcode()));
				}
				if (StringUtils.isNotBlank(entity.getQrcode2())) {
					entity.setQrcode2(resolveQrcode2ForResponse(entity.getQrcode2()));
				}
				return AjaxJson.success().put("member", entity);
			}
			return AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存移动端用户
	 */
	@ApiLog("保存移动端用户")
	@RepeatSubmit
	@RequiresPermissions(value={"social:user:list:add","social:user:list:edit"},logical=Logical.OR)
//	@RequiresPermissions(value={"member:member:add","member:member:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(  @RequestBody  Member member) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(member);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		String s1 = memberService.executeGetSql("select count(1) from t_member where nickname = '" + member.getNickname() + "' and id != '" + member.getId() + "'").toString();
		if(!"0".equals(s1)){
			return AjaxJson.error("昵称已存在，请更换");
		}
		if(StringUtils.isNotEmpty(member.getLianghao())){
			// 根据配置决定是否添加M前缀
			String lianghao = member.getLianghao();
			if (prefixM) {
				lianghao = 888 + lianghao;
				member.setLianghao(lianghao);
			}
			String s = memberService.executeGetSql("select count(1) from t_member where (idno = '" + lianghao + "' or lianghao = '" + lianghao + "') and id != '" + member.getId() + "'").toString();
			if(!"0".equals(s)){
				return AjaxJson.error("此id号已存在");
			}
			if(member.getEndtime() == null){
				return AjaxJson.error("靓号到期时间不可为空");
			}
			if(member.getEndtime().before(new Date())){
				return AjaxJson.error("靓号到期时间不可小于当前时间");
			}
			long t = member.getEndtime().getTime()-new Date().getTime();
			long l = t / (60 * 1000);//分钟
			redisDelayedQueue.addQueueMinutes(member.getId(),(int)l, LianghaoDelayedQueueListener.class);

		}
		if(StringUtils.isNotEmpty(member.getState()) && !member.getIsNewRecord()){
			Member mm = memberService.get(member.getId());
			if(!mm.getState().equals(member.getState())){
				if("1".equals(member.getState())){	// 状态 0正常 1禁用
					MemberUtils.remove(member.getId());
					// 追加野火服务强制下线（clientId 传空，踢掉该用户所有客户端）
					ImUtils.kickoffUserClient(member.getId(), null);
					// 同步野火侧封禁，避免旧 imtoken/会话还能重连
					ImUtils.updateUserBlockStatus(member.getId(), 1);
					ImUtils.sendMsg(ImUtils.robot_id,1005,member.getId(),member.getId());
				}
				else {
					// 解除野火侧封禁
					ImUtils.updateUserBlockStatus(member.getId(), 0);
				}
			}
		}
		// 编辑已有用户时不更新名片/登录二维码，避免展示用绝对路径被误写回库
		if (!member.getIsNewRecord()) {
			member.setQrcode(null);
			member.setQrcode2(null);
		} else {
			member.setQrcode(stripDomain(member.getQrcode()));
			member.setQrcode2(stripDomain(member.getQrcode2()));
		}
		//新增或编辑表单保存
		memberService.save(member);//保存
		// 同步昵称与头像到野火 IM，聊天会话/好友列表读的是 IM portrait 不是 t_member.icon
		Member imMember = memberService.get(member.getId());
		if (imMember != null) {
			String nicknameForIm = StringUtils.isNotBlank(member.getNickname()) ? member.getNickname() : imMember.getNickname();
			String iconForIm = StringUtils.isNotBlank(member.getIcon()) ? member.getIcon() : imMember.getIcon();
			ImUtils.editUser(imMember.getId(), nicknameForIm, getRealPath(iconForIm));
		}
		return AjaxJson.success("保存移动端用户成功");
	}

	/**
	 * 添加用户
	 * @param member
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "add", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson add(    Member member) throws Exception{
		Member mem = new Member();
		String s1 = memberService.executeGetSql("select count(1) from t_member where nickname = '" + member.getNickname() + "'").toString();
		if(!"0".equals(s1)){
			return AjaxJson.error("昵称已存在，请更换");
		}
		if(StringUtils.isNotBlank(member.getPhone())){
			String s = memberService.executeGetSql("select count(1) from t_member where phone = '" + member.getPhone() + "'").toString();
			if(!"0".equals(s)){
				return AjaxJson.error("此手机号已存在");
			}
			mem.setPhone(member.getPhone());
		}
		if(StringUtils.isNotBlank(member.getAcount())){
			String s = memberService.executeGetSql("select count(1) from t_member where acount = '" + member.getAcount() + "'").toString();
			if(!"0".equals(s)){
				return AjaxJson.error("此账号已存在");
			}
			mem.setAccSerch(member.getAcount());
		}
		mem.setNickname(member.getNickname());
		mem.setIcon(stripDomain(member.getIcon()));
		mem.setSex(member.getSex());
		mem.setPassword(memberService.entryptPassword(member.getPassword()));
		memberService.regist(member);
		return AjaxJson.success("添加成功");
	}

//	/**
//	 * 去除固定域名前缀，保留相对路径
//	 */
//	private String stripDomain(String path) {
//		if (StringUtils.isBlank(path)) {
//			return path;
//		}
//		String p = path.trim();
//		p = p.replace("http://api-fat.seekweb4.net/", "")
//				.replace("https://api-fat.seekweb4.net/", "");
//		// 兼容双斜杠开头
//		if (p.startsWith("//api-fat.seekweb4.net/")) {
//			p = p.substring("//api-fat.seekweb4.net/".length());
//		}
//		return p;
//	}


	/**
	 * 去除域名前缀（支持所有环境），只保留相对路径
	 * 支持格式：
	 * - http://xxx/xxx/yyy.png
	 * - https://xxx/xxx/yyy.png
	 * - //xxx/xxx/yyy.png
	 * - 已经是相对路径时原样返回
	 */
	private String stripDomain(String path) {
		if (StringUtils.isBlank(path)) {
			return path;
		}
		String p = path.trim();

		// 1. 处理形如 //domain/path 的地址
		if (p.startsWith("//")) {
			int idx = p.indexOf('/', 2); // 从第3个字符开始找下一个 '/'
			if (idx > 0 && idx + 1 < p.length()) {
				return p.substring(idx + 1);
			}
			// 没有路径部分，直接返回空或原始值，这里返回原始值以避免意外清空
			return p;
		}

		// 2. 处理 http:// 或 https:// 开头的地址（任何域名或IP）
		int schemeIdx = p.indexOf("://");
		if (schemeIdx > 0) {
			int pathStart = p.indexOf('/', schemeIdx + 3);
			if (pathStart >= 0 && pathStart + 1 < p.length()) {
				return p.substring(pathStart + 1);
			}
			// 只有域名没有路径，返回空字符串
			return "";
		}

		// 3. 处理形如 119.45.9.228:82/path 或 domain:82/path（无协议）
		// 找到第一个 '/'，如果前面看起来像 host[:port]，则去掉
		int slashIdx = p.indexOf('/');
		if (slashIdx > 0) {
			String hostPart = p.substring(0, slashIdx);
			// 简单判断：包含点或冒号，认为是 host[:port]
			if (hostPart.contains(".") || hostPart.contains(":")) {
				if (slashIdx + 1 < p.length()) {
					return p.substring(slashIdx + 1);
				}
				return "";
			}
		}

		// 4. 其他情况（本来就是相对路径），直接返回
		return p;
	}

	/**
	 * 批量封禁/解禁
	 * @param member
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "allfeng", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson allfeng(    Member member) throws Exception{
		if(StringUtils.isBlank(member.getIdnos())){
			return AjaxJson.error("id为空");
		}
		String[] split = member.getIdnos().split("[|]", 0);
		for(String no:split){
			if("1".equals(member.getFtype())){
				memberService.executeUpdateSql("update t_member set state = '1' where idno = '"+no+"'");
				Member idno = memberService.findUniqueByProperty("idno", no);
				MemberUtils.remove(idno.getId());
				// 追加野火服务强制下线（clientId 传空，踢掉该用户所有客户端）
				ImUtils.kickoffUserClient(idno.getId(), null);
				// 同步野火侧封禁，避免旧 imtoken/会话还能重连
				ImUtils.updateUserBlockStatus(idno.getId(), 1);
				ImUtils.sendMsg(ImUtils.robot_id,1005,idno.getId(),idno.getId());
			}else {
				memberService.executeUpdateSql("update t_member set state = '0' where idno = '"+no+"'");
				Member idno = memberService.findUniqueByProperty("idno", no);
				// 解除野火侧封禁
				ImUtils.updateUserBlockStatus(idno.getId(), 0);
			}
		}
		return AjaxJson.success("处理成功");
	}
	/**
	 * 批量内部号/取消内部号
	 * @param member
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "allnb", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson allnb(    Member member) throws Exception{
		if(StringUtils.isBlank(member.getIdnos())){
			return AjaxJson.error("id为空");
		}
		String[] split = member.getIdnos().split("[|]", 0);
		for(String no:split){
			if("1".equals(member.getFtype())){
				memberService.executeUpdateSql("update t_member set isneibu = '1' where idno = '"+no+"'");
			}else {
				memberService.executeUpdateSql("update t_member set isneibu = '0' where idno = '"+no+"'");
			}
		}
		return AjaxJson.success("处理成功");
	}

	/**
	 * 封禁
	 * @param ids
	 * @return
	 */
	@DeleteMapping("feng")
	public AjaxJson fengjin(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			memberService.executeUpdateSql("update t_member set state = '1' where id = '"+id+"'");
			MemberUtils.remove(id);
			// 追加野火服务强制下线（clientId 传空，踢掉该用户所有客户端）
			ImUtils.kickoffUserClient(id, null);
			// 同步野火侧封禁，避免旧 imtoken/会话还能重连
			ImUtils.updateUserBlockStatus(id, 1);
			ImUtils.sendMsg(ImUtils.robot_id,1005,id,id);
		}
		return AjaxJson.success("处理成功");
	}
	/**
	 * 解禁
	 * @param ids
	 * @return
	 */
	@DeleteMapping("jie")
	public AjaxJson jie(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			memberService.executeUpdateSql("update t_member set state = '0' where id = '"+id+"'");
			// 解除野火侧封禁
			ImUtils.updateUserBlockStatus(id, 0);
		}
		return AjaxJson.success("处理成功");
	}
	/**
	 * 清除支付密码
	 * @param id
	 * @return
	 */
	@DeleteMapping("clearpwd")
	public AjaxJson clearpwd(String id) {
		memberService.executeUpdateSql("update t_member set paypwd = '' where id = '"+id+"'");
		return AjaxJson.success("清除成功");
	}
	/**
	 * 变更余额
	 * @param member
	 * @return
	 * @throws Exception
	 */
	@RepeatSubmit
	@PostMapping(value = "changeMoney", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson changeMoney(@RequestBody Member member) throws Exception{
		Member mm = memberService.get(member.getId());
		if("0".equals(member.getBtype()) && member.getMoney().compareTo(mm.getBalance()) == 1){
			return AjaxJson.error("扣减金额不可大于余额");
		}
		memberService.updateBalance(member,member.getMoney(),member.getBtype(),"1".equals(member.getBtype())?"后台充值余额":"后台扣减余额");
		return AjaxJson.success("处理成功");
	}


	/**
	 * 批量删除移动端用户
	 */
	@ApiLog("删除移动端用户")
//	@RequiresPermissions("social:user:list:delete")
//	@RequiresPermissions("member:member:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			GroupItem item = new GroupItem();
			item.setU(new Member(id));
			List<GroupItem> list = groupItemService.findList(item);
			if(!list.isEmpty()){
				for(GroupItem o:list){
					ImUtils.quitGroup(id,o.getGroup().getId());
					groupItemService.delete(o);
				}
			}
			ImUtils.deleteUser(id);
			memberService.delete(new Member(id));
			friendApplyService.executeDeleteSql("delete from t_friend_apply where uid = '"+id+"' or uid2 = '"+id+"'");
			friendService.executeDeleteSql("delete from t_friend where uid = '"+id+"' or uid2 = '"+id+"'");
		}
		return AjaxJson.success("删除移动端用户成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出移动端用户")
//	@RequiresPermissions("member:member:export")
    @GetMapping("export")
    public AjaxJson exportFile(Member member, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "移动端用户"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Member> page = memberService.findPage(new Page<Member>(request, response, -1), member);
    		new ExportExcel("移动端用户", Member.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出移动端用户记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入移动端用户")
//	@RequiresPermissions("member:member:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Member> list = ei.getDataList(Member.class);
			for (Member member : list){
				try{
					if("1".equals(member.getAccType())){
						String s = memberService.executeGetSql("select count(1) from t_member where phone = '" + member.getPhone() + "'").toString();
						if(!"0".equals(s)){
							throw new RuntimeException("手机号（"+member.getPhone()+"）已存在");
						}
					}else if("2".equals(member.getAccType())){
						String s2 = memberService.executeGetSql("select count(1) from t_member where acount = '" + member.getAcount() + "'").toString();
						if(!"0".equals(s2)){
							throw new RuntimeException("账户号（"+member.getAcount()+"）已存在");
						}
					}else if("3".equals(member.getAccType())){
						String s3 = memberService.executeGetSql("select count(1) from t_member where eqno = '" + member.getEqno() + "'").toString();
						if(!"0".equals(s3)){
							throw new RuntimeException("设备号（"+member.getEqno()+"）已存在");
						}
					}
					member.setIcon(AppProperites.newInstance().filePath+"/userfiles/icon.png");
					member.setIdno(IdGen.getNumber(4));
					member.setQrcode(QrCodeUtil.getCode("1-"+member.getIdno(),"/memberCode"));
					member.setPassword(memberService.entryptPassword(member.getPassword()));
					member.setState("0");
					member.setZiti("20");
					member.setShowQuan("1");
					member.setIsAddYz("1");
					member.setIsQuanTx("1");
					member.setIsMsgTx("1");
					member.setAccSerch("1");
					member.setPhoneSerch("1");
					member.setGroAdd("1");
					member.setMpAdd("1");
					member.setQrAdd("1");
					member.setBalance(BigDecimal.ZERO);
					memberService.save(member);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条移动端用户记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条移动端用户记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入移动端用户失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入移动端用户数据模板
	 */
	@ApiLog("下载移动端用户模板")
//	@RequiresPermissions("member:member:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "移动端用户数据导入模板.xlsx";
    		List<Member> list = Lists.newArrayList();
    		new ExportExcel("移动端用户数据", Member.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 未登录天数筛选：兼容 @ModelAttribute / 网关改写导致 member 绑定不到 query 参数的情况；
	 * 同时支持参数名 inactiveDays（驼峰）与 inactive_days（个别前端）。
	 */
	private void applyInactiveDays(Member member, Integer inactiveDaysParam, HttpServletRequest request) {
		if (member == null) {
			return;
		}
		if (inactiveDaysParam != null && inactiveDaysParam > 0) {
			member.setInactiveDays(inactiveDaysParam);
			return;
		}
		String raw = request != null ? request.getParameter("inactiveDays") : null;
		if (StringUtils.isBlank(raw)) {
			raw = request != null ? request.getParameter("inactive_days") : null;
		}
		if (StringUtils.isBlank(raw)) {
			return;
		}
		try {
			int v = Integer.parseInt(raw.trim());
			if (v > 0) {
				member.setInactiveDays(v);
			}
		} catch (NumberFormatException ignored) {
			//
		}
	}

	/** 将最近一次登录成功时间写入列表（不依赖 SQL 列映射，避免返回值缺字段） */
	private void fillLastLoginForMembers(List<Member> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		List<String> ids = new ArrayList<>(list.size());
		for (Member m : list) {
			if (m != null && StringUtils.isNotBlank(m.getId())) {
				ids.add(m.getId());
			}
		}
		Map<String, Date> lastMap = loginLogService.getLastSuccessLoginMap(ids);
		for (Member m : list) {
			if (m == null) {
				continue;
			}
			m.setLastLoginDate(lastMap.get(m.getId()));
		}
	}


}