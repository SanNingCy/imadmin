package com.seekweb4.chat.asset.service.impl;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekweb4.chat.api.config.AssetConfig;
import com.seekweb4.chat.api.config.WithdrawalConfig;
import com.seekweb4.chat.api.constant.WXRequestConstant;
import com.seekweb4.chat.api.constant.WithdrawalRequestConstant;
import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.api.utils.HttpUtil;
import com.seekweb4.chat.api.utils.sign.SignUtil;
import com.seekweb4.chat.asset.config.BusinessException;
import com.seekweb4.chat.asset.constant.RedisKeyConstant;
import com.seekweb4.chat.asset.service.AssetTransactionService;
import com.seekweb4.chat.asset.service.AssetWithdrawService;
import com.seekweb4.chat.asset.util.TransactionIdGenerator;
import com.seekweb4.chat.asset.vo.request.TransactionRateRequestVO;
import com.seekweb4.chat.asset.vo.request.WithdrawAddRequestVO;
import com.seekweb4.chat.asset.vo.response.*;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.dto.response.HttpResponseDTO;
import com.seekweb4.chat.enumUtil.CoinTypeEnum;
import com.seekweb4.chat.enumUtil.PaymentTypeEnum;
import com.seekweb4.chat.enumUtil.WithdrawStatusEnum;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawApply;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawPageRequestDTO;
import com.seekweb4.chat.modules.WithdrawApply.mapper.WithdrawApplyMapper;
import com.seekweb4.chat.modules.buttonConfig.entity.ButtonConfig;
import com.seekweb4.chat.modules.buttonConfig.mapper.ButtonConfigMapper;
import com.seekweb4.chat.modules.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author coderpwh
 */
@Slf4j
@Service
public class AssetWithdrawServiceImpl implements AssetWithdrawService {
    TransactionIdGenerator generator = TransactionIdGenerator.getInstance(1);


    private static final int LOCK_EXPIRE_SECONDS = 30;
    private static final int IDEMPOTENT_EXPIRE_SECONDS = 3;
    @Resource
    private WithdrawApplyMapper withdrawApplyMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private MemberService memberService;

    @Resource
    private AssetTransactionService assetTransactionService;

    @Resource
    private AssetConfig assetConfig;

    @Resource
    private WithdrawalConfig withdrawalConfig;


    @Resource
    private WXRequestConstant wxRequestConstant;

    @Resource
    private WithdrawalRequestConstant withdrawalRequestConstant;

    @Resource
    private ButtonConfigMapper buttonConfigMapper;

    /** 提现按钮key */
    private static final String BUTTON_KEY_WITHDRAW = "withdraw";

    /***
     *  分页查询提现记录
     * @param req
     * @param userId
     * @return
     */
    @Override
    public Page<WithdrawApplyResponseDTO> getWithdrawPage(ReqJson req, String userId) {
        WithdrawPageRequestDTO pageRequestDTO = new WithdrawPageRequestDTO();
        pageRequestDTO.setUserId(userId);
        pageRequestDTO.setPageNum(req.getPageNo());
        pageRequestDTO.setPageSize(req.getPageSize());

        List<WithdrawApply> data = withdrawApplyMapper.getWithdrawPage(pageRequestDTO);
        Long count = withdrawApplyMapper.getWithdrawPageCount(pageRequestDTO);

        Page<WithdrawApplyResponseDTO> page = new Page<>(req.getPageNo(), req.getPageSize(), count);
        if (data != null && data.size() > 0) {
            List<WithdrawApplyResponseDTO> list = new ArrayList<>();
            for (WithdrawApply withdrawApply : data) {
                WithdrawApplyResponseDTO withdrawApplyResponseDTO = new WithdrawApplyResponseDTO();
                withdrawApplyResponseDTO.setId(withdrawApply.getId());
                withdrawApplyResponseDTO.setTransactionNumber(withdrawApply.getTransactionNumber());
                withdrawApplyResponseDTO.setCoinId(withdrawApply.getCoinId());
                withdrawApplyResponseDTO.setActualAmount(withdrawApply.getActualAmount());
                withdrawApplyResponseDTO.setAmount(withdrawApply.getAmount());
                withdrawApplyResponseDTO.setRateAmount(withdrawApply.getRateAmount());
                withdrawApplyResponseDTO.setReceivingAddress(withdrawApply.getReceivingAddress());
                withdrawApplyResponseDTO.setWithdrawalHash(withdrawApply.getWithdrawalHash());
                withdrawApplyResponseDTO.setStatus(withdrawApply.getStatus());
                withdrawApplyResponseDTO.setCreateTime(withdrawApply.getCreateTime());
                withdrawApplyResponseDTO.setUpdateTime(withdrawApply.getUpdateTime());
                list.add(withdrawApplyResponseDTO);
            }
            page.setList(list);
        }
        page.setCount(count);
        return page;
    }


