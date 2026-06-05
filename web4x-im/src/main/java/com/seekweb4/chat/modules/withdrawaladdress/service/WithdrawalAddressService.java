package com.seekweb4.chat.modules.withdrawaladdress.service;

import com.seekweb4.chat.modules.withdrawaladdress.entity.WithdrawalAddress;
import com.seekweb4.chat.modules.withdrawaladdress.mapper.WithdrawalAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class WithdrawalAddressService {

    @Autowired
    private WithdrawalAddressMapper withdrawalAddressMapper;

    public WithdrawalAddress get(Integer id) {
        return withdrawalAddressMapper.get(id);
    }

    public List<WithdrawalAddress> findList(WithdrawalAddress query) {
        return withdrawalAddressMapper.findList(query);
    }

    public List<WithdrawalAddress> findByUser(String userId, Integer addressType) {
        return withdrawalAddressMapper.findByUser(userId, addressType);
    }

    @Transactional(readOnly = false)
    public void addAddress(WithdrawalAddress addr) {
        if (addr.getIsDefault() == null) {
            addr.setIsDefault(0);
        }
        if (addr.getType() == null) {
            addr.setType(1); // 默认正常
        }
        if (addr.getFlag() == null) {
            addr.setFlag(1); // 默认未删除
        }
        // 如果设为默认，先清空该用户该类型的默认
        if (addr.getIsDefault() != null && addr.getIsDefault() == 1) {
            withdrawalAddressMapper.clearDefaultForUser(addr.getUserId(), addr.getAddressType());
        }
        withdrawalAddressMapper.insert(addr);
    }

    @Transactional(readOnly = false)
    public void updateAddress(WithdrawalAddress addr) {
        withdrawalAddressMapper.update(addr);
    }

    @Transactional(readOnly = false)
    public void setDefault(Integer id) {
        WithdrawalAddress exist = withdrawalAddressMapper.get(id);
        if (exist == null) {
            return;
        }
        withdrawalAddressMapper.clearDefaultForUser(exist.getUserId(), exist.getAddressType());
        withdrawalAddressMapper.setDefault(id);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        withdrawalAddressMapper.softDelete(id);
    }

    /**
     * 将地址标记为黑名单：type = 3
     */
    @Transactional(readOnly = false)
    public void blacklist(Integer id) {
        WithdrawalAddress addr = new WithdrawalAddress();
        addr.setId(id);
        addr.setType(3);
        withdrawalAddressMapper.update(addr);
    }
}

