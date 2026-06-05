package com.seekweb4.chat.modules.assetAdmin.controller;

import com.seekweb4.chat.api.config.WithdrawalConfig;
import com.seekweb4.chat.asset.util.wallet.BscWalletSignUtil;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.enumUtil.WithdrawStatusEnum;
import com.seekweb4.chat.modules.assetAdmin.dto.*;
import com.seekweb4.chat.modules.assetAdmin.service.impl.AssetAdminService;
import com.seekweb4.chat.modules.paymentRateConfig.entity.PaymentRateConfig;
import com.seekweb4.chat.modules.PaymentRecord.entity.PaymentRecord;
import com.seekweb4.chat.modules.PaymentTransaction.entity.PaymentTransaction;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawApply;
import com.seekweb4.chat.modules.buttonConfig.entity.ButtonConfig;
import com.seekweb4.chat.modules.buttonConfig.mapper.ButtonConfigMapper;
import com.seekweb4.chat.modules.sys.entity.User;
import com.seekweb4.chat.modules.sys.service.UserService;
import com.seekweb4.chat.modules.sys.utils.GoogleAuthenticationToolUtil;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 资产后台管理控制器
 *
 * @author caoyucheng
 * @since 2025-10-28
 */
@Slf4j
@RestController
@RequestMapping(value = "/admin/asset",produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "资产后台管理")
public class AssetAdminController extends BaseController {

    @Autowired
    private AssetAdminService assetAdminService;

    @Resource
    private ButtonConfigMapper buttonConfigMapper;

    @Resource
    private WithdrawalConfig withdrawalConfig;

    @Resource
    private UserService userService;

    /** 提现审核模式按钮key：0=自动审核，1=手动审核(需钱包地址验签) */
    private static final String BUTTON_KEY_WITHDRAW_AUDIT = "withdraw_audit";

    @ApiOperation("分页查询费率配置")
//    @RequiresPermissions("property:tariff:list")
    @GetMapping("/rateConfig/page")
    public AjaxJson getPaymentRateConfigPage(
            PaymentRateConfigQueryDto queryDto
    ) {

        try {
            Page<PaymentRateConfig> page = assetAdminService.getPaymentRateConfigPage(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询费率配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询费率配置")
//    @RequiresPermissions("rateConfig:rateConfig:view")
    @RequiresPermissions(value = {"asset:rate:platform:view", "asset:rate:platform:add", "asset:rate:platform:view"}, logical = Logical.OR)
    @GetMapping("/rateConfig/{id}")
    public AjaxJson getPaymentRateConfigById(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            PaymentRateConfig config = assetAdminService.getPaymentRateConfigById(id);
            if (config == null) {
                return AjaxJson.error("费率配置不存在");
            }
            return AjaxJson.success().put("config", config);
        } catch (Exception e) {
            log.error("查询费率配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据支付类型查询费率配置")
//    @RequiresPermissions("rateConfig:rateConfig:view")
//    @RequiresPermissions(value = {"asset:rate:platform:view", "asset:rate:platform:add"}, logical = Logical.OR)
    @GetMapping("/rateConfig/queryByPaymentType")
    public AjaxJson getPaymentRateConfigByPaymentType(@ApiParam("支付类型(1:入金wx 2:提现 3：IM内部)") @RequestParam Integer paymentType) {
        try {
            PaymentRateConfig config = assetAdminService.getPaymentRateConfigByPaymentType(paymentType);
            if (config == null) {
                return AjaxJson.error("该支付类型的费率配置不存在");
            }
            return AjaxJson.success().put("config", config);
        } catch (Exception e) {
            log.error("查询费率配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("保存费率配置")
//    @RequiresPermissions("asset:rate:platform:add")
    @PostMapping("/rateConfig/save")
    public AjaxJson savePaymentRateConfig(@RequestBody PaymentRateConfig paymentRateConfig) {
        try {
            boolean success = assetAdminService.savePaymentRateConfig(paymentRateConfig);
            if (success) {
                return AjaxJson.success("保存成功");
            } else {
                return AjaxJson.error("保存失败");
            }
        } catch (Exception e) {
            log.error("保存费率配置失败", e);
            return AjaxJson.error("保存失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新费率配置")
    @RequiresPermissions("asset:rate:platform:view")
    @PutMapping("/rateConfig/update")
    public AjaxJson updatePaymentRateConfig(@RequestBody PaymentRateConfig paymentRateConfig) {
        try {
            boolean success = assetAdminService.updatePaymentRateConfig(paymentRateConfig);
            if (success) {
                return AjaxJson.success("更新成功");
            } else {
                return AjaxJson.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新费率配置失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据类型和ID更新费率配置")
//    @RequiresPermissions("asset:rate:platform:view")
    @PutMapping("/rateConfig/updateByTypeAndId")
    public AjaxJson updatePaymentRateConfigByTypeAndId(
            @ApiParam("主键ID") @RequestParam Long id,
            @ApiParam("支付类型(1:入金wx 2:提现 3：IM内部)") @RequestParam Integer paymentType,
            @ApiParam("费率") @RequestParam BigDecimal rate,
            @ApiParam("更新人") @RequestParam(required = false) String updateBy) {
        try {
            boolean success = assetAdminService.updatePaymentRateConfigByTypeAndId(id, paymentType, rate, updateBy);
            if (success) {
                return AjaxJson.success("更新成功");
            } else {
                return AjaxJson.error("更新失败");
            }
        } catch (Exception e) {
            log.error("根据类型和ID更新费率配置失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除费率配置")
//    @RequiresPermissions("rateConfig:rateConfig:remove")
    @DeleteMapping("/rateConfig/remove")
    public AjaxJson deletePaymentRateConfig(@ApiParam("主键ID") @RequestParam Long id) {
        try {
            boolean success = assetAdminService.deletePaymentRateConfig(id);
            if (success) {
                return AjaxJson.success("删除成功");
            } else {
                return AjaxJson.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除费率配置失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }


    // ==================== 入金记录管理 ====================

    @ApiOperation("分页查询入金记录")
    @GetMapping("/paymentRecord/page")
    public AjaxJson getPaymentRecordPage(
            PaymentRecordQueryDto queryDto
    ) {

        try {

            Page<PaymentRecord> page = assetAdminService.getPaymentRecordPage(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询入金记录失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询入金记录")
    @RequiresPermissions("asset:fund:payment:view")
//    @GetMapping("/paymentRecord/{id}")
//    public AjaxJson getPaymentRecordById(@ApiParam("主键ID") @PathVariable Long id) {

    @GetMapping("/paymentRecord/queryById")
    public AjaxJson getPaymentRecordById(@ApiParam("主键ID") @RequestParam Long id) {
        try {
            PaymentRecord record = assetAdminService.getPaymentRecordById(id);
            if (record == null) {
                return AjaxJson.error("入金记录不存在");
            }
            return AjaxJson.success().put("record", record);
        } catch (Exception e) {
            log.error("查询入金记录失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("分页查询交易记录")
//    @RequiresPermissions("property:transaction:page")
    @GetMapping("/paymentTransaction/page")
    public AjaxJson getPaymentTransactionPage(
            PaymentTransactionQueryDto queryDto
    ) {

        try {
            Page<PaymentTransaction> page = assetAdminService.getPaymentTransactionPage(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询交易记录失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询交易记录")
    @RequiresPermissions("asset:trade:streams:view")
    @GetMapping("/paymentTransaction/{id}")
    public AjaxJson getPaymentTransactionById(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            PaymentTransaction transaction = assetAdminService.getPaymentTransactionById(id);
            if (transaction == null) {
                return AjaxJson.error("交易记录不存在");
            }
            return AjaxJson.success().put("transaction", transaction);
        } catch (Exception e) {
            log.error("查询交易记录失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("分页查询交易记录IM内部")
//    @RequiresPermissions("property:transaction:page")
    @GetMapping("/paymentTransactionIm/page")
    public AjaxJson getPaymentTransactionPageIM(
            PaymentTransactionQueryDto queryDto
    ) {

        try {
            Page<PaymentTransaction> page = assetAdminService.getPaymentTransactionPageIm(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询交易记录失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询交易记录IM内部")
    @RequiresPermissions("asset:trade:im-streams:view")
//    @RequiresPermissions("asset:trade:streams:view")
    @GetMapping("/paymentTransactionIm/{id}")
    public AjaxJson getPaymentTransactionByIdIm(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            PaymentTransaction transaction = assetAdminService.getPaymentTransactionByIdIm(id);
            if (transaction == null) {
                return AjaxJson.error("交易记录不存在");
            }
            return AjaxJson.success().put("transaction", transaction);
        } catch (Exception e) {
            log.error("查询交易记录失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }



    // ==================== 提现申请管理 ====================

    @ApiOperation("分页查询提现申请")
    @GetMapping("/withdrawApply/page")
    public AjaxJson getWithdrawApplyPage(
            WithdrawApplyQueryDto queryDto
    ) {

        try {
            Page<WithdrawApply> page = assetAdminService.getWithdrawApplyPage(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询提现申请失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("分页查询提现金额10000及以上的提现申请")
    @GetMapping("/withdrawApply/pageAmount10000")
    public AjaxJson getWithdrawApplyPageAmount10000(WithdrawApplyQueryDto queryDto) {
        try {
            queryDto.setAmountMin(new BigDecimal("10000"));
            Page<WithdrawApply> page = assetAdminService.getWithdrawApplyPage(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询大额提现申请失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("查询状态为申请中(4)的提现记录列表-待审核")
    @GetMapping("/withdrawApply/pendingAudit")
    public AjaxJson listWithdrawApplyPendingAudit() {
        try {
            java.util.List<WithdrawApply> list = assetAdminService.listWithdrawApplyByStatus(WithdrawStatusEnum.APPLY.getCode());
            return AjaxJson.success().put("list", list);
        } catch (Exception e) {
            log.error("查询待审核提现列表失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询提现申请")
    @RequiresPermissions("asset:fund:withdraw:view")
//    @RequiresPermissions("property:withdraw:view")
    @GetMapping("/withdrawApply/{id}")
    public AjaxJson getWithdrawApplyById(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            WithdrawApply apply = assetAdminService.getWithdrawApplyById(id);
            if (apply == null) {
                return AjaxJson.error("提现申请不存在");
            }
            return AjaxJson.success().put("apply", apply);
        } catch (Exception e) {
            log.error("查询提现申请失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新提现申请状态")
    @RequiresPermissions("asset:fund:withdraw:review")
//    @RequiresPermissions("property:withdraw:edit")
    @PutMapping("/withdrawApply/status")
    public AjaxJson updateWithdrawApplyStatus(
            @ApiParam("主键ID") @RequestParam Long id,
            @ApiParam("状态") @RequestParam Integer status,
            @ApiParam("谷歌身份验证码（手动审核模式时必填）") @RequestParam(required = false) String inputGoogleCode,
            @ApiParam("审核人 BSC 钱包地址（0x 开头）") @RequestParam(required = false) String walletAddress,
            @ApiParam("钱包签名（对 withdraw_audit:id:status 的 personal_sign）") @RequestParam(required = false) String walletSignature,
            @ApiParam("备注（非必填）") @RequestParam(required = false) String remark,
            @ApiParam("更新人（非必填）") @RequestParam(required = false) String updateBy
    ) {

//        if (StringUtils.isBlank(inputGoogleCode)) {
//            return AjaxJson.error("请输入谷歌验证码");
//        }
//        UserUtils.getCasUser();
//        String randomSecretKey = "SZKNPRZ67USOFJORZSMWQ432HQRSVWLT";
//        User user = userService.selectByTwoFactorCode(randomSecretKey);
//        if (Objects.isNull(user)) {
//            return AjaxJson.error("请绑定google后再试");
//        }
//        String rightCode = GoogleAuthenticationToolUtil.getTOTPCode(user.getTwoFactorCode());
//        log.info("更新提现申请状态-校验时,rightCode:{},inputGoogleCode:{}", rightCode, inputGoogleCode);
//        if (!rightCode.equals(inputGoogleCode)) {
//            log.info("谷歌验证码错误");
//            return AjaxJson.error("谷歌验证码错误");
//        }

        // 手动审核模式：使用钱包地址验签替代原谷歌验证
        ButtonConfig auditConfig = buttonConfigMapper.selectByButtonKey(BUTTON_KEY_WITHDRAW_AUDIT);
        boolean needWalletVerify = (auditConfig != null && auditConfig.getButtonStatus() != null && auditConfig.getButtonStatus() == 1);
        if (needWalletVerify) {
        // TODO 谷歌验证码审核
        if (StringUtils.isBlank(inputGoogleCode)) {
            return AjaxJson.error("请输入谷歌验证码");
        }
        UserUtils.getCasUser();
        String randomSecretKey = "SZKNPRZ67USOFJORZSMWQ432HQRSVWLT";
        User user = userService.selectByTwoFactorCode(randomSecretKey);
        if (Objects.isNull(user)) {
            return AjaxJson.error("请绑定google后再试");
        }
        String rightCode = GoogleAuthenticationToolUtil.getTOTPCode(user.getTwoFactorCode());
        log.info("更新提现申请状态-校验时,rightCode:{},inputGoogleCode:{}", rightCode, inputGoogleCode);
        if (!rightCode.equals(inputGoogleCode)) {
            log.info("谷歌验证码错误");
            return AjaxJson.error("谷歌验证码错误");
        }
            // TODO 审核钱包签名代码
            /*
            if (StringUtils.isBlank(walletAddress) || StringUtils.isBlank(walletSignature)) {
                return AjaxJson.error("手动审核需使用 BSC 钱包签名，请传入钱包地址与签名");
            }
            String message = String.format("withdraw_audit:%d:%d", id, status);
            if (!BscWalletSignUtil.verifyPersonalSign(walletAddress, message, walletSignature)) {
                log.warn("提现审核钱包验签失败, id:{}, walletAddress:{}", id, walletAddress);
                return AjaxJson.error("钱包地址签名验证失败");
            }
            String allowedAddress = withdrawalConfig.getAuditWalletAddress();
            if (StringUtils.isNotBlank(allowedAddress)) {
                String normalizedAllowed = BscWalletSignUtil.normalizeAddress(allowedAddress);
                String normalizedSigner = BscWalletSignUtil.normalizeAddress(walletAddress);
                if (!normalizedAllowed.equals(normalizedSigner)) {
                    log.warn("提现审核钱包地址不在预留白名单, 签名地址:{}, 预留地址:{}", walletAddress, allowedAddress);
                    return AjaxJson.error("当前钱包地址无审核权限");
                }
            }
            log.info("提现审核钱包验签通过, id:{}, walletAddress:{}", id, walletAddress);
            */
        }

        if (status.equals(WithdrawStatusEnum.REJECT.getCode()) && StringUtils.isEmpty(remark)) {
            return AjaxJson.error("状态为拒绝时，备注不能为空");
        }

        try {
            boolean success = assetAdminService.auditWithdraw(id, status, remark, updateBy);
            if (success) {
                return AjaxJson.success("更新成功");
            } else {
                return AjaxJson.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新提现申请状态失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新提现申请状态和外部提现ID")
    @PutMapping("/withdrawApply/statusAndWithdrawalId")
    public AjaxJson updateWithdrawApplyStatusAndWithdrawalId(
            @ApiParam("主键ID") @RequestParam Long id,
            @ApiParam("状态") @RequestParam Integer status,
            @ApiParam("外部提现ID") @RequestParam Long withdrawalId,
            @ApiParam("更新人") @RequestParam String updateBy) {

        try {
            boolean success = assetAdminService.updateWithdrawApplyStatusAndWithdrawalId(id, status, withdrawalId, updateBy);
            if (success) {
                return AjaxJson.success("更新成功");
            } else {
                return AjaxJson.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新提现申请状态和外部提现ID失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    // ==================== 统计信息 ====================

    @ApiOperation("获取资产统计信息")
    @GetMapping("/statistics")
    public AjaxJson getAssetStatistics(
            @ApiParam("开始日期") @RequestParam(required = false) Date startDate,
            @ApiParam("结束日期") @RequestParam(required = false) Date endDate) {

        try {
            AssetStatisticsDto statistics = assetAdminService.getAssetStatistics(startDate, endDate);
            return AjaxJson.success().put("statistics", statistics);
        } catch (Exception e) {
            log.error("获取资产统计信息失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取今日资产统计信息")
    @GetMapping("/statistics/today")
    public AjaxJson getTodayAssetStatistics() {
        try {
            AssetStatisticsDto statistics = assetAdminService.getTodayAssetStatistics();
            return AjaxJson.success().put("statistics", statistics);
        } catch (Exception e) {
            log.error("获取今日资产统计信息失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }
}
