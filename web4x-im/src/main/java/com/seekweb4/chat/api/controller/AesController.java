package com.seekweb4.chat.api.controller;

import com.seekweb4.chat.api.req.AesUtil;
import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author coderpwh
 */
@Slf4j
@RequestMapping("/api/aes")
@RestController
public class AesController {


    /**
     * 加密
     *
     * @param object
     * @return
     */
    @PostMapping("/encrypt")
    public Map<String, Object> encrypt(@RequestBody JSONObject object) {
        log.info("加密接口-请求参数：{}", object);
        String encrypt = AesUtil.encrypt(object.toString());
        Map<String, Object> map = new HashMap<>();
        map.put("encrData", encrypt);
        log.info("加密后的数据为:{}", encrypt);
        return map;
    }

    /**
     * 解密
     *
     * @param object
     * @return
     */

    @PostMapping("/decrypt")
    public AjaxJson decrypt(@RequestBody JSONObject object) {
        log.info("解密接口-请求参数：{}", object);
        String encrData = object.getString("encrData");
        String decrypt = AesUtil.decrypt(encrData);
        return AjaxJson.success().put("decrypt", decrypt);
    }


    @PostMapping("/log")
    public AjaxJson log() {
        log.info("这是log的info日志打印，参数:{}", 1);
        log.error("这是log的error日志打印，参数:{}", 2);
        log.debug("这是log的debug日志打印，参数:{}", 3);
        for (int i = 1; i <= 10; i++) {
            log.info("info级别的日志参数打印,当前数字为:{}", i);
        }
        return AjaxJson.success();
    }


}
