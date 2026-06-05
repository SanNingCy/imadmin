package com.seekweb4.chat.asset.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekweb4.chat.api.config.AssetConfig;
import com.seekweb4.chat.api.constant.WXRequestConstant;
import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.api.utils.HttpUtil;
import com.seekweb4.chat.api.utils.sign.SignUtil;
import com.seekweb4.chat.asset.config.BusinessException;
import com.seekweb4.chat.asset.constant.RedisKeyConstant;
import com.seekweb4.chat.asset.constant.WXTransactionException;
import com.seekweb4.chat.asset.constant.WXTransactionResult;
import com.seekweb4.chat.asset.dto.request.TransactionWXRequestDTO;
import com.seekweb4.chat.asset.dto.response.UserInfoByAddressResponseDTO;
import com.seekweb4.chat.asset.service.AssetTransactionService;
import com.seekweb4.chat.asset.service.AssetUserService;
import com.seekweb4.chat.asset.util.AddressKeyUtil;
import com.seekweb4.chat.asset.util.TransactionIdGenerator;
import com.seekweb4.chat.asset.vo.AddressKeyVO;
import com.seekweb4.chat.asset.vo.request.TransactionAddRequestVO;
import com.seekweb4.chat.asset.vo.request.TransactionRateRequestVO;
import com.seekweb4.chat.asset.vo.response.*;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.dto.response.HttpResponseDTO;
import com.seekweb4.chat.enumUtil.PaymentStatusEnum;
import com.seekweb4.chat.enumUtil.PaymentTypeEnum;
import com.seekweb4.chat.modules.PaymentTransaction.entity.PaymentTransaction;
import com.seekweb4.chat.modules.PaymentTransaction.entity.TransactionPageRequestDTO;
import com.seekweb4.chat.modules.PaymentTransaction.mapper.PaymentTransactionMapper;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.paymentRateConfig.entity.PaymentRateConfig;
import com.seekweb4.chat.modules.paymentRateConfig.mapper.PaymentRateConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author coderpwh
 */
@Slf4j
@Service
public class AssetTransactionServiceImpl implements AssetTransactionService {

    TransactionIdGenerator generator = TransactionIdGenerator.getInstance(1);

    private static final int LOCK_WAIT_TIME = 3;
    private static final int LOCK_LEASE_TIME = 10;
    @Resource
    private MemberService memberService;

    @Resource
    private PaymentRateConfigMapper paymentRateConfigMapper;


    @Resource
    private AssetUserService assetUserService;

    @Resource
    private PaymentTransactionMapper paymentTransactionMapper;

    @Resource
    private AssetConfig assetConfig;

    @Resource
    private WXRequestConstant wxRequestConstant;

    @Resource
    private RedissonClient redissonClient;


    @Override
    public TransactionInitializeResponseVO transactionInitialize(Member member) {
        TransactionInitializeResponseVO transactionInitializeResponseVO = new TransactionInitializeResponseVO();
        if (StringUtils.isNotBlank(member.getPaymentAddress())) {
            transactionInitializeResponseVO.setPaymentAddress(member.getPaymentAddress());
            transactionInitializeResponseVO.setBalances(member.getBalance());
            transactionInitializeResponseVO.setPointTotal(member.getPointTotal());
        } else {
            try {
                AddressKeyVO addressKeyVO = new AddressKeyUtil().getEthAddress();
                String paymentAddress = AddressKeyUtil.modifyAddressPrefix(addressKeyVO.getAddress());
                transactionInitializeResponseVO.setPaymentAddress(paymentAddress);
                transactionInitializeResponseVO.setBalances(member.getBalance());
                transactionInitializeResponseVO.setPointTotal(member.getPointTotal());
                member.setPaymentAddress(paymentAddress);
                memberService.updateMember(member);
            } catch (Exception e) {
                log.error("获取用户地址失败,异常信息为:{}", e.getMessage());
            }
        }
        return transactionInitializeResponseVO;
    }


