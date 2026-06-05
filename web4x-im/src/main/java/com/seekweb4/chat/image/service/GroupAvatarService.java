package com.seekweb4.chat.image.service;

import com.seekweb4.chat.image.dto.GroupAvatarRequest;
import com.seekweb4.chat.image.dto.GroupAvatarResponse;
import com.seekweb4.chat.image.layout.DingLayoutManager;
import com.seekweb4.chat.image.layout.ILayoutManager;
import com.seekweb4.chat.image.layout.WechatLayoutManager;
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
import java.util.stream.Collectors;

/**
 * 群组头像生成服务
 * 
 * @author sdeven
 * @date 2024/01/01
 */
@Service
public class GroupAvatarService {
    
    @Autowired
    private ImageDownloadService imageDownloadService;
    
    @Autowired
    private ImageProcessingService imageProcessingService;
    
    // @Autowired
    // private OSSStorageService ossStorageService;
    
    
    /**
     * 生成群组头像
     */
    public CompletableFuture<GroupAvatarResponse> generateGroupAvatar(GroupAvatarRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> datas = new ArrayList<>(request.getDatas());
                
                // 获取布局管理器
                ILayoutManager layoutManager = getLayoutManager(request.getLayoutType());
                
                // 根据布局类型限制数据数量
                if (layoutManager instanceof DingLayoutManager && datas.size() > 4) {
                    datas = datas.subList(0, 4);
                } else if (layoutManager instanceof WechatLayoutManager && datas.size() > 9) {
                    datas = datas.subList(0, 9);
                }
                
                int count = datas.size();
                int subSize = getSubSize(request.getSize(), request.getGap(), layoutManager, count);
                
                // 异步处理每个头像
                List<CompletableFuture<BufferedImage>> futures = datas.stream().map(data -> {
                    if (data.startsWith("http://") || data.startsWith("https://")) {
                        // 下载网络图片
                        return imageDownloadService.downloadImage(data)
                                .thenApply(img -> {
                                    if (img != null) {
                                        BufferedImage resized = imageProcessingService.resizeImage(img, subSize, subSize);
                                        if (request.getLayoutType() == GroupAvatarRequest.LayoutType.WECHAT && request.getChildAvatarRoundPx() > 0) {
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
                        if (request.getLayoutType() == GroupAvatarRequest.LayoutType.WECHAT && request.getChildAvatarRoundPx() > 0) {
                            return CompletableFuture.completedFuture(imageProcessingService.applyRoundedCorners(nickAvatar, request.getChildAvatarRoundPx()));
                        }
                        return CompletableFuture.completedFuture(nickAvatar);
                    }
                }).collect(Collectors.toList());
                
                // 等待所有头像处理完成
                Object[] mObjects = new Object[count];
                for (int i = 0; i < count; i++) {
                    mObjects[i] = futures.get(i).get();
                }
                
                // 生成群组头像
                int gapColor = imageProcessingService.parseColor(request.getGapColor(), 0xFFFFFF);
                int nickAvatarColor = imageProcessingService.parseColor(request.getNickAvatarColor(), 0x808080);
                int nickTextSize = request.getNickTextSize() > 0 ? request.getNickTextSize() : subSize / 4;
                
                BufferedImage combinedBitmap = layoutManager.combineBitmap(
                        request.getSize(), subSize, request.getGap(), gapColor, mObjects,
                        request.getChildAvatarRoundPx(), nickAvatarColor, nickTextSize
                );
                
                // 应用最终效果
                if (request.getLayoutType() == GroupAvatarRequest.LayoutType.DING) {
                    combinedBitmap = imageProcessingService.applyCircleCrop(combinedBitmap);
                } else if (request.getLayoutType() == GroupAvatarRequest.LayoutType.WECHAT && request.getRoundPx() > 0) {
                    combinedBitmap = imageProcessingService.applyRoundedCorners(combinedBitmap, request.getRoundPx());
                }
                
                // 保存图片到本地文件系统
                String localFilePath = saveImageToFile(combinedBitmap, request.getGroupId(), generateCacheKey(request));
                
                // 转换为Base64
                String base64Image = imageProcessingService.imageToBase64(combinedBitmap, "PNG");
                
                // 返回本地文件路径
                String filePath = localFilePath;
                
                return GroupAvatarResponse.success(base64Image, request.getGroupId(), generateCacheKey(request), filePath);
                
            } catch (Exception e) {
                return GroupAvatarResponse.error("生成群组头像失败: " + e.getMessage());
            }
        });
    }
    
    private ILayoutManager getLayoutManager(GroupAvatarRequest.LayoutType layoutType) {
        if (layoutType == GroupAvatarRequest.LayoutType.WECHAT) {
            return new WechatLayoutManager();
        } else if (layoutType == GroupAvatarRequest.LayoutType.DING) {
            return new DingLayoutManager();
        }
        throw new IllegalArgumentException("不支持的布局类型: " + layoutType);
    }
    
    private int getSubSize(int size, int gap, ILayoutManager layoutManager, int count) {
        if (layoutManager instanceof DingLayoutManager) {
            return size; // 钉钉布局内部处理裁剪
        } else if (layoutManager instanceof WechatLayoutManager) {
            if (count < 2) {
                return size;
            } else if (count < 5) {
                return (size - 3 * gap) / 2;
            } else {
                return (size - 4 * gap) / 3;
            }
        }
        throw new IllegalArgumentException("必须使用DingLayoutManager或WechatLayoutManager!");
    }
    
    private BufferedImage createDefaultAvatar(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, size, size);
        g2d.dispose();
        return image;
    }
    
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
            String fileName = String.format("%s_%s_%s.png", groupId, timestamp, cacheKey.hashCode());
            Path filePath = outputPath.resolve(fileName);
            
            // 保存图片
            File outputFile = filePath.toFile();
            javax.imageio.ImageIO.write(image, "PNG", outputFile);
            
            return filePath.toString();
        } catch (IOException e) {
            System.err.println("保存图片失败: " + e.getMessage());
            return null;
        }
    }
}
