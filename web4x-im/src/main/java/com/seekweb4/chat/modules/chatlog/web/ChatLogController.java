package com.seekweb4.chat.modules.chatlog.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.google.common.collect.Maps;
import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.modules.group.service.GroupService;
import com.seekweb4.chat.modules.loginlog.service.LoginLogService;
import com.seekweb4.chat.modules.member.entity.MemberTongji;
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
import com.seekweb4.chat.modules.chatlog.entity.ChatLog;
import com.seekweb4.chat.modules.chatlog.service.ChatLogService;

/**
 * 聊天记录Controller
 * @author lixinapp
 * @version 2024-09-26
 */
@RestController
@RequestMapping(value = "/chatlog/chatLog")
public class ChatLogController extends BaseController {

	@Autowired
	private ChatLogService chatLogService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private LoginLogService loginLogService;

	@ModelAttribute
	public ChatLog get(@RequestParam(required=false) String id) {
		ChatLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = chatLogService.get(id);
		}
		if (entity == null){
			entity = new ChatLog();
		}
		return entity;
	}

	/**
	 * 近7日/30日活跃用户趋势图
	 * @param days 天数，默认7天，可选7或30
	 * @return
	 */
	@ApiLog("查询活跃用户趋势")
//	@RequiresPermissions("chatlog:chatLog:view")
	@RequestMapping("/activeUserTrend")
	public AjaxJson activeUserTrend(@RequestParam(defaultValue = "7") Integer days) {
		try {
			// 参数校验
			if (days != 7 && days != 30) {
				return AjaxJson.error("天数参数只能为7或30");
			}

			// 计算日期范围
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			List<Map<String, Object>> dataList = Lists.newArrayList();

			// 生成日期列表并初始化数据
			Map<String, Integer> dateCountMap = new LinkedHashMap<>();
			for (int i = days - 1; i >= 0; i--) {
				Calendar dayCalendar = (Calendar) calendar.clone();
				dayCalendar.add(Calendar.DAY_OF_MONTH, -i);
				String dateStr = dateFormat.format(dayCalendar.getTime());
				dateCountMap.put(dateStr, 0);
			}

			// 查询登录日志，按日期统计活跃用户数
			String sql = "SELECT DATE_FORMAT(create_date, '%Y-%m-%d') as date, COUNT(DISTINCT uid) as count " +
					"FROM t_login_log " +
					"WHERE create_date >= DATE_SUB(CURDATE(), INTERVAL " + (days - 1) + " DAY) " +
					"GROUP BY DATE_FORMAT(create_date, '%Y-%m-%d') " +
					"ORDER BY date ASC";

			List<Map<String, Object>> queryResult = loginLogService.executeSelectSql(sql);

			// 将查询结果填充到日期映射中
			if (queryResult != null) {
				for (Map<String, Object> row : queryResult) {
					Object dateObj = row.get("date");
					if (dateObj == null) {
						continue;
					}
					String date = dateObj.toString();
					Object countObj = row.get("count");
					int count = 0;
					if (countObj != null) {
						if (countObj instanceof Number) {
							count = ((Number) countObj).intValue();
						} else {
							count = Integer.parseInt(countObj.toString());
						}
					}
					if (dateCountMap.containsKey(date)) {
						dateCountMap.put(date, count);
					}
				}
			}

			// 转换为前端需要的格式
			for (Map.Entry<String, Integer> entry : dateCountMap.entrySet()) {
				Map<String, Object> dayData = Maps.newHashMap();
				dayData.put("date", entry.getKey());
				dayData.put("count", entry.getValue());
				dataList.add(dayData);
			}

			Map<String, Object> result = Maps.newHashMap();
			result.put("days", days);
			result.put("data", dataList);

			return AjaxJson.success().put("data", result);
		} catch (Exception e) {
			return AjaxJson.error("查询活跃用户趋势失败：" + e.getMessage());
		}
	}

	/**
	 * 顶部数据
	 * @return
	 */
	@RequestMapping("/topData")
	public AjaxJson topData() {
		Map map = Maps.newHashMap();
		map.put("totalCount", memberService.executeGetSql("select count(1) from t_member").toString());
//		map.put("todayMsgCount", chatLogService.executeGetSql("select count(1) from t_chat_log where to_days(create_date) = to_days(now())").toString());
//		map.put("msgCount", chatLogService.executeGetSql("select count(1) from t_chat_log ").toString());
		map.put("groupCount", groupService.executeGetSql("select count(1) from t_group where del_flag = '0'").toString());
		// 在线设备数：统计今天登录的不同设备数
		String onlineDeviceSql = "select count(distinct m.eqno) from t_login_log ll inner join t_member m on ll.uid = m.id where to_days(ll.create_date) = to_days(now()) and m.eqno is not null and m.eqno != ''";
		map.put("onlineDeviceCount", loginLogService.executeGetSql(onlineDeviceSql).toString());
		// 日活跃用户：统计今天登录的不同用户数
		String dailyActiveUserSql = "select count(distinct uid) from t_login_log where to_days(create_date) = to_days(now())";
		map.put("dailyActiveUserCount", loginLogService.executeGetSql(dailyActiveUserSql).toString());
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
	 * 聊天记录列表数据
	 */
	@ApiLog("查询聊天记录列表")
	@RequiresPermissions("chatlog:chatLog:list")
	@GetMapping("list")
	public AjaxJson list(ChatLog chatLog, HttpServletRequest request, HttpServletResponse response) {
		Page<ChatLog> page = chatLogService.findPage(new Page<ChatLog>(request, response), chatLog);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取聊天记录数据
	 */
	@ApiLog("查询聊天记录")
	@RequiresPermissions(value={"chatlog:chatLog:view","chatlog:chatLog:add","chatlog:chatLog:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(ChatLog chatLog) {
		return AjaxJson.success().put("chatLog", chatLog);
	}

	/**
	 * 保存聊天记录
	 */
	@ApiLog("保存聊天记录")
	@RequiresPermissions(value={"chatlog:chatLog:add","chatlog:chatLog:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(ChatLog chatLog, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(chatLog);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		chatLogService.save(chatLog);//保存
		return AjaxJson.success("保存聊天记录成功");
	}


	/**
	 * 批量删除聊天记录
	 */
	@ApiLog("删除聊天记录")
	@RequiresPermissions("chatlog:chatLog:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(@RequestBody String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			chatLogService.delete(new ChatLog(id));
		}
		return AjaxJson.success("删除聊天记录成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出聊天记录")
	@RequiresPermissions("chatlog:chatLog:export")
    @GetMapping("export")
    public AjaxJson exportFile(ChatLog chatLog, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "聊天记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ChatLog> page = chatLogService.findPage(new Page<ChatLog>(request, response, -1), chatLog);
    		new ExportExcel("聊天记录", ChatLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出聊天记录记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入聊天记录")
	@RequiresPermissions("chatlog:chatLog:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ChatLog> list = ei.getDataList(ChatLog.class);
			for (ChatLog chatLog : list){
				try{
					chatLogService.save(chatLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条聊天记录记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条聊天记录记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入聊天记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入聊天记录数据模板
	 */
	@ApiLog("下载聊天记录模板")
	@RequiresPermissions("chatlog:chatLog:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "聊天记录数据导入模板.xlsx";
    		List<ChatLog> list = Lists.newArrayList();
    		new ExportExcel("聊天记录数据", ChatLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}