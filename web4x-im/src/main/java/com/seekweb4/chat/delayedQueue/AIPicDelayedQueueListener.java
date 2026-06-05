package com.seekweb4.chat.delayedQueue;


import com.seekweb4.chat.api.utils.AiChatUtils;
import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.group.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ai返回图片
 */
@Component
@Slf4j
public class AIPicDelayedQueueListener implements RedisDelayedQueueListener<String> {

    @Autowired
    private GroupService groupService;

    @Override
    public void invoke(String id) {
        String[] split = id.split("[-]", 0);
        String uid = split[0];
        String info = split[1];
        String gid = split[2];
        Group group = groupService.get(gid);
        Map<String, String> map = AiChatUtils.chatImg(uid, info, group.getAikey());
        if("0".equals(map.get("code"))){
            List<String> extracted = extractParenthesesContent(map.get("pic"));
            for(String pic:extracted){
                ImUtils.sendGroupPicMsg(ImUtils.robot_id,pic,group.getId());
            }
        }
    }
    public static List<String> extractParenthesesContent(String input) {
        List<String> result = new ArrayList<>();
        // 正则表达式匹配小括号内的内容
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }
}
