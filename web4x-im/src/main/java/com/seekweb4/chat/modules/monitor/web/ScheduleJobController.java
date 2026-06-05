package com.seekweb4.chat.modules.monitor.web;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.monitor.entity.ScheduleJob;
import com.seekweb4.chat.modules.monitor.service.ScheduleJobService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 定时任务Controller
 *
 * @author lgf
 * @version 2017-02-04
 */
@RestController
@RequestMapping("/quartz/scheduleJob")
public class ScheduleJobController extends BaseController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @ModelAttribute
    public ScheduleJob get(@RequestParam(required = false) String id) {
        ScheduleJob entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = scheduleJobService.get(id);
        }
        if (entity == null) {
            entity = new ScheduleJob();
        }
        return entity;
    }


    /**
     * 定时任务列表数据
     */
    @RequiresPermissions("quartz:scheduleJob:list")
    @GetMapping("list")
    public AjaxJson data(ScheduleJob scheduleJob, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<ScheduleJob> page = scheduleJobService.findPage(new Page<ScheduleJob>(request, response), scheduleJob);
        return AjaxJson.success().put("page", page);
    }

    /**
     * 查看，增加，编辑定时任务表单页面
     */
    @GetMapping("queryById")
    public AjaxJson queryById(ScheduleJob scheduleJob, Model model) {
        return AjaxJson.success().put("scheduleJob", scheduleJob);
    }

    /**
     * 保存定时任务
     */
    @RequiresPermissions(value = {"quartz:scheduleJob:add", "quartz:scheduleJob:edit"}, logical = Logical.OR)
    @PostMapping("save")
    public AjaxJson save(ScheduleJob scheduleJob, Model model, RedirectAttributes redirectAttributes) throws Exception {
        /**
         * 后台hibernate-validation插件校验
         */
        String errMsg = beanValidator(scheduleJob);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }

        //验证cron表达式
        if (!CronExpression.isValidExpression(scheduleJob.getCronExpression())) {
            return AjaxJson.error("cron表达式不正确！");
        }

        scheduleJobService.save(scheduleJob);//保存

        return AjaxJson.success("保存定时任务成功!");
    }


    /**
     * 批量删除定时任务
     */
    @RequiresPermissions("quartz:scheduleJob:del")
    @DeleteMapping("delete")
    public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
        AjaxJson j = new AjaxJson();
        String idArray[] = ids.split(",");
        for (String id : idArray) {
            scheduleJobService.delete(scheduleJobService.get(id));
        }
        return AjaxJson.success("删除定时任务成功");
    }


    /**
     * 暂停任务
     */
    @RequiresPermissions("quartz:scheduleJob:stop")
    @PostMapping("stop")
    public AjaxJson stop(ScheduleJob scheduleJob) {
        scheduleJobService.stopJob(scheduleJob);
        return AjaxJson.success("暂停成功!");
    }


    /**
     * 立即运行一次
     */
    @RequiresPermissions("quartz:scheduleJob:startNow")
    @PostMapping("startNow")
    public AjaxJson stratNow(ScheduleJob scheduleJob) {
        scheduleJobService.startNowJob(scheduleJob);
        return AjaxJson.success("运行成功");
    }

    /**
     * 恢复
     */
    @RequiresPermissions("quartz:scheduleJob:resume")
    @PostMapping("resume")
    public AjaxJson resume(ScheduleJob scheduleJob, RedirectAttributes redirectAttributes) {
        scheduleJobService.restartJob(scheduleJob);
        scheduleJobService.startNowJob(scheduleJob);//恢复之后，立即触发一次激活定时任务，不然定时任务有可能不会执行，这是bug的回避案，具体原因我没找到。
        return AjaxJson.success("恢复成功");
    }

    /**
     * 验证类任务类是否存在
     */
    @GetMapping("existsClass")
    public AjaxJson existsClass(String className) {
        Class job = null;
        try {
            job = Class.forName(className);
            return AjaxJson.success();
        } catch (ClassNotFoundException e1) {
            return AjaxJson.error("类不存在");
        }
    }

}
