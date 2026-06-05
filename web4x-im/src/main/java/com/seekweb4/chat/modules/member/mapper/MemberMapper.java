package com.seekweb4.chat.modules.member.mapper;

import com.seekweb4.chat.modules.member.entity.MemberTongji;
import org.apache.ibatis.annotations.Param;
import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.member.entity.Member;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 移动端用户MAPPER接口
 * @author lixinapp
 * @version 2024-09-20
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    public List<MemberTongji> cityList();
    public List<MemberTongji> eqList();

    /**
     * 根据接收地址查询用户
     * @param receivingAddress
     * @return
     */
    Member selectByReceivingAddress(@Param("receivingAddress")String receivingAddress);

     /**
     * 根据用户id更新余额
     * @param amount
     * @param id
     * @return
     */
    int updateBalanceByUserId(@Param("amount") BigDecimal amount, @Param("id") String id);

    int updateSubtractionBalanceByUserId(@Param("amount") BigDecimal amount, @Param("id") String id);



    int updateTwoFactorCode(@Param("id") String id, @Param("twoFactorCode") String twoFactorCode, @Param("twoFactorTime") Date twoFactorTime);


    /**
     * 更新用户信息
     * @param member
     * @return
     */
    int updateMember(Member member);

    /***
     * 获取客服信息
     * @return
     */
    Member getCustomer();


    Member selectBasicById(String id);

    /**
     * 查询已启用谷歌验证的用户列表（分页）
     * @param member 查询条件
     * @return 用户列表
     */
    List<Member> findListWithTwoFactor(Member member);

    /**
     * 重置用户的谷歌验证码
     * @param id 用户ID
     * @return 更新行数
     */
    int resetTwoFactorCode(@Param("id") String id);

    /**
     * 查询已设置密保问题的用户列表（分页）
     * @param member 查询条件
     * @return 用户列表
     */
    List<Member> findListWithMibao(Member member);

    /**
     * 修改用户的密保问题
     * @param id 用户ID
     * @param mbid 密保问题ID
     * @param mbname 密保问题名称
     * @param mbda 密保答案
     * @return 更新行数
     */
    int updateMibao(@Param("id") String id, @Param("mbid") String mbid, @Param("mbname") String mbname, @Param("mbda") String mbda);

    /**
     * 重置用户的密保问题
     * @param id 用户ID
     * @return 更新行数
     */
    int resetMibao(@Param("id") String id);

    Member getMibaoId(@Param("id") String id);
}
