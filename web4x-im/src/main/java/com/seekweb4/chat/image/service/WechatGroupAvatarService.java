package com.seekweb4.chat.image.service;

import com.seekweb4.chat.image.dto.GroupAvatarRequest;
import com.seekweb4.chat.image.dto.GroupAvatarResponse;
import com.seekweb4.chat.image.dto.WechatAvatarS3Response;
import com.seekweb4.chat.image.layout.WechatLayoutManager;
import com.seekweb4.chat.common.repository.AccessoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 微信群组头像生成服务
 * 专门处理微信样式的群组头像生成
 * 
 * @author sdeven
 * @date 2024/01/01
 */
@Service
public class WechatGroupAvatarService {
    
    @Autowired
    private ImageDownloadService imageDownloadService;
    
    @Autowired
    private ImageProcessingService imageProcessingService;
    
    @Autowired
    private AccessoryRepository accessoryRepository;
    
    /**
     * 生成微信样式群组头像
     * 使用微信固定样式配置，简化调用
     * 
     * @param imageUrls 图片URL列表
     * @return 异步返回群组头像响应
     */
    public CompletableFuture<GroupAvatarResponse> generateWechatGroupAvatar(List<String> imageUrls) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 创建微信样式的请求配置
                GroupAvatarRequest request = createWechatRequest(imageUrls);
                
                // 处理图片数据
                List<String> datas = new ArrayList<>(imageUrls);
                
                // 限制微信样式最多9个头像
                if (datas.size() > 9) {
                    datas = datas.subList(0, 9);
                }
                
                int count = datas.size();
                WechatLayoutManager layoutManager = new WechatLayoutManager();
                int subSize = calculateWechatSubSize(request.getSize(), request.getGap(), count);
                
                // 异步处理每个头像
                List<CompletableFuture<BufferedImage>> futures = datas.stream().map(data -> {
                    if (data.startsWith("http://") || data.startsWith("https://")) {
                        // 下载网络图片
                        return imageDownloadService.downloadImage(data)
                                .thenApply(img -> {
                                    if (img != null) {
                                        BufferedImage resized = imageProcessingService.resizeImage(img, subSize, subSize);
                                        // 应用微信样式的圆角
                                        if (request.getChildAvatarRoundPx() > 0) {
                                            return imageProcessingService.applyRoundedCorners(resized, request.getChildAvatarRoundPx());
                                        }
                                        return resized;
                                    }
                                    return createDefaultAvatar(subSize);
                                });
                    } else {
                        // 创建昵称头像
                        String shortNick = imageProcessingService.getShortNickName(data);
                        int nickBgColor = imageProcessingService.parseColor(request.getNickAvatarColor(), 0x808080);
                        int nickTextSize = request.getNickTextSize() > 0 ? request.getNickTextSize() : subSize / 4;
                        BufferedImage nickAvatar = imageProcessingService.createNicknameAvatar(shortNick, subSize, nickBgColor, nickTextSize);
                        // 应用微信样式的圆角
                        if (request.getChildAvatarRoundPx() > 0) {
                            return CompletableFuture.completedFuture(imageProcessingService.applyRoundedCorners(nickAvatar, request.getChildAvatarRoundPx()));
                        }
                        return CompletableFuture.completedFuture(nickAvatar);
                    }
                }).collect(Collectors.toList());
                
