package com.seekweb4.chat.delayedQueue;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.kejian.entity.Kejian;
import com.seekweb4.chat.modules.kejian.service.KejianService;
import com.seekweb4.chat.modules.kejianzugroup.entity.KejianZuGroup;
import com.seekweb4.chat.modules.kejianzugroup.service.KejianZuGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 课件消息
 */
@Component
@Slf4j
public class KejianDelayedQueueListener implements RedisDelayedQueueListener<String> {

    @Autowired
    private KejianService kejianService;
    @Autowired
    private KejianZuGroupService kejianZuGroupService;

    @Override
    public void invoke(String id) {
        log.info("课件消息触发：{}",id);
        Kejian kejian = kejianService.get(id);
        if(kejian != null){
            KejianZuGroup group = new KejianZuGroup();
            group.setZu(kejian.getZu());
            List<KejianZuGroup> list = kejianZuGroupService.findList(group);
            for(KejianZuGroup g:list){
                if("1".equals(kejian.getType())){//类型 1：纯文本 2：图片 3：视频 4：文件 5：语音
                    ImUtils.sendGroupTxtMsg(ImUtils.robot_id,kejian.getTitle(),g.getGroup().getId());
                }else if("2".equals(kejian.getType())){
                    if(StringUtils.isNotBlank(kejian.getImgs())){
                        String[] split2 = kejian.getImgs().split("[|]", 0);
                        for(String pic:split2){
                            ImUtils.sendGroupPicMsg(ImUtils.robot_id,pic,g.getGroup().getId());
                        }
                    }
                }else if("3".equals(kejian.getType())){
                    if(StringUtils.isNotBlank(kejian.getVideo())){
                        ImUtils.sendGroupVideoMsg(ImUtils.robot_id,kejian.getVideo(),g.getGroup().getId(),kejian.getMiao());
                    }
                }else if("4".equals(kejian.getType())){
                    if(StringUtils.isNotBlank(kejian.getPdf())){
                        ImUtils.sendGroupFileMsg(ImUtils.robot_id,kejian.getPdf(),g.getGroup().getId(),kejian.getName(),kejian.getSize());
                    }
                }else if("5".equals(kejian.getType())){
                    if(StringUtils.isNotBlank(kejian.getSound())){
                        ImUtils.sendGroupSoundMsg(ImUtils.robot_id,kejian.getSound(),g.getGroup().getId(),kejian.getMiao());
                    }
                }
            }
        }
    }
}
