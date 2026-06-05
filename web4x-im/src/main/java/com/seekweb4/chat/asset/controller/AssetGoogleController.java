package com.seekweb4.chat.asset.controller;

import com.alibaba.fastjson2.JSON;
import com.google.zxing.WriterException;
import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.api.utils.MemberUtils;
import com.seekweb4.chat.asset.constant.RedisKeyConstant;
import com.seekweb4.chat.asset.util.google.GoogleAuthenticationTool;
import com.seekweb4.chat.asset.vo.request.GoogleValidateRequestVO;
import com.seekweb4.chat.asset.vo.response.GoogleCodeResponseVO;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * @author coderpwh
 */
@Slf4j
@RestController
@RequestMapping("/asset/google")
public class AssetGoogleController {


    @Resource
    private StringRedisUtils stringRedisUtils;

    @Resource
    private MemberService memberService;

    /**
     * 获得两步验证二维码
     */
    @PostMapping("/getTwoStepVerificationQRCode")
    public AjaxJson toBindingGoogleTwoFactorValidate() {
        GoogleCodeResponseVO googleCodeResponseVO = new GoogleCodeResponseVO();
        String twitterUid = null;
        Member member = MemberUtils.getMember();
        if (member != null) {
            twitterUid = member.getId();
        }

        String randomSecretKey = GoogleAuthenticationTool.generateSecretKey();
        log.info("google生成的秘钥为:{}", randomSecretKey);
        //此步设置的参数就是App扫码后展示出来的参数
        String qrCodeString = GoogleAuthenticationTool.spawnScanQRString(String.valueOf(twitterUid), randomSecretKey, "应用信息");
        String qrCodeImageBase64 = null;
        try {
            qrCodeImageBase64 = GoogleAuthenticationTool.createQRCode(qrCodeString, null, 512, 512);
        } catch (WriterException | IOException e) {
            log.error("生成二维码失败,异常信息为:{}", e.getMessage());
        }
        String key = RedisKeyConstant.GOOGLE_INFOMON_KEY + twitterUid;
        stringRedisUtils.set(key, randomSecretKey);
        googleCodeResponseVO.setQrCodeImageBase64(qrCodeImageBase64);
        googleCodeResponseVO.setRandomSecretKey(randomSecretKey);
        return AjaxJson.success().put("data", googleCodeResponseVO);

    }

    /**
     * 执行谷歌两步验证绑定
     *
     * @return
     */

    @PostMapping("/bindingTwoFactorValidate")
    public AjaxJson bindingTwoFactorValidate(@RequestBody ReqJson req) {
        GoogleValidateRequestVO googleValidateRequestVO = JSON.parseObject(JSON.toJSONString(req), GoogleValidateRequestVO.class);
        String twitterUid = "";
        Member member = MemberUtils.getMember();
        if (member != null) {
            twitterUid = member.getId();
        }

        /*if (StringUtils.isNotBlank(member.getTwoFactorCode())) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "不可重复绑定");
        }*/

        String inputGoogleCode = googleValidateRequestVO.getInputGoogleCode();

        String key = RedisKeyConstant.GOOGLE_INFOMON_KEY + twitterUid;
        String randomSecretKey = String.valueOf(stringRedisUtils.get(key));
        log.info("校验时,randomSecretKey:{}", randomSecretKey);
        String rightCode = GoogleAuthenticationTool.getTOTPCode(randomSecretKey);
        log.info("校验时,rightCode:{},inputGoogleCode:{}", rightCode, inputGoogleCode);
        if (!rightCode.equals(inputGoogleCode)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "验证码无效");
        }
        member.setTwoFactorCode(randomSecretKey);
        member.setTwoFactorTime(new Date());

        int count = memberService.updateTwoFactorCode(member);

        if (count >= 1) {
            return AjaxJson.success();
        }
        return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "验证码错误”");

    }

    /**
     * 验证谷歌两步验证码的接口
     *
     * @param inputGoogleCode 用户输入的谷歌验证码
     * @return CheersResponse 包含验证结果
     */

    @PostMapping("/validateGoogleTwoFactor")
    public AjaxJson validateGoogleTwoFactor(@RequestBody ReqJson req) {
        GoogleValidateRequestVO googleValidateRequestVO = JSON.parseObject(JSON.toJSONString(req), GoogleValidateRequestVO.class);
        Member member = MemberUtils.getMember();
        if (Objects.isNull(member)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }

//        String key = RedisKeyConstant.GOOGLE_INFOMON_KEY + twitterUid;
//        String randomSecretKey = String.valueOf(stringRedisUtils.get(key));

        String randomSecretKey = member.getTwoFactorCode();
        String rightCode = GoogleAuthenticationTool.getTOTPCode(randomSecretKey);
        log.info("校验时,rightCode:{},inputGoogleCode:{}", rightCode, googleValidateRequestVO.getInputGoogleCode());

        // 验证用户输入的谷歌验证码是否正确
        if (!rightCode.equals(googleValidateRequestVO.getInputGoogleCode())) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "验证码无效");
        }
        return AjaxJson.success();
    }


}
