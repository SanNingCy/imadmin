package com.seekweb4.chat.image.layout;

import java.awt.image.BufferedImage;

/**
 * 布局管理器接口
 * 
 * @author sdeven
 * @date 2024/01/01
 */
public interface ILayoutManager {
    
    /**
     * 组合多个头像为群组头像
     * 
     * @param size 最终图片尺寸
     * @param subSize 子图片尺寸
     * @param gap 间距
     * @param gapColor 间距颜色
     * @param mObjects 头像对象数组（BufferedImage或String昵称）
     * @param childAvatarRoundPx 子头像圆角
     * @param nickNameBgColor 昵称背景色
     * @param nickNameTextSize 昵称文字大小
     * @return 组合后的群组头像
     */
    BufferedImage combineBitmap(int size, int subSize, int gap, int gapColor, Object[] mObjects, 
                               int childAvatarRoundPx, int nickNameBgColor, int nickNameTextSize);
}
