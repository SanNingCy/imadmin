package com.seekweb4.chat.image.dto;

/**
 * 群组头像生成响应DTO
 * 
 * @author sdeven
 * @date 2024/01/01
 */
public class GroupAvatarResponse {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误信息
     */
    private String message;
    
    /**
     * 生成的群组头像Base64编码
     */
    private String avatarBase64;
    
    /**
     * 群组ID
     */
    private String groupId;
    
    /**
     * 缓存键
     */
    private String cacheKey;
    
    /**
     * 生成时间戳
     */
    private Long timestamp;
    
    /**
     * 保存的文件路径
     */
    private String filePath;
    
    // Constructors
    public GroupAvatarResponse() {}
    
    public GroupAvatarResponse(boolean success, String message, String avatarBase64, 
                             String groupId, String cacheKey, Long timestamp) {
        this.success = success;
        this.message = message;
        this.avatarBase64 = avatarBase64;
        this.groupId = groupId;
        this.cacheKey = cacheKey;
        this.timestamp = timestamp;
    }
    
    public GroupAvatarResponse(boolean success, String message, String avatarBase64, 
                             String groupId, String cacheKey, Long timestamp, String filePath) {
        this.success = success;
        this.message = message;
        this.avatarBase64 = avatarBase64;
        this.groupId = groupId;
        this.cacheKey = cacheKey;
        this.timestamp = timestamp;
        this.filePath = filePath;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getAvatarBase64() {
        return avatarBase64;
    }
    
    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public String getCacheKey() {
        return cacheKey;
    }
    
    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * 成功响应
     */
    public static GroupAvatarResponse success(String avatarBase64, String groupId, String cacheKey) {
        return new GroupAvatarResponse(true, null, avatarBase64, groupId, cacheKey, System.currentTimeMillis());
    }
    
    /**
     * 成功响应（包含文件路径）
     */
    public static GroupAvatarResponse success(String avatarBase64, String groupId, String cacheKey, String filePath) {
        return new GroupAvatarResponse(true, null, avatarBase64, groupId, cacheKey, System.currentTimeMillis(), filePath);
    }
    
    /**
     * 失败响应
     */
    public static GroupAvatarResponse error(String message) {
        return new GroupAvatarResponse(false, message, null, null, null, System.currentTimeMillis());
    }
}
