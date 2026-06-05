package com.seekweb4.chat.api.utils;

import com.seekweb4.chat.common.utils.FileUtils;
import com.seekweb4.chat.config.properties.AppProperites;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * MySQL数据库备份工具类
 * （需要将MySQL添加到系统环境变量）
 */
@Slf4j
public class MySQLBackupUtil{
    public static boolean startBackUp() {
        return startBackUp(new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".sql");
    }

    public static boolean startBackUp(String fileName) {
        try {
            String url = AppProperites.newInstance().getJdbcUrl();
            String[] str = url.split("\\?")[0].replace("jdbc:mysql://", "").split("\\/");
            String host = str[0].split(":")[0];
            String port = str[0].split(":")[1];
            String dataBase = str[1];
            String username = AppProperites.newInstance().getJdbcUsername();
            String password = AppProperites.newInstance().getJdbcPassword();

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH ) + 1;

            String path = AppProperites.newInstance().getUserfilesBaseDir() + AppProperites.USERFILES_BASE_URL+"backup/"+year+"/"+month+"/";
            return exportDatabaseTool(host, port, username, password, path, fileName, dataBase);
        } catch (Exception e) {
            log.error("数据库备份失败：{}", e);
        }
        return false;
    }

    public static boolean exportDatabaseTool(String hostIP, String hostPort, String userName, String password, String savePath, String fileName, String databaseName) {
        try {
            File saveFile = new File(savePath);
            if (!saveFile.exists()) {// 如果目录不存在
                saveFile.mkdirs();// 创建文件夹
            }
            if (!savePath.endsWith(File.separator)) {
                savePath = savePath + File.separator;
            }

            File newFile = FileUtils.getAvailableFile(savePath + fileName, 0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("mysqldump")
                    .append(" --skip-opt")
                    .append(" -h ")
                    .append(hostIP)
                    .append(" -P ")
                    .append(hostPort)
                    .append(" --user=")
                    .append(userName)
                    .append(" --password=")
                    .append(password)
                    .append(" --result-file=")
                    .append(newFile.getPath())
                    .append(" --default-character-set=utf8 ")
                    .append(databaseName);
            log.debug("MySQL执行命令：{}", stringBuilder.toString());
            Process process = Runtime.getRuntime().exec(stringBuilder.toString());
            if (process.waitFor() == 0) {// 0 表示线程正常终止。
                return true;
            }
        } catch (Exception e) {
            log.error("数据库备份失败：{}", e);
        }
        return false;
    }

}
