package com.seekweb4.chat.api.utils;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 数据压缩、解压工具类
 */
public class ZipUtil {
    /**
     * 压缩
     * @param data
     * @return
     */
    public static String zipString(String data) {
        Deflater deflater = new Deflater(9); // 0 ~ 9 压缩等级 低到高
        deflater.setInput(data.getBytes());
        deflater.finish();
        final byte[] bytes = new byte[256];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
        while (!deflater.finished()) {
            int length = deflater.deflate(bytes);
            outputStream.write(bytes, 0, length);
        }
        deflater.end();
        return Base64Encoder.encode(outputStream.toByteArray());
    }

    /**
     * 解压
     * @param data
     * @return
     */
    public static String unzipString(String data) {
        byte[] decode = Base64Decoder.decode(data);
        Inflater inflater = new Inflater();
        inflater.setInput(decode);
        final byte[] bytes = new byte[256];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
        try {
            while (!inflater.finished()) {
                int length = inflater.inflate(bytes);
                outputStream.write(bytes, 0, length);
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        } finally {
            inflater.end();
        }
        return outputStream.toString();
    }
}
