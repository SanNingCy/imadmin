package com.seekweb4.chat.image.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 群组头像生成请求DTO
 * 
 * @author sdeven
 * @date 2024/01/01
 */
public class GroupAvatarRequest {
    
    /**
     * 群组ID，用于缓存
     */
    @NotEmpty(message = "群组ID不能为空")
    private String groupId;
    
    /**
     * 头像数据列表（URL或昵称）
     */
    @NotEmpty(message = "头像数据不能为空")
    @Size(max = 9, message = "最多支持9个头像")
    private List<String> datas;
    
    /**
     * 布局类型：WECHAT（微信样式）、DING（钉钉样式）
     */
    @NotNull(message = "布局类型不能为空")
    private LayoutType layoutType = LayoutType.WECHAT;
    
    /**
     * 最终生成图片的尺寸（像素）
     */
    private Integer size = 200;
    
    /**
     * 每个小头像之间的距离（像素）
     */
    private Integer gap = 4;
    
    /**
     * 间距的颜色（十六进制，如：#FFFFFF）
     */
    private String gapColor = "#FFFFFF";
    
    /**
     * 生成图片的圆角（像素）
     */
    private Integer roundPx = 0;
    
    /**
     * 内部小头像的圆角（像素）
     */
    private Integer childAvatarRoundPx = 0;
    
    /**
     * 昵称生成头像时的背景色（十六进制）
     */
    private String nickAvatarColor = "#808080";
    
    /**
     * 昵称生成头像时的文字大小（像素）
     */
    private Integer nickTextSize = 0;
    
    /**
     * 布局类型枚举
     */
    public enum LayoutType {
        WECHAT,  // 微信样式，最多9个
        DING     // 钉钉样式，最多4个
    }
    
    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public List<String> getDatas() {
        return datas;
    }
    
    public void setDatas(List<String> datas) {
        this.datas = datas;
    }
    
    public LayoutType getLayoutType() {
        return layoutType;
    }
    
    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public Integer getGap() {
        return gap;
    }
    
    public void setGap(Integer gap) {
        this.gap = gap;
    }
    
    public String getGapColor() {
        return gapColor;
    }
    
    public void setGapColor(String gapColor) {
        this.gapColor = gapColor;
    }
    
    public Integer getRoundPx() {
        return roundPx;
    }
    
    public void setRoundPx(Integer roundPx) {
        this.roundPx = roundPx;
    }
    
    public Integer getChildAvatarRoundPx() {
        return childAvatarRoundPx;
    }
    
    public void setChildAvatarRoundPx(Integer childAvatarRoundPx) {
        this.childAvatarRoundPx = childAvatarRoundPx;
    }
    
    public String getNickAvatarColor() {
        return nickAvatarColor;
    }
    
    public void setNickAvatarColor(String nickAvatarColor) {
        this.nickAvatarColor = nickAvatarColor;
    }
    
    public Integer getNickTextSize() {
        return nickTextSize;
    }
    
    public void setNickTextSize(Integer nickTextSize) {
        this.nickTextSize = nickTextSize;
    }
}
