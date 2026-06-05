package com.seekweb4.chat.modules.withdrawaladdress.mapper;

import com.seekweb4.chat.modules.withdrawaladdress.entity.WithdrawalAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WithdrawalAddressMapper {

    WithdrawalAddress get(@Param("id") Integer id);

    List<WithdrawalAddress> findList(WithdrawalAddress query);

    int insert(WithdrawalAddress entity);

    int update(WithdrawalAddress entity);

    /**
     * 软删除：flag = 0
     */
    int softDelete(@Param("id") Integer id);

    /**
     * 某个用户、某种地址类型全部取消默认
     */
    int clearDefaultForUser(@Param("userId") String userId,
                            @Param("addressType") Integer addressType);

    /**
     * 将指定记录设为默认 is_default = 1
     */
    int setDefault(@Param("id") Integer id);

    /**
     * 查询用户有效地址（未删除 && 正常状态）
     */
    List<WithdrawalAddress> findByUser(@Param("userId") String userId,
                                       @Param("addressType") Integer addressType);
}
