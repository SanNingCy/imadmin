package com.seekweb4.chat.modules.sys.web;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.web.BaseController;
import org.springframework.web.bind.annotation.*;

/**
 * 数据库连接Controller
 */
@RestController
@RequestMapping(value = "/database/datalink/dataSource")
public class DataSourceController extends BaseController {

    @RequestMapping(value = "treeData2")
    public AjaxJson treeData2(HttpServletResponse response) {
        List<Map<String, Object>> rootTree = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("type", "host");
        map.put("parentId", "0");
        map.put("label", "默认数据源");
        map.put("id", "master-parent");
        map.put("enName", "");
        map.put("disabled", true);
        List<Map<String, Object>> children = Lists.newArrayList();
        Map<String, Object> child = Maps.newHashMap();
        child.put("type", "db");
        child.put("parentId", "master-parent");
        child.put("label", "本地数据库");
        child.put("id", "master");
        child.put("enName", "master");
        child.put("disabled", false);
        child.put("dbType", "mysql");
        child.put("children", Lists.newArrayList());
        children.add(child);
        map.put("children", children);
        rootTree.add(map);
        return AjaxJson.success().put("treeData", rootTree);
    }

}
