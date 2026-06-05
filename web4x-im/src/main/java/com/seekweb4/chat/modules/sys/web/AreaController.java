package com.seekweb4.chat.modules.sys.web;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.sys.entity.Area;
import com.seekweb4.chat.modules.sys.service.AreaService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 区域Controller
 *
 * @author lixinapp
 * @version 2016-5-15
 */
@RestController
@RequestMapping("/sys/area")
public class AreaController extends BaseController {

    @Autowired
    private AreaService areaService;

    @ModelAttribute("area")
    public Area get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return areaService.get(id);
        } else {
            return new Area();
        }
    }

    @ApiLog("查询区域列表")
    @RequiresPermissions("sys:area:list")
    @GetMapping("list")
    public AjaxJson list(Area area) {
        return AjaxJson.success().put("list", areaService.findAll());
    }

    @ApiLog("查询区域")
    @RequiresPermissions(value = {"sys:area:view", "sys:area:add", "sys:area:edit"}, logical = Logical.OR)
    @GetMapping("queryById")
    public AjaxJson queryById(Area area) {
        return AjaxJson.success().put("area", area);
    }

    @ApiLog("保存区域")
    @RequiresPermissions(value = {"sys:area:add", "sys:area:edit"}, logical = Logical.OR)
    @PostMapping("save")
    public AjaxJson save(Area area, Model model) {
        if (appProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作！");
        }

        /**
         * 后台hibernate-validation插件校验
         */
        String errMsg = beanValidator(area);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }
        areaService.save(area);
        return AjaxJson.success("保存成功！").put("area", area);
    }

    @ApiLog("删除区域")
    @RequiresPermissions("sys:area:del")
    @DeleteMapping("delete")
    public AjaxJson delete(String ids) {
        AjaxJson j = new AjaxJson();
        if (appProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作！");
        }
        String idArray[] =ids.split(",");
        for(String id : idArray){
            areaService.delete(new Area (id));
        }
        return AjaxJson.success("删除区域成功！");
    }


    /**
     * 获取区域JSON数据。
     *
     * @param extId    排除的ID
     * @return
     */
    @RequiresPermissions("user")
    @GetMapping("treeData")
    public AjaxJson treeData(@RequestParam(required = false) String extId) {
        List<Area> list = areaService.findAll();
        List rootTree = areaService.formatListToTree (new Area ("0"),list, extId );
        return AjaxJson.success().put("treeData", rootTree);
    }

}
