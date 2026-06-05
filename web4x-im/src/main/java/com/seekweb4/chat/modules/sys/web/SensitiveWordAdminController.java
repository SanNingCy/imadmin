package com.seekweb4.chat.modules.sys.web;

import com.alibaba.fastjson2.JSONObject;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.sys.entity.SensitiveWord;
import com.seekweb4.chat.modules.sys.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/admin/sensitive")
public class SensitiveWordAdminController extends BaseController {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * 敏感词分页列表
     */
    @ApiLog("查询全局敏感词(分页)")
    @RequestMapping(value = "list", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson list(SensitiveWord sensitiveWord, HttpServletRequest request, HttpServletResponse response) {
        SensitiveWord query = sensitiveWord != null ? sensitiveWord : new SensitiveWord();
        Page<SensitiveWord> page = sensitiveWordService.findPage(new Page<>(request, response), query);
        List<String> words = new ArrayList<>();
        if (page.getList() != null) {
            for (SensitiveWord item : page.getList()) {
                if (item != null && StringUtils.isNotBlank(item.getWord())) {
                    words.add(item.getWord());
                }
            }
        }
        return AjaxJson.success()
                .put("page", page)   // 分页明细（包含 id/word/createDate/updateDate）
                .put("words", words); // 纯敏感词数组
    }

    @ApiLog("添加全局敏感词")
//    @RequiresPermissions("ops:sys:sensitive:edit")
    @PostMapping("add")
    public AjaxJson add(@RequestBody(required = false) JSONObject body) {
        List<String> words = parseWords(body);
        if (words.isEmpty()) {
            return AjaxJson.error("words不能为空");
        }
        boolean ok = ImUtils.addSensitiveWords(words);
        if (!ok) {
            return AjaxJson.error("添加敏感词失败");
        }
        sensitiveWordService.saveWords(words);
        return AjaxJson.success("添加成功");
    }

    @ApiLog("删除全局敏感词")
//    @RequiresPermissions("ops:sys:sensitive:edit")
    @PostMapping("del")
    public AjaxJson del(@RequestBody(required = false) JSONObject body) {
        List<String> words = parseWords(body);
        if (words.isEmpty()) {
            return AjaxJson.error("words不能为空");
        }
        boolean ok = ImUtils.removeSensitiveWords(words);
        if (!ok) {
            return AjaxJson.error("删除敏感词失败");
        }
        sensitiveWordService.removeWords(words);
        return AjaxJson.success("删除成功");
    }

    @ApiLog("修改全局敏感词")
//    @RequiresPermissions("ops:sys:sensitive:edit")
    @PostMapping("edit")
    public AjaxJson edit(@RequestBody(required = false) JSONObject body) {
        if (body == null) {
            return AjaxJson.error("请求参数不能为空");
        }
        String id = body.getString("id");
        String word = body.getString("word");
        if (StringUtils.isBlank(id)) {
            return AjaxJson.error("id不能为空");
        }
        if (StringUtils.isBlank(word)) {
            return AjaxJson.error("word不能为空");
        }
        String newWord = word.trim();
        SensitiveWord current = sensitiveWordService.get(id);
        if (current == null) {
            return AjaxJson.error("敏感词不存在");
        }
        String oldWord = current.getWord();
        if (StringUtils.equals(oldWord, newWord)) {
            return AjaxJson.success("修改成功");
        }
        SensitiveWord duplicate = sensitiveWordService.findUniqueByProperty("word", newWord);
        if (duplicate != null && !StringUtils.equals(duplicate.getId(), id)) {
            return AjaxJson.error("敏感词已存在");
        }
        boolean delOk = ImUtils.removeSensitiveWords(Collections.singletonList(oldWord));
        if (!delOk) {
            return AjaxJson.error("修改敏感词失败(删除旧词失败)");
        }
        boolean addOk = ImUtils.addSensitiveWords(Collections.singletonList(newWord));
        if (!addOk) {
            // 尝试补偿恢复旧词，避免远端词库漂移
            ImUtils.addSensitiveWords(Collections.singletonList(oldWord));
            return AjaxJson.error("修改敏感词失败(添加新词失败)");
        }
        current.setWord(newWord);
        sensitiveWordService.save(current);
        return AjaxJson.success("修改成功");
    }

    @ApiLog("从野火同步敏感词到本地库")
//    @RequiresPermissions("ops:sys:sensitive:edit")
    @PostMapping("sync")
    public AjaxJson syncFromRemote() {
        List<String> remoteWords = ImUtils.getSensitiveWords();
        sensitiveWordService.replaceAll(remoteWords);
        return AjaxJson.success("同步成功").put("words", remoteWords);
    }

    private List<String> parseWords(JSONObject body) {
        if (body == null) {
            return new ArrayList<>();
        }
        List<String> rawWords = body.getList("words", String.class);
        if (rawWords == null || rawWords.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> words = new ArrayList<>();
        for (String word : rawWords) {
            if (StringUtils.isNotBlank(word)) {
                words.add(word.trim());
            }
        }
        return words;
    }
}
