package com.seekweb4.chat.image.controller;

import com.seekweb4.chat.image.dto.GroupAvatarRequest;
import com.seekweb4.chat.image.dto.GroupAvatarResponse;
import com.seekweb4.chat.image.dto.WechatAvatarS3Response;
import com.seekweb4.chat.image.service.GroupAvatarService;
import com.seekweb4.chat.image.service.WechatGroupAvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 群组头像生成控制器
 * 
 * @author sdeven
 * @date 2024/01/01
 */
@RestController
@RequestMapping("/api/group-avatar")
@Validated
public class GroupAvatarController {
    
    @Autowired
    private GroupAvatarService groupAvatarService;
    
    @Autowired
    private WechatGroupAvatarService wechatGroupAvatarService;
    
    /**
     * 生成群组头像
     */
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupAvatarResponse> generateGroupAvatar(@Valid @RequestBody GroupAvatarRequest request) {
        try {
            CompletableFuture<GroupAvatarResponse> future = groupAvatarService.generateGroupAvatar(request);
            GroupAvatarResponse response = future.get();
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GroupAvatarResponse.error("生成群组头像时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Group Avatar Service is running!");
    }
    
    /**
     * 测试接口
     */
    @GetMapping("/test")
    public ResponseEntity<GroupAvatarResponse> test() {
        GroupAvatarRequest request = new GroupAvatarRequest();
        request.setGroupId("test-group");
        request.setDatas(java.util.Arrays.asList("张三", "李四", "王五"));
        request.setLayoutType(GroupAvatarRequest.LayoutType.WECHAT);
        request.setSize(200);
        request.setGap(4);
        request.setGapColor("#FFFFFF");
        request.setChildAvatarRoundPx(10);
        request.setNickAvatarColor("#4CAF50");
        request.setNickTextSize(20);
        
        try {
            CompletableFuture<GroupAvatarResponse> future = groupAvatarService.generateGroupAvatar(request);
            GroupAvatarResponse response = future.get();
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GroupAvatarResponse.error("测试失败: " + e.getMessage()));
        }
    }
    
    /**
     * 生成微信样式群组头像（简化版）
     * 只需要传入图片地址列表
     */
    @PostMapping(value = "/wechat", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupAvatarResponse> generateWechatGroupAvatar(@RequestBody java.util.List<String> imageUrls) {
        try {
            CompletableFuture<GroupAvatarResponse> future = wechatGroupAvatarService.generateWechatGroupAvatar(imageUrls);
            GroupAvatarResponse response = future.get();
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GroupAvatarResponse.error("生成微信群组头像时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 生成微信样式群组头像并存储到S3
     * 只需要传入图片地址列表，自动存储到S3对象桶
     * 返回简化的响应，只包含S3 URL、群组ID、URL路径信息和时间戳
     * 失败时直接抛异常
     */
    @PostMapping(value = "/seekweb/s3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WechatAvatarS3Response> generateWechatGroupAvatarWithS3(@RequestBody java.util.List<String> imageUrls) throws Exception {
        CompletableFuture<WechatAvatarS3Response> future = wechatGroupAvatarService.generateWechatGroupAvatarWithS3("123",imageUrls);
        WechatAvatarS3Response response = future.get();
        return ResponseEntity.ok(response);
    }
}
