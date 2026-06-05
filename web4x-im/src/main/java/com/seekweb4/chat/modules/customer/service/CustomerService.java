package com.seekweb4.chat.modules.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.customer.entity.Customer;
import com.seekweb4.chat.modules.customer.mapper.CustomerMapper;

/**
 * 平台参数Service
 * @author lixinapp
 * @version 2024-10-11
 */
@Service
@Transactional(readOnly = true)
public class CustomerService extends CrudService<CustomerMapper, Customer> {

	public Customer get(String id) {
		return super.get(id);
	}
	
	public List<Customer> findList(Customer customer) {
		return super.findList(customer);
	}
	
	public Page<Customer> findPage(Page<Customer> page, Customer customer) {
		return super.findPage(page, customer);
	}
	
	@Transactional(readOnly = false)
	public void save(Customer customer) {
		super.save(customer);
	}
	@Transactional(readOnly = false)
	public void update(Customer customer) {
		super.save(customer);
	}

	@Transactional(readOnly = false)
	public void delete(Customer customer) {
		super.delete(customer);
	}
	
}