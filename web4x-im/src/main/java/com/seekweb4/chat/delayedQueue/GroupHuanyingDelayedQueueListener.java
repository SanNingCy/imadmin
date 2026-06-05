package com.seekweb4.chat.delayedQueue;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.groupdingshi.entity.GroupDingshi;
import com.seekweb4.chat.modules.groupdingshi.service.GroupDingshiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 群定时消息
 */
@Component
@Slf4j
public class GroupHuanyingDelayedQueueListener implements RedisDelayedQueueListener<String> {
    @Autowired
    private GroupDingshiService groupDingshiService;

    @Override
    public void invoke(String id) {
        log.info("群定时消息触发：{}",id);
        String[] split = id.split("[-]", 0);
        GroupDingshi dingshi = groupDingshiService.get(split[1]);
        if(dingshi != null){
            if(StringUtils.isNotEmpty(dingshi.getTitle())){
                ImUtils.sendGroupTxtMsg(ImUtils.robot_id,dingshi.getTitle(),split[0]);
            }
            if("2".equals(dingshi.getType())){	//类型 1：纯文本 2：文本+图片 3：文本+视频 4：文本+文件
                if(StringUtils.isNotBlank(dingshi.getImgs())){
                    String[] split2 = dingshi.getImgs().split("[|]", 0);
                    for(String pic:split2){
                        ImUtils.sendGroupPicMsg(ImUtils.robot_id,pic,split[0]);
                    }
                }
            }else if("3".equals(dingshi.getType())){
                if(StringUtils.isNotBlank(dingshi.getVideo())){
                    ImUtils.sendGroupVideoMsg(ImUtils.robot_id,dingshi.getVideo(),split[0],dingshi.getMiao());
                }
            }else if("4".equals(dingshi.getType())){
                if(StringUtils.isNotBlank(dingshi.getPdf())){
                    ImUtils.sendGroupFileMsg(ImUtils.robot_id,dingshi.getPdf(),split[0],dingshi.getName(),dingshi.getSize());
                }
            }
            if("2".equals(dingshi.getTxtype())){//重复提醒
                groupDingshiService.addDingshi(id);
            }
        }
    }
}
