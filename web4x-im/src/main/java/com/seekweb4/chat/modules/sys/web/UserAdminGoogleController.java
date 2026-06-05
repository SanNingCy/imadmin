package com.seekweb4.chat.modules.sys.web;

import com.google.zxing.WriterException;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.sys.entity.User;
import com.seekweb4.chat.modules.sys.service.UserService;
import com.seekweb4.chat.modules.sys.utils.GoogleAuthenticationToolUtil;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import com.seekweb4.chat.modules.sys.utils.constant.RedisKeyUtilConstant;
import com.seekweb4.chat.modules.sys.vo.GoogleCodeUtilResponseVO;
import com.seekweb4.chat.modules.sys.vo.GoogleValidateUtilRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;


/**
 * @author coderpwh
 */
@Slf4j
@RestController
@RequestMapping("/userAdmin/google")
public class UserAdminGoogleController {


    @Resource
    private StringRedisUtils stringRedisUtils;

    @Resource
    private UserService userService;


    /**
     * 获得两步验证二维码
     */
    @PostMapping("/getTwoStepVerificationQRCodeAdmin")
    public AjaxJson toBindingGoogleTwoFactorValidate() {
        GoogleCodeUtilResponseVO googleCodeResponseVO = new GoogleCodeUtilResponseVO();
        String twitterUid = null;

        User user = UserUtils.getUser();

        if (user != null) {
            twitterUid = user.getId();
        }

//        String randomSecretKey = GoogleAuthenticationToolUtil.generateSecretKey();
        String randomSecretKey = "SZKNPRZ67USOFJORZSMWQ432HQRSVWLT";
        log.info("google生成的秘钥为:{}", randomSecretKey);
        //此步设置的参数就是App扫码后展示出来的参数
        String qrCodeString = GoogleAuthenticationToolUtil.spawnScanQRString(String.valueOf(twitterUid), randomSecretKey, "应用信息");
        String qrCodeImageBase64 = null;
        try {
            qrCodeImageBase64 = GoogleAuthenticationToolUtil.createQRCode(qrCodeString, null, 512, 512);
        } catch (WriterException | IOException e) {
            log.error("生成二维码失败,异常信息为:{}", e.getMessage());
        }
        String key = RedisKeyUtilConstant.GOOGLE_INFOMON_KEY + twitterUid;
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

    @PostMapping("/bindingTwoFactorValidateAdmin")
    public AjaxJson bindingTwoFactorValidate(GoogleValidateUtilRequestVO requestVO) {
        String twitterUid = "";
        User user = UserUtils.getUser();
        if (user != null) {
            twitterUid = user.getId();
        }

        String inputGoogleCode = requestVO.getInputGoogleCode();

        String key = RedisKeyUtilConstant.GOOGLE_INFOMON_KEY + twitterUid;
        String randomSecretKey = String.valueOf(stringRedisUtils.get(key));
        log.info("校验时,randomSecretKey:{}", randomSecretKey);
        String rightCode = GoogleAuthenticationToolUtil.getTOTPCode(randomSecretKey);
        log.info("校验时,rightCode:{},inputGoogleCode:{}", rightCode, inputGoogleCode);
        if (!rightCode.equals(inputGoogleCode)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "验证码无效");
        }
        user.setTwoFactorCode(randomSecretKey);
        user.setTwoFactorTime(new Date());

        int count = userService.updateTwoFactorCode(user);
        if (count > 0) {
            return AjaxJson.success("绑定成功");
        }
        return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "验证码错误”");

    }


    @PostMapping("/validateGoogleTwoFactorAdmin")
    public AjaxJson validateGoogleTwoFactor(GoogleValidateUtilRequestVO requestVO) {
        User user = UserUtils.getUser();
        if (Objects.isNull(user)) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请先登录");
        }

        if(StringUtils.isBlank(user.getTwoFactorCode())){
           user =  userService.selectByUserId(user.getId());
        }

        String randomSecretKey = user.getTwoFactorCode();
        String rightCode = GoogleAuthenticationToolUtil.getTOTPCode(randomSecretKey);
        log.info("校验时,rightCode:{},inputGoogleCode:{}", rightCode, requestVO.getInputGoogleCode());

        // 验证用户输入的谷歌验证码是否正确
        if (!rightCode.equals(requestVO.getInputGoogleCode())) {
            return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "验证码无效");
        }
        return AjaxJson.success();
    }


}
