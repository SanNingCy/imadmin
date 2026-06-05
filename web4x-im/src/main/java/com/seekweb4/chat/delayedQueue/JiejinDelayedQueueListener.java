package com.seekweb4.chat.delayedQueue;

import com.seekweb4.chat.api.utils.yehuo.ImUtils;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.group.service.GroupService;
import com.seekweb4.chat.modules.groupitem.entity.GroupItem;
import com.seekweb4.chat.modules.groupitem.service.GroupItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 群成员发言解禁
 */
@Component
@Slf4j
public class JiejinDelayedQueueListener implements RedisDelayedQueueListener<String> {
    @Autowired
    private GroupItemService groupItemService;
    @Autowired
    private GroupService groupService;

    @Override
    public void invoke(String id) {
        GroupItem item = groupItemService.get(id);
        if(item != null && item.getJyTime() != null){
            try{
                Thread.sleep(1000);
                if("1".equals(item.getIsjy()) && item.getJyTime().before(new Date())){
                    Group group = groupService.get(item.getGroup().getId());
                    groupItemService.executeUpdateSql("update t_group_item set isjy = '0',jy_time = null where id = '"+id+"'");
                    List<String> ids = new ArrayList<>();
                    ids.add(item.getU().getId());
                    ImUtils.jinyan(group.getU().getId(),group.getId(),ids,false);
                }
            }catch (Exception e){

            }
        }
    }
}
