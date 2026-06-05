package com.seekweb4.chat.image.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * 图像处理服务
 * 
 * @author sdeven
 * @date 2024/01/01
 */
@Service
public class ImageProcessingService {
    
    /**
     * 调整图片大小
     */
    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        if (originalImage == null) {
            return null;
        }
        
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
    
    /**
     * 创建昵称头像
     */
    public BufferedImage createNicknameAvatar(String nickname, int size, int bgColor, int textSize) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 绘制背景
        g2d.setColor(new Color(bgColor));
        g2d.fillRect(0, 0, size, size);
        
        // 绘制文字
        g2d.setColor(Color.WHITE);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, textSize);
        g2d.setFont(font);
        
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(nickname, frc);
        
        float textX = (size - (float) bounds.getWidth()) / 2;
        float textY = (size - (float) bounds.getHeight()) / 2 + (float) -bounds.getY();
        
        g2d.drawString(nickname, textX, textY);
        g2d.dispose();
        return image;
    }
    
    /**
     * 应用圆角
     */
    public BufferedImage applyRoundedCorners(BufferedImage image, int cornerRadius) {
        if (image == null || cornerRadius <= 0) {
            return image;
        }
        
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = output.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        g2d.setComposite(AlphaComposite.SrcIn);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return output;
    }
    
    /**
     * 应用圆形裁剪
     */
    public BufferedImage applyCircleCrop(BufferedImage image) {
        if (image == null) {
            return null;
        }
        
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = output.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setClip(new Ellipse2D.Float(0, 0, w, h));
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return output;
    }
    
    /**
     * 获取昵称的短名称（取最后两个字符）
     */
    public String getShortNickName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        if (name.length() <= 2) {
            return name;
        }
        return name.substring(name.length() - 2);
    }
    
    /**
     * 将BufferedImage转换为Base64字符串
     */
    public String imageToBase64(BufferedImage image, String format) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, format, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image to base64", e);
        }
    }
    
    /**
     * 解析颜色字符串为RGB值
     */
    public int parseColor(String colorHex, int defaultColor) {
        if (colorHex == null || colorHex.isEmpty()) {
            return defaultColor;
        }
        try {
            return Color.decode(colorHex).getRGB();
        } catch (NumberFormatException e) {
            return defaultColor;
        }
    }
}
