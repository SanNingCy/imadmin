package com.seekweb4.chat.modules.exchangeApply.web;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.assetAdmin.dto.ExchangeApplyQueryDto;
import com.seekweb4.chat.modules.exchangeApply.entity.ExchangeApply;
import com.seekweb4.chat.modules.exchangeApply.service.ExchangeApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 靓号兑换申请 Controller
 */
@Slf4j
@RestController
@RequestMapping(value = "/admin/exchangeApply", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "靓号兑换申请管理")
public class ExchangeApplyController extends BaseController {

    @Autowired
    private ExchangeApplyService exchangeApplyService;

    @ApiOperation("分页查询靓号兑换申请")
    @GetMapping("/page")
    public AjaxJson page(ExchangeApplyQueryDto queryDto) {
        try {
            Page<ExchangeApply> page = exchangeApplyService.getPage(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询靓号兑换申请失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询靓号兑换申请")
    @GetMapping("/{id}")
    public AjaxJson getById(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            ExchangeApply apply = exchangeApplyService.getById(id);
            if (apply == null) {
                return AjaxJson.error("靓号兑换申请不存在");
            }
            return AjaxJson.success().put("apply", apply);
        } catch (Exception e) {
            log.error("查询靓号兑换申请失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }
}