    /***
     * 获取费率信息
     */
    @Override
    public TransactionRateResponseVO getRateInfo(TransactionRateRequestVO request) {
        log.info("获取费率信息,参数为:{}", JSON.toJSONString(request));
        TransactionRateResponseVO transactionRate = new TransactionRateResponseVO();

        PaymentRateConfig paymentRateConfig = null;
        if (request.getType() != null && request.getType().equals(PaymentTypeEnum.WITHDRAW.getCode())) {
            paymentRateConfig = paymentRateConfigMapper.selectByPaymentType(PaymentTypeEnum.DEPOSIT_WX.getCode());
        } else {
            paymentRateConfig = paymentRateConfigMapper.selectByPaymentType(PaymentTypeEnum.WITHDRAW.getCode());
        }
        log.info("获取费率信息,参数为:{}", JSON.toJSONString(paymentRateConfig));
        if (paymentRateConfig != null && paymentRateConfig.getRate() != null) {
            BigDecimal rateAmount = request.getAmount().multiply(paymentRateConfig.getRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
            transactionRate.setRateAmount(rateAmount);
            BigDecimal actualAmount = request.getAmount().subtract(rateAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
            transactionRate.setAmount(request.getAmount());
            transactionRate.setActualAmount(actualAmount);
            transactionRate.setRateInfo(paymentRateConfig.getRate());
        } else {
            transactionRate.setAmount(request.getAmount());
            transactionRate.setActualAmount(request.getAmount());
            transactionRate.setRateAmount(new BigDecimal(0));
            transactionRate.setRateInfo(new BigDecimal(0));

        }
        return transactionRate;
    }


    /***
     * 获取资产交易人的信息
     */
    @Override
    public TransactionPersonInfoResponseVO getTransactionPersonInfo(String receivingAddress) {
        log.info("获取资产交易人的信息,参数为:{}", receivingAddress);
        TransactionPersonInfoResponseVO personInfoResponse = new TransactionPersonInfoResponseVO();
        UserInfoByAddressResponseDTO userInfoByAddressResponseDTO = assetUserService.getUserInfoByAddress(receivingAddress);
        log.info("获取资产交易人的信息,参数为:{}", JSON.toJSONString(userInfoByAddressResponseDTO));
        if (userInfoByAddressResponseDTO != null) {
            personInfoResponse.setUid(userInfoByAddressResponseDTO.getUid());
            personInfoResponse.setUserName(userInfoByAddressResponseDTO.getUserName());
            personInfoResponse.setUserId(userInfoByAddressResponseDTO.getUserId());
            if (StringUtils.isNotBlank(userInfoByAddressResponseDTO.getIcon())) {
                personInfoResponse.setIcon(userInfoByAddressResponseDTO.getIcon());
            } else {
                personInfoResponse.setIcon("");
            }
            return personInfoResponse;
        } else {
            throw new BusinessException("收款地址不存在");
        }
    }

    /***
     * 添加资产交易
     */

 /*   @Override
    public TransactionAddResponseVO addTransaction(TransactionAddRequestVO requestVO, String userId) {
        TransactionAddResponseVO transactionAddResponseVO = new TransactionAddResponseVO();
        log.info("添加资产交易,参数为:{},用户id:{}", JSON.toJSONString(requestVO), userId);
        UserInfoByAddressResponseDTO userInfoByAddressResponseDTO = assetUserService.getUserInfoByAddress(requestVO.getReceivingAddress());
        if (Objects.isNull(userInfoByAddressResponseDTO)) {
            throw new BusinessException("收款地址不存在");
        }

        Member member = memberService.get(userId);
        if (member.getBalance().subtract(requestVO.getAmount()).compareTo(BigDecimal.ZERO) <= 0) {
            log.error("资产-入金WX,用户余额不足");
            throw new BusinessException("余额不足");
        }

        PaymentTransaction paymentTransaction = new PaymentTransaction();

        // 获取费率
        TransactionRateRequestVO rate = new TransactionRateRequestVO();
        rate.setAmount(requestVO.getAmount());
        TransactionRateResponseVO rateInfo = getRateInfo(rate);
        if (rateInfo != null) {
            paymentTransaction.setAmount(rateInfo.getAmount());
            paymentTransaction.setActualAmount(rateInfo.getActualAmount());
            paymentTransaction.setRateAmount(rateInfo.getRateAmount());
            paymentTransaction.setRateInfo(String.valueOf(rateInfo.getRateInfo()));
        }
        String transactionNumber = generator.generate();
        paymentTransaction.setReceivingAddress(requestVO.getReceivingAddress());
        paymentTransaction.setUserId(userId);
        paymentTransaction.setPaymentStatus(PaymentStatusEnum.PROCESSING.getCode());
        paymentTransaction.setTransactionNumber(transactionNumber);
        int count = paymentTransactionMapper.insertSelective(paymentTransaction);
        if (count > 0) {
            TransactionWXRequestDTO transactionWXRequestDTO = new TransactionWXRequestDTO();
            transactionWXRequestDTO.setPaymentAddress(member.getPaymentAddress());
            transactionWXRequestDTO.setReceivingAddress(requestVO.getReceivingAddress());
            transactionWXRequestDTO.setAmount(String.valueOf(paymentTransaction.getAmount()));
            transactionWXRequestDTO.setImUserId(member.getId());
            transactionWXRequestDTO.setSerialNumber(paymentTransaction.getTransactionNumber());
            // 请求 wx 入金
            Boolean result = transactionByWx(transactionWXRequestDTO, paymentTransaction);
            if (result) {
                log.info("资产入金WX,入金成功");
                paymentTransaction.setPaymentStatus(PaymentStatusEnum.SUCCESS.getCode());
                paymentTransactionMapper.updateByPrimaryKeySelective(paymentTransaction);
                memberService.updateSubtractionBalanceByUserId(paymentTransaction.getAmount(),userId);
                transactionAddResponseVO.setPaymentStatus(PaymentStatusEnum.SUCCESS.getCode());
            } else {
                log.error("资产入金WX,入金失败");
                paymentTransaction.setPaymentStatus(PaymentStatusEnum.FAIL.getCode());
                paymentTransactionMapper.updateByPrimaryKeySelective(paymentTransaction);
                transactionAddResponseVO.setPaymentStatus(paymentTransaction.getPaymentStatus());
                transactionAddResponseVO.setPaymentStatus(PaymentStatusEnum.FAIL.getCode());
            }
        }
        transactionAddResponseVO.setTransactionNumber(paymentTransaction.getTransactionNumber());
        transactionAddResponseVO.setActualAmount(paymentTransaction.getActualAmount());
        transactionAddResponseVO.setAmount(paymentTransaction.getAmount());
        transactionAddResponseVO.setRateAmount(paymentTransaction.getRateAmount());
        transactionAddResponseVO.setReceivingAddress(paymentTransaction.getReceivingAddress());
        return transactionAddResponseVO;
    }*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionAddResponseVO addTransaction(TransactionAddRequestVO requestVO, String userId) {
        log.info("开始处理转账请求, 参数:{}, 用户ID:{}", JSON.toJSONString(requestVO), userId);

        // 1. 幂等性校验（使用客户端传入的请求ID或生成幂等键）
        String idempotentKey = generateIdempotentKey(userId, requestVO);
        if (!checkIdempotent(idempotentKey)) {
            throw new BusinessException("请勿重复提交");
        }

        // 2. 获取分布式锁，防止同一用户并发转账
        String lockKey = RedisKeyConstant.TRANSACTION_LOCK_PREFIX + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，最多等待3秒，锁自动释放时间10秒
            boolean locked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("系统繁忙，请稍后重试");
            }
            try {
                log.info("开始处理转账请求, 获取锁成功, 锁key:{}", lockKey);
                return executeTransaction(requestVO, userId, idempotentKey);
            } finally {
                // 确保锁被释放
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断, userId:{}", userId, e);
            throw new BusinessException("系统繁忙，请稍后重试");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("转账处理异常, userId:{}, requestVO:{}", userId, JSON.toJSONString(requestVO), e);
            throw new BusinessException("转账失败，请稍后重试");
        }

    }

    /**
     * 执行转账核心逻辑
     */
    private TransactionAddResponseVO executeTransaction(TransactionAddRequestVO requestVO, String userId, String idempotentKey) {

        // 1. 参数校验
        validateTransactionRequest(requestVO, userId);

        // 2. 获取最新用户信息（避免使用缓存）
        Member member = memberService.get(userId);
        if (member == null) {
            throw new BusinessException("用户不存在");
        }

        // 3. 获取费率信息
        TransactionRateRequestVO rate = new TransactionRateRequestVO();
        rate.setAmount(requestVO.getAmount());
        TransactionRateResponseVO rateInfo = getRateInfo(rate);

        // 4. 余额检查（修正逻辑）
        if (member.getBalance().compareTo(rateInfo.getAmount()) < 0) {
            log.error("余额不足, userId:{}, balance:{}, amount:{}",
                    userId, member.getBalance(), rateInfo.getAmount());
            throw new BusinessException("余额不足");
        }

        // 5. 创建交易记录（状态：处理中）
        PaymentTransaction paymentTransaction = createPaymentTransaction(userId, requestVO, rateInfo);

        int insertCount = paymentTransactionMapper.insertSelective(paymentTransaction);
        if (insertCount <= 0) {
            throw new BusinessException("创建交易记录失败");
        }

        // 6. 先扣减余额（在事务中，失败会自动回滚）
        int updateCount = memberService.updateSubtractionBalanceByUserId(paymentTransaction.getAmount(), userId);

        if (updateCount <= 0) {
            throw new BusinessException("余额扣减失败");
        }
        log.info("余额扣减成功, userId:{}, amount:{}", userId, paymentTransaction.getAmount());

        // 7. 调用WX转账接口
        long beginTime = System.currentTimeMillis();
        TransactionWXRequestDTO wxRequest = buildWXRequest(member, paymentTransaction);
        WXTransactionResult wxResult = transactionByWxSafe(wxRequest, paymentTransaction);
        long endTime = System.currentTimeMillis();
        log.info("WX转账耗时:{} ms", endTime - beginTime);

        // 8. 根据WX转账结果更新状态
        TransactionAddResponseVO response = handleWXResult(paymentTransaction, wxResult, userId, idempotentKey);
        log.info("转账处理完成, transactionNumber:{}, status:{}",
                paymentTransaction.getTransactionNumber(), response.getPaymentStatus());

        return response;
    }


    /**
     * 参数校验
     */
    private void validateTransactionRequest(TransactionAddRequestVO requestVO, String userId) {
        if (requestVO.getAmount() == null || requestVO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("转账金额必须大于0");
        }

        if (StringUtils.isBlank(requestVO.getReceivingAddress())) {
            throw new BusinessException("收款地址不能为空");
        }

        long beginTime = System.currentTimeMillis();
        // 验证收款地址是否存在
        UserInfoByAddressResponseDTO receiverInfo = assetUserService.getUserInfoByAddress(requestVO.getReceivingAddress());
        if (receiverInfo == null) {
            throw new BusinessException("收款地址不存在");
        }
        long endTime = System.currentTimeMillis();
        log.info("验证收款地址耗时:{} ms", endTime - beginTime);

        // 不允许向自己转账
        Member sender = memberService.get(userId);
        if (sender != null && StringUtils.isNotBlank(sender.getReceivingAddress()) && requestVO.getReceivingAddress().equals(sender.getReceivingAddress())) {
            throw new BusinessException("不能向自己转账");
        }
    }

    /**
     * 创建支付交易记录
     */
    private PaymentTransaction createPaymentTransaction(String userId, TransactionAddRequestVO requestVO, TransactionRateResponseVO rateInfo) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setUserId(userId);
        transaction.setTransactionNumber(generator.generate());
        transaction.setReceivingAddress(requestVO.getReceivingAddress());
        transaction.setAmount(rateInfo.getAmount());
        transaction.setActualAmount(rateInfo.getActualAmount());
        transaction.setRateAmount(rateInfo.getRateAmount());
        transaction.setRateInfo(String.valueOf(rateInfo.getRateInfo()));
        transaction.setPaymentStatus(PaymentStatusEnum.PROCESSING.getCode());
        return transaction;
    }

    /**
     * 构建WX请求参数
     */
    private TransactionWXRequestDTO buildWXRequest(Member member, PaymentTransaction transaction) {
        TransactionWXRequestDTO request = new TransactionWXRequestDTO();
        request.setPaymentAddress(member.getPaymentAddress());
        request.setReceivingAddress(transaction.getReceivingAddress());
        request.setAmount(String.valueOf(transaction.getAmount()));
        request.setImUserId(member.getId());
        request.setSerialNumber(transaction.getTransactionNumber());
        return request;
    }

    /**
     * 安全的WX转账调用（改进异常处理）
     */
    private WXTransactionResult transactionByWxSafe(TransactionWXRequestDTO request, PaymentTransaction transaction) {

        try {
            return transactionByWxEnhanced(request, transaction);
        } catch (WXTransactionException e) {
            log.error("WX转账业务异常, transactionNumber:{}, error:{}",
                    transaction.getTransactionNumber(), e.getMessage(), e);
            return WXTransactionResult.failure(e.getMessage());
        } catch (Exception e) {
            log.error("WX转账系统异常, transactionNumber:{}",
                    transaction.getTransactionNumber(), e);
            return WXTransactionResult.failure("系统异常，请联系客服");
        }
    }

    /**
     * 增强的WX转账方法（区分不同异常类型）
     */
    private WXTransactionResult transactionByWxEnhanced(TransactionWXRequestDTO request, PaymentTransaction transaction) throws Exception {

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", "im");
        params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
        params.put("timestamp", System.currentTimeMillis() / 1000);
        params.put("imUserId", request.getImUserId());
        params.put("paymentAddress", request.getPaymentAddress());
        params.put("receivingAddress", request.getReceivingAddress());
        params.put("amount", request.getAmount());
        params.put("serialNumber", transaction.getTransactionNumber());

        String signContent = SignUtil.buildSignContent(params);
        String sign = SignUtil.sign(signContent, assetConfig.getPrivateKey());
        params.put("sign", sign);

        String json = new ObjectMapper().writeValueAsString(params);
        log.info("WX转账请求参数:{}", json);
        log.info("WX转账请求url为:{}", wxRequestConstant.getDepositWxUrl());

        String response = HttpUtil.postJson(wxRequestConstant.getDepositWxUrl(), json);
        log.info("WX转账响应结果:{}", response);

        HttpResponseDTO httpResponse = new ObjectMapper().readValue(response, HttpResponseDTO.class);

        if (httpResponse.getCode() != 0) {
            throw new WXTransactionException("WX转账失败: " + httpResponse.getMsg());
        }

        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(httpResponse.getData()));
        Boolean result = jsonObject.getBoolean("result");

        if (Boolean.TRUE.equals(result)) {
            return WXTransactionResult.success();
        } else {
            String errorMsg = jsonObject.getString("message");
            return WXTransactionResult.failure(errorMsg != null ? errorMsg : "转账失败");
        }
    }


