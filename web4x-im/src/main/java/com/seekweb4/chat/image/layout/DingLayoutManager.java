package com.seekweb4.chat.image.layout;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * 钉钉群头像布局管理器
 * 最多显示4个
 * 
 * @author sdeven
 * @date 2024/01/01
 */
public class DingLayoutManager implements ILayoutManager {
    
    @Override
    public BufferedImage combineBitmap(int size, int subSize, int gap, int gapColor, Object[] mObjects, 
                                     int childAvatarRoundPx, int nickNameBgColor, int nickNameTextSize) {
        BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D canvas = result.createGraphics();
        
        // 抗锯齿
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 设置背景色
        if (gapColor == 0) {
            gapColor = Color.WHITE.getRGB();
        }
        canvas.setColor(new Color(gapColor));
        canvas.fillRect(0, 0, size, size);
        
        int count = mObjects.length;
        int[][] dxy = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
        
        for (int i = 0; i < count; i++) {
            if (mObjects[i] == null) {
                continue;
            }
            
            boolean isBitmap = mObjects[i] instanceof BufferedImage;
            int dx = dxy[i][0];
            int dy = dxy[i][1];
            float startX = dx * (size + gap) / 2.0f;
            float startY = dy * (size + gap) / 2.0f;
            float width = size;
            float height = size;
            
            if (isBitmap) {
                BufferedImage subBitmap = (BufferedImage) mObjects[i];
                // 钉钉布局需要裁剪图片
                if (count == 2 || (count == 3 && i == 0)) {
                    // 对于2个头像或3个头像的第一个，水平裁剪
                    int cropX = (subBitmap.getWidth() - (size - gap) / 2) / 2;
                    subBitmap = subBitmap.getSubimage(cropX, 0, (size - gap) / 2, subBitmap.getHeight());
                    canvas.drawImage(subBitmap, (int) startX, (int) startY, (size - gap) / 2, size, null);
                } else if ((count == 3 && (i == 1 || i == 2)) || count == 4) {
                    // 对于3个头像的剩余部分或4个头像，水平和垂直都裁剪
                    int cropX = (subBitmap.getWidth() - (size - gap) / 2) / 2;
                    int cropY = (subBitmap.getHeight() - (size - gap) / 2) / 2;
                    subBitmap = subBitmap.getSubimage(cropX, cropY, (size - gap) / 2, (size - gap) / 2);
                    canvas.drawImage(subBitmap, (int) startX, (int) startY, (size - gap) / 2, (size - gap) / 2, null);
                } else {
                    canvas.drawImage(subBitmap, (int) startX, (int) startY, subSize, subSize, null);
                }
            } else {
                if (count == 2 || (count == 3 && i == 0)) {
                    width = (size - gap) / 2f;
                    height = size;
                } else if ((count == 3 && (i == 1 || i == 2)) || count == 4) {
                    width = (size - gap) / 2f;
                    height = width;
                }
                String nickName = (String) mObjects[i];
                drawNickHead(canvas, subSize, startX, startY, width, height, childAvatarRoundPx, 
                           nickName, nickNameBgColor, nickNameTextSize);
            }
        }
        
        canvas.dispose();
        return result;
    }
    
    private void drawNickHead(Graphics2D canvas, int subSize, float x, float y, float width, float height,
                             int childAvatarRoundPx, String nickName, int nickNameBgColor, int nickNameTextSize) {
        // 绘制背景
        canvas.setColor(new Color(nickNameBgColor == 0 ? Color.GRAY.getRGB() : nickNameBgColor));
        canvas.fillRect((int) x, (int) y, (int) width, (int) height);
        
        // 绘制文字
        canvas.setColor(Color.WHITE);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, nickNameTextSize);
        canvas.setFont(font);
        
        FontRenderContext frc = canvas.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(nickName, frc);
        
        float textX = x + (width - (float) bounds.getWidth()) / 2;
        float textY = y + (height - (float) bounds.getHeight()) / 2 + (float) -bounds.getY();
        
        canvas.drawString(nickName, textX, textY);
    }
}
