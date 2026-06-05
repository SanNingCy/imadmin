package com.seekweb4.chat.asset.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekweb4.chat.api.config.AssetConfig;
import com.seekweb4.chat.api.config.WithdrawalConfig;
import com.seekweb4.chat.api.constant.WXRequestConstant;
import com.seekweb4.chat.api.constant.WithdrawalRequestConstant;
import com.seekweb4.chat.api.utils.HttpUtil;
import com.seekweb4.chat.api.utils.sign.SignUtil;
import com.seekweb4.chat.asset.config.BusinessException;
import com.seekweb4.chat.asset.dto.request.TransactionWXRequestDTO;
import com.seekweb4.chat.asset.dto.request.WithdrawalRequestDTO;
import com.seekweb4.chat.asset.dto.response.*;
import com.seekweb4.chat.asset.service.AssetUserService;
import com.seekweb4.chat.asset.util.TransactionIdGenerator;
import com.seekweb4.chat.asset.vo.request.TransactionRequestVO;
import com.seekweb4.chat.asset.vo.request.UserInfoRequestVO;
import com.seekweb4.chat.asset.vo.response.UserInfoResponseVO;
import com.seekweb4.chat.dto.response.HttpResponseDTO;
import com.seekweb4.chat.enumUtil.PaymentStatusEnum;
import com.seekweb4.chat.enumUtil.PaymentTypeEnum;
import com.seekweb4.chat.enumUtil.WithdrawStatusEnum;
import com.seekweb4.chat.modules.PaymentRecord.entity.PaymentRecord;
import com.seekweb4.chat.modules.PaymentRecord.mapper.PaymentRecordMapper;
import com.seekweb4.chat.modules.PaymentTransaction.entity.PaymentTransaction;
import com.seekweb4.chat.modules.PaymentTransaction.mapper.PaymentTransactionMapper;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawApply;
import com.seekweb4.chat.modules.WithdrawApply.mapper.WithdrawApplyMapper;
import com.seekweb4.chat.modules.buttonConfig.entity.ButtonConfig;
import com.seekweb4.chat.modules.buttonConfig.mapper.ButtonConfigMapper;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.paymentRateConfig.entity.PaymentRateConfig;
import com.seekweb4.chat.modules.paymentRateConfig.mapper.PaymentRateConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author coderpwh
 */
@Slf4j
@Service
public class AssetUserServiceImpl implements AssetUserService {


    TransactionIdGenerator generator = TransactionIdGenerator.getInstance(1);


    @Resource
    private MemberService memberService;


    @Resource
    private PaymentRecordMapper paymentRecordMapper;

    @Resource
    private AssetConfig assetConfig;

    @Resource
    private WithdrawalConfig withdrawalConfig;


    @Resource
    private WXRequestConstant wxRequestConstant;

    @Resource
    private WithdrawalRequestConstant withdrawalRequestConstant;


    @Resource
    private PaymentTransactionMapper paymentTransactionMapper;

    @Resource
    private WithdrawApplyMapper withdrawApplyMapper;


    @Resource
    private PaymentRateConfigMapper paymentRateConfigMapper;

    @Resource
    private ButtonConfigMapper buttonConfigMapper;

    private static final String BUTTON_KEY_WITHDRAW = "withdraw";

    @Override
    public UserInfoResponseVO getUserInfo(UserInfoRequestVO userInfoRequestVO) {
        UserInfoResponseVO userInfoResponseVO = new UserInfoResponseVO();
        log.info("资产-获取用户信息,入参为:{}", JSON.toJSON(userInfoRequestVO));
        String receivingAddress = userInfoRequestVO.getReceivingAddress();
        Member member = memberService.selectByReceivingAddress(receivingAddress);
        if (member != null) {
            log.info("资产-获取用户信息,用户信息为:{}", JSON.toJSON(member));
            userInfoResponseVO.setUserName(member.getNickname());
            userInfoResponseVO.setUserId(member.getId());
            userInfoResponseVO.setReceivingAddress(member.getReceivingAddress());
        }
        log.info("资产-获取用户信息,返回结果为:{}", JSON.toJSON(userInfoResponseVO));
        return userInfoResponseVO;
    }


