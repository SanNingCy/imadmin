package com.seekweb4.chat.common.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Component
public interface AccessoryRepository {


    /**
     * 根据保存的相对路径获取存储的文件对象
     *
     * @return java.io.File
     * @Param path
     * @Param fileName
     **/
    File get(String path, String fileName);

    /**
     *
     * @param path
     * @param fileName
     * @return
     */
    URL getURL(String path, String fileName);


    /**
     * 保存文件对象
     *
     * @Param file
     * @Param fileName
     **/
    String save(File file, String path, String fileName);

    /**
     * 保存文件流文件为文件对象
     *
     * @Param file
     * @Param fileName
     **/
    String save(MultipartFile file, String path, String fileName);

    /**
     * 保存输入流为文件对象
     *
     * @Param file
     * @Param fileName
     **/
    String save(InputStream is, String path, @NotNull String fileName);

    Map<String, Object> getPolicy(String fileName);

    /**
     * 删除文件
     *
     * @param url 文件的完整URL或key
     * @return 如果删除成功，则返回true，否则返回false
     */
    boolean delete(String url);
}
