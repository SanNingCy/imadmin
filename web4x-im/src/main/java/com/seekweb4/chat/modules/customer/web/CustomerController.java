package com.seekweb4.chat.modules.customer.web;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
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
import com.seekweb4.chat.modules.customer.entity.Customer;
import com.seekweb4.chat.modules.customer.service.CustomerService;

/**
 * 平台参数Controller
 * @author lixinapp
 * @version 2024-10-11
 */
@RestController
@RequestMapping(value = "/customer/customer")
public class CustomerController extends BaseController {

	@Autowired
	private CustomerService customerService;
	@Autowired
	private MemberService memberService;

	@ModelAttribute
	public Customer get(@RequestParam(required=false) String id) {
		Customer entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = customerService.get(id);
		}
		if (entity == null){
			entity = new Customer();
		}
		return entity;
	}

	/**
	 * 平台参数列表数据
	 */
	@ApiLog("查询平台参数列表")
//	@RequiresPermissions("customer:customer:list")
	@GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson list(    Customer customer, HttpServletRequest request, HttpServletResponse response) {
		Page<Customer> page = customerService.findPage(new Page<Customer>(request, response), customer);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取平台参数数据
	 */
	@ApiLog("查询平台参数")
	@RequiresPermissions(value={"ops:system:base:view","ops:system:base:add","ops:system:base:edit"},logical=Logical.OR)
	@GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson queryById(    Customer customer) {
		if (StringUtils.isNotBlank(customer.getId())) {
			Customer entity = customerService.get(customer.getId());
			return entity != null ? AjaxJson.success().put("customer", entity) : AjaxJson.error("不存在");
		}
		return AjaxJson.error("id不能为空");
	}

	/**
	 * 保存平台参数
	 */
	@ApiLog("保存平台参数")
	@RequiresPermissions(value={"ops:system:base:add","ops:system:base:edit"},logical=Logical.OR)
	@PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson save( @RequestBody   Customer customer) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(customer);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		Customer cust = customerService.get("1");
		if(!cust.getRobname().equals(customer.getRobname())){
			Member member = memberService.get(ImUtils.robot_id);
			member.setNickname(customer.getRobname());
			memberService.save(member);
			ImUtils.editUser(member.getId(),member.getNickname(),member.getIcon());
		}
		//新增或编辑表单保存
		customerService.save(customer);//保存
//		String key = "system:whiteip";
//		redisUtils.set(key,customer.getIpwhite());
		return AjaxJson.success("保存平台参数成功");
	}

	/**
	 * 修改平台参数
	 */
	@ApiLog("修改平台参数")
	@RequiresPermissions(value={"ops:system:base:add","ops:system:base:edit"},logical=Logical.OR)
	@PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson update(    Customer customer) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(customer);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		Customer cust = customerService.get("1");
		if(!cust.getRobname().equals(customer.getRobname())){
			Member member = memberService.get(ImUtils.robot_id);
			member.setNickname(customer.getRobname());
			memberService.update(member);
			ImUtils.editUser(member.getId(),member.getNickname(),member.getIcon());
		}
		//新增或编辑表单保存
		customerService.update(customer);//保存
//		String key = "system:whiteip";
//		redisUtils.set(key,customer.getIpwhite());
		return AjaxJson.success("修改平台参数成功");
	}


	/**
	 * 批量删除平台参数
	 */
	@ApiLog("删除平台参数")
//	@RequiresPermissions("customer:customer:del")
	@DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public AjaxJson delete(    String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			customerService.delete(new Customer(id));
		}
		return AjaxJson.success("删除平台参数成功");
	}

	/**
	 * 导出excel文件
	 */
	@ApiLog("导出平台参数")
	@RequiresPermissions("customer:customer:export")
    @GetMapping("export")
    public AjaxJson exportFile(Customer customer, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "平台参数"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Customer> page = customerService.findPage(new Page<Customer>(request, response, -1), customer);
    		new ExportExcel("平台参数", Customer.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出平台参数记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据
	 */
	@ApiLog("导入平台参数")
	@RequiresPermissions("customer:customer:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Customer> list = ei.getDataList(Customer.class);
			for (Customer customer : list){
				try{
					customerService.save(customer);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条平台参数记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条平台参数记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入平台参数失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入平台参数数据模板
	 */
	@ApiLog("下载平台参数模板")
	@RequiresPermissions("customer:customer:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "平台参数数据导入模板.xlsx";
    		List<Customer> list = Lists.newArrayList();
    		new ExportExcel("平台参数数据", Customer.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}