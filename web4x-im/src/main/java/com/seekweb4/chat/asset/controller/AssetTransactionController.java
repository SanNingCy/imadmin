package com.seekweb4.chat.asset.controller;

import com.alibaba.fastjson2.JSON;
import com.seekweb4.chat.api.error.BizException;
import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.api.utils.MemberUtils;
import com.seekweb4.chat.api.utils.PageResult;
import com.seekweb4.chat.asset.service.AssetTransactionService;
import com.seekweb4.chat.asset.vo.request.*;
import com.seekweb4.chat.asset.vo.response.*;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.member.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 资产交易控制类
 *
 * @author coderpwh
 */
@Slf4j
@RestController
@RequestMapping("/asset/transaction")
public class AssetTransactionController {


    private static final BigDecimal TWENTY = new BigDecimal("20");
    @Resource
    private AssetTransactionService assetTransactionService;


    /***
     * 获取资产交易信息
     * @return
     */
    @PostMapping("/initialize")
    public AjaxJson transactionInitialize() {
        Member member = MemberUtils.getMember();
        if (Objects.isNull(member)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }
        TransactionInitializeResponseVO transactionInitializeResponseVO = assetTransactionService.transactionInitialize(member);
        return AjaxJson.success().put("data", transactionInitializeResponseVO);
    }


    /**
     * 获取费率信息
     *
     * @param request
     * @return
     */
    @PostMapping("/rateInfo")
    public AjaxJson getRateInfo(@RequestBody ReqJson req) {
        reqValidator(req, "amount");
        Member member = MemberUtils.getMember();
        if (Objects.isNull(member)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }
        TransactionRateRequestVO transactionRate = JSON.parseObject(JSON.toJSONString(req), TransactionRateRequestVO.class);
        TransactionRateResponseVO transactionRateResponseVO = assetTransactionService.getRateInfo(transactionRate);
        return AjaxJson.success().put("data", transactionRateResponseVO);
    }


    @PostMapping("/personInfo")
    public AjaxJson getTransactionPersonInfo(@RequestBody ReqJson req) {
        reqValidator(req, "receivingAddress");
        TransactionPersonInfoRequestVO request = JSON.parseObject(JSON.toJSONString(req), TransactionPersonInfoRequestVO.class);
        TransactionPersonInfoResponseVO personInfo = assetTransactionService.getTransactionPersonInfo(request.getReceivingAddress());
        if (personInfo != null) {
            return AjaxJson.success().put("data", personInfo);
        } else {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "用户不存在");
        }
    }


    @PostMapping("/add")
    public AjaxJson addTransaction(@RequestBody ReqJson req) {
        reqValidator(req, "receivingAddress", "amount");
        Member member = MemberUtils.getMember();
        if (Objects.isNull(member)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }
        TransactionAddRequestVO requestVO = JSON.parseObject(JSON.toJSONString(req), TransactionAddRequestVO.class);
        if (requestVO.getAmount().compareTo(TWENTY) <0) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "金额不能低于20");
        }
        TransactionAddResponseVO transactionAddResponseVO = assetTransactionService.addTransaction(requestVO, member.getId());
        if (transactionAddResponseVO.getErrorMessage() != null) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), transactionAddResponseVO.getErrorMessage());
        }
        return AjaxJson.success().put("data", transactionAddResponseVO);
    }


    @PostMapping("/page")
    public AjaxJson getTransactionPage(@RequestBody ReqJson req) {
        Member member = MemberUtils.getMember();
        if (Objects.isNull(member)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }
        Page<TransactionPageResponseVO> page = assetTransactionService.getTransactionPage(req, member.getId());
        return AjaxJson.success().setDataList(page.getList()).put("totalCount", page.getCount()).put("totalPage", page.getTotalPage());
    }

    @PostMapping("/detail")
    public AjaxJson getTransactionDetail(@RequestBody ReqJson req) {
        reqValidator(req, "id");
        TransactionDetailRequestVO detail = JSON.parseObject(JSON.toJSONString(req), TransactionDetailRequestVO.class);
        Member member = new Member();
        if (Objects.isNull(member)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }
        TransactionDetailResponseVO transactionDetailResponseVO = assetTransactionService.getTransactionDetail(detail.getId());
        return AjaxJson.success().put("data", transactionDetailResponseVO);
    }


    /**
     * 请求参数非空校验
     *
     * @param req
     * @return
     */
    private void reqValidator(ReqJson req, String... keys) {
        for (String key : keys) {
            if (StringUtils.isBlank(req.getString(key))) {
                throw new BizException(key + "不能为空");
            }
        }
    }


}
