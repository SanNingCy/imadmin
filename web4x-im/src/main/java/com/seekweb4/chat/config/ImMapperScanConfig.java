package com.seekweb4.chat.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * IM Mapper 独立扫描（Bean 名带 im 前缀，与若依 com.web4x.**.mapper 区分）。
 */
@Configuration
@MapperScan(
        basePackages = "com.seekweb4.chat.**.mapper",
        nameGenerator = ImMapperBeanNameGenerator.class)
public class ImMapperScanConfig {
}
