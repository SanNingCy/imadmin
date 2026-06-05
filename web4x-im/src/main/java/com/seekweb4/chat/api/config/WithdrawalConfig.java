package com.seekweb4.chat.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author coderpwh
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "withdrawal")
public class WithdrawalConfig implements Serializable {

    private String appId;

    private String privateKey;

    private String publicKey;

    private String wxPayUrl;

    /**
     * 提现审核预留的 BSC 钱包地址（0x 开头），手动审核时仅允许该地址验签通过。
     * 配置后：审核接口需传 walletAddress + walletSignature，且签名恢复出的地址需与此一致。
     * 不配置时：仍要求传钱包签名并验签通过，但不校验是否为本地址（便于先联调）。
     */
    private String auditWalletAddress;

}
