package com.seekweb4.chat.asset.util;

import com.seekweb4.chat.asset.vo.AddressKeyVO;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.math.BigInteger;

import static cn.hutool.core.util.IdUtil.randomUUID;

/**
 * @author coderpwh
 */
@Slf4j
public class AddressKeyUtil {

    public  AddressKeyVO getEthAddress() {
        AddressKeyVO addressKeyVO = new AddressKeyVO();
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();
            String privateKey = privateKeyInDec.toString(16);
            if (privateKey.length() != 64) {
                return getEthAddress();
            }
            WalletFile aWallet = Wallet.createLight(randomUUID(), ecKeyPair);
            String address = aWallet.getAddress();
            if (address.startsWith("0x")) {
                address = address.substring(2).toLowerCase();
            } else {
                address = address.toLowerCase();
            }
            address = "0x" + address;
            addressKeyVO.setAddress(address.toLowerCase());
            addressKeyVO.setKey(privateKey);
        } catch (Exception e) {
            log.error("获取钱包地址失败：{}", e.getMessage());
        }
        return addressKeyVO;
    }

    /***
     替换地址前缀，改为w开头的钱包内部地址
     ***/
    public static String modifyAddressPrefix(String address) {
        if (address != null && (address.startsWith("0x") || address.startsWith("0X"))) {
            return "w" + address.substring(2);
        }
        return address;
    }

    public static void main(String[] args) {
        AddressKeyVO addressKeyVO = new AddressKeyUtil().getEthAddress();
        System.out.println(addressKeyVO.getAddress());
        System.out.println(addressKeyVO.getKey());
        System.out.println(modifyAddressPrefix(addressKeyVO.getAddress()));
    }

}
