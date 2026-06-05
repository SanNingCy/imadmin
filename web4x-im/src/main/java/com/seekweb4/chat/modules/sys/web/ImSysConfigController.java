package com.seekweb4.chat.modules.sys.web;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.MyBeanUtils;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.sys.entity.SysConfig;
import com.seekweb4.chat.modules.sys.service.SysConfigService;
import com.seekweb4.chat.modules.sys.vo.SysConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * IM 系统配置 API（与若依 {@code com.web4x.web.controller.system.SysConfigController} 区分）
 */
@RestController
@RequestMapping("/sys/sysConfig")
public class ImSysConfigController extends BaseController {

	@Autowired
	private SysConfigService sysConfigService;

	@ApiLog("获取系统配置")
	@GetMapping("queryById")
	public AjaxJson queryById(HttpServletRequest request, HttpServletResponse response, Model model) {
		SysConfig config = sysConfigService.get("1");
		return AjaxJson.success().put("config", config);
	}

	@GetMapping("getConfig")
	public AjaxJson getConfig() {
		SysConfig config = sysConfigService.get("1");
		SysConfigVo vo = new SysConfigVo ();
		vo.setDefaultLayout (config.getDefaultLayout ());
		vo.setDefaultTheme (config.getDefaultTheme ());
		vo.setLogo (config.getLogo ());
		vo.setProductName (config.getProductName ());
		return AjaxJson.success().put("config", vo);
	}

	@ApiLog("保存系统配置")
	@PostMapping("save")
	public AjaxJson save(SysConfig config)throws Exception {
		if(appProperites.isDemoMode()){
			return AjaxJson.error("演示模式，禁止修改!");
		}
		if(config.getMultiAccountLogin() == null){
			config.setMultiAccountLogin("0");
		}
		SysConfig target = sysConfigService.get("1");
		MyBeanUtils.copyBeanNotNull2Bean(config, target);
		target.setIsNewRecord(false);
		sysConfigService.save(target);
		return AjaxJson.success("保存系统配置成功").put("config", target);
	}

}
