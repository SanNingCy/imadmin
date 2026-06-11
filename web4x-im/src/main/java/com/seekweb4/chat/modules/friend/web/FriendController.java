package com.seekweb4.chat.modules.friend.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.seekweb4.chat.modules.friend.entity.Friend;
import com.seekweb4.chat.modules.friend.service.FriendService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import java.util.Map;

/**
 * 好友关系Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/friend/friend")
public class FriendController extends BaseController {

	@Autowired
	private FriendService friendService;
	@Autowired
	private MemberService memberService;

	@ModelAttribute
	public Friend get(@RequestParam(required=false) String id) {
		Friend entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = friendService.get(id);
		}
		if (entity == null){
			entity = new Friend();
		}
		return entity;
	}

	/**
	 * 好友关系列表数据
	 */
	@ApiLog("查询好友关系列表")
//	@RequiresPermissions("friend:friend:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Friend friend, HttpServletRequest request, HttpServletResponse response) {
		// 清理查询参数：将空字符串转换为 null，避免影响查询
		normalizeQueryParams(friend);
		Page<Friend> page = new Page<Friend>(request, response);
		sanitizeFriendOrderBy(page);
		page = friendService.findPage(page, friend);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取好友关系数据
	 */
	@ApiLog("查询好友关系")
	@RequiresPermissions(value={"social:user:friends:view","social:user:friends:add","social:user:friends:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Friend friend) {
		if (StringUtils.isNotBlank(friend.getId())) {
			Friend entity = friendService.get(friend.getId());
			return entity != null ? AjaxJson.success().put("friend", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存好友关系
	 */
	@ApiLog("保存好友关系")
	@RequiresPermissions(value={"social:user:friends:add","social:user:friends:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(@RequestBody Map<String, Object> requestMap) throws Exception{
		// 处理前端传过来的 u.id 和 uid2.id 字段（带点的字段名）
		// Spring 的 @RequestBody 无法自动绑定带点的字段名，需要手动处理
		Friend friend = new Friend();
		
		// 处理 u.id
		Object uIdObj = requestMap.get("u.id");
		if (uIdObj != null) {
			Member u = new Member();
			u.setId(uIdObj.toString());
			friend.setU(u);
		} else if (requestMap.containsKey("u")) {
			// 如果传的是 u 对象
			Map<String, Object> uMap = (Map<String, Object>) requestMap.get("u");
			if (uMap != null && uMap.containsKey("id")) {
				Member u = new Member();
				u.setId(uMap.get("id").toString());
				friend.setU(u);
			}
		}
		
		// 处理 uid2.id
		Object uid2IdObj = requestMap.get("uid2.id");
		if (uid2IdObj != null) {
			Member uid2 = new Member();
			uid2.setId(uid2IdObj.toString());
			friend.setUid2(uid2);
		} else if (requestMap.containsKey("uid2")) {
			// 如果传的是 uid2 对象
			Map<String, Object> uid2Map = (Map<String, Object>) requestMap.get("uid2");
			if (uid2Map != null && uid2Map.containsKey("id")) {
				Member uid2 = new Member();
				uid2.setId(uid2Map.get("id").toString());
				friend.setUid2(uid2);
			}
		}
		
		// 处理其他字段
		if (requestMap.containsKey("id")) {
			friend.setId(requestMap.get("id").toString());
		}
		if (requestMap.containsKey("mdr")) {
			friend.setMdr(requestMap.get("mdr").toString());
		}
		if (requestMap.containsKey("isTop")) {
			friend.setIsTop(requestMap.get("isTop").toString());
		}
		if (requestMap.containsKey("zimu")) {
			friend.setZimu(requestMap.get("zimu").toString());
		}
		if (requestMap.containsKey("bei")) {
			friend.setBei(requestMap.get("bei").toString());
		}
		
		// 验证必填字段
		if (friend.getU() == null || StringUtils.isBlank(friend.getU().getId())) {
			return AjaxJson.error("用户ID不能为空");
		}
		if (friend.getUid2() == null || StringUtils.isBlank(friend.getUid2().getId())) {
			return AjaxJson.error("好友ID不能为空");
		}
		
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(friend);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		
		//新增或编辑表单保存
		friendService.save(friend);//保存
		return AjaxJson.success("保存好友关系成功");
	}


	/**
	 * 批量删除好友关系
	 */
	@ApiLog("删除好友关系")
	@RequiresPermissions("social:user:friends:delete")
//	@RequiresPermissions("friend:friend:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(String ids) {
		String idArray[] = ids.split(",");
		for(String id : idArray){
			// 直接从数据库查询 uid 和 uid2，避免 MyBatis 映射问题
			// 使用更安全的查询方式，直接查询单个字段
			String uid = null;
			String uid2 = null;
			
			try {
				// 分别查询 uid 和 uid2
				String sqlUid = "SELECT uid FROM t_friend WHERE id = '" + id + "'";
				Object uidObj = friendService.executeGetSql(sqlUid);
				if (uidObj != null) {
					uid = uidObj.toString();
				}
				
				String sqlUid2 = "SELECT uid2 FROM t_friend WHERE id = '" + id + "'";
				Object uid2Obj = friendService.executeGetSql(sqlUid2);
				if (uid2Obj != null) {
					uid2 = uid2Obj.toString();
				}
			} catch (Exception e) {
				// 如果查询失败，跳过这条记录
				logger.error("查询好友关系失败: id={}", id, e);
				continue;
			}
			
			// 如果获取不到 uid 或 uid2，跳过这条记录
			if (StringUtils.isBlank(uid) || StringUtils.isBlank(uid2)) {
				continue;
			}
			
			// 删除双向好友关系
			friendService.executeDeleteSql("delete from t_friend where uid = '"+ uid +"' and uid2 = '"+ uid2 +"'");
			friendService.executeDeleteSql("delete from t_friend where uid2 = '"+ uid +"' and uid = '"+ uid2 +"'");
			ImUtils.addFrinend(uid, uid2, false);
		}
		return AjaxJson.success("删除好友关系成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出好友关系")
//	@RequiresPermissions("friend:friend:export")
    @GetMapping("export")
    public AjaxJson exportFile(Friend friend, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "好友关系"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Friend> page = friendService.findPage(new Page<Friend>(request, response, -1), friend);
    		new ExportExcel("好友关系", Friend.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出好友关系记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入好友关系")
//	@RequiresPermissions("friend:friend:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Friend> list = ei.getDataList(Friend.class);
			for (Friend friend : list){
				try{
					friendService.save(friend);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条好友关系记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条好友关系记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入好友关系失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入好友关系数据模板
	 */
	@ApiLog("下载好友关系模板")
//	@RequiresPermissions("friend:friend:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "好友关系数据导入模板.xlsx";
    		List<Friend> list = Lists.newArrayList();
    		new ExportExcel("好友关系数据", Friend.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }

	/** 好友关系列表仅允许按添加时间排序，避免大表多列排序导致性能问题 */
	private void sanitizeFriendOrderBy(Page<Friend> page) {
		String orderBy = page.getOrderBy();
		if (StringUtils.isBlank(orderBy)) {
			return;
		}
		String[] parts = orderBy.trim().split("\\s+");
		if (parts.length == 0 || !"createDate".equals(parts[0])) {
			page.setOrderBy("");
			return;
		}
		String direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1]) ? "asc" : "desc";
		page.setOrderBy("a.create_date " + direction);
	}

}