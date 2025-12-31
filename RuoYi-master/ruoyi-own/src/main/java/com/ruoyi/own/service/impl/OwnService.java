package com.ruoyi.own.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.Md5Utils;
import com.ruoyi.own.service.IOwnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class OwnService implements IOwnService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String AMAP_STATIC_BASE = "https://restapi.amap.com/v3/staticmap";
    private static final Pattern LOCATION_PATTERN = Pattern.compile("^(-?\\d+(?:\\.\\d{1,6})?),(-?\\d+(?:\\.\\d{1,6})?)$");


    @Override
    public String buildStaticMapUrl(String location, Integer zoom, String size, Integer scale,
                                    String markers, String labels, String paths, Integer traffic) throws UnsupportedEncodingException {
        if (zoom == null || zoom < 1 || zoom > 17) {
            throw new IllegalArgumentException("zoom 必须在[1,17]范围内");
        }
        if (StringUtils.isNotBlank(location) && !LOCATION_PATTERN.matcher(location.trim()).matches()) {
            throw new IllegalArgumentException("location 坐标格式错误，示例：116.397428,39.90923（小数不超过6位）");
        }
        String finalSize = StringUtils.isNotBlank(size) ? size.trim() : "400*400";
        int[] wh = parseSize(finalSize);
        if (wh[0] > 1024 || wh[1] > 1024) {
            throw new IllegalArgumentException("size 最大值为 1024*1024");
        }
        int finalScale = (scale == null || (scale != 1 && scale != 2)) ? 1 : scale;
        int finalTraffic = (traffic == null || (traffic != 0 && traffic != 1)) ? 0 : traffic;

        String key = "b0a1dab53fe0d5f06c5caa7eda28334c";
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("系统未配置高德地图 Key（sys.map.amap.key）");
        }

        Map<String, String> params = new LinkedHashMap<>();
        params.put("key", key);
        putIfNotBlank(params, "location", location);
        params.put("zoom", String.valueOf(zoom));
        params.put("size", finalSize);
        params.put("scale", String.valueOf(finalScale));
        putIfNotBlank(params, "markers", markers);
        putIfNotBlank(params, "labels", labels);
        putIfNotBlank(params, "paths", paths);
        params.put("traffic", String.valueOf(finalTraffic));

        String query = buildOrderedQuery(params);

        String url = AMAP_STATIC_BASE + "?" + query;
        System.out.println(url);

        return url;
    }

    private static void putIfNotBlank(Map<String, String> map, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value.trim());
        }
    }

    private static int[] parseSize(String size) {
        String[] arr = size.split("\\*");
        if (arr.length != 2) {
            throw new IllegalArgumentException("size 格式错误，示例：400*400");
        }
        try {
            int w = Integer.parseInt(arr[0]);
            int h = Integer.parseInt(arr[1]);
            return new int[]{w, h};
        } catch (Exception e) {
            throw new IllegalArgumentException("size 必须为数字格式，示例：400*400");
        }
    }

    private static String buildOrderedQuery(Map<String, String> params) throws UnsupportedEncodingException {
        List<Map.Entry<String, String>> list = new ArrayList<>(params.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getKey));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, String> e = list.get(i);
            sb.append(e.getKey()).append("=").append(urlEncode(e.getValue()));
            if (i < list.size() - 1) sb.append("&");
        }
        return sb.toString();
    }

    private static String urlEncode(String v) throws UnsupportedEncodingException {
        return URLEncoder.encode(v, String.valueOf(StandardCharsets.UTF_8));
    }
}
