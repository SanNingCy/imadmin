package com.seekweb4.chat.image.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 图像下载服务
 * 
 * @author sdeven
 * @date 2024/01/01
 */
@Service
public class ImageDownloadService implements DisposableBean {
    
    private final OkHttpClient httpClient;
    private final ExecutorService executorService;
    
    public ImageDownloadService() {
        // 配置HTTP客户端，添加超时设置
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        
        // 创建线程池，使用Spring管理的线程池
        this.executorService = Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors()),
                r -> {
                    Thread t = new Thread(r, "image-download-" + System.currentTimeMillis());
                    t.setDaemon(true);
                    return t;
                }
        );
    }
    
    /**
     * 异步下载图片
     */
    public CompletableFuture<BufferedImage> downloadImage(String imageUrl) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                        .url(imageUrl)
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        BufferedImage image = ImageIO.read(response.body().byteStream());
                        if (image == null) {
                            System.err.println("Failed to parse image from " + imageUrl);
                        }
                        return image;
                    } else {
                        System.err.println("HTTP request failed for " + imageUrl + ": " + response.code());
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to download image from " + imageUrl + ": " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error downloading image from " + imageUrl + ": " + e.getMessage());
            }
            return null;
        }, executorService).exceptionally(throwable -> {
            System.err.println("CompletableFuture failed for " + imageUrl + ": " + throwable.getMessage());
            return null;
        });
    }
    
    /**
     * 清理资源
     */
    @Override
    public void destroy() throws Exception {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
}
