package com.seekweb4.chat.asset.util.wallet;

import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * BSC 钱包签名验证工具（地址格式：0x 开头的 BSC/以太坊地址）。
 * 使用以太坊/BSC 常用的 personal_sign(EIP-191) 签名格式，验证签名对应的钱包地址。
 */
@Slf4j
public class BscWalletSignUtil {

    /**
     * 校验 personal_sign 签名是否由指定地址签出。
     *
     * @param address       钱包地址（0x 开头或不带 0x 均可，大小写不敏感）
     * @param message       前端签名的原文（必须与前端签名时完全一致）
     * @param signatureHex  签名值（16 进制字符串，通常为 65 字节）
     * @return true 表示验签通过
     */
    public static boolean verifyPersonalSign(String address, String message, String signatureHex) {
        try {
            if (address == null || message == null || signatureHex == null) {
                return false;
            }
            String normalizedAddress = normalizeAddress(address);

            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] signatureBytes = Numeric.hexStringToByteArray(signatureHex);
            if (signatureBytes.length != 65) {
                log.warn("签名长度不为 65 字节，实际为: {}", signatureBytes.length);
                return false;
            }

            // r(32) + s(32) + v(1)
            byte v = signatureBytes[64];
            if (v < 27) {
                v = (byte) (v + 27);
            }

            Sign.SignatureData signatureData = new Sign.SignatureData(
                    v,
                    Arrays.copyOfRange(signatureBytes, 0, 32),
                    Arrays.copyOfRange(signatureBytes, 32, 64)
            );

            // personal_sign 使用带前缀的 signedPrefixedMessageToKey
            BigInteger publicKey = Sign.signedPrefixedMessageToKey(messageBytes, signatureData);
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);
            String normalizedRecovered = normalizeAddress(recoveredAddress);

            boolean match = normalizedAddress.equals(normalizedRecovered);
            if (!match) {
                log.warn("钱包验签失败，期望地址: {}，恢复地址: {}", normalizedAddress, normalizedRecovered);
            }
            return match;
        } catch (Exception e) {
            log.error("BSC 钱包签名验证异常", e);
            return false;
        }
    }

    /**
     * 标准化 BSC 地址（小写、去掉 0x 前缀），用于比较。
     */
    public static String normalizeAddress(String address) {
        if (address == null) {
            return "";
        }
        String addr = address.trim();
        if (addr.startsWith("0x") || addr.startsWith("0X")) {
            addr = addr.substring(2);
        }
        return addr.toLowerCase();
    }
}

