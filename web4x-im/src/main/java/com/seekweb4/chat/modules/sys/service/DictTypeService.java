package com.seekweb4.chat.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.sys.entity.DictType;
import com.seekweb4.chat.modules.sys.entity.DictValue;
import com.seekweb4.chat.modules.sys.mapper.DictTypeMapper;
import com.seekweb4.chat.modules.sys.mapper.DictValueMapper;
import com.seekweb4.chat.modules.sys.utils.DictUtils;

/**
 * 数据字典Service
 * @author lgf
 * @version 2017-01-16
 */
@Service
@Transactional(readOnly = true)
public class DictTypeService extends CrudService<DictTypeMapper, DictType> {

	@Autowired
	private DictValueMapper dictValueMapper;

	public DictType get(String id) {
		DictType dictType = super.get(id);
		dictType.setDictValueList(dictValueMapper.findList(new DictValue(dictType)));
		return dictType;
	}

	public DictValue getDictValue(String id) {
		return dictValueMapper.get(id);
	}

	public List<DictType> findList(DictType dictType) {
		return super.findList(dictType);
	}

	public Page<DictType> findPage(Page<DictType> page, DictType dictType) {
		return super.findPage(page, dictType);
	}

	public List<DictType> getDict () {
		return mapper.getDict();
	}

	@Transactional(readOnly = false)
	public void save(DictType dictType) {
		super.save(dictType);
		scheduleReloadDictCacheAfterCommit();
	}

	@Transactional(readOnly = false)
	public void saveDictValue(DictValue dictValue) {
		if (StringUtils.isBlank(dictValue.getId())){
			dictValue.preInsert();
			dictValueMapper.insert(dictValue);
		}else{
			dictValue.preUpdate();
			dictValueMapper.update(dictValue);
		}
		scheduleReloadDictCacheAfterCommit();
	}

	@Transactional(readOnly = false)
	public void deleteDictValue(DictValue dictValue) {
		dictValueMapper.delete(dictValue);
		scheduleReloadDictCacheAfterCommit();
	}

	@Transactional(readOnly = false)
	public void batchDeleteDictValue(String[] ids) {
		dictValueMapper.batchDelete(ids);
		scheduleReloadDictCacheAfterCommit();
	}

	@Transactional(readOnly = false)
	public void batchDelete(String[] ids) {
		super.batchDelete(ids);
		scheduleReloadDictCacheAfterCommit();
	}

	@Transactional(readOnly = false)
	public void delete(DictType dictType) {
		super.delete(dictType);
		dictValueMapper.delete(new DictValue(dictType));
		scheduleReloadDictCacheAfterCommit();
	}

	private void scheduleReloadDictCacheAfterCommit() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					DictUtils.reloadCache();
				}
			});
			return;
		}
		DictUtils.reloadCache();
	}

}
