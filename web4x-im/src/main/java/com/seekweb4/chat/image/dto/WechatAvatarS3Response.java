package com.seekweb4.chat.image.dto;

/**
 * 微信群组头像S3存储响应DTO
 * 只包含必要的字段：S3 URL、群组ID、URL路径信息和时间戳
 * 
 * @author sdeven
 * @date 2024/01/01
 */
public class WechatAvatarS3Response {
    
    /**
     * S3存储的URL
     */
    private String s3Url;
    
    /**
     * 群组ID
     */
    private String groupId;
    
    /**
     * 生成时间戳
     */
    private Long timestamp;
    
    /**
     * URL路径信息（用于调试或日志记录）
     */
    private String urlPath;
    
    // Constructors
    public WechatAvatarS3Response() {}
    
    public WechatAvatarS3Response(String s3Url, String groupId, Long timestamp, String urlPath) {
        this.s3Url = s3Url;
        this.groupId = groupId;
        this.timestamp = timestamp;
        this.urlPath = urlPath;
    }
    
    // Getters and Setters
    public String getS3Url() {
        return s3Url;
    }
    
    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getUrlPath() {
        return urlPath;
    }
    
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
    
    /**
     * 创建响应对象
     */
    public static WechatAvatarS3Response of(String s3Url, String groupId, String urlPath) {
        return new WechatAvatarS3Response(s3Url, groupId, System.currentTimeMillis(), urlPath);
    }
}
