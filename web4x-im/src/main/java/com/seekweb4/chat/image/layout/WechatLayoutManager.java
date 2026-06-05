package com.seekweb4.chat.image.layout;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * 微信群头像布局管理器
 * 最多显示9个
 * 
 * @author sdeven
 * @date 2024/01/01
 */
public class WechatLayoutManager implements ILayoutManager {
    
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
        for (int i = 0; i < count; i++) {
            if (mObjects[i] == null) {
                continue;
            }
            
            boolean isBitmap = mObjects[i] instanceof BufferedImage;
            float x = 0;
            float y = 0;
            
            // 根据数量计算位置
            if (count == 1) {
                x = (size - subSize) / 2.0f;
                y = (size - subSize) / 2.0f;
            } else if (count == 2) {
                x = gap + i * (subSize + gap);
                y = (size - subSize) / 2.0f;
            } else if (count == 3) {
                if (i == 0) {
                    x = (size - subSize) / 2.0f;
                    y = gap;
                } else {
                    x = gap + (i - 1) * (subSize + gap);
                    y = subSize + 2 * gap;
                }
            } else if (count == 4) {
                x = gap + (i % 2) * (subSize + gap);
                if (i < 2) {
                    y = gap;
                } else {
                    y = subSize + 2 * gap;
                }
            } else if (count == 5) {
                if (i == 0) {
                    x = y = (size - 2 * subSize - gap) / 2.0f;
                } else if (i == 1) {
                    x = (size + gap) / 2.0f;
                    y = (size - 2 * subSize - gap) / 2.0f;
                } else if (i > 1) {
                    x = gap + (i - 2) * (subSize + gap);
                    y = (size + gap) / 2.0f;
                }
            } else if (count == 6) {
                x = gap + (i % 3) * (subSize + gap);
                if (i < 3) {
                    y = (size - 2 * subSize - gap) / 2.0f;
                } else {
                    y = (size + gap) / 2.0f;
                }
            } else if (count == 7) {
                if (i == 0) {
                    x = (size - subSize) / 2.0f;
                    y = gap;
                } else if (i < 4) {
                    x = gap + (i - 1) * (subSize + gap);
                    y = subSize + 2 * gap;
                } else {
                    x = gap + (i - 4) * (subSize + gap);
                    y = gap + 2 * (subSize + gap);
                }
            } else if (count == 8) {
                if (i == 0) {
                    x = (size - 2 * subSize - gap) / 2.0f;
                    y = gap;
                } else if (i == 1) {
                    x = (size + gap) / 2.0f;
                    y = gap;
                } else if (i < 5) {
                    x = gap + (i - 2) * (subSize + gap);
                    y = subSize + 2 * gap;
                } else {
                    x = gap + (i - 5) * (subSize + gap);
                    y = gap + 2 * (subSize + gap);
                }
            } else if (count == 9) {
                x = gap + (i % 3) * (subSize + gap);
                if (i < 3) {
                    y = gap;
                } else if (i < 6) {
                    y = subSize + 2 * gap;
                } else {
                    y = gap + 2 * (subSize + gap);
                }
            }
            
            if (isBitmap) {
                BufferedImage subBitmap = (BufferedImage) mObjects[i];
                canvas.drawImage(subBitmap, (int) x, (int) y, subSize, subSize, null);
            } else {
                String nickName = (String) mObjects[i];
                drawNickHead(canvas, subSize, x, y, childAvatarRoundPx, nickName, nickNameBgColor, nickNameTextSize);
            }
        }
        
        canvas.dispose();
        return result;
    }
    
    private void drawNickHead(Graphics2D canvas, int subSize, float x, float y, int childAvatarRoundPx, 
                             String nickName, int nickNameBgColor, int nickNameTextSize) {
        // 绘制背景
        canvas.setColor(new Color(nickNameBgColor == 0 ? Color.GRAY.getRGB() : nickNameBgColor));
        if (childAvatarRoundPx > 0) {
            canvas.fillRoundRect((int) x, (int) y, subSize, subSize, childAvatarRoundPx, childAvatarRoundPx);
        } else {
            canvas.fillRect((int) x, (int) y, subSize, subSize);
        }
        
        // 绘制文字
        canvas.setColor(Color.WHITE);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, nickNameTextSize);
        canvas.setFont(font);
        
        FontRenderContext frc = canvas.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(nickName, frc);
        
        float textX = x + (subSize - (float) bounds.getWidth()) / 2;
        float textY = y + (subSize - (float) bounds.getHeight()) / 2 + (float) -bounds.getY();
        
        canvas.drawString(nickName, textX, textY);
    }
}