    /***
     * 交易(入金IM)
     * @param transactionRequestVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean transaction(TransactionRequestVO transactionRequestVO) {
        log.info("资产-交易(入金IM),入参为:{}", JSON.toJSON(transactionRequestVO));
        Member member = memberService.selectByReceivingAddress(transactionRequestVO.getReceivingAddress());
        if (Objects.isNull(member)) {
            throw new BusinessException("用户不存在");
        }
        String transactionNumber = generator.generate();
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setUserId(member.getId());
        paymentRecord.setTransactionNumber(transactionNumber);
        paymentRecord.setPartnerNumber(transactionRequestVO.getPartnerNumber());
        paymentRecord.setAmount(new BigDecimal(transactionRequestVO.getAmount()));
        paymentRecord.setReceivingAddress(transactionRequestVO.getReceivingAddress());
        int count = paymentRecordMapper.insert(paymentRecord);
        log.info("资产-交易(入金IM),结果为:{}", count);
        if (count > 0) {
            BigDecimal amount = new BigDecimal(transactionRequestVO.getAmount());
            member.setBalance(member.getBalance().add(amount));
            memberService.updateBalanceByUserId(amount, member.getId());
            return true;
        }
        return false;
    }


    /***
     * 入金WX
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transactionByWx(TransactionWXRequestDTO request) {
        log.info("资产-入金WX,入参为:{}", JSON.toJSON(request));
        // 通过收款地址查询用户信息
        UserInfoByAddressResponseDTO userInfoByAddressResponseDTO = getUserInfoByAddress(request.getReceivingAddress());
        if (Objects.isNull(userInfoByAddressResponseDTO)) {
            log.error("资产-入金WX,用户不存在");
            throw new BusinessException("用户不存在");
        }
        Member member = memberService.get(request.getImUserId());
        if (Objects.isNull(member)) {
            log.error("资产-入金WX,用户不存在");
            throw new BusinessException("用户不存在");
        }
        if (member.getBalance().compareTo(new BigDecimal(request.getAmount())) < 0) {
            log.error("资产-入金WX,用户余额不足");
            throw new BusinessException("余额不足");
        }

        String transactionNumber = generator.generate();
        request.setPaymentAddress(member.getPaymentAddress());
        request.setReceivingAddress(member.getReceivingAddress());
        request.setImUserId(member.getId());
        request.setSerialNumber(transactionNumber);

        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("appId", "im");
            params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
            params.put("timestamp", System.currentTimeMillis() / 1000);
            params.put("imUserId", request.getImUserId());
            params.put("paymentAddress", request.getPaymentAddress());
            params.put("receivingAddress", request.getReceivingAddress());
            params.put("amount", request.getAmount());
            params.put("serialNumber", transactionNumber);

            String signContent = SignUtil.buildSignContent(params);
            log.info("资产入金WX,私钥为:{}", assetConfig.getPrivateKey());

            String sign = SignUtil.sign(signContent, assetConfig.getPrivateKey());
            log.info("资产入金WX签名sign为:{}", sign);

            params.put("sign", sign);
            String json = new ObjectMapper().writeValueAsString(params);
            log.info("资产入金WX参数为:{}", json);

            log.info("资产入金WX,对应的url为:{}", wxRequestConstant.getDepositWxUrl());
            String response = HttpUtil.postJson(wxRequestConstant.getDepositWxUrl(), json);
            log.info("资产入金WX响应结果为:{}", response);

            HttpResponseDTO httpResponseDTO = new ObjectMapper().readValue(response, HttpResponseDTO.class);
            if (httpResponseDTO.getCode() != 0) {
                throw new BusinessException("入金WX失败");
            }

            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setUserId(member.getId());
            paymentTransaction.setTransactionNumber(transactionNumber);
            paymentTransaction.setPaymentStatus(PaymentStatusEnum.SUCCESS.getCode());
            paymentTransaction.setAmount(new BigDecimal(request.getAmount()));
            paymentTransaction.setPaymentAddress(request.getPaymentAddress());
            paymentTransaction.setReceivingAddress(request.getReceivingAddress());
            paymentTransaction.setCreateTime(new Date());
            paymentTransaction.setUpdateTime(new Date());
            int count = paymentTransactionMapper.insert(paymentTransaction);
            if (count > 0) {
                BigDecimal amount = new BigDecimal(request.getAmount());
                member.setBalance(member.getBalance().subtract(amount));
                memberService.updateBalanceByUserId(amount, member.getId());
            }
        } catch (Exception e) {
            log.error("资产-入金WX,异常:{}", e.getMessage());
        }

    }


    /***
     * 通过地址
     * @param address
     * @return
     */
    @Override
    public UserInfoByAddressResponseDTO getUserInfoByAddress(String address) {
        UserInfoByAddressResponseDTO info = new UserInfoByAddressResponseDTO();
        try {
            log.info("资产-通过地址获取用户信息,入参为:{}", address);
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("appId", "im");
            params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
            params.put("timestamp", System.currentTimeMillis() / 1000);
            params.put("address", address);

            String signContent = SignUtil.buildSignContent(params);
            String sign = SignUtil.sign(signContent, assetConfig.getPrivateKey());
            params.put("sign", sign);
            log.info("资产-通过地址获取用户信息签名,sign为:{}", sign);
            String json = new ObjectMapper().writeValueAsString(params);
            log.info("资产-通过地址获取用户信息,请求url:{}", wxRequestConstant.getUserInfoUrl());
            log.info("资产-通过地址获取用户信息参数为:{}", json);
            String response = HttpUtil.postJson(wxRequestConstant.getUserInfoUrl(), json);
            log.info("资产-通过地址获取用户信息,响应结果为:{}", response);
            HttpResponseDTO httpResponseDTO = new ObjectMapper().readValue(response, HttpResponseDTO.class);
            if (httpResponseDTO.getCode() != 0) {
                throw new BusinessException("通过地址获取用户信息异常");
            }
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(httpResponseDTO.getData()));

            info.setUserName(jsonObject.getString("userName"));
            info.setUid(jsonObject.getString("uid"));
            info.setUserId(jsonObject.getString("userId"));
            info.setIcon(jsonObject.getString("icon"));
            return info;

        } catch (Exception e) {
            log.error("资产-通过地址获取用户信息,异常:{}", e.getMessage());
        }
        return null;

    }


    /***
     * 提现
     * @param request
     * @return
     */
    @Override
    public Long withdrawal(WithdrawalRequestDTO request) {
        log.info("资产-提现,入参为:{}", JSON.toJSON(request));
        // 0. 检查提现按钮状态
        ButtonConfig buttonConfig = buttonConfigMapper.selectByButtonKey(BUTTON_KEY_WITHDRAW);
        if (buttonConfig == null || buttonConfig.getButtonStatus() == null || buttonConfig.getButtonStatus() == 0) {
            throw new BusinessException("提现功能暂时关闭，请稍后再试");
        }
        try {
            String transactionNumber = generator.generate();

            // 获取费率
            WithdrawAmountResponseDTO withdrawAmountResponseDTO = getRateAmount(new BigDecimal(request.getAmount()));
            WithdrawApply withdrawApply = new WithdrawApply();
            withdrawApply.setUserId(request.getUserId());
            withdrawApply.setTransactionNumber(transactionNumber);
            withdrawApply.setCoinId(request.getCoinId());
            withdrawApply.setActualAmount(withdrawAmountResponseDTO.getActualAmount());
            withdrawApply.setAmount(withdrawAmountResponseDTO.getAmount());
            withdrawApply.setRateAmount(withdrawAmountResponseDTO.getRateAmount());
            withdrawApply.setReceivingAddress(request.getToAddress());
            withdrawApply.setStatus(WithdrawStatusEnum.INITIATED.getCode());
            withdrawApply.setCreateTime(new Date());
            withdrawApply.setUpdateTime(new Date());
            withdrawApply.setCreateBy(request.getUserId());
            withdrawApply.setUpdateBy(request.getUserId());
            int count = withdrawApplyMapper.insert(withdrawApply);
            log.info("资产-提现,插入提现申请记录,结果为:{}", count);


            Map<String, Object> params = new LinkedHashMap<>();
            params.put("appId", "im");
            params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
            params.put("timestamp", System.currentTimeMillis() / 1000);
            params.put("userId", request.getUserId());
            // 对外接口 amount 为实际到账金额
            params.put("amount", withdrawAmountResponseDTO.getActualAmount().toString());
            params.put("toAddress", request.getToAddress());
            params.put("coinId", request.getCoinId());
            params.put("originType", request.getOriginType());
            // 对外接口订单号字段为 orderNumber
            params.put("orderNumber", transactionNumber);


            String signContent = SignUtil.buildSignContent(params);
            String sign = SignUtil.sign(signContent, withdrawalConfig.getPrivateKey());
            log.info("资产-提现签名,sign为:{}", sign);

            params.put("sign", sign);

            String json = new ObjectMapper().writeValueAsString(params);
            log.info("资产-提现参数为:{}", json);

            log.info("资产-提现,对应的url为:{}", withdrawalRequestConstant.getWithdrawUrl());
            String response = HttpUtil.postJson(withdrawalRequestConstant.getWithdrawUrl(), json);
            log.info("资产-提现,响应结果为:{}", response);

            HttpResponseDTO httpResponseDTO = new ObjectMapper().readValue(response, HttpResponseDTO.class);
            if (httpResponseDTO.getCode() != 0) {
                throw new BusinessException("提现失败");
            }

            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(httpResponseDTO.getData()));
            Long withdrawalId = jsonObject.getLong("withdrawalId");
            String serialNumber = jsonObject.getString("serialNumber");

            WithdrawApply apply = withdrawApplyMapper.selectByTransactionNumber(serialNumber);
            if (apply != null) {
                apply.setWithdrawalId(withdrawalId);
                withdrawApplyMapper.updateByPrimaryKeySelective(apply);
            }
            return withdrawalId;
        } catch (Exception e) {
            log.error("资产-提现,异常:{}", e.getMessage());
        }
        return null;
    }


    /***
     * 查询提现结果
     * @param withdrawalId
     * @return
     */
    @Override
    public WithdrawalIdResponseDTO getWithdrawalResult(Long withdrawalId) {
        log.info("资产-查询提现结果,入参为:{}", withdrawalId);
        try {
            WithdrawApply withdrawApply = withdrawApplyMapper.selectByWithdrawalId(withdrawalId);
            if (withdrawApply == null) {
                throw new BusinessException("提现记录不存在");
            }
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("appId", "im");
            params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
            params.put("timestamp", System.currentTimeMillis() / 1000);
            params.put("id", withdrawalId);

            String signContent = SignUtil.buildSignContent(params);
            String sign = SignUtil.sign(signContent, withdrawalConfig.getPrivateKey());
            params.put("sign", sign);
            log.info("资产-查询提现结果签名,sign为:{}", sign);
            String json = new ObjectMapper().writeValueAsString(params);
            log.info("资产-查询提现结果参数为:{}", json);
            String response = HttpUtil.postJson(withdrawalRequestConstant.getWithdrawResultUrl(), json);
            log.info("资产-查询提现结果,响应结果为:{}", response);


            HttpResponseDTO httpResponseDTO = new ObjectMapper().readValue(response, HttpResponseDTO.class);
            if (httpResponseDTO.getCode() != 0) {
                throw new BusinessException("查询提现失败");
            }

            WithdrawalResultResponseDTO withdrawalResultResponseDTO = JSON.parseObject(JSON.toJSONString(httpResponseDTO.getData()), WithdrawalResultResponseDTO.class);
            withdrawApply.setStatus(withdrawalResultResponseDTO.getStatus());
            withdrawApply.setUpdateTime(new Date());
            withdrawApplyMapper.updateByPrimaryKeySelective(withdrawApply);

        } catch (Exception e) {
            log.error("资产-查询提现结果,异常:{}", e.getMessage());
        }

        return null;
    }


    /***
     * 计算费率
     * @param amount
     * @return
     */
    public WithdrawAmountResponseDTO getRateAmount(BigDecimal amount) {
        WithdrawAmountResponseDTO response = new WithdrawAmountResponseDTO();
        PaymentRateConfig paymentRateConfig = paymentRateConfigMapper.selectByPaymentType(PaymentTypeEnum.WITHDRAW.getCode());
        if (paymentRateConfig != null) {
            response.setAmount(amount);
            BigDecimal rateAmount = amount.multiply(paymentRateConfig.getRate()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal actualAmount = amount.subtract(rateAmount);
            response.setActualAmount(actualAmount);
            response.setRateAmount(rateAmount);
        }
        return response;
    }


}
