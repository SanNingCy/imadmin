package com.seekweb4.chat.asset.service;

import com.seekweb4.chat.asset.dto.request.TransactionWXRequestDTO;
import com.seekweb4.chat.asset.dto.request.WithdrawalRequestDTO;
import com.seekweb4.chat.asset.dto.response.UserInfoByAddressResponseDTO;
import com.seekweb4.chat.asset.dto.response.WithdrawalIdResponseDTO;
import com.seekweb4.chat.asset.vo.request.TransactionRequestVO;
import com.seekweb4.chat.asset.vo.request.UserInfoRequestVO;
import com.seekweb4.chat.asset.vo.response.UserInfoResponseVO;

/**
 * @author coderpwh
 */
public interface AssetUserService {

    /**
     * 获取用户信息
     *
     * @param userInfoRequestVO
     */
    UserInfoResponseVO getUserInfo(UserInfoRequestVO userInfoRequestVO);


    /**
     * 交易
     *
     * @param transactionRequestVO
     */
    Boolean transaction(TransactionRequestVO transactionRequestVO);


    /***
     * 入金WX
     * @param request
     */
    public void transactionByWx(TransactionWXRequestDTO request);


    /***
     * 通过wx地址查询用户信息
     * @param address
     * @return
     */
    public UserInfoByAddressResponseDTO getUserInfoByAddress(String address);


    /***
     * 提现
     * @return
     */
    public Long withdrawal(WithdrawalRequestDTO request);


    public WithdrawalIdResponseDTO getWithdrawalResult(Long withdrawalId);


}
