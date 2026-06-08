package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 字典管理 Thymeleaf 页面入口，路径与 IM {@code DictController}（{@code /sys/dict}）对齐。
 * 列表/保存等数据接口走 web4x-im REST；若依 {@code /system/dict} 原有 Controller 不改动。
 */
@Controller
@RequestMapping("/sys/dict")
public class ImDictViewController {

    private static final String TYPE_PREFIX = "system/dict/type";
    private static final String DATA_PREFIX = "system/dict/data";

    @RequiresPermissions("system:dict:view")
    @GetMapping(value = {"", "/", "/type"})
    public String typeList() {
        return TYPE_PREFIX + "/type";
    }

    @RequiresPermissions("system:dict:add")
    @GetMapping("/type/add")
    public String typeAdd() {
        return TYPE_PREFIX + "/add";
    }

    @RequiresPermissions("system:dict:edit")
    @GetMapping("/type/edit/{id}")
    public String typeEdit(@PathVariable("id") String id, ModelMap mmap) {
        mmap.put("dictId", id);
        return TYPE_PREFIX + "/edit";
    }

    @RequiresPermissions("system:dict:list")
    @GetMapping("/data")
    public String dataList() {
        return DATA_PREFIX + "/data";
    }

    @RequiresPermissions("system:dict:add")
    @GetMapping("/data/add/{dictTypeId}")
    public String dataAdd(@PathVariable("dictTypeId") String dictTypeId, ModelMap mmap) {
        mmap.put("dictTypeId", dictTypeId);
        return DATA_PREFIX + "/add";
    }

    @RequiresPermissions("system:dict:edit")
    @GetMapping("/data/edit/{id}")
    public String dataEdit(@PathVariable("id") String id, ModelMap mmap) {
        mmap.put("dictValueId", id);
        return DATA_PREFIX + "/edit";
    }

    @GetMapping("/selectDictTree/{columnId}/{dictType}")
    public String selectDictTree(@PathVariable("columnId") Long columnId,
                                 @PathVariable("dictType") String dictType,
                                 ModelMap mmap) {
        mmap.put("columnId", columnId);
        mmap.put("dictType", dictType);
        return TYPE_PREFIX + "/tree";
    }
}
