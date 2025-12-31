package com.ruoyi.own.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.own.service.IOwnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/own/map")
public class OwnController {
    @Autowired
    private IOwnService ownService;

    @GetMapping("/static")
    public String index() {
        return "own/static";
    }

    @PostMapping("/static/search")
    @ResponseBody
    public AjaxResult buildStaticMapUrl(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "zoom") Integer zoom,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "scale", required = false, defaultValue = "1") Integer scale,
            @RequestParam(value = "markers", required = false) String markers,
            @RequestParam(value = "labels", required = false) String labels,
            @RequestParam(value = "paths", required = false) String paths,
            @RequestParam(value = "traffic", required = false, defaultValue = "0") Integer traffic
    ) {
        try {
            return AjaxResult.success(ownService.buildStaticMapUrl(location, zoom, size, scale, markers, labels, paths, traffic));
        } catch (IllegalArgumentException ex) {
            return AjaxResult.error(ex.getMessage());
        } catch (Exception ex) {
            return AjaxResult.error("生成静态地图失败");
        }
    }
}
