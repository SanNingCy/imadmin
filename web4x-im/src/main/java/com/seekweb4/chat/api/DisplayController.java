package com.seekweb4.chat.api;

import jakarta.servlet.http.HttpServletRequest;

import com.seekweb4.chat.modules.agreement.entity.Agreement;
import com.seekweb4.chat.modules.agreement.service.AgreementService;
import com.seekweb4.chat.modules.customer.entity.Customer;
import com.seekweb4.chat.modules.customer.service.CustomerService;
import com.seekweb4.chat.modules.faq.entity.Faq;
import com.seekweb4.chat.modules.faq.service.FaqService;
import com.seekweb4.chat.modules.membernotice.entity.MemberNotice;
import com.seekweb4.chat.modules.membernotice.service.MemberNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.seekweb4.chat.core.web.BaseController;

/**
 * 富文本内容展示Controller
 * 
 * @author mall
 * @version 2018-08-09
 */
@Controller
@RequestMapping(value = "/display")
public class DisplayController extends BaseController {
	@Autowired
	private AgreementService agreementService;
	@Autowired
	private FaqService faqService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private MemberNoticeService memberNoticeService;
	/**
	 * 常见问题
	 *
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping("/faq")
	public String faq(HttpServletRequest request, String id) {
		String content = "";
		String title = "";
		Faq faq = faqService.get(id);
		if(faq != null) {
			content = faq.getContent();
		}
		request.setAttribute("content", content);
		request.setAttribute("title", title);
		return "content";
	}
	/**
	 * 协议
	 *
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping("/agreement")
	public String agreement(HttpServletRequest request, String id) {
		String content = "";
		String title = "";
		Agreement agreement = agreementService.get(id);
		if(agreement != null) {
			content = agreement.getContent();
		}
		request.setAttribute("content", content);
		request.setAttribute("title", title);
		return "content";
	}
	/**
	 * 注册成功文本
	 *
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping("/customer")
	public String customer(HttpServletRequest request, String id) {
		String content = "";
		String title = "";
		Customer agreement = customerService.get(id);
		if(agreement != null) {
			content = agreement.getReginfo();
		}
		request.setAttribute("content", content);
		request.setAttribute("title", title);
		return "content";
	}
	/**
	 * 用户消息文本
	 *
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping("/memNotice")
	public String memNotice(HttpServletRequest request, String id) {
		String content = "";
		String title = "";
		MemberNotice agreement = memberNoticeService.get(id);
		if(agreement != null) {
			content = agreement.getInfo();
		}
		request.setAttribute("content", content);
		request.setAttribute("title", title);
		return "content";
	}
}
