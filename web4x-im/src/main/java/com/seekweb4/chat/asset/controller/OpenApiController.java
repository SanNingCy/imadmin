package com.seekweb4.chat.asset.controller;

import com.seekweb4.chat.api.utils.sign.RsaSignVerify;
import com.seekweb4.chat.asset.dto.request.TransactionWXRequestDTO;
import com.seekweb4.chat.asset.dto.request.WithdrawalRequestDTO;
import com.seekweb4.chat.asset.service.AssetUserService;
import com.seekweb4.chat.asset.vo.request.TransactionRequestVO;
import com.seekweb4.chat.asset.vo.request.UserInfoRequestVO;
import com.seekweb4.chat.asset.vo.response.UserInfoResponseVO;
import com.seekweb4.chat.common.json.AjaxJson;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;


/**
 * 对外接口
 *
 * @author coderpwh
 */
@RestController
@RequestMapping("/asset/wx")
public class OpenApiController {


    @Resource
    private AssetUserService assetUserService;


    /**
     * 获取用户信息
     *
     * @param userInfoRequestVO
     */
    @RsaSignVerify
    @RequestMapping(value = "/userInfo", method = RequestMethod.POST)
    public AjaxJson getUserInfo(@RequestBody @Validated UserInfoRequestVO userInfoRequestVO) {
        UserInfoResponseVO userInfoResponseVO = assetUserService.getUserInfo(userInfoRequestVO);
        return AjaxJson.success().put("data", userInfoResponseVO);
    }


    @RsaSignVerify
    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public AjaxJson transaction(@RequestBody @Validated TransactionRequestVO transactionRequestVO) {
        Boolean result = assetUserService.transaction(transactionRequestVO);
        if (result) {
            return AjaxJson.success();
        } else {
            return AjaxJson.error(500, "交易失败");
        }
    }


    /***
     * 测试入金
     * @param request
     * @return
     */

    @RequestMapping(value = "/transactionByWx", method = RequestMethod.POST)
    public AjaxJson transactionByWx(@RequestBody TransactionWXRequestDTO request) {
        assetUserService.transactionByWx(request);
        return AjaxJson.success();

    }

    @RequestMapping(value = "/getUserInfoByAddress", method = RequestMethod.GET)
    public AjaxJson getUserInfoByAddress(String address) {
        assetUserService.getUserInfoByAddress(address);
        return AjaxJson.success();

    }

    @RequestMapping(value = "/withdrawal", method = RequestMethod.POST)
    public AjaxJson withdrawal(@RequestBody WithdrawalRequestDTO request) {
        assetUserService.withdrawal(request);
        return AjaxJson.success();

    }

    @RequestMapping(value = "/withdrawal/result", method = RequestMethod.GET)
    public AjaxJson getUserInfoByAddress(Long withdrawalId) {
        assetUserService.getWithdrawalResult(withdrawalId);
        return AjaxJson.success();

    }


}
