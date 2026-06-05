package com.seekweb4.chat.modules.friendapply.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.api.utils.PinyinUtils;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.modules.friend.entity.Friend;
import com.seekweb4.chat.modules.friend.service.FriendService;
import com.seekweb4.chat.modules.groupapply.service.GroupApplyService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
import com.seekweb4.chat.modules.friendapply.entity.FriendApply;
import com.seekweb4.chat.modules.friendapply.service.FriendApplyService;

/**
 * 好友申请记录Controller
 * @author lixinapp
 * @version 2024-09-20
 */
@RestController
@RequestMapping(value = "/friendapply/friendApply")
public class FriendApplyController extends BaseController {

	@Autowired
	private FriendApplyService friendApplyService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private FriendService friendService;
	@Autowired
	private GroupApplyService groupApplyService;

	@ModelAttribute
	public FriendApply get(@RequestParam(required=false) String id) {
		FriendApply entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = friendApplyService.get(id);
		}
		if (entity == null){
			entity = new FriendApply();
		}
		return entity;
	}

	/**
	 * 好友申请记录列表数据
	 */
	@ApiLog("查询好友申请记录列表")
	@RequiresPermissions("friendapply:friendApply:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    FriendApply friendApply, HttpServletRequest request, HttpServletResponse response) {
		Page<FriendApply> page = friendApplyService.findPage(new Page<FriendApply>(request, response), friendApply);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取好友申请记录数据
	 */
	@ApiLog("查询好友申请记录")
	@RequiresPermissions(value={"friendapply:friendApply:view","friendapply:friendApply:add","friendapply:friendApply:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    FriendApply friendApply) {
		if (StringUtils.isNotBlank(friendApply.getId())){
			FriendApply entity = friendApplyService.get(friendApply.getId());
			return entity != null ? AjaxJson.success().put("friendApply", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存好友申请记录
	 */
	@ApiLog("保存好友申请记录")
	@RequiresPermissions(value={"friendapply:friendApply:add","friendapply:friendApply:edit"},logical=Logical.OR)
	@PostMapping(value = "save",produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save(  @RequestBody  FriendApply friendApply, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(friendApply);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		friendApplyService.save(friendApply);//保存
		return AjaxJson.success("保存好友申请记录成功");
	}

	/**
	 * 添加好友
	 * @param friendApply
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("add")
	public AjaxJson add(FriendApply friendApply, Model model) throws Exception{
		Member me = memberService.get(friendApply.getU().getId());
		Member member = memberService.get(friendApply.getUid2().getId());
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
		}else {
			FriendApply apply = new FriendApply();
			apply.setU(me);
			apply.setUid2(member);
			apply.setState("1");
			apply.setBei(friendApply.getBei());
			friendApplyService.save(apply);

			String s1 = friendApplyService.executeGetSql("select count(1) from t_friend_apply where state = 1 and uid2 = '" + member.getId() + "'").toString();
			String s2 = groupApplyService.executeGetSql("select count(1) from t_group_apply where state = '1' and showids like '%" + member.getId() + "%'").toString();

			ImUtils.sendMsg(me.getId(),1002,s1+"|"+s2,member.getId());
		}
		return AjaxJson.success("提交成功");
	}


//	/**
//	 * 批量删除好友申请记录
//	 */
//	@ApiLog("删除好友申请记录")
//	@RequiresPermissions("friendapply:friendApply:del")
//	@DeleteMapping("delete")
//	public AjaxJson delete(String ids) {
//		String idArray[] =ids.split(",");
//		for(String id : idArray){
//			friendApplyService.delete(new FriendApply(id));
//		}
//		return AjaxJson.success("删除好友申请记录成功");
//	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出好友申请记录")
	@RequiresPermissions("friendapply:friendApply:export")
    @GetMapping("export")
    public AjaxJson exportFile(FriendApply friendApply, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "好友申请记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<FriendApply> page = friendApplyService.findPage(new Page<FriendApply>(request, response, -1), friendApply);
    		new ExportExcel("好友申请记录", FriendApply.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出好友申请记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入好友申请记录")
	@RequiresPermissions("friendapply:friendApply:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<FriendApply> list = ei.getDataList(FriendApply.class);
			for (FriendApply friendApply : list){
				try{
					friendApplyService.save(friendApply);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条好友申请记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条好友申请记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入好友申请记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入好友申请记录数据模板
	 */
	@ApiLog("下载好友申请记录模板")
	@RequiresPermissions("friendapply:friendApply:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "好友申请记录数据导入模板.xlsx";
    		List<FriendApply> list = Lists.newArrayList();
    		new ExportExcel("好友申请记录数据", FriendApply.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}