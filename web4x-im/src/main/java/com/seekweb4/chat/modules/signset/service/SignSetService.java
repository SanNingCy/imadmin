package com.seekweb4.chat.modules.signset.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.signset.entity.SignSet;
import com.seekweb4.chat.modules.signset.mapper.SignSetMapper;
import com.seekweb4.chat.modules.signset.entity.SignSetItem;
import com.seekweb4.chat.modules.signset.mapper.SignSetItemMapper;

/**
 * 签到奖励配置Service
 * @author lixinapp
 * @version 2024-11-26
 */
@Service
@Transactional(readOnly = true)
public class SignSetService extends CrudService<SignSetMapper, SignSet> {

	@Autowired
	private SignSetItemMapper signSetItemMapper;

	public SignSet get(String id) {
		SignSet signSet = super.get(id);
		if (signSet != null) {
    		signSet.setSignSetItemList(signSetItemMapper.findList(new SignSetItem(signSet)));
		}
		return signSet;
	}

	public List<SignSet> findList(SignSet signSet) {
		return super.findList(signSet);
	}

	public Page<SignSet> findPage(Page<SignSet> page, SignSet signSet) {
		return super.findPage(page, signSet);
	}

	@Transactional(readOnly = false)
	public void save(SignSet signSet) {
		super.save(signSet);
		for (SignSetItem signSetItem : signSet.getSignSetItemList()){
			if (signSetItem.getId() == null){
				continue;
			}
			if (SignSetItem.DEL_FLAG_NORMAL.equals(signSetItem.getDelFlag())){
				if (StringUtils.isBlank(signSetItem.getId())){
					signSetItem.setSid(signSet);
					signSetItem.preInsert();
					signSetItemMapper.insert(signSetItem);
				}else{
					signSetItem.preUpdate();
					signSetItemMapper.update(signSetItem);
				}
			}else{
				signSetItemMapper.delete(signSetItem);
			}
		}
	}

	@Transactional(readOnly = false)
	public void delete(SignSet signSet) {
		super.delete(signSet);
		signSetItemMapper.delete(new SignSetItem(signSet));
	}

}