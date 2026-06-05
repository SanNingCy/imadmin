package com.seekweb4.chat.modules.assetAdmin.service.impl.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekweb4.chat.api.config.WithdrawalConfig;
import com.seekweb4.chat.api.constant.WithdrawalRequestConstant;
import com.seekweb4.chat.api.utils.HttpUtil;
import com.seekweb4.chat.api.utils.sign.SignUtil;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.dto.request.WithdrawAudioRequestVO;
import com.seekweb4.chat.dto.response.HttpResponseDTO;
import com.seekweb4.chat.enumUtil.WithdrawStatusEnum;
import com.seekweb4.chat.modules.PaymentRecord.entity.PaymentRecord;
import com.seekweb4.chat.modules.PaymentRecord.mapper.PaymentRecordMapper;
import com.seekweb4.chat.modules.PaymentTransaction.entity.PaymentTransaction;
import com.seekweb4.chat.modules.PaymentTransaction.mapper.PaymentTransactionMapper;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawApply;
import com.seekweb4.chat.modules.WithdrawApply.mapper.WithdrawApplyMapper;
import com.seekweb4.chat.modules.assetAdmin.dto.*;
import com.seekweb4.chat.modules.assetAdmin.service.impl.AssetAdminService;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.paymentRateConfig.entity.PaymentRateConfig;
import com.seekweb4.chat.modules.paymentRateConfig.mapper.PaymentRateConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 资产后台管理服务实现类
 *
 * @author admin
 * @since 2025-10-28
 */
@Slf4j
@Service
@Transactional
public class AssetAdminServiceImpl implements AssetAdminService {

    @Autowired
    private PaymentRateConfigMapper paymentRateConfigMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private PaymentTransactionMapper paymentTransactionMapper;

    @Autowired
    private WithdrawApplyMapper withdrawApplyMapper;

    @Resource
    private MemberService memberService;


    @Resource
    private WithdrawalConfig withdrawalConfig;

    @Resource
    private WithdrawalRequestConstant withdrawalRequestConstant;

    @Override
    public Page<PaymentRateConfig> getPaymentRateConfigPage(PaymentRateConfigQueryDto queryDto) {
        Page<PaymentRateConfig> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        
        // 设置分页参数
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        queryDto.setPaymentType(queryDto.getPaymentType());
        queryDto.setRate(queryDto.getRate());
        
        // 转换orderBy字段：将驼峰命名转换为下划线命名（数据库列名）
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            String convertedOrderBy = convertOrderByToUnderscore(queryDto.getOrderBy());
            queryDto.setOrderBy(convertedOrderBy);
        }
        
        // 查询总数
        Long count = paymentRateConfigMapper.selectAdminCount(queryDto);
        page.setCount(count);
        
        // 查询数据
        List<PaymentRateConfig> list = paymentRateConfigMapper.selectAdminPageList(queryDto);
        page.setList(list);
        
        return page;
    }

    @Override
    public PaymentRateConfig getPaymentRateConfigById(Long id) {
        return paymentRateConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public PaymentRateConfig getPaymentRateConfigByPaymentType(Integer paymentType) {
        return paymentRateConfigMapper.selectByPaymentType(paymentType);
    }

    @Override
    public boolean savePaymentRateConfig(PaymentRateConfig paymentRateConfig) {
        try {
            // 校验费率合法性
            validateRate(paymentRateConfig.getRate());
            
            // 检查该类型是否已经存在
            if (paymentRateConfig.getPaymentType() != null) {
                PaymentRateConfig existing = paymentRateConfigMapper.selectByPaymentType(paymentRateConfig.getPaymentType());
                if (existing != null) {
                    throw new IllegalArgumentException("该支付类型（" + paymentRateConfig.getPaymentType() + "）的费率配置已存在，每个类型只能有一条配置");
                }
            }
            
            paymentRateConfig.setCreateTime(new Date());
            paymentRateConfig.setUpdateTime(new Date());
            paymentRateConfig.setIsDeleted(0);
            return paymentRateConfigMapper.insert(paymentRateConfig) > 0;
        } catch (IllegalArgumentException e) {
            // 参数校验异常，直接向上抛出，让Controller层处理
            log.error("保存费率配置失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("保存费率配置失败", e);
            throw new RuntimeException("保存失败：" + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePaymentRateConfig(PaymentRateConfig paymentRateConfig) {
        try {
            // 只有传了 rate 才校验
            if (paymentRateConfig.getRate() != null) {
                validateRate(paymentRateConfig.getRate());
            }

            paymentRateConfig.setUpdateTime(new Date());
            return paymentRateConfigMapper.updateByPrimaryKeySelective(paymentRateConfig) > 0;
        } catch (IllegalArgumentException e) {
            // 参数校验异常，直接向上抛出，让Controller层处理
            log.error("更新费率配置失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新费率配置失败", e);
            throw new RuntimeException("更新失败：" + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePaymentRateConfigByTypeAndId(Long id, Integer paymentType, BigDecimal rate, String updateBy) {
        try {
            // 校验费率合法性
            if (rate != null) {
                validateRate(rate);
            }
            
            // 根据ID查询现有配置
            PaymentRateConfig existing = paymentRateConfigMapper.selectByPrimaryKey(id);
            if (existing == null) {
                throw new IllegalArgumentException("费率配置不存在");
            }
            
            // 验证paymentType是否匹配
            if (paymentType != null && !paymentType.equals(existing.getPaymentType())) {
                throw new IllegalArgumentException("支付类型不匹配，无法更新");
            }
            
            // 更新费率配置
            PaymentRateConfig updateConfig = new PaymentRateConfig();
            updateConfig.setId(id);
            if (rate != null) {
                updateConfig.setRate(rate);
            }
            if (updateBy != null) {
                updateConfig.setUpdateBy(updateBy);
            }
            updateConfig.setUpdateTime(new Date());
            
            return paymentRateConfigMapper.updateByPrimaryKeySelective(updateConfig) > 0;
        } catch (IllegalArgumentException e) {
            // 参数校验异常，直接向上抛出，让Controller层处理
            log.error("根据类型和ID更新费率配置失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("根据类型和ID更新费率配置失败", e);
            throw new RuntimeException("更新失败：" + e.getMessage(), e);
        }
    }

    /**
     * 校验费率：允许 0 及正数，仅禁止负数（可为 0）
     */
    private void validateRate(BigDecimal rate) {
        if (rate == null) {
            throw new IllegalArgumentException("费率不能为空");
        }
        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("费率不能为负数");
        }
    }

    @Override
    public boolean deletePaymentRateConfig(Long id) {
        try {
            PaymentRateConfig config = new PaymentRateConfig();
            config.setId(id);
            config.setIsDeleted(1);
            config.setUpdateTime(new Date());
//            config.setUpdateTime(LocalDateTime.now());
            return paymentRateConfigMapper.updateByPrimaryKeySelective(config) > 0;
        } catch (Exception e) {
            log.error("删除费率配置失败", e);
            return false;
        }
    }

    @Override
    public Page<PaymentRecord> getPaymentRecordPage(PaymentRecordQueryDto queryDto) {
        Page<PaymentRecord> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        
        // 设置分页参数
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());

        // 转换orderBy字段：将驼峰命名转换为下划线命名（数据库列名）
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            String convertedOrderBy = convertOrderByToUnderscore(queryDto.getOrderBy());
            queryDto.setOrderBy(convertedOrderBy);
        }
        
        // 查询总数
        Long count = paymentRecordMapper.selectAdminCount(queryDto);
        page.setCount(count);
        
        // 查询数据
        List<PaymentRecord> list = paymentRecordMapper.selectAdminPageList(queryDto);
        if (list != null && !list.isEmpty()) {
            for (PaymentRecord paymentRecord : list) {
                fillpaymentRecord(paymentRecord);
            }
        }
        page.setList(list);
        
        return page;
    }

    private void fillpaymentRecord(PaymentRecord paymentRecord) {
        if (paymentRecord == null || StringUtils.isBlank(paymentRecord.getUserId())) {
            return;
        }
        Member member = memberService.selectBasicById(paymentRecord.getUserId());
        if (member == null) {
            return;
        }
        paymentRecord.setNickname(member.getNickname());
        paymentRecord.setIdno(member.getIdno());
    }

    @Override
    public PaymentRecord getPaymentRecordById(Long id) {
        PaymentRecord paymentRecord = paymentRecordMapper.selectByPrimaryKey(id);
        if (paymentRecord != null) {
            Member member = memberService.selectBasicById(paymentRecord.getUserId());
            paymentRecord.setNickname(member.getNickname());
            paymentRecord.setIdno(member.getIdno());
        }
        return paymentRecord;
    }

    @Override
    public Page<PaymentTransaction> getPaymentTransactionPage(PaymentTransactionQueryDto queryDto) {
        Page<PaymentTransaction> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        
        // 设置分页参数
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());

        // 转换orderBy字段：将驼峰命名转换为下划线命名（数据库列名）
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            String convertedOrderBy = convertOrderByToUnderscore(queryDto.getOrderBy());
            queryDto.setOrderBy(convertedOrderBy);
        }
        
        // 查询总数
        Long count = paymentTransactionMapper.selectAdminCount(queryDto);
        page.setCount(count);
        
        // 查询数据
        List<PaymentTransaction> list = paymentTransactionMapper.selectAdminPageList(queryDto);
        if (list != null && !list.isEmpty()) {
            for (PaymentTransaction paymentTransaction : list) {
                fillpaymentTransactionInfo(paymentTransaction);
            }
        }
        page.setList(list);
        
        return page;
    }

    private void fillpaymentTransactionInfo(PaymentTransaction paymentTransaction) {
        if (paymentTransaction == null || StringUtils.isBlank(paymentTransaction.getUserId())) {
            return;
        }
        Member member = memberService.selectBasicById(paymentTransaction.getUserId());
        if (member == null) {
            return;
        }
        paymentTransaction.setNickname(member.getNickname());
        paymentTransaction.setIdno(member.getIdno());
    }


    @Override
    public PaymentTransaction getPaymentTransactionById(Long id) {
        PaymentTransaction paymentTransaction = paymentTransactionMapper.selectByPrimaryKey(id);
        if (paymentTransaction != null) {
            Member member = memberService.selectBasicById(paymentTransaction.getUserId());
            paymentTransaction.setNickname(member.getNickname());
            paymentTransaction.setIdno(member.getIdno());
        }
        return paymentTransaction;
    }

    @Override
    public Page<WithdrawApply> getWithdrawApplyPage(WithdrawApplyQueryDto queryDto) {
        Page<WithdrawApply> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        
        // 设置分页参数
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());

        // 转换orderBy字段：将驼峰命名转换为下划线命名（数据库列名）
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            String convertedOrderBy = convertOrderByToUnderscore(queryDto.getOrderBy());
            queryDto.setOrderBy(convertedOrderBy);
        }

        // 查询总数
        Long count = withdrawApplyMapper.selectAdminCount(queryDto);
        page.setCount(count);
        
        // 查询数据
        List<WithdrawApply> list = withdrawApplyMapper.selectAdminPageList(queryDto);
        if (list != null && !list.isEmpty()) {
            for (WithdrawApply withdrawApply : list) {
                fillMemberInfo(withdrawApply);
            }
        }
        page.setList(list);
        
        return page;
    }

    @Override
    public WithdrawApply getWithdrawApplyById(Long id) {
        WithdrawApply apply = withdrawApplyMapper.selectByPrimaryKey(id);
        fillMemberInfo(apply);
        return apply;
    }

    @Override
    public List<WithdrawApply> listWithdrawApplyByStatus(Integer status) {
        if (status == null) {
            return Collections.emptyList();
        }
        List<WithdrawApply> list = withdrawApplyMapper.selectListByStatus(status);
        if (list != null && !list.isEmpty()) {
            for (WithdrawApply apply : list) {
                fillMemberInfo(apply);
            }
        }
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public boolean updateWithdrawApplyStatus(Long id, Integer status, String updateBy) {
        try {
            return withdrawApplyMapper.updateStatus(id, status, updateBy) > 0;
        } catch (Exception e) {
            log.error("更新提现申请状态失败", e);
            return false;
        }
    }

    @Override
    public boolean updateWithdrawApplyStatusAndWithdrawalId(Long id, Integer status, Long withdrawalId, String updateBy) {
        try {
            return withdrawApplyMapper.updateStatusAndWithdrawalId(id, status, withdrawalId, updateBy) > 0;
        } catch (Exception e) {
            log.error("更新提现申请状态和外部提现ID失败", e);
            return false;
        }
    }

    @Override
    public AssetStatisticsDto getAssetStatistics(Date startDate, Date endDate) {
        AssetStatisticsDto statistics = new AssetStatisticsDto();
        
        try {
            // 设置查询条件
            PaymentRecordQueryDto recordQuery = new PaymentRecordQueryDto();
            recordQuery.setCreateTimeStart(startDate);
            recordQuery.setCreateTimeEnd(endDate);
            
            PaymentTransactionQueryDto transactionQuery = new PaymentTransactionQueryDto();
            transactionQuery.setCreateTimeStart(startDate);
            transactionQuery.setCreateTimeEnd(endDate);
            
            WithdrawApplyQueryDto withdrawQuery = new WithdrawApplyQueryDto();
            withdrawQuery.setCreateTimeStart(startDate);
            withdrawQuery.setCreateTimeEnd(endDate);
            
            // 统计入金金额
            BigDecimal totalDepositAmount = paymentRecordMapper.selectAdminTotalAmount(recordQuery);
            statistics.setTotalDepositAmount(totalDepositAmount);
            
            // 统计交易金额
            BigDecimal totalTransactionAmount = paymentTransactionMapper.selectAdminTotalAmount(transactionQuery);
            statistics.setTotalDepositAmount(totalTransactionAmount);
            
            // 统计手续费
            BigDecimal totalFeeAmount = paymentTransactionMapper.selectAdminTotalFeeAmount(transactionQuery);
            statistics.setTotalFeeAmount(totalFeeAmount);
            
            // 统计提现金额
            BigDecimal totalWithdrawAmount = withdrawApplyMapper.selectAdminTotalAmount(withdrawQuery);
            statistics.setTotalWithdrawAmount(totalWithdrawAmount);
            
            // 统计提现手续费
            BigDecimal totalWithdrawFeeAmount = withdrawApplyMapper.selectAdminTotalFeeAmount(withdrawQuery);
            statistics.setTotalFeeAmount(statistics.getTotalFeeAmount().add(totalWithdrawFeeAmount));
            
            // 统计成功入金笔数
            recordQuery.setPaymentStatus(1);
            Long successDepositCount = paymentRecordMapper.selectAdminCount(recordQuery);
            statistics.setSuccessDepositCount(successDepositCount);
            
            // 统计失败入金笔数
            recordQuery.setPaymentStatus(2);
            Long failedDepositCount = paymentRecordMapper.selectAdminCount(recordQuery);
            statistics.setFailedDepositCount(failedDepositCount);
            
            // 统计成功提现笔数
            withdrawQuery.setStatus(2);
            Long successWithdrawCount = withdrawApplyMapper.selectAdminCount(withdrawQuery);
            statistics.setSuccessWithdrawCount(successWithdrawCount);
            
            // 统计失败提现笔数
            withdrawQuery.setStatus(3);
            Long failedWithdrawCount = withdrawApplyMapper.selectAdminCount(withdrawQuery);
            statistics.setFailedWithdrawCount(failedWithdrawCount);
            
            // 统计待处理提现笔数
            withdrawQuery.setStatus(0);
            Long pendingWithdrawCount = withdrawApplyMapper.selectAdminCount(withdrawQuery);
            statistics.setPendingWithdrawCount(pendingWithdrawCount);
            
        } catch (Exception e) {
            log.error("获取资产统计信息失败", e);
        }
        
        return statistics;
    }

    @Override
    public AssetStatisticsDto getTodayAssetStatistics() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar.getTime();
        
        AssetStatisticsDto statistics = getAssetStatistics(startDate, endDate);
        
        // 设置今日统计
        statistics.setTodayDepositAmount(statistics.getTotalDepositAmount());
        statistics.setTodayWithdrawAmount(statistics.getTotalWithdrawAmount());
        statistics.setTodayFeeAmount(statistics.getTotalFeeAmount());
        
        return statistics;
    }



    /**
     * 将orderBy字符串中的驼峰字段名转换为下划线命名（数据库列名）
     * 例如：paymentType asc -> payment_type asc
     *      createTime desc, id asc -> create_time desc, id asc
     * 
     * @param orderBy 原始orderBy字符串
     * @return 转换后的orderBy字符串
     */
    private static final Set<String> CAMEL_CASE_COLUMN_WHITELIST = new HashSet<>(Arrays.asList(
            "coinId"
    ));

    private String convertOrderByToUnderscore(String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return orderBy;
        }
        
        // 按逗号分割多个排序字段
        String[] parts = orderBy.split(",");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (StringUtils.isBlank(part)) {
                continue;
            }
            
            // 分割字段名和排序方向（asc/desc）
            String[] fieldAndOrder = part.split("\\s+");
            if (fieldAndOrder.length > 0) {
                String fieldName = fieldAndOrder[0].trim();
                // 将驼峰转换为下划线
                String underscoreField;
                if (fieldName.contains("_") || CAMEL_CASE_COLUMN_WHITELIST.contains(fieldName)) {
                    underscoreField = fieldName;
                } else {
                    underscoreField = StringUtils.toUnderScoreCase(fieldName);
                }
                
                if (i > 0) {
                    result.append(", ");
                }
                result.append(underscoreField);
                
                // 保留排序方向
                if (fieldAndOrder.length > 1) {
                    result.append(" ").append(fieldAndOrder[1].trim());
                }
            }
        }
        
        return result.toString();
    }

    @Override
    public Boolean auditWithdraw(Long id, Integer status, String remark, String updateBy) {
        log.info("审核提现, id: {}, status: {}, remark: {}, updateBy: {}", id, status, remark, updateBy);

        WithdrawApply apply = withdrawApplyMapper.selectByPrimaryKey(id);
        if (apply == null) {
//            throw new BusinessException("提现申请不存在");
            log.error("提现申请不存在");
            return  false;
        }
        if (status.equals(WithdrawStatusEnum.REJECT.getCode())) {
            apply.setStatus(WithdrawStatusEnum.REJECT.getCode());
            apply.setRemark(remark);
            apply.setUpdateBy(updateBy);
            withdrawApplyMapper.updateByPrimaryKeySelective(apply);
            memberService.updateBalanceByUserId(apply.getAmount(), apply.getUserId());
        }

        if (status.equals(WithdrawStatusEnum.INITIATED.getCode())) {
            WithdrawAudioRequestVO requestVO = new WithdrawAudioRequestVO();
            requestVO.setAmount(apply.getActualAmount());
            requestVO.setOriginAmount(apply.getAmount());
            requestVO.setReceivingAddress(apply.getReceivingAddress());


            //7. 冻结用户余额
//            memberService.updateSubtractionBalanceByUserId(apply.getAmount(), apply.getUserId());
            // 8. 调用外部提现接口
            Long withdrawalId = null;
            try {
                 //  调用外部提现接口
                Map<String, Object> map = callExternalWithdrawal(apply.getUserId(), requestVO, apply.getTransactionNumber(),apply.getActualAmount());
                if (map == null) {
                    throw new IllegalStateException("外部提现接口返回为空");
                }
                Object withdrawalIdObj = map.get("withdrawalId");
                if (withdrawalIdObj == null) {
                    throw new IllegalStateException("外部提现接口未返回withdrawalId");
                }
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
                log.error("提现申请提交失败，请稍后重试");
                return  false;
            }
        }
        return true;
    }

    @Override
    public Page<PaymentTransaction> getPaymentTransactionPageIm(PaymentTransactionQueryDto queryDto) {
        Page<PaymentTransaction> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());

        // 设置分页参数
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());

        // 转换orderBy字段：将驼峰命名转换为下划线命名（数据库列名）
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            String convertedOrderBy = convertOrderByToUnderscore(queryDto.getOrderBy());
            queryDto.setOrderBy(convertedOrderBy);
        }

        // 查询总数
        Long count = paymentTransactionMapper.selectIMAdminCount(queryDto);
        page.setCount(count);

        // 查询数据
        List<PaymentTransaction> list = paymentTransactionMapper.selectIMAdminPageList(queryDto);
        if (list != null && !list.isEmpty()) {
            for (PaymentTransaction paymentTransaction : list) {
                fillpaymentTransactionInfo(paymentTransaction);
            }
        }
        page.setList(list);

        return page;
    }

    @Override
    public PaymentTransaction getPaymentTransactionByIdIm(Long id) {
        PaymentTransaction paymentTransaction = paymentTransactionMapper.selectByPrimaryKeyIMByID(id);
        if (paymentTransaction != null) {
            Member member = memberService.selectBasicById(paymentTransaction.getUserId());
            paymentTransaction.setNickname(member.getNickname());
            paymentTransaction.setIdno(member.getIdno());
        }
        return paymentTransaction;
    }

    private void fillMemberInfo(WithdrawApply withdrawApply) {
        if (withdrawApply == null || StringUtils.isBlank(withdrawApply.getUserId())) {
            return;
        }
        Member member = memberService.selectBasicById(withdrawApply.getUserId());
        if (member == null) {
            return;
        }
        withdrawApply.setNickname(member.getNickname());
        withdrawApply.setIdno(member.getIdno());
    }



    /**
     * 调用外部提现接口
     */
    private Map<String, Object> callExternalWithdrawal(String userId, WithdrawAudioRequestVO requestVO, String transactionNumber,BigDecimal actualAmount) throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", "im");
        params.put("nonce", UUID.randomUUID().toString().replace("-", ""));
//        params.put("timestamp", System.currentTimeMillis() / 1000);
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        params.put("timestamp", timestamp);
        params.put("userId", userId);
        params.put("originAmount",String.valueOf(requestVO.getOriginAmount()));
        params.put("amount", String.valueOf(actualAmount));
//        params.put("amount", requestVO.getAmount().toString());
        params.put("toAddress", requestVO.getReceivingAddress());
        params.put("coinId", 2);
        params.put("originType", 2);
        // 对外接口订单号字段为 orderNumber
        params.put("orderNumber", transactionNumber);
        params.put("redemption", 0);

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
            String msg = httpResponseDTO.getMsg() != null ? httpResponseDTO.getMsg() : "Service error";
            log.error("外部提现接口返回失败：{}", msg);
            throw new RuntimeException("外部提现接口返回失败：" + msg);
        }

        Object dataObj = httpResponseDTO.getData();
        if (!(dataObj instanceof Map)) {
            throw new RuntimeException("外部提现接口返回data为空或格式错误");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) dataObj;
        Object withdrawalIdObj = data.get("withdrawalId");
        Object serialNumber = data.get("serialNumber");
        Map<String, Object> map = new HashMap<>();
        map.put("withdrawalId", withdrawalIdObj);
        map.put("serialNumber", serialNumber);


        if (withdrawalIdObj == null) {
            log.error("外部提现接口未返回withdrawalId");
            throw new RuntimeException("外部提现接口未返回withdrawalId");
        }

        return map;
    }
}
