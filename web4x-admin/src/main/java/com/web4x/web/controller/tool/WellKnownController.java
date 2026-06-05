package com.web4x.web.controller.tool;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 兜底处理浏览器/外部工具可能探测的 well-known 资源，避免触发
 * {@code NoResourceFoundException} 导致无意义的 500 日志。
 *
 * 注意：不用于业务功能，只为兼容请求探测。
 */
@RestController
@RequestMapping
public class WellKnownController {

 
    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    public ResponseEntity<Object> chromeDevtools() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Collections.emptyList());
    }
}

