package com.seekweb4.chat.asset.service;

import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.api.utils.PageResult;
import com.seekweb4.chat.asset.vo.request.TransactionAddRequestVO;
import com.seekweb4.chat.asset.vo.request.TransactionRateRequestVO;
import com.seekweb4.chat.asset.vo.response.*;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.member.entity.Member;

/**
 * @author coderpwh
 */
public interface AssetTransactionService {


    /***
     * 初始化资产交易信息
     * @param
     * @return
     */
    TransactionInitializeResponseVO transactionInitialize(Member member);

    /***
     * 获取资产交易信息
     */
    TransactionRateResponseVO getRateInfo(TransactionRateRequestVO request);


    /****
     *  获取资产交易人信息
     */
    TransactionPersonInfoResponseVO getTransactionPersonInfo(String receivingAddress);

    /***
     * 添加资产交易信息
     * @param requestVO
     * @param id
     * @return
     */
    TransactionAddResponseVO addTransaction(TransactionAddRequestVO requestVO, String id);

     /***
     * 获取资产交易信息
     * @param req
     * @return
     */
     Page<TransactionPageResponseVO> getTransactionPage(ReqJson req, String userId);

    /***
     * 获取资产交易详情
     * @param id
     * @return
     */
    TransactionDetailResponseVO getTransactionDetail(Long id);


}
