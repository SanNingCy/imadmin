package com.seekweb4.chat.common.repository;

import cn.hutool.core.util.RandomUtil;
import com.seekweb4.chat.common.utils.FileUtils;
import com.seekweb4.chat.common.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AWS S3存储服务类
 * 优化点：复用 AmazonS3 客户端，减少连接建立开销
 * @author coderpwh
 */
@Component
@ConditionalOnProperty(name = "config.accessory.type", havingValue = "aws")
public class AwsS3OSSRepository implements AccessoryRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${config.accessory.aws.baseDir}")
    private String accessoryBaseDir;

    @Value("${config.accessory.aws.bucketName}")
    private String bucketName;

    @Value("${config.accessory.aws.cdnDomain}")
    private String cdnDomain;


    @Value("${config.accessory.aws.endpoint}")
    private String endpoint;

    @Value("${config.accessory.aws.accessKey}")
    private String accessKey;

    @Value("${config.accessory.aws.secretKey}")
    private String secretKey;

    @Value("${config.accessory.aws.accessUrl}")
    private String  accessUrl;


    @Value("${config.accessory.aws.region}")
    private String region;

    @Resource
    private S3Client s3Client;


    /**
     * 获取直传签名
     */
    @Override
    public Map<String, Object> getPolicy(String fileName) {
        try {
            Map<String, Object> respMap = new LinkedHashMap<>();

            // 延长签名有效期到300秒，防止客户端网络慢导致过期
            long expireTime = 300;
            Date expiration = new Date(System.currentTimeMillis() + expireTime * 1000);

            String suffix = StringUtils.substringAfterLast(fileName, ".");
            String key = accessoryBaseDir + "/" + System.currentTimeMillis()
                    + RandomUtil.randomNumbers(4) + (StringUtils.isNotBlank(suffix) ? "." + suffix : "");

            // 创建 S3Presigner 实例 - 移除 httpClientBuilder 配置
            // 使用配置的region而不是硬编码
            S3Presigner presigner = S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .endpointOverride(new URI(endpoint))
                    .build(); // 移除 httpClientBuilder

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();


            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expireTime))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            respMap.put("expire", String.valueOf(expiration.getTime() / 1000));
            // 前端使用此 URL 进行 PUT 请求即可上传
            respMap.put("presignedUrl", presignedRequest.url().toString());
            String url = getResultUrl(key);
            respMap.put("url", url);

            // 关闭 presigner
            presigner.close();

            return respMap;
        } catch (Exception e) {
            logger.error("获取AWS S3签名失败:{}", e.getMessage(), e);
        }
        return null;
    }
    @Override
    public File get(String path, String fileName) {
        long begin = System.currentTimeMillis();
        try {
            String key = buildKey(path, fileName);
            logger.info("开始从AWS S3获取文件: {}", key);

            // 直接获取输入流并处理，移除异步操作
            try (InputStream is = s3Client.getObject(
                    software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    software.amazon.awssdk.core.sync.ResponseTransformer.toInputStream())) {

                File localTempDir = new File(FileUtils.getTempDirectory() + File.separator + accessoryBaseDir + File.separator + path);
                if (!localTempDir.exists()) {
                    FileUtils.createDirectory(localTempDir.getPath());
                }
                File dest = new File(localTempDir, fileName);
                FileUtils.copyInputStreamToFile(is, dest);
                return dest;
            }
        } catch (Exception e) {
            logger.error("获取文件失败: {}", e.getMessage(), e);
        } finally {
            logger.info("完成从AWS S3获取文件: {}ms", System.currentTimeMillis() - begin);
        }
        return null;
    }
    @Override
    public URL getURL(String path, String fileName) {
        try {
            String key = buildKey(path, fileName);

            // 使用 S3Presigner 生成预签名URL - SDK v2 方式
            S3Presigner presigner = S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .endpointOverride(URI.create(endpoint))
                    .build();

            // 创建GetObjectRequest
            software.amazon.awssdk.services.s3.model.GetObjectRequest getObjectRequest =
                    software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

            // 创建预签名请求，设置过期时间为1小时
            software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest presignRequest =
                    software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofHours(1))
                            .getObjectRequest(getObjectRequest)
                            .build();

            // 生成预签名URL
            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            URL url = presignedRequest.url();

            // 关闭presigner
            presigner.close();

            return url;
        } catch (Exception e) {
            logger.error("获取预签名URL失败: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String save(File file, String path, String fileName) {
        long begin = System.currentTimeMillis();
        try {
            String key = buildKey(path, fileName);
            logger.info("开始保存File对象: {}", key);

            // 修复：使用 SDK v2 的 PutObjectRequest
            software.amazon.awssdk.services.s3.model.PutObjectRequest putObjectRequest =
                    software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(getContentType(fileName))
                            .cacheControl("public, max-age=31536000")
                            .build();

            // 修复：使用 SDK v2 的 putObject 方法
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(file));
            return getResultUrl(key);
        } catch (Exception e) {
            logger.error("保存文件失败: {}", e.getMessage(), e);
        } finally {
            logger.info("保存文件耗时: {}ms", System.currentTimeMillis() - begin);
        }
        return null;
    }


    @Override
    public String save(MultipartFile file, String path, String fileName) {
        long begin = System.currentTimeMillis();
        try {
            String key = buildKey(path, fileName);
            logger.info("开始保存MultipartFile: {}", key);

            software.amazon.awssdk.services.s3.model.PutObjectRequest putObjectRequest =
                    software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentLength(file.getSize())
                            .contentType(file.getContentType())
                            .build();

            // 使用InputStream流式上传，避免大文件导致OutOfMemoryError
            // 直接使用MultipartFile的InputStream，避免将整个文件加载到内存
            try (InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(putObjectRequest,
                        software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));
            }

            return getResultUrl(key);
        } catch (Exception e) {
            logger.error("保存MultipartFile异常: {}", e.getMessage(), e);
        } finally {
            logger.info("保存MultipartFile耗时: {}ms", System.currentTimeMillis() - begin);
        }
        return null;
    }

    @Override
    public String save(InputStream is, String path, @NotNull String fileName) {
        long begin = System.currentTimeMillis();
        try {
            String key = buildKey(path, fileName);
            logger.info("开始保存InputStream: {}", key);

            // 创建临时文件
            File tempFile = File.createTempFile("s3-upload-", ".tmp");
            try {
                // 将 InputStream 内容写入临时文件
//                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                    is.transferTo(fos);
//                }
                // 将 InputStream 内容写入临时文件 - 使用传统方式替代 transferTo
                try (FileOutputStream fos = new FileOutputStream(tempFile);
                     java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(fos)) {

                    byte[] buffer = new byte[8192]; // 8KB 缓冲区
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                    bos.flush();
                }

                // 使用新版 SDK 的 PutObjectRequest，直接在构建器中设置元数据
                software.amazon.awssdk.services.s3.model.PutObjectRequest putObjectRequest =
                        software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .contentType(getContentType(fileName))
                                // 设置缓存控制
                                .cacheControl("public, max-age=31536000")
                                .build();

                s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(tempFile));

                return getResultUrl(key);
            } finally {
                // 清理临时文件
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }
        } catch (Exception e) {
            logger.error("保存流文件失败: {}", e.getMessage(), e);
        } finally {
            logger.info("保存流文件耗时: {}ms", System.currentTimeMillis() - begin);
        }
        return null;
    }

    // 辅助方法：构建 S3 Key
    private String buildKey(String path, String fileName) {
        StringBuilder sb = new StringBuilder(accessoryBaseDir);
        if (StringUtils.isNotBlank(path)) {
            sb.append("/").append(path);
        }
        sb.append("/").append(fileName);
        return sb.toString();
    }

    // 辅助方法：构建返回 URL
    private String getResultUrl(String key) {
        return accessUrl + "/" + key;
    }

    private String getContentType(String fileName) {
        if (fileName == null) return "application/octet-stream";
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // 简单映射，生产环境建议使用 Files.probeContentType 或 Tika
        switch (extension) {
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            case "pdf":
                return "application/pdf";
            case "mp4":
                return "video/mp4";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 删除文件
     */
    @Override
    public boolean delete(String url) {
        logger.debug("开始删除文件: {}", url);
        long begin = System.currentTimeMillis();
        try {
//            AmazonS3 s3Client = getS3Client();
            String key;

            // 如果传入的是完整URL，需要提取key
            if (url.startsWith("http://") || url.startsWith("https://")) {
                // 从URL中提取key，格式: https://cdnDomain/key
                // 移除协议和域名部分
                String urlWithoutProtocol = url.replaceFirst("^https?://", "");
                int firstSlashIndex = urlWithoutProtocol.indexOf("/");
                if (firstSlashIndex > 0) {
                    key = urlWithoutProtocol.substring(firstSlashIndex + 1);
                } else {
                    logger.error("无法从URL中提取key: {}", url);
                    return false;
                }
            } else {
                // 如果传入的是key，直接使用
                key = url;
            }
            // 使用SDK v2的DeleteObjectRequest构建器模式
            software.amazon.awssdk.services.s3.model.DeleteObjectRequest deleteRequest =
                    software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

            // 执行删除操作
            s3Client.deleteObject(deleteRequest);
            // 删除S3对象
            logger.debug("成功删除文件: {}, 耗时: {}ms", key, System.currentTimeMillis() - begin);
            return true;
        } catch (Exception e) {
            logger.error("删除文件失败: {}, 错误: {}", url, e.getMessage(), e);
            return false;
        }
    }
}