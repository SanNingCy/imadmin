package com.seekweb4.chat.common.utils;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.http.HttpUtil;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 二维码工具类
 */
public class QrCodeUtil extends cn.hutool.extra.qrcode.QrCodeUtil{
    /**
     * 生成二维码BASE64格式
     * @param content 二维码内容
     * @return
     */
    public static String generateBase64(String content) {
        return "data:image/png;base64," + Base64Encoder.encode(generatePng(content, 300, 300));
    }

    /**
     * 生成带 Logo 的二维码字节数组（logo 支持网络地址）
     * @param content 二维码内容
     * @param logoUrl logo 地址（可为空）
     * @return PNG 格式的字节数组
     */
    public static byte[] generatePngWithLogo(String content, String logoUrl) {
        QrConfig config = new QrConfig(300, 300);
        if (StrUtil.isNotBlank(logoUrl)) {
            try {
                byte[] logoBytes = HttpUtil.downloadBytes(logoUrl);
                if (logoBytes != null && logoBytes.length > 0) {
                    Image logo = ImageIO.read(new ByteArrayInputStream(logoBytes));
                    if (logo != null) {
                        config.setImg(logo);
                    }
                }
            } catch (Exception ignore) {
                // logo 下载或解析失败时忽略，继续生成纯二维码
            }
        }
        return generatePng(content, config);
    }

    /**
     * 生成带 Logo 的二维码输入流（logo 支持网络地址）
     * @param content 二维码内容
     * @param logoUrl logo 地址（可为空）
     * @return PNG 格式的输入流
     */
    public static InputStream generateInputStreamWithLogo(String content, String logoUrl) {
        byte[] pngBytes = generatePngWithLogo(content, logoUrl);
        return new ByteArrayInputStream(pngBytes);
    }

    /**
     * 生成二维码
     * @param content 内容
     * @param path 路径
     * @return
     */
    public static File generate(String content, String path) {
        return generate(content, 300, 300, FileUtil.file(path));
    }
}
