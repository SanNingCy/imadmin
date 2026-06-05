package com.seekweb4.chat.api.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.seekweb4.chat.common.utils.IdGen;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: fxq
 * @Date 2023-06-19 18:36
 */
@Slf4j
public class QrCodeUtil {
    private final static String IMG_PATH = "/";

    public static String getCode(String param,String dic) {
        String dirname = "/userfiles"+dic;
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+ IdGen.getNumber(6) + ".jpg";
        return createQrCode(param, dirname, fileName);
    }

    /**
     * 生成带参数的二维码
     * @param url
     * @param dirname
     * @param fileName
     * @return
     */
    public static String createQrCode(String url, String dirname, String fileName) {
        String urlPath = null;
        try {
            File folder = new File(IMG_PATH);
            String pt =  File.separator +dirname+File.separator+new SimpleDateFormat("yyyyMMdd").format(new Date())+File.separator;
            String path = IMG_PATH +pt;
            Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 200, 200, hints);
            File file = new File(path, fileName);
            if (file.exists() || ((file.getParentFile().exists() || file.getParentFile().mkdirs()) && file.createNewFile())) {
                writeToFile(bitMatrix, "jpg", file);
                urlPath = File.separator+folder.getName()+pt+fileName;
            }
            urlPath = urlPath.substring(2);
            urlPath = urlPath.replaceAll("\\\\","/");
            return urlPath;

        } catch (Exception e) {
            log.error("异常信息为:{}",e.getMessage());
        }
        return "";

    }

    static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * 将任意文本内容编成二维码图片，返回 JPEG 的 data URL（不落盘）。
     */
    public static String encodeStringToQrJpegDataUrl(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        try {
            Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 200, 200, hints);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writeToStream(bitMatrix, "jpg", baos);
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.error("encodeStringToQrJpegDataUrl: {}", e.getMessage());
            return "";
        }
    }
}