    /**
     * 获取提现详情
     *
     * @param id
     * @return
     */
    @Override
    public WithdrawDetailResponseVO getWithdrawDetail(Long id) {
        WithdrawDetailResponseVO withdrawDetailResponseVO = new WithdrawDetailResponseVO();
        WithdrawApply withdrawApply = withdrawApplyMapper.selectByPrimaryKey(id);
        if (withdrawApply != null) {
            withdrawDetailResponseVO.setUserId(withdrawApply.getUserId());
            withdrawDetailResponseVO.setTransactionNumber(withdrawApply.getTransactionNumber());
            withdrawDetailResponseVO.setCoinId(withdrawApply.getCoinId());
            withdrawDetailResponseVO.setActualAmount(withdrawApply.getActualAmount());
            withdrawDetailResponseVO.setAmount(withdrawApply.getAmount());
            withdrawDetailResponseVO.setRateAmount(withdrawApply.getRateAmount());
            withdrawDetailResponseVO.setReceivingAddress(withdrawApply.getReceivingAddress());
            withdrawDetailResponseVO.setWithdrawalHash(withdrawApply.getWithdrawalHash());
            withdrawDetailResponseVO.setStatus(withdrawApply.getStatus());
            withdrawDetailResponseVO.setCreateTime(withdrawApply.getCreateTime());
            withdrawDetailResponseVO.setUpdateTime(withdrawApply.getUpdateTime());
            withdrawDetailResponseVO.setRemark(withdrawApply.getRemark());
        }
        return withdrawDetailResponseVO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public WithdrawAddResponseVO addWithdraw(WithdrawAddRequestVO requestVO, String userId) {
        log.info("用户[{}]发起提现请求，金额：{}，地址：{}", userId, requestVO.getAmount(), requestVO.getReceivingAddress());

        // 0. 检查提现按钮状态
        ButtonConfig buttonConfig = buttonConfigMapper.selectByButtonKey(BUTTON_KEY_WITHDRAW);
        if (buttonConfig == null || buttonConfig.getButtonStatus() == null || buttonConfig.getButtonStatus() == 0) {
            throw new BusinessException("提现功能暂时关闭，请稍后再试");
        }

        // 1. 参数校验
        validateWithdrawRequest(requestVO, userId);

        // 2. 幂等性检查（基于用户ID + 金额 + 地址）
        String idempotentKey = buildIdempotentKey(userId, requestVO);
        if (!checkIdempotent(idempotentKey)) {
            throw new BusinessException("请勿重复提交提现申请");
        }

        // 3. 分布式锁防止并发提现
        String lockKey = RedisKeyConstant.WITHDRAW_LOCK_PREFIX + userId;
        String lockValue = UUID.randomUUID().toString();
        boolean locked = acquireLock(lockKey, lockValue, LOCK_EXPIRE_SECONDS);

        if (!locked) {
            throw new BusinessException("系统繁忙，请稍后再试");
        }
        try {
            // 4. 检查用户余额是否足够
            checkUserBalance(userId, requestVO.getAmount());

            // 5. 计算费率和实际到账金额
            TransactionRateRequestVO rate = new TransactionRateRequestVO();
            rate.setAmount(requestVO.getAmount());
            rate.setType(PaymentTypeEnum.WITHDRAW.getCode());
            TransactionRateResponseVO amountInfo = assetTransactionService.getRateInfo(rate);
            log.info("提现费率信息：{}", JSON.toJSONString(amountInfo));

            // 6. 创建提现申请记录
            WithdrawApply withdrawApply = createWithdrawApply(userId, requestVO, amountInfo);
            int insertCount = withdrawApplyMapper.insert(withdrawApply);

            if (insertCount <= 0) {
                throw new BusinessException("创建提现申请失败");
            }

            log.info("提现申请记录创建成功，交易号：{}", withdrawApply.getTransactionNumber());

            // 7. 冻结用户余额
         /*   memberService.updateSubtractionBalanceByUserId(requestVO.getAmount(), userId);
            // 8. 调用外部提现接口
            Long withdrawalId = null;
            try {
                Map<String, Object> map = callExternalWithdrawal(userId, requestVO, withdrawApply.getTransactionNumber());
                Object withdrawalIdObj = map.get("withdrawalId");
                withdrawalId = Long.valueOf(withdrawalIdObj.toString());
                withdrawApply.setWithdrawalId(withdrawalId);
                withdrawApply.setStatus(WithdrawStatusEnum.PROCESSING.getCode());
                log.info("外部提现接口调用成功，withdrawalId：{}", withdrawalId);

                // 9. 更新提现申请状态为处理中
                withdrawApplyMapper.updateStatusAndWithdrawalId(withdrawApply.getId(), WithdrawStatusEnum.PROCESSING.getCode(), withdrawalId, userId);
            } catch (Exception e) {
                log.error("调用外部提现接口失败，交易号：{}，错误：{}", withdrawApply.getTransactionNumber(), e.getMessage(), e);
                // 提现请求失败，更新状态并解冻余额
                withdrawApplyMapper.updateStatus(withdrawApply.getId(), WithdrawStatusEnum.FAIL.getCode(), userId);
                memberService.updateBalanceByUserId(requestVO.getAmount(), userId);
                throw new BusinessException("提现申请提交失败，请稍后重试");
            }
            */


            // 10. 设置幂等标识
            setIdempotent(idempotentKey);

            // 11. 构造返回结果
            return buildResponse(withdrawApply);

        } finally {
            // 释放分布式锁
            releaseLock(lockKey, lockValue);
        }
    }


    /**
     * 构造返回结果
     */
    private WithdrawAddResponseVO buildResponse(WithdrawApply apply) {
        WithdrawAddResponseVO response = new WithdrawAddResponseVO();
        response.setTransactionNumber(apply.getTransactionNumber());
        response.setAmount(apply.getAmount());
        response.setActualAmount(apply.getActualAmount());
        response.setRateAmount(apply.getRateAmount());
        response.setStatus(apply.getStatus());
        response.setReceivingAddress(apply.getReceivingAddress());
        response.setWithdrawalHash(apply.getWithdrawalHash());
        response.setCreateTime(apply.getCreateTime());
        response.setUpdateTime(apply.getUpdateTime());
        response.setWithdrawalId(apply.getWithdrawalId());
        return response;
    }


    /**
     * 创建提现申请记录
     */
    private WithdrawApply createWithdrawApply(String userId, WithdrawAddRequestVO requestVO, TransactionRateResponseVO amountInfo) {
        String transactionNumber = generator.generate();

        WithdrawApply apply = new WithdrawApply();
        apply.setUserId(userId);
        apply.setTransactionNumber(transactionNumber);
        apply.setCoinId(CoinTypeEnum.TOKEN.getCode());
        apply.setAmount(amountInfo.getAmount());
        apply.setActualAmount(amountInfo.getActualAmount());
        apply.setRateAmount(amountInfo.getRateAmount());
        apply.setReceivingAddress(requestVO.getReceivingAddress());
        apply.setStatus(WithdrawStatusEnum.APPLY.getCode());
        apply.setCreateTime(new Date());
        apply.setUpdateTime(new Date());
        apply.setCreateBy(userId);
        apply.setUpdateBy(userId);
        return apply;
    }


    /**
     * 参数校验
     */
    private void validateWithdrawRequest(WithdrawAddRequestVO requestVO, String userId) {
        if (requestVO.getAmount() == null || requestVO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("提现金额必须大于0");
        }

        if (requestVO.getAmount().compareTo(new BigDecimal("100000")) > 0) {
            throw new BusinessException("单笔提现金额不能超过100000");
        }

        if (requestVO.getReceivingAddress() == null || requestVO.getReceivingAddress().trim().isEmpty()) {
            throw new BusinessException("收款地址不能为空");
        }
    }

    /**
     * 检查用户余额
     */
    private void checkUserBalance(String userId, BigDecimal amount) {
        BigDecimal balance = memberService.get(userId).getBalance();
        if (balance == null || balance.compareTo(amount) < 0) {
            throw new BusinessException("账户余额不足");
        }
    }

    /**
     * 调用外部提现接口
     */
    private Map<String, Object> callExternalWithdrawal(String userId, WithdrawAddRequestVO requestVO,
                                                       String transactionNumber) throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", "im");
        params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
        params.put("timestamp", System.currentTimeMillis() / 1000);
        params.put("userId", userId);
        params.put("amount", requestVO.getAmount().toString());
        params.put("toAddress", requestVO.getReceivingAddress());
        params.put("coinId", 2);
        params.put("originType", 1);
        // 对外接口订单号字段为 orderNumber
        params.put("orderNumber", transactionNumber);

        // 签名
        String signContent = SignUtil.buildSignContent(params);
        String sign = SignUtil.sign(signContent, withdrawalConfig.getPrivateKey());
        params.put("sign", sign);

        String json = new ObjectMapper().writeValueAsString(params);
        log.info("调用外部提现接口，参数：{}", json);

        String response = HttpUtil.postJson(withdrawalRequestConstant.getWithdrawUrl(), json);
        log.info("外部提现接口响应：{}", response);

        HttpResponseDTO httpResponseDTO = new ObjectMapper().readValue(response, HttpResponseDTO.class);

        if (httpResponseDTO.getCode() != 0) {
            throw new BusinessException("外部提现接口返回失败：" + httpResponseDTO.getMsg());
        }

        Map<String, Object> data = (Map<String, Object>) httpResponseDTO.getData();
        Object withdrawalIdObj = data.get("withdrawalId");
        Object serialNumber = data.get("serialNumber");
        Map<String, Object> map = new HashMap<>();
        map.put("withdrawalId", withdrawalIdObj);
        map.put("serialNumber", serialNumber);


        if (withdrawalIdObj == null) {
            throw new BusinessException("外部提现接口未返回withdrawalId");
        }

        return map;
    }


