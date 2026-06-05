package com.seekweb4.chat.asset.service;

import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.asset.vo.request.WithdrawAddRequestVO;
import com.seekweb4.chat.asset.vo.response.TransactionPageResponseVO;
import com.seekweb4.chat.asset.vo.response.WithdrawAddResponseVO;
import com.seekweb4.chat.asset.vo.response.WithdrawApplyResponseDTO;
import com.seekweb4.chat.asset.vo.response.WithdrawDetailResponseVO;
import com.seekweb4.chat.core.persistence.Page;

/**
 * @author coderpwh
 */
public interface AssetWithdrawService {


    /***
     * 提现列表
     * @param req
     * @param id
     * @return
     */
    Page<WithdrawApplyResponseDTO> getWithdrawPage(ReqJson req, String id);


    /***
     * 提现详情
     *
     * @param id
     * @return
     */
    WithdrawDetailResponseVO getWithdrawDetail(Long id);


    /***
     * 提现申请
     * @param requestVO
     * @param userId
     * @return
     */
    WithdrawAddResponseVO addWithdraw(WithdrawAddRequestVO requestVO, String userId);


    /***
     * 提现审核
     * @param id
     * @param status
     * @param remark
     * @param updateBy
     * @return
     */
    String  auditWithdraw(Long id, Integer status, String remark, String updateBy);

}
