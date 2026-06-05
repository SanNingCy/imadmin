package com.seekweb4.chat.api;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.seekweb4.chat.api.req.ReqJson;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.repository.AccessoryRepository;
import com.seekweb4.chat.common.utils.QrCodeUtil;
import com.seekweb4.chat.config.properties.FileProperties;
import com.seekweb4.chat.security.AppIntercept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.web.BaseController;

/**
 * 上传图片
 * 
 * @author mall
 *
 */
@RestController
@RequestMapping(value = "/api")
public class UploadController extends BaseController {
//	private static final String DEFAULT_QR_LOGO = "https://static-fat.seekweb4.net/file/app/20251210/1765353122595.png";

	@Value("${custom.moment.default-bg-url}")
	private String DEFAULT_QR_LOGO;
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	@Autowired
	private FileProperties fileProperties;
	@Autowired
	private AccessoryRepository accessoryRepository;

	@PostMapping("/uploadFile")
	@AppIntercept
	public AjaxJson uploadFile(@RequestParam(value = "file", required = true) MultipartFile file) {
		// 判断文件是否为空
		if (!file.isEmpty()) {
			String originalFilename = file.getOriginalFilename();
			if (StringUtils.isBlank(originalFilename)) {
				return AjaxJson.error("文件名不能为空!");
			}
			
			// 获取文件扩展名
			String extension = StringUtils.substringAfterLast(originalFilename, ".");
			if (StringUtils.isBlank(extension)) {
				return AjaxJson.error("文件扩展名不能为空!");
			}
			
			// 判断是否是apk或ipa文件
			boolean isApk = "apk".equalsIgnoreCase(extension);
			boolean isIpa = "ipa".equalsIgnoreCase(extension);
			
			// 生成文件名：apk和ipa文件使用原文件名，其他文件使用时间戳
			String name;
			if (isApk || isIpa) {
				// APK和IPA文件使用原文件名
				name = originalFilename;
			} else {
				// 其他文件使用时间戳
				name = System.currentTimeMillis() + "." + extension;
			}
			
			if(fileProperties.isAvailable(name)) {
				// 在当前目录下加一层文件夹（使用日期作为文件夹名：yyyyMMdd）
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String dateFolder = sdf.format(new Date());
				String path = "admin/" + dateFolder;
				
				String url = accessoryRepository.save(file, path, name);
				String realUrl = getRealPath(url);
				
				// 生成二维码并上传到 S3
				String qrCodeUrl = null;
				try {
					// 生成二维码文件名
					String qrCodeFileName = "qr_" + System.currentTimeMillis() + ".png";
					// 生成二维码输入流
					InputStream qrCodeInputStream = QrCodeUtil.generateInputStreamWithLogo(realUrl, DEFAULT_QR_LOGO);
					// 上传二维码到 S3
					String qrCodePath = accessoryRepository.save(qrCodeInputStream, path, qrCodeFileName);
					qrCodeUrl = getRealPath(qrCodePath);
				} catch (Exception e) {
					// 二维码生成失败不影响主功能，记录日志即可
					logger.warn("二维码生成失败", e);
				}
				
				AjaxJson result = AjaxJson.success().put("url", realUrl);
				if (qrCodeUrl != null) {
					result.put("qrCode", qrCodeUrl);
				}
				return result;
			}
			return AjaxJson.error("请勿上传非法文件!");
		}
		return AjaxJson.error("文件不存在!");
	}
	@PostMapping("/uploadFiles")
	@AppIntercept
	public AjaxJson uploadFiles(@RequestParam(value = "files", required = true) MultipartFile[] files) {
		List<String> urls = Lists.newArrayList();
		// 生成日期文件夹名（所有文件使用同一个日期文件夹）
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateFolder = sdf.format(new Date());
		String path = "admin/" + dateFolder;
		
		for (MultipartFile file : files) {
			// 判断文件是否为空
			if (!file.isEmpty()) {
				String originalFilename = file.getOriginalFilename();
				if (StringUtils.isBlank(originalFilename)) {
					return AjaxJson.error("文件名不能为空!");
				}
				
				// 获取文件扩展名
				String extension = StringUtils.substringAfterLast(originalFilename, ".");
				if (StringUtils.isBlank(extension)) {
					return AjaxJson.error("文件扩展名不能为空!");
				}
				
				// 判断是否是apk或ipa文件
				boolean isApk = "apk".equalsIgnoreCase(extension);
				boolean isIpa = "ipa".equalsIgnoreCase(extension);
				
				// 生成文件名：apk和ipa文件使用原文件名，其他文件使用时间戳
				String name;
				if (isApk || isIpa) {
					// APK和IPA文件使用原文件名
					name = originalFilename;
				} else {
					// 其他文件使用时间戳
					name = System.currentTimeMillis() + "." + extension;
				}
				
				if(fileProperties.isAvailable(name)) {
					String url = accessoryRepository.save(file, path, name);
					urls.add(getRealPath(url));
				} else {
					return AjaxJson.error("请勿上传非法文件!");
				}
			}
		}
		return AjaxJson.success().put("urls", urls);
	}

	@PostMapping("/getOssSign")
	@AppIntercept
	public AjaxJson getOssSign(@RequestBody ReqJson req) {
		String fileName = req.getString("fileName");
		if (fileName == null || fileName.isEmpty()) {
			return AjaxJson.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请选择上传文件!");
		}
		Map<String, Object> map = accessoryRepository.getPolicy(fileName);
		return AjaxJson.success().put("data", map);
	}

	/**
	 * 删除文件
	 * 
	 * @param url 文件的完整URL
	 * @return
	 */
	@DeleteMapping("/deleteFile")
	@AppIntercept
	public AjaxJson deleteFile(@RequestParam(value = "url", required = true) String url) {
		if (StringUtils.isBlank(url)) {
			return AjaxJson.error("文件URL不能为空!");
		}
		
		boolean result = accessoryRepository.delete(url);
		if (result) {
			return AjaxJson.success("删除文件成功");
		} else {
			return AjaxJson.error("删除文件失败");
		}
	}
}
