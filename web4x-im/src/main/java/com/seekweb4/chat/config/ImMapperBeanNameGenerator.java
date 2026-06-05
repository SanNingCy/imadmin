package com.seekweb4.chat.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * IM Mapper Bean 名加 im 前缀，避免与若依同名 Mapper（如 SysNoticeReadMapper）冲突。
 */
public class ImMapperBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String name = super.generateBeanName(definition, registry);
        if (name.startsWith("im") && name.length() > 2 && Character.isUpperCase(name.charAt(2))) {
            return name;
        }
        return "im" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
