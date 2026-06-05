package com.seekweb4.chat.api.constant;

import com.seekweb4.chat.api.config.WithdrawalConfig;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;
import java.io.Serializable;

/**
 * @author coderpwh
 */
@Configuration
public class WithdrawalRequestConstant implements Serializable {


    @Resource
    private WithdrawalConfig withdrawalConfig;


    /**
     * IM 提现
     */
    private final String WITHDRAW_WX_URL = "/api/withdrawal/create";
//    private final String WITHDRAW_WX_URL = "/api/withdrawal/withdrawal";


    /**
     * IM 提现结果
     */
    private final String WITHDRAW_RESULT_URL = "/api/withdrawal/result";



     /***
     * IM提现
     * @return
     */
    public String getWithdrawUrl() {
        return withdrawalConfig.getWxPayUrl() + WITHDRAW_WX_URL;
    }

    /***
     * IM提现结果
     * @return
     */
    public String getWithdrawResultUrl() {
        return withdrawalConfig.getWxPayUrl() + WITHDRAW_RESULT_URL;
    }


}
