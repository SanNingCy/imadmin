package com.seekweb4.chat.modules.creditScore.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreLogQueryDto;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreLogService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreLogDetailVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreLogUserSummaryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@Slf4j
@RestController
@RequestMapping(value = "/admin/creditScore/log", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "信用分后台管理-明细记录")
public class CreditScoreLogAdminController extends BaseController {

    @Resource
    private CreditScoreLogService creditScoreLogService;

    @ApiOperation("信用分明细分页")
    @GetMapping("/pageByUser")
    public AjaxJson pageByUser(CreditScoreLogQueryDto queryDto) {
        try {
            CreditScoreLogUserSummaryVo summary = null;
            if (StringUtils.isNotBlank(queryDto.getUserId())
                    || StringUtils.isNotBlank(queryDto.getIdno())
                    || StringUtils.isNotBlank(queryDto.getLianghao())) {
                String summaryUserId = StringUtils.trimToNull(queryDto.getUserId());
                if (summaryUserId == null) {
                    summaryUserId = creditScoreLogService.resolveUserIdForSummary(
                            queryDto.getIdno(), queryDto.getLianghao());
                }
                if (summaryUserId != null) {
                    summary = creditScoreLogService.getUserSummary(summaryUserId);
                }
            }
            Page<CreditScoreLogDetailVo> page = creditScoreLogService.pageByUser(queryDto);
            return AjaxJson.success()
                    .put("summary", summary)
                    .put("page", page);
        } catch (Exception e) {
            log.error("查询信用分明细失败, query={}", queryDto, e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据日志主键查询信用分记录详情")
    @GetMapping("/queryById/{id}")
    public AjaxJson queryById(@ApiParam("日志主键ID") @PathVariable Long id) {
        try {
            CreditScoreLogDetailVo detail = creditScoreLogService.getDetailById(id);
            if (detail == null) {
                return AjaxJson.error("记录不存在");
            }
            return AjaxJson.success().put("detail", detail);
        } catch (Exception e) {
            log.error("查询信用分记录详情失败, id={}", id, e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }
}
