package com.seekweb4.chat.api.constant;

import com.seekweb4.chat.api.config.AssetConfig;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;
import java.io.Serializable;

/**
 * @author coderpwh
 */
@Configuration
public class WXRequestConstant implements Serializable {


    @Resource
    private AssetConfig assetConfig;


    /***
     * IM 入金WX
     */
    private final String DEPOSIT_WX_URL = "/api/im/depositWx";


    /**
     * IM 获取用户信息
     */
    private final String USER_INFO_URL = "/api/im/getUserInfoByAddress";

    /**
     * IM 提现
     */
    private final String WITHDRAW_WX_URL = "/api/withdrawal/withdrawal";


    /**
     * IM 提现结果
     */
    private final String WITHDRAW_RESULT_URL = "/api/withdrawal/result";

    /**
     * 获取卡类型(获取链桥卡密类型)
     */
    private final String KEY_Card_URL = "/api/imAd/getKeyCardType";
//    private final String KEY_Card_URL = "/admin/keyCardType/getKeyCardType";
//    private final String KEY_Card_URL = "/project/keyCardType/getKeyCardType";

    /**
     * 会员码，同步链桥接口
     */
    private final String SYNC_FROM_URL = "/api/imAd/syncFromIm";
//    private final String SYNC_FROM_URL = "/admin/keyCard/syncFromIm";
//    private final String SYNC_FROM_URL = "/project/keyCard/syncFromIm";

    /**
     * 获取链桥卡密类型 URL（供会员码获取类型接口调用）
     */
    public String getKeyCardTypeUrl() {
        return assetConfig.getWxPayUrl() + KEY_Card_URL;
    }

    /**
     * 会员码同步到链桥同步接口
     */
    public String getSyncFromTypeUrl() {
        return assetConfig.getWxPayUrl() + SYNC_FROM_URL;
    }


    /***
     * IM入金WX
     * @return
     */
    public String getDepositWxUrl() {
        return assetConfig.getWxPayUrl() + DEPOSIT_WX_URL;
    }


    /***
     *  获取用户信息URL
     * @return
     */
    public String getUserInfoUrl() {
        return assetConfig.getWxPayUrl() + USER_INFO_URL;
    }





}
