package com.ruoyi.own.service;

import java.io.UnsupportedEncodingException;

public interface IOwnService {
    String buildStaticMapUrl(String location, Integer zoom, String size, Integer scale,
                             String markers, String labels, String paths, Integer traffic) throws UnsupportedEncodingException;
}
