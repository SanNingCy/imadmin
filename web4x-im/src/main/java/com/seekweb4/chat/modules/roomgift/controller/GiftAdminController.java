package com.seekweb4.chat.modules.roomgift.controller;


import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.rechagelog.entity.RechageLog;
import com.seekweb4.chat.modules.roomgift.entity.Gift;
import com.seekweb4.chat.modules.roomgift.entity.GiftConfig;
import com.seekweb4.chat.modules.roomgift.service.GiftConfigService;
import com.seekweb4.chat.modules.roomgift.service.GiftService;
import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 礼物配置管理Controller
 * @author lixinapp
 * @version 2025-10-23
 */
@Slf4j
@RestController
@RequestMapping("/admin/gift")
public class GiftAdminController {
    @Autowired
    private GiftService giftService;

    @Autowired
    private GiftConfigService giftConfigService;

    /**
     * 获取礼物分页列表
     * @param request HTTP请求
     * @param response HTTP响应
     * @param gift 查询条件
     * @return 礼物分页列表
     */
    @PostMapping("/page")
    public AjaxJson getGiftPage(HttpServletRequest request, HttpServletResponse response, Gift gift) {
        try {
            Page<Gift> page = new Page<>(request, response);
            page = giftService.findPage(page, gift);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("获取礼物分页列表失败", e);
            return AjaxJson.error("获取礼物分页列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取礼物列表
     * @param gift 查询条件
     * @return 礼物列表
     */
    @GetMapping("/list")
    public AjaxJson getGiftList(Gift gift) {
        try {
            List<Gift> list = giftService.findList(gift);
            return AjaxJson.success().setDataList(list);
        } catch (Exception e) {
            log.error("获取礼物列表失败", e);
            return AjaxJson.error("获取礼物列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取礼物详情
     * @param id 礼物ID
     * @return 礼物详情
     */
    @GetMapping("/giftById/{id}")
    public AjaxJson getGift(@PathVariable String id) {
        try {
            Gift gift = giftService.get(id);
            return AjaxJson.success().put("gift", gift);
        } catch (Exception e) {
            log.error("获取礼物详情失败", e);
            return AjaxJson.error("获取礼物详情失败: " + e.getMessage());
        }
    }

    /**
     * 保存礼物
     * @param gift 礼物信息
     * @return 操作结果
     */
    @PostMapping("/save")
    public AjaxJson saveGift(@RequestBody Gift gift) {
        try {
            giftService.save(gift);
            return AjaxJson.success("保存成功");
        } catch (Exception e) {
            log.error("保存礼物失败", e);
            return AjaxJson.error("保存礼物失败: " + e.getMessage());
        }
    }

    /**
     * 修改礼物
     * @param gift 礼物信息
     * @return 操作结果
     */
    @PostMapping("/update")
    public AjaxJson updateGift(@RequestBody Gift gift) {
        try {
            giftService.update(gift);
            return AjaxJson.success("修改成功");
        } catch (Exception e) {
            log.error("修改礼物失败", e);
            return AjaxJson.error("修改礼物失败: " + e.getMessage());
        }
    }

    /**
     * 删除礼物
     * @param id 礼物ID
     * @return 操作结果
     */
    @DeleteMapping("/remove/{id}")
    public AjaxJson deleteGift(@PathVariable String id) {
        try {
            giftService.delete(giftService.get(id));
            return AjaxJson.success("删除成功");
        } catch (Exception e) {
            log.error("删除礼物失败", e);
            return AjaxJson.error("删除礼物失败: " + e.getMessage());
        }
    }

    /**
     * 获取礼物规则配置
     * @return 礼物规则配置
     */
    @GetMapping("/config")
    public AjaxJson getGiftConfig() {
        try {
            List<GiftConfig> list = giftConfigService.findList(new GiftConfig());
            return AjaxJson.success().setDataList(list);
        } catch (Exception e) {
            log.error("获取礼物规则配置失败", e);
            return AjaxJson.error("获取礼物规则配置失败: " + e.getMessage());
        }
    }

    /**
     * 保存礼物规则配置
     * @param config 配置信息
     * @return 操作结果
     */
    @PostMapping("/config/save")
    public AjaxJson saveGiftConfig(@RequestBody GiftConfig config) {
        try {
            giftConfigService.save(config);
            return AjaxJson.success("保存成功");
        } catch (Exception e) {
            log.error("保存礼物规则配置失败", e);
            return AjaxJson.error("保存礼物规则配置失败: " + e.getMessage());
        }
    }

    /**
     * 修改礼物规则配置
     * @param config 配置信息
     * @return 操作结果
     */
    @PostMapping("/config/update")
    public AjaxJson updateGiftConfig(@RequestBody GiftConfig config) {
        try {
            giftConfigService.update(config);
            return AjaxJson.success("修改成功");
        } catch (Exception e) {
            log.error("修改礼物规则配置失败", e);
            return AjaxJson.error("修改礼物规则配置失败: " + e.getMessage());
        }
    }
}
