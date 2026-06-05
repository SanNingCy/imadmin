package com.seekweb4.chat.asset.controller;

import com.alibaba.fastjson2.JSON;
import com.seekweb4.chat.api.error.BizException;
import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.api.utils.MemberUtils;
import com.seekweb4.chat.asset.service.AssetWithdrawService;
import com.seekweb4.chat.asset.vo.request.WithdrawAddRequestVO;
import com.seekweb4.chat.asset.vo.request.WithdrawDetailRequestVO;
import com.seekweb4.chat.asset.vo.response.WithdrawAddResponseVO;
import com.seekweb4.chat.asset.vo.response.WithdrawApplyResponseDTO;
import com.seekweb4.chat.asset.vo.response.WithdrawDetailResponseVO;
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
import java.util.Objects;

/**
 * @author coderpwh
 */
@Slf4j
@RestController
@RequestMapping("/asset/withdraw")
public class AssetWithdrawController {

    private static final BigDecimal TWENTY = new BigDecimal("20");
    @Resource
    private AssetWithdrawService assetWithdrawService;


    @PostMapping("/page")
    public AjaxJson getWithdrawPage(@RequestBody ReqJson req) {
        Member member = MemberUtils.getMember();
        if (Objects.isNull(member)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }
        Page<WithdrawApplyResponseDTO> page = assetWithdrawService.getWithdrawPage(req, member.getId());
        return AjaxJson.success().setDataList(page.getList()).put("totalCount", page.getCount()).put("totalPage", page.getTotalPage());
    }


    @PostMapping("/detail")
    public AjaxJson getWithdrawDetail(@RequestBody ReqJson req) {
        reqValidator(req, "id");
        Member member = MemberUtils.getMember();
        if (Objects.isNull(member)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }
        WithdrawDetailRequestVO detail = JSON.parseObject(JSON.toJSONString(req), WithdrawDetailRequestVO.class);

        WithdrawDetailResponseVO withdrawDetailResponseVO = assetWithdrawService.getWithdrawDetail(detail.getId());
        return AjaxJson.success().put("data", withdrawDetailResponseVO);
    }


    @PostMapping("/add")
    public AjaxJson addWithdraw(@RequestBody ReqJson req) {
        reqValidator(req, "receivingAddress", "amount");
        Member member = MemberUtils.getMember();
        if (Objects.isNull(member)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }
        WithdrawAddRequestVO requestVO = JSON.parseObject(JSON.toJSONString(req), WithdrawAddRequestVO.class);
        if (requestVO.getAmount().compareTo(TWENTY) <0) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "金额不能低于20");
        }
        WithdrawAddResponseVO withdrawAddResponseVO = assetWithdrawService.addWithdraw(requestVO, member.getId());
        return AjaxJson.success().put("data", withdrawAddResponseVO);
    }


    private void reqValidator(ReqJson req, String... keys) {
        for (String key : keys) {
            if (StringUtils.isBlank(req.getString(key))) {
                throw new BizException(key + "不能为空");
            }
        }
    }


}
