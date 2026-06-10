package com.web4x.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.web4x.common.config.Web4xConfig;

/**
 * 获取地址类
 *
 * @author web4x
 */
public class AddressUtils
{
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    private static final String IP_API_URL = "http://ip-api.com/json/";
    private static final int CONNECT_TIMEOUT_MS = 2000;
    private static final int READ_TIMEOUT_MS = 3000;
    private static final int CACHE_MAX = 512;

    private static final ConcurrentHashMap<String, String> ADDRESS_CACHE = new ConcurrentHashMap<>();

    public static String getRealAddressByIP(String ip)
    {
        if (StringUtils.isBlank(ip) || IpUtils.internalIp(ip))
        {
            return "内网IP";
        }
        if (!Web4xConfig.isAddressEnabled())
        {
            return UNKNOWN;
        }

        String cached = ADDRESS_CACHE.get(ip);
        if (cached != null)
        {
            return cached;
        }

        String address = lookupByIpApi(ip);
        if (StringUtils.isBlank(address))
        {
            address = UNKNOWN;
        }
        cacheAddress(ip, address);
        return address;
    }

    private static String lookupByIpApi(String ip)
    {
        HttpURLConnection conn = null;
        try
        {
            String urlStr = IP_API_URL + ip.trim() + "?lang=zh-CN&fields=status,country,regionName,city";
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept", "application/json");

            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK)
            {
                log.warn("IP归属地查询失败 ip={} httpCode={}", ip, code);
                return null;
            }

            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    body.append(line);
                }
            }

            JSONObject obj = JSONObject.parseObject(body.toString());
            if (!"success".equalsIgnoreCase(obj.getString("status")))
            {
                log.debug("IP归属地查询无结果 ip={} body={}", ip, body);
                return null;
            }

            return formatIpApiLocation(obj.getString("country"), obj.getString("regionName"), obj.getString("city"));
        }
        catch (Exception e)
        {
            log.warn("IP归属地查询异常 ip={}: {}", ip, e.getMessage());
            return null;
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }
    }

    private static String formatIpApiLocation(String country, String region, String city)
    {
        country = normalizePart(country);
        region = normalizePart(region);
        city = normalizePart(city);

        if (StringUtils.isEmpty(country) && StringUtils.isEmpty(region) && StringUtils.isEmpty(city))
        {
            return null;
        }

        if ("中国".equals(country) || "China".equalsIgnoreCase(country))
        {
            if (StringUtils.isNotEmpty(region) && StringUtils.isNotEmpty(city))
            {
                return region.equals(city) ? region : region + " " + city;
            }
            return StringUtils.isNotEmpty(region) ? region : city;
        }

        if (StringUtils.isNotEmpty(city) && !city.equals(country) && !city.equals(region))
        {
            return country + " " + city;
        }
        if (StringUtils.isNotEmpty(region) && !region.equals(country))
        {
            return country + " " + region;
        }
        return country;
    }

    private static String normalizePart(String part)
    {
        if (StringUtils.isEmpty(part) || "0".equals(part))
        {
            return "";
        }
        return part.trim();
    }

    private static void cacheAddress(String ip, String address)
    {
        if (!UNKNOWN.equals(address))
        {
            if (ADDRESS_CACHE.size() >= CACHE_MAX)
            {
                ADDRESS_CACHE.clear();
            }
            ADDRESS_CACHE.put(ip, address);
        }
    }
}