    /***
     * 入金WX
     * @param request
     * @return
     */
    public Boolean transactionByWx(TransactionWXRequestDTO request, PaymentTransaction paymentTransaction) {
        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("appId", "im");
            params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
            params.put("timestamp", System.currentTimeMillis() / 1000);
            params.put("imUserId", request.getImUserId());
            params.put("paymentAddress", request.getPaymentAddress());
            params.put("receivingAddress", request.getReceivingAddress());
            params.put("amount", request.getAmount());
            params.put("serialNumber", paymentTransaction.getTransactionNumber());

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

            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(httpResponseDTO.getData()));
            Boolean result = jsonObject.getBoolean("result");
            log.info("资产入金WX,入金结果为:{}", result);
            return result;
        } catch (Exception e) {
            log.error("资产-入金WX,异常:{}", e.getMessage());
        }
        return false;
    }


    /***
     * 获取资产交易分页列表
     */
    @Override
    public Page<TransactionPageResponseVO> getTransactionPage(ReqJson req, String userId) {
        TransactionPageRequestDTO pageRequestDTO = new TransactionPageRequestDTO();
        pageRequestDTO.setUserId(userId);
        pageRequestDTO.setPageNum(req.getPageNo());
        pageRequestDTO.setPageSize(req.getPageSize());

        List<PaymentTransaction> data = paymentTransactionMapper.getTransactionPage(pageRequestDTO);
        Long count = paymentTransactionMapper.getTransactionPageCount(pageRequestDTO);
        Page<TransactionPageResponseVO> page = new Page<>(req.getPageNo(), req.getPageSize(), count);
        if (data != null && data.size() > 0) {
            List<TransactionPageResponseVO> list = new ArrayList<>();
            for (PaymentTransaction paymentTransaction : data) {
                TransactionPageResponseVO transactionPageResponseVO = new TransactionPageResponseVO();
                transactionPageResponseVO.setId(paymentTransaction.getId());
                transactionPageResponseVO.setUserId(paymentTransaction.getUserId());
                transactionPageResponseVO.setTransactionNumber(paymentTransaction.getTransactionNumber());
                transactionPageResponseVO.setAmount(paymentTransaction.getAmount());
                transactionPageResponseVO.setActualAmount(paymentTransaction.getActualAmount());
                transactionPageResponseVO.setRateAmount(paymentTransaction.getRateAmount());
                transactionPageResponseVO.setPaymentStatus(paymentTransaction.getPaymentStatus());
                transactionPageResponseVO.setCreateTime(paymentTransaction.getCreateTime());
                transactionPageResponseVO.setUpdateTime(paymentTransaction.getUpdateTime());
                transactionPageResponseVO.setReceivingAddress(paymentTransaction.getReceivingAddress());
                list.add(transactionPageResponseVO);
                page.setList(list);
            }
        }
        page.setCount(count);
        return page;
    }


    /***
     *  交易
     *
     */
    @Override
    public TransactionDetailResponseVO getTransactionDetail(Long id) {
        TransactionDetailResponseVO transactionDetailResponseVO = new TransactionDetailResponseVO();
        PaymentTransaction paymentTransaction = paymentTransactionMapper.selectByPrimaryKey(id);
        if (paymentTransaction != null) {
            transactionDetailResponseVO.setTransactionNumber(paymentTransaction.getTransactionNumber());
            transactionDetailResponseVO.setActualAmount(paymentTransaction.getActualAmount());
            transactionDetailResponseVO.setAmount(paymentTransaction.getAmount());
            transactionDetailResponseVO.setRateAmount(paymentTransaction.getRateAmount());
            transactionDetailResponseVO.setPaymentStatus(paymentTransaction.getPaymentStatus());
            transactionDetailResponseVO.setCreateTime(paymentTransaction.getCreateTime());
            transactionDetailResponseVO.setUpdateTime(paymentTransaction.getUpdateTime());
            transactionDetailResponseVO.setReceivingAddress(paymentTransaction.getReceivingAddress());
            Member member = memberService.getCustomer();
            if (member != null) {
                transactionDetailResponseVO.setNickName(member.getNickname());
            }
        }
        return transactionDetailResponseVO;
    }


    /**
     * 生成幂等键
     */
    private String generateIdempotentKey(String userId, TransactionAddRequestVO requestVO) {
        // 使用用户ID、收款地址、金额、时间戳（分钟级）生成幂等键  // 分钟级别
        long timestamp = System.currentTimeMillis() / 60000;
        String key = userId + ":" + requestVO.getReceivingAddress() + ":" +
                requestVO.getAmount() + ":" + timestamp;
        return RedisKeyConstant.IDEMPOTENT_KEY_PREFIX + key;
    }


    /**
     * 处理WX转账结果
     */
    private TransactionAddResponseVO handleWXResult(PaymentTransaction transaction, WXTransactionResult wxResult, String userId, String idempotentKey) {

        TransactionAddResponseVO response = new TransactionAddResponseVO();
        response.setTransactionNumber(transaction.getTransactionNumber());
        response.setAmount(transaction.getAmount());
        response.setActualAmount(transaction.getActualAmount());
        response.setRateAmount(transaction.getRateAmount());
        response.setReceivingAddress(transaction.getReceivingAddress());

        if (wxResult.isSuccess()) {
            // 转账成功
            transaction.setPaymentStatus(PaymentStatusEnum.SUCCESS.getCode());
            paymentTransactionMapper.updateByPrimaryKeySelective(transaction);
            response.setPaymentStatus(PaymentStatusEnum.SUCCESS.getCode());
            // 事务提交后删除幂等键
            registerTransactionCallback(() -> deleteIdempotentKey(idempotentKey));
            log.info("转账成功, transactionNumber:{}", transaction.getTransactionNumber());
        } else {
            // 转账失败 - 恢复余额
            transaction.setPaymentStatus(PaymentStatusEnum.FAIL.getCode());
            paymentTransactionMapper.updateByPrimaryKeySelective(transaction);

            // 回退余额
            int rollbackCount = memberService.updateBalanceByUserId(transaction.getAmount(), userId);
            if (rollbackCount <= 0) {
                log.error("余额回退失败, userId:{}, amount:{}", userId, transaction.getAmount());
            }
            response.setPaymentStatus(PaymentStatusEnum.FAIL.getCode());
            response.setErrorMessage(wxResult.getErrorMessage());
            log.error("转账失败, transactionNumber:{}, reason:{}", transaction.getTransactionNumber(), wxResult.getErrorMessage());
        }

        return response;
    }

    /**
     * 检查幂等性
     */
    private boolean checkIdempotent(String key) {
        return redissonClient.getBucket(key).trySet("1", 60, TimeUnit.SECONDS);

    }

    /**
     * 删除幂等键
     */
    private void deleteIdempotentKey(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 注册事务提交后的回调
     */
    private void registerTransactionCallback(Runnable callback) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            callback.run();
                        }
                    }
            );
        }
    }


}
