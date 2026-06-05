package com.seekweb4.chat.modules.piamom.controller.admin;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.piamom.dto.*;
import com.seekweb4.chat.modules.piamom.entity.*;
import com.seekweb4.chat.modules.piamom.service.PiamomAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@Slf4j
@RestController
@RequestMapping(value = "/admin/piamom", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "朋友圈/广场(piamom)后台管理")
public class PiamomAdminController extends BaseController {

    @Resource
    private PiamomAdminService piamomAdminService;

    // ===== 朋友圈动态 =====

    @ApiOperation("分页查询朋友圈动态")
    @ApiLog("分页查询朋友圈动态")
    @GetMapping("moment/page")
    public AjaxJson momentPage(PiamomMomentQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.momentPage(queryDto));
        } catch (Exception e) {
            log.error("分页查询朋友圈失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("朋友圈动态详情")
    @GetMapping("moment/queryById")
    public AjaxJson momentQueryById(@RequestParam Long id) {
        PiamomMoment moment = piamomAdminService.momentGetById(id);
        return moment == null ? AjaxJson.error("不存在") : AjaxJson.success().put("moment", moment);
    }

    @ApiOperation("删除朋友圈动态")
    @ApiLog("删除朋友圈动态")
    @DeleteMapping("moment/delete")
    public AjaxJson momentDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.momentDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除朋友圈失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    @ApiOperation("朋友圈置顶/取消置顶")
    @ApiLog("朋友圈置顶")
    @PostMapping("moment/updateTop")
    public AjaxJson momentUpdateTop(@RequestBody PiamomTopDto dto) {
        try {
            return piamomAdminService.momentUpdateTop(dto) ? AjaxJson.success("操作成功") : AjaxJson.error("操作失败");
        } catch (Exception e) {
            log.error("朋友圈置顶失败", e);
            return AjaxJson.error("操作失败：" + e.getMessage());
        }
    }

    // ===== 朋友圈评论 =====

    @ApiOperation("分页查询朋友圈评论（按动态查评论传 momentId）")
    @GetMapping("moment/comment/page")
    public AjaxJson momentCommentPage(PiamomCommentQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.momentCommentPage(queryDto));
        } catch (Exception e) {
            log.error("分页查询朋友圈评论失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("朋友圈评论详情")
    @GetMapping("moment/comment/queryById")
    public AjaxJson momentCommentQueryById(@RequestParam Long id) {
        PiamomMomentComment comment = piamomAdminService.momentCommentGetById(id);
        return comment == null ? AjaxJson.error("不存在") : AjaxJson.success().put("comment", comment);
    }

    @ApiOperation("删除朋友圈评论")
    @ApiLog("删除朋友圈评论")
    @DeleteMapping("moment/comment/delete")
    public AjaxJson momentCommentDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.momentCommentDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除朋友圈评论失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 朋友圈点赞 =====

    @ApiOperation("分页查询朋友圈点赞记录（按动态查点赞传 momentId）")
    @ApiLog("分页查询朋友圈点赞记录")
    @GetMapping("moment/like/page")
    public AjaxJson momentLikePage(PiamomMomentLikeQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.momentLikePage(queryDto));
        } catch (Exception e) {
            log.error("分页查询朋友圈点赞失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("朋友圈点赞记录详情")
    @GetMapping("moment/like/queryById")
    public AjaxJson momentLikeQueryById(@RequestParam Long id) {
        PiamomMomentLike like = piamomAdminService.momentLikeGetById(id);
        return like == null ? AjaxJson.error("不存在") : AjaxJson.success().put("like", like);
    }

    @ApiOperation("删除朋友圈点赞记录")
    @ApiLog("删除朋友圈点赞记录")
    @DeleteMapping("moment/like/delete")
    public AjaxJson momentLikeDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.momentLikeDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除朋友圈点赞失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 广场帖子 =====

    @ApiOperation("分页查询广场帖子")
    @ApiLog("分页查询广场帖子")
    @GetMapping("square/page")
    public AjaxJson squarePage(PiamomSquarePostQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.squarePage(queryDto));
        } catch (Exception e) {
            log.error("分页查询广场失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("广场帖子详情")
    @GetMapping("square/queryById")
    public AjaxJson squareQueryById(@RequestParam Long id) {
        PiamomSquarePost post = piamomAdminService.squareGetById(id);
        return post == null ? AjaxJson.error("不存在") : AjaxJson.success().put("square", post);
    }

    @ApiOperation("删除广场帖子")
    @ApiLog("删除广场帖子")
    @DeleteMapping("square/delete")
    public AjaxJson squareDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.squareDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除广场帖子失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    @ApiOperation("广场置顶/取消置顶")
    @ApiLog("广场置顶")
    @PostMapping("square/updateTop")
    public AjaxJson squareUpdateTop(@RequestBody PiamomTopDto dto) {
        try {
            return piamomAdminService.squareUpdateTop(dto) ? AjaxJson.success("操作成功") : AjaxJson.error("操作失败");
        } catch (Exception e) {
            log.error("广场置顶失败", e);
            return AjaxJson.error("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新广场质押状态 0质押中 1已退还 2举报没收")
    @ApiLog("更新广场质押状态")
    @PostMapping("square/updateStakeStatus")
    public AjaxJson squareUpdateStakeStatus(@RequestParam Long id, @RequestParam Integer odicStakeStatus) {
        try {
            return piamomAdminService.squareUpdateStakeStatus(id, odicStakeStatus)
                    ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新广场质押状态失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    // ===== 广场评论 =====

    @ApiOperation("分页查询广场评论（按帖子查评论传 squareId）")
    @GetMapping("square/comment/page")
    public AjaxJson squareCommentPage(PiamomCommentQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.squareCommentPage(queryDto));
        } catch (Exception e) {
            log.error("分页查询广场评论失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("广场评论详情")
    @GetMapping("square/comment/queryById")
    public AjaxJson squareCommentQueryById(@RequestParam Long id) {
        PiamomSquareComment comment = piamomAdminService.squareCommentGetById(id);
        return comment == null ? AjaxJson.error("不存在") : AjaxJson.success().put("comment", comment);
    }

    @ApiOperation("删除广场评论")
    @ApiLog("删除广场评论")
    @DeleteMapping("square/comment/delete")
    public AjaxJson squareCommentDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.squareCommentDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除广场评论失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 广场点赞 =====

    @ApiOperation("分页查询广场点赞记录（按帖子查点赞传 squareId）")
    @ApiLog("分页查询广场点赞记录")
    @GetMapping("square/like/page")
    public AjaxJson squareLikePage(PiamomSquareLikeQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.squareLikePage(queryDto));
        } catch (Exception e) {
            log.error("分页查询广场点赞失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("广场点赞记录详情")
    @GetMapping("square/like/queryById")
    public AjaxJson squareLikeQueryById(@RequestParam Long id) {
        PiamomSquareLike like = piamomAdminService.squareLikeGetById(id);
        return like == null ? AjaxJson.error("不存在") : AjaxJson.success().put("like", like);
    }

    @ApiOperation("删除广场点赞记录")
    @ApiLog("删除广场点赞记录")
    @DeleteMapping("square/like/delete")
    public AjaxJson squareLikeDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.squareLikeDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除广场点赞失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 热门及广场配置 =====

    @ApiOperation("热门及广场配置列表")
    @GetMapping("hotConfig/list")
    public AjaxJson hotConfigList() {
        return AjaxJson.success().put("list", piamomAdminService.hotConfigList());
    }

    @ApiOperation("热门及广场配置详情")
    @GetMapping("hotConfig/queryById")
    public AjaxJson hotConfigQueryById(@RequestParam Long id) {
        PiamomHotConfig config = piamomAdminService.hotConfigGetById(id);
        return config == null ? AjaxJson.error("不存在") : AjaxJson.success().put("config", config);
    }

    @ApiOperation("新增热门及广场配置")
    @ApiLog("新增热门及广场配置")
    @PostMapping("hotConfig/save")
    public AjaxJson hotConfigSave(@RequestBody PiamomHotConfig config) {
        try {
            return piamomAdminService.hotConfigCreate(config) ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增热门配置失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新热门及广场配置")
    @ApiLog("更新热门及广场配置")
    @PostMapping("hotConfig/update")
    public AjaxJson hotConfigUpdate(@RequestBody PiamomHotConfig config) {
        try {
            return piamomAdminService.hotConfigUpdate(config) ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新热门配置失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除热门及广场配置")
    @ApiLog("删除热门及广场配置")
    @DeleteMapping("hotConfig/delete")
    public AjaxJson hotConfigDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.hotConfigDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除热门配置失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 广场发帖信用分额度档位 =====

    @ApiOperation("广场发帖额度档位列表")
    @GetMapping("squarePublishQuota/list")
    public AjaxJson squarePublishQuotaList() {
        return AjaxJson.success().put("list", piamomAdminService.squarePublishQuotaList());
    }

    @ApiOperation("广场发帖额度档位详情")
    @GetMapping("squarePublishQuota/queryById")
    public AjaxJson squarePublishQuotaQueryById(@RequestParam Long id) {
        PiamomSquarePublishQuota quota = piamomAdminService.squarePublishQuotaGetById(id);
        return quota == null ? AjaxJson.error("不存在") : AjaxJson.success().put("quota", quota);
    }

    @ApiOperation("新增广场发帖额度档位")
    @ApiLog("新增广场发帖额度档位")
    @PostMapping("squarePublishQuota/save")
    public AjaxJson squarePublishQuotaSave(@RequestBody PiamomSquarePublishQuota quota) {
        try {
            return piamomAdminService.squarePublishQuotaCreate(quota) ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增广场发帖额度档位失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新广场发帖额度档位")
    @ApiLog("更新广场发帖额度档位")
    @PostMapping("squarePublishQuota/update")
    public AjaxJson squarePublishQuotaUpdate(@RequestBody PiamomSquarePublishQuota quota) {
        try {
            return piamomAdminService.squarePublishQuotaUpdate(quota) ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新广场发帖额度档位失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除广场发帖额度档位")
    @ApiLog("删除广场发帖额度档位")
    @DeleteMapping("squarePublishQuota/delete")
    public AjaxJson squarePublishQuotaDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.squarePublishQuotaDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除广场发帖额度档位失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 举报配置 =====

    @ApiOperation("举报类型配置列表")
    @GetMapping("reportConfig/list")
    public AjaxJson reportConfigList() {
        return AjaxJson.success().put("list", piamomAdminService.reportConfigList());
    }

    @ApiOperation("举报类型配置详情")
    @GetMapping("reportConfig/queryById")
    public AjaxJson reportConfigQueryById(@RequestParam Long id) {
        PiamomReportConfig config = piamomAdminService.reportConfigGetById(id);
        return config == null ? AjaxJson.error("不存在") : AjaxJson.success().put("config", config);
    }

    @ApiOperation("新增举报类型配置")
    @PostMapping("reportConfig/save")
    public AjaxJson reportConfigSave(@RequestBody PiamomReportConfig config) {
        try {
            return piamomAdminService.reportConfigCreate(config) ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增举报配置失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新举报类型配置")
    @PostMapping("reportConfig/update")
    public AjaxJson reportConfigUpdate(@RequestBody PiamomReportConfig config) {
        try {
            return piamomAdminService.reportConfigUpdate(config) ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新举报配置失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除举报类型配置")
    @DeleteMapping("reportConfig/delete")
    public AjaxJson reportConfigDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.reportConfigDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除举报配置失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 举报记录 =====

    @ApiOperation("分页查询举报记录")
    @GetMapping("reportRecord/page")
    public AjaxJson reportRecordPage(PiamomReportRecordQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.reportRecordPage(queryDto));
        } catch (Exception e) {
            log.error("分页查询举报记录失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("举报记录详情")
    @GetMapping("reportRecord/queryById")
    public AjaxJson reportRecordQueryById(@RequestParam Long id) {
        PiamomReportRecord record = piamomAdminService.reportRecordGetById(id);
        return record == null ? AjaxJson.error("不存在") : AjaxJson.success().put("record", record);
    }

    @ApiOperation("审核举报记录 1通过(下架隐藏帖子;广场帖质押中→没收,不退ODIC) 2驳回(不改帖子与质押)")
    @ApiLog("审核举报记录")
    @PostMapping("reportRecord/audit")
    public AjaxJson reportRecordAudit(@RequestBody PiamomReportAuditDto dto) {
        try {
            return piamomAdminService.reportRecordAudit(dto) ? AjaxJson.success("审核成功") : AjaxJson.error("审核失败");
        } catch (Exception e) {
            log.error("审核举报记录失败", e);
            return AjaxJson.error("审核失败：" + e.getMessage());
        }
    }

    // ===== 互动消息 =====

    @ApiOperation("分页查询互动消息")
    @GetMapping("notify/page")
    public AjaxJson notifyPage(PiamomNotifyQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.notifyPage(queryDto));
        } catch (Exception e) {
            log.error("分页查询互动消息失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("互动消息详情")
    @GetMapping("notify/queryById")
    public AjaxJson notifyQueryById(@RequestParam Long id) {
        PiamomNotifyMessage msg = piamomAdminService.notifyGetById(id);
        return msg == null ? AjaxJson.error("不存在") : AjaxJson.success().put("notify", msg);
    }

    // ===== 用户主页统计 =====

    @ApiOperation("分页查询用户主页统计")
    @GetMapping("profileStat/page")
    public AjaxJson profileStatPage(PiamomProfileStatQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.profileStatPage(queryDto));
        } catch (Exception e) {
            log.error("分页查询用户主页统计失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("用户主页统计详情(按id)")
    @GetMapping("profileStat/queryById")
    public AjaxJson profileStatQueryById(@RequestParam Long id) {
        PiamomUserProfileStat stat = piamomAdminService.profileStatGetById(id);
        return stat == null ? AjaxJson.error("不存在") : AjaxJson.success().put("stat", stat);
    }

    @ApiOperation("用户主页统计详情(按userId)")
    @GetMapping("profileStat/queryByUserId")
    public AjaxJson profileStatQueryByUserId(@RequestParam String userId) {
        PiamomUserProfileStat stat = piamomAdminService.profileStatGetByUserId(userId);
        return stat == null ? AjaxJson.error("不存在") : AjaxJson.success().put("stat", stat);
    }

    // ===== 用户获赞记录 =====

    @ApiOperation("分页查询用户获赞记录")
    @ApiLog("分页查询用户获赞记录")
    @GetMapping("user/likeRecord/page")
    public AjaxJson userLikeRecordPage(PiamomUserLikeRecordQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.userLikeRecordPage(queryDto));
        } catch (Exception e) {
            log.error("分页查询用户获赞记录失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("用户获赞记录详情")
    @GetMapping("user/likeRecord/queryById")
    public AjaxJson userLikeRecordQueryById(@RequestParam Long id) {
        PiamomUserLikeRecord record = piamomAdminService.userLikeRecordGetById(id);
        return record == null ? AjaxJson.error("不存在") : AjaxJson.success().put("record", record);
    }

    @ApiOperation("删除用户获赞记录")
    @ApiLog("删除用户获赞记录")
    @DeleteMapping("user/likeRecord/delete")
    public AjaxJson userLikeRecordDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.userLikeRecordDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除用户获赞记录失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 关注关系 =====

    @ApiOperation("分页查询用户关注")
    @GetMapping("follow/page")
    public AjaxJson followPage(PiamomFollowQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.followPage(queryDto));
        } catch (Exception e) {
            log.error("分页查询关注失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("关注关系详情")
    @GetMapping("follow/queryById")
    public AjaxJson followQueryById(@RequestParam Long id) {
        PiamomUserFollow follow = piamomAdminService.followGetById(id);
        return follow == null ? AjaxJson.error("不存在") : AjaxJson.success().put("follow", follow);
    }

    @ApiOperation("删除关注关系")
    @DeleteMapping("follow/delete")
    public AjaxJson followDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.followDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除关注失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 拉黑 =====

    @ApiOperation("分页查询拉黑记录")
    @GetMapping("blacklist/page")
    public AjaxJson blacklistPage(PiamomBlacklistQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", piamomAdminService.blacklistPage(queryDto));
        } catch (Exception e) {
            log.error("分页查询拉黑失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("拉黑记录详情")
    @GetMapping("blacklist/queryById")
    public AjaxJson blacklistQueryById(@RequestParam Long id) {
        PiamomUserBlacklist blacklist = piamomAdminService.blacklistGetById(id);
        return blacklist == null ? AjaxJson.error("不存在") : AjaxJson.success().put("blacklist", blacklist);
    }

    @ApiOperation("删除拉黑记录")
    @DeleteMapping("blacklist/delete")
    public AjaxJson blacklistDelete(@RequestParam Long id) {
        try {
            return piamomAdminService.blacklistDelete(id) ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除拉黑失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }
}
