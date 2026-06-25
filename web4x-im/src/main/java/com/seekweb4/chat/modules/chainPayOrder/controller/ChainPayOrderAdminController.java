package com.seekweb4.chat.modules.chainPayOrder.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.chainPayOrder.dto.ChainPayOrderQueryDto;
import com.seekweb4.chat.modules.chainPayOrder.entity.ChainPayOrder;
import com.seekweb4.chat.modules.chainPayOrder.service.ChainPayOrderAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 链上支付订单后台管理
 */
@Slf4j
@RestController
@RequestMapping(value = "/admin/asset/chainPayOrder", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "链上支付订单后台管理")
public class ChainPayOrderAdminController extends BaseController {

    @Autowired
    private ChainPayOrderAdminService chainPayOrderAdminService;

    @ApiOperation("分页查询链上支付订单")
    @GetMapping("/page")
    public AjaxJson page(ChainPayOrderQueryDto queryDto) {
        try {
            Page<ChainPayOrder> page = chainPayOrderAdminService.page(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询链上支付订单失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询链上支付订单详情")
    @RequiresPermissions("asset:fund:chain-pay:view")
    @GetMapping("/queryById")
    public AjaxJson queryById(@ApiParam("主键ID") @RequestParam String id) {
        try {
            ChainPayOrder order = chainPayOrderAdminService.getById(id);
            if (order == null) {
                return AjaxJson.error("订单不存在");
            }
            return AjaxJson.success().put("order", order);
        } catch (Exception e) {
            log.error("查询链上支付订单失败, id={}", id, e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }
}