                // 等待所有头像处理完成，使用异常处理确保单个任务失败不影响其他任务
                Object[] mObjects = new Object[count];
                for (int i = 0; i < count; i++) {
                    try {
                        // 添加超时处理，避免无限等待
                        mObjects[i] = futures.get(i).get(30, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        System.err.println("处理第" + (i + 1) + "个头像超时: " + e.getMessage());
                        // 使用默认头像替代
                        mObjects[i] = createDefaultAvatar(subSize);
                    } catch (Exception e) {
                        System.err.println("处理第" + (i + 1) + "个头像时发生错误: " + e.getMessage());
                        // 使用默认头像替代
                        mObjects[i] = createDefaultAvatar(subSize);
                    }
                }
                
                // 生成微信群组头像
                int gapColor = imageProcessingService.parseColor(request.getGapColor(), 0xFFFFFF);
                int nickAvatarColor = imageProcessingService.parseColor(request.getNickAvatarColor(), 0x808080);
                int nickTextSize = request.getNickTextSize() > 0 ? request.getNickTextSize() : subSize / 4;
                
                BufferedImage combinedBitmap = layoutManager.combineBitmap(
                        request.getSize(), subSize, request.getGap(), gapColor, mObjects,
                        request.getChildAvatarRoundPx(), nickAvatarColor, nickTextSize
                );
                
                // 应用微信样式的最终效果
                if (request.getRoundPx() > 0) {
                    combinedBitmap = imageProcessingService.applyRoundedCorners(combinedBitmap, request.getRoundPx());
                }
                
                // 保存图片到本地文件系统
                String localFilePath = saveImageToFile(combinedBitmap, request.getGroupId(), generateCacheKey(request));
                
                // 转换为Base64
                String base64Image = imageProcessingService.imageToBase64(combinedBitmap, "PNG");
                
                return GroupAvatarResponse.success(base64Image, request.getGroupId(), generateCacheKey(request), localFilePath);
                
            } catch (Exception e) {
                return GroupAvatarResponse.error("生成微信群组头像失败: " + e.getMessage());
            }
        });
    }
    
    /**
     * 生成微信样式群组头像并存储到S3
     * 使用微信固定样式配置，存储到S3对象桶
     * 
     * @param imageUrls 图片URL列表
     * @return 异步返回简化的S3存储响应，只包含必要信息
     */
    public CompletableFuture<WechatAvatarS3Response> generateWechatGroupAvatarWithS3(String groupId, List<String> imageUrls) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 创建微信样式的请求配置
                GroupAvatarRequest request = createWechatRequest(imageUrls);
                request.setGroupId(groupId);
                // 处理图片数据
                List<String> datas = new ArrayList<>(imageUrls);
                
                // 限制微信样式最多9个头像
                if (datas.size() > 9) {
                    datas = datas.subList(0, 9);
                }
                
                int count = datas.size();
                WechatLayoutManager layoutManager = new WechatLayoutManager();
                int subSize = calculateWechatSubSize(request.getSize(), request.getGap(), count);
                
                // 异步处理每个头像
                List<CompletableFuture<BufferedImage>> futures = datas.stream().map(data -> {
                    if (data.startsWith("http://") || data.startsWith("https://")) {
                        // 下载网络图片
                        return imageDownloadService.downloadImage(data)
                                .thenApply(img -> {
                                    if (img != null) {
                                        BufferedImage resized = imageProcessingService.resizeImage(img, subSize, subSize);
                                        // 应用微信样式的圆角
                                        if (request.getChildAvatarRoundPx() > 0) {
                                            return imageProcessingService.applyRoundedCorners(resized, request.getChildAvatarRoundPx());
                                        }
                                        return resized;
                                    }
                                    return createDefaultAvatar(subSize);
                                });
                    } else {
                        // 创建昵称头像
                        String shortNick = imageProcessingService.getShortNickName(data);
                        int nickBgColor = imageProcessingService.parseColor(request.getNickAvatarColor(), 0x808080);
                        int nickTextSize = request.getNickTextSize() > 0 ? request.getNickTextSize() : subSize / 4;
                        BufferedImage nickAvatar = imageProcessingService.createNicknameAvatar(shortNick, subSize, nickBgColor, nickTextSize);
                        // 应用微信样式的圆角
                        if (request.getChildAvatarRoundPx() > 0) {
                            return CompletableFuture.completedFuture(imageProcessingService.applyRoundedCorners(nickAvatar, request.getChildAvatarRoundPx()));
                        }
                        return CompletableFuture.completedFuture(nickAvatar);
                    }
                }).collect(Collectors.toList());
                
                // 等待所有头像处理完成，使用异常处理确保单个任务失败不影响其他任务
                Object[] mObjects = new Object[count];
                for (int i = 0; i < count; i++) {
                    try {
                        // 添加超时处理，避免无限等待
                        mObjects[i] = futures.get(i).get(30, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        System.err.println("处理第" + (i + 1) + "个头像超时: " + e.getMessage());
                        // 使用默认头像替代
                        mObjects[i] = createDefaultAvatar(subSize);
                    } catch (Exception e) {
                        System.err.println("处理第" + (i + 1) + "个头像时发生错误: " + e.getMessage());
                        // 使用默认头像替代
                        mObjects[i] = createDefaultAvatar(subSize);
                    }
                }
                
                // 生成微信群组头像
                int gapColor = imageProcessingService.parseColor(request.getGapColor(), 0xFFFFFF);
                int nickAvatarColor = imageProcessingService.parseColor(request.getNickAvatarColor(), 0x808080);
                int nickTextSize = request.getNickTextSize() > 0 ? request.getNickTextSize() : subSize / 4;
                
                BufferedImage combinedBitmap = layoutManager.combineBitmap(
                        request.getSize(), subSize, request.getGap(), gapColor, mObjects,
                        request.getChildAvatarRoundPx(), nickAvatarColor, nickTextSize
                );
                
                // 应用微信样式的最终效果
                if (request.getRoundPx() > 0) {
                    combinedBitmap = imageProcessingService.applyRoundedCorners(combinedBitmap, request.getRoundPx());
                }
                
                // 保存图片到S3对象存储
                String s3Url = saveImageToS3(combinedBitmap, request.getGroupId());
                
                // 提取URL路径信息（用于调试或日志记录）
                String urlPath = extractUrlPath(s3Url);
                
                return WechatAvatarS3Response.of(s3Url, request.getGroupId(), urlPath);
                
            } catch (Exception e) {
                throw new RuntimeException("生成微信群组头像并存储到S3失败: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * 创建微信样式的请求配置
     */
    private GroupAvatarRequest createWechatRequest(List<String> imageUrls) {
        GroupAvatarRequest request = new GroupAvatarRequest();
        request.setGroupId("im_" + System.currentTimeMillis());
        request.setDatas(imageUrls);
        request.setLayoutType(GroupAvatarRequest.LayoutType.WECHAT);
        request.setSize(200); // 固定尺寸200x200
        request.setGap(4); // 固定间距4px
        request.setGapColor("#FFFFFF"); // 白色背景
        request.setChildAvatarRoundPx(10); // 子头像圆角10px
        request.setNickAvatarColor("#4CAF50"); // 默认昵称背景色
        request.setNickTextSize(20); // 默认文字大小
        request.setRoundPx(0); // 微信样式通常不需要外圆角
        return request;
    }
    
    /**
     * 计算微信样式的子头像尺寸
     */
    private int calculateWechatSubSize(int size, int gap, int count) {
        if (count < 2) {
            return size;
        } else if (count < 5) {
            return (size - 3 * gap) / 2;
        } else {
            return (size - 4 * gap) / 3;
        }
    }
    
    /**
     * 创建默认头像
     */
    private BufferedImage createDefaultAvatar(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, size, size);
        g2d.dispose();
        return image;
    }
    
    /**
     * 生成缓存键
     */
    private String generateCacheKey(GroupAvatarRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getGroupId()).append("_");
        sb.append(request.getLayoutType().name()).append("_");
        sb.append(request.getSize()).append("_");
        sb.append(request.getGap()).append("_");
        sb.append(request.getGapColor()).append("_");
        sb.append(request.getRoundPx()).append("_");
        sb.append(request.getChildAvatarRoundPx()).append("_");
        sb.append(request.getNickAvatarColor()).append("_");
        sb.append(request.getNickTextSize()).append("_");
        for (String data : request.getDatas()) {
            sb.append(data).append("_");
        }
        return sb.toString();
    }
    
    /**
     * 提取URL路径信息
     */
    private String extractUrlPath(String s3Url) {
        if (s3Url == null || s3Url.isEmpty()) {
            return null;
        }
        try {
            java.net.URL url = new java.net.URL(s3Url);
            return url.getPath(); // 返回路径部分，如：/group-avatars/wechat/wechat_xxx.png
        } catch (Exception e) {
            return s3Url; // 如果解析失败，返回完整URL
        }
    }
    
    /**
     * 保存图片到S3对象存储（通过流上传）
     */
    private String saveImageToS3(BufferedImage image, String groupId) {
        try {
            // 生成文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = String.format("group_%s_%s.png", groupId, timestamp);
            
            // 将BufferedImage转换为InputStream，使用try-with-resources自动管理资源
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                
                // 将图片写入到字节数组输出流
                javax.imageio.ImageIO.write(image, "PNG", baos);
                
                // 获取图片数据
                byte[] imageData = baos.toByteArray();
                
                // 创建输入流并上传
                try (java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(imageData)) {
                    // 直接通过流上传到S3，避免创建临时文件
                    String s3Path = "group-avatars/seekweb/"; // S3存储路径
                    String s3Url = accessoryRepository.save(inputStream, s3Path, fileName);
                    return s3Url;
                }
            }
        } catch (IOException e) {
            System.err.println("保存微信群组头像到S3失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 保存图片到文件系统
     */
    private String saveImageToFile(BufferedImage image, String groupId, String cacheKey) {
        try {
            // 创建输出目录
            String outputDir = System.getProperty("user.dir") + "/generated-avatars";
            Path outputPath = Paths.get(outputDir);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            // 生成文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("group_%s_%s_%s.png", groupId, timestamp, cacheKey.hashCode());
            Path filePath = outputPath.resolve(fileName);
            
            // 保存图片
            File outputFile = filePath.toFile();
            javax.imageio.ImageIO.write(image, "PNG", outputFile);
            
            return filePath.toString();
        } catch (IOException e) {
            System.err.println("保存微信群组头像失败: " + e.getMessage());
            return null;
        }
    }
}