    /**
     * 构造幂等Key
     */
    private String buildIdempotentKey(String userId, WithdrawAddRequestVO requestVO) {
        return RedisKeyConstant.WITHDRAW_IDEMPOTENT_PREFIX + userId + ":" + requestVO.getAmount() + ":" +
                requestVO.getReceivingAddress();
    }

    /**
     * 检查幂等性
     */
    private boolean checkIdempotent(String key) {
        return redisTemplate.opsForValue().setIfAbsent(key, "1", IDEMPOTENT_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 设置幂等标识
     */
    private void setIdempotent(String key) {
        redisTemplate.opsForValue().set(key, "1", IDEMPOTENT_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 获取分布式锁
     */
    private boolean acquireLock(String key, String value, int expireSeconds) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, expireSeconds, TimeUnit.SECONDS));
    }

    /**
     * 释放分布式锁
     */
    private void releaseLock(String key, String value) {
        try {
            String currentValue = (String) redisTemplate.opsForValue().get(key);
            if (value.equals(currentValue)) {
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error("释放锁异常", e);
        }
    }

    /***
     * 审核提现
     * @param id
     * @param status
     * @param remark
     * @param updateBy
     * @return
     */
    @Override
    public String auditWithdraw(Long id, Integer status, String remark, String updateBy) {
        log.info("审核提现, id: {}, status: {}, remark: {}, updateBy: {}", id, status, remark, updateBy);

        WithdrawApply apply = withdrawApplyMapper.selectByPrimaryKey(id);
        if (apply == null) {
            throw new BusinessException("提现申请不存在");
        }
        if (status.equals(WithdrawStatusEnum.REJECT.getCode())) {
            apply.setStatus(WithdrawStatusEnum.REJECT.getCode());
            apply.setRemark(remark);
            apply.setUpdateBy(updateBy);
            withdrawApplyMapper.updateByPrimaryKeySelective(apply);
        }

        if (status.equals(WithdrawStatusEnum.INITIATED.getCode())) {
            WithdrawAddRequestVO requestVO = new WithdrawAddRequestVO();
            requestVO.setAmount(apply.getAmount());
            requestVO.setReceivingAddress(apply.getReceivingAddress());

            //7. 冻结用户余额
            memberService.updateSubtractionBalanceByUserId(apply.getAmount(), apply.getUserId());
            // 8. 调用外部提现接口
            Long withdrawalId = null;
            try {
                Map<String, Object> map = callExternalWithdrawal(apply.getUserId(), requestVO, apply.getTransactionNumber());
                Object withdrawalIdObj = map.get("withdrawalId");
                withdrawalId = Long.valueOf(withdrawalIdObj.toString());
                apply.setWithdrawalId(withdrawalId);
                apply.setStatus(WithdrawStatusEnum.PROCESSING.getCode());
                log.info("外部提现接口调用成功，withdrawalId：{}", withdrawalId);

                // 9. 更新提现申请状态为处理中
                withdrawApplyMapper.updateStatusAndWithdrawalId(apply.getId(), WithdrawStatusEnum.PROCESSING.getCode(), withdrawalId, apply.getUserId());
            } catch (Exception e) {
                log.error("调用外部提现接口失败，交易号：{}，错误：{}", apply.getTransactionNumber(), e.getMessage(), e);
                // 提现请求失败，更新状态并解冻余额
                withdrawApplyMapper.updateStatus(apply.getId(), WithdrawStatusEnum.FAIL.getCode(), apply.getUserId());
                memberService.updateBalanceByUserId(apply.getAmount(), apply.getUserId());
                throw new BusinessException("提现申请提交失败，请稍后重试");
            }
        }
        return "SUCCESS";
    }


}
