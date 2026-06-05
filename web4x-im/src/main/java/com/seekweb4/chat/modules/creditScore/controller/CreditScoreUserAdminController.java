package com.seekweb4.chat.modules.creditScore.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserOperateDto;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserTypePageQueryDto;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserDetailsPageQueryDto;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserTypeService;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserDetailsService;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserDetailsPageService;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserDetailsTypePageService;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserOperateResultVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserTypeVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsWithTypesVo;
import com.seekweb4.chat.core.persistence.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@Slf4j
@RestController
@RequestMapping(value = "/admin/creditScore/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "信用分后台管理-用户手动加减分")
public class CreditScoreUserAdminController extends BaseController {

    @Resource
    private CreditScoreUserService creditScoreUserService;

    @Resource
    private CreditScoreUserTypeService creditScoreUserTypeService;

    @Resource
    private CreditScoreUserDetailsService creditScoreUserDetailsService;

    @Resource
    private CreditScoreUserDetailsPageService creditScoreUserDetailsPageService;

    @Resource
    private CreditScoreUserDetailsTypePageService creditScoreUserDetailsTypePageService;

    @ApiOperation("系统添加：type固定为7")
    @PostMapping("/addSystem")
    public AjaxJson addSystem(@RequestBody CreditScoreUserOperateDto dto) {
        try {
            if (StringUtils.isBlank(dto.getUserId())) {
                return AjaxJson.error("userId不能为空");
            }
            CreditScoreUserOperateResultVo result =
                    creditScoreUserService.addSystem(dto.getUserId(), dto.getSubtype(), dto.getDesc());
            return AjaxJson.success().put("result", result);
        } catch (Exception e) {
            log.error("系统添加信用分失败", e);
            return AjaxJson.error("添加失败：" + e.getMessage());
        }
    }

    @ApiOperation("系统扣减：type固定为8")
    @PostMapping("/reduceSystem")
    public AjaxJson reduceSystem(@RequestBody CreditScoreUserOperateDto dto) {
        try {
            if (StringUtils.isBlank(dto.getUserId())) {
                return AjaxJson.error("userId不能为空");
            }
            CreditScoreUserOperateResultVo result =
                    creditScoreUserService.reduceSystem(dto.getUserId(), dto.getSubtype(), dto.getDesc());
            return AjaxJson.success().put("result", result);
        } catch (Exception e) {
            log.error("系统扣减信用分失败", e);
            return AjaxJson.error("扣减失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增信用分：自定义type")
    @PostMapping("/addScore")
    public AjaxJson addScore(@RequestBody CreditScoreUserOperateDto dto) {
        try {
            if (StringUtils.isBlank(dto.getUserId())) {
                return AjaxJson.error("userId不能为空");
            }
            if (dto.getType() == null) {
                return AjaxJson.error("type不能为空");
            }
            if (Integer.valueOf(5).equals(dto.getType()) && StringUtils.isBlank(dto.getDesc())) {
                return AjaxJson.error("type=5(平台贡献)必须输入desc");
            }
            CreditScoreUserOperateResultVo result =
                    creditScoreUserService.addScore(dto.getUserId(), dto.getType(), dto.getSubtype(), dto.getDesc(), dto.getScore());
            return AjaxJson.success().put("result", result);
        } catch (Exception e) {
            log.error("新增信用分失败", e);
            return AjaxJson.error("添加失败：" + e.getMessage());
        }
    }

    @ApiOperation("减少信用分：自定义type")
    @PostMapping("/reduceScore")
    public AjaxJson reduceScore(@RequestBody CreditScoreUserOperateDto dto) {
        try {
            if (StringUtils.isBlank(dto.getUserId())) {
                return AjaxJson.error("userId不能为空");
            }
            if (dto.getType() == null) {
                return AjaxJson.error("type不能为空");
            }
            if (Integer.valueOf(5).equals(dto.getType()) && StringUtils.isBlank(dto.getDesc())) {
                return AjaxJson.error("type=5(平台贡献)必须输入desc");
            }
            CreditScoreUserOperateResultVo result =
                    creditScoreUserService.reduceScore(dto.getUserId(), dto.getType(), dto.getSubtype(), dto.getDesc(), dto.getScore());
            return AjaxJson.success().put("result", result);
        } catch (Exception e) {
            log.error("减少信用分失败", e);
            return AjaxJson.error("扣减失败：" + e.getMessage());
        }
    }

    @ApiOperation("用户累计类型分分页（t_member_credit_score_type）")
    @GetMapping("/type/page")
    public AjaxJson pageUserType(CreditScoreUserTypePageQueryDto queryDto) {
        try {
            Page<CreditScoreUserTypeVo> page = creditScoreUserTypeService.page(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询用户信用分类型失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("用户累计类型分详情（按 t_member_credit_score_type.id）")
    @GetMapping("/type/queryById/{id}")
    public AjaxJson queryUserTypeDetail(@PathVariable Long id) {
        try {
            CreditScoreUserTypeVo detail = creditScoreUserTypeService.getDetailById(id);
            if (detail == null) {
                return AjaxJson.error("记录不存在");
            }
            return AjaxJson.success().put("detail", detail);
        } catch (Exception e) {
            log.error("查询用户信用分类型详情失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("用户总信用分详情（t_member_details.credit_score）")
    @GetMapping("/details/queryByUserId/{userId}")
    public AjaxJson queryUserDetails(@PathVariable String userId) {
        try {
            CreditScoreUserDetailsVo detail = creditScoreUserDetailsService.getDetailsByUserId(userId);
            if (detail == null) {
                return AjaxJson.error("用户不存在或无数据");
            }
            return AjaxJson.success().put("detail", detail);
        } catch (Exception e) {
            log.error("查询用户信用分详情失败, userId={}", userId, e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("用户总信用分分页（以 t_member_details 为主表）")
    @GetMapping("/details/page")
    public AjaxJson pageUserDetails(CreditScoreUserDetailsPageQueryDto queryDto) {
        try {
            Page<CreditScoreUserDetailsWithTypesVo> page = creditScoreUserDetailsTypePageService.page(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询用户总信用分失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("用户总信用分详情（以 t_member_details 为主表，附带 types[]）")
    @GetMapping("/details/queryByUserIdWithTypes/{userId}")
    public AjaxJson queryUserDetailsWithTypes(@PathVariable String userId) {
        try {
            CreditScoreUserDetailsWithTypesVo detail =
                    creditScoreUserDetailsTypePageService.getDetailWithTypesByUserId(userId);
            if (detail == null) {
                return AjaxJson.error("用户不存在或无数据");
            }
            return AjaxJson.success().put("detail", detail);
        } catch (Exception e) {
            log.error("查询用户信用分详情(含types)失败, userId={}", userId, e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("后台开通信用分：按基础配置初始分加分并将 credit_status 置为已开通")
    @PostMapping("/activateCredit")
    public AjaxJson activateCredit(@RequestBody CreditScoreUserOperateDto dto) {
        try {
            if (dto == null || StringUtils.isBlank(dto.getUserId())) {
                return AjaxJson.error("userId不能为空");
            }
            CreditScoreUserOperateResultVo result = creditScoreUserService.activateCredit(dto.getUserId());
            return AjaxJson.success().put("result", result);
        } catch (Exception e) {
            log.error("后台开通信用分失败, userId={}", dto != null ? dto.getUserId() : null, e);
            return AjaxJson.error("开通失败：" + e.getMessage());
        }
    }
}

