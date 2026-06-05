package com.seekweb4.chat.agora.roomduration.service.impl;

import com.seekweb4.chat.agora.roomduration.entity.MeetingConfigV2Entity;
import com.seekweb4.chat.agora.roomduration.repository.MeetingConfigV2Repository;
import com.seekweb4.chat.agora.roomduration.service.IMeetingConfigV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MeetingConfigV2ServiceImpl implements IMeetingConfigV2Service {

    @Resource
    private MeetingConfigV2Repository repository;

    @Override
    public MeetingConfigV2Entity getOrInit() {
        List<MeetingConfigV2Entity> all = repository.findAll();
        if (!all.isEmpty()) {
            return all.get(0);
        }
        // 初始化默认配置
        MeetingConfigV2Entity e = buildDefault();
        return repository.save(e);
    }

    @Override
    public MeetingConfigV2Entity save(MeetingConfigV2Entity entity) {
        entity.setUpdateTime(System.currentTimeMillis());
        if (entity.getId() == null) {
            entity.setCreateTime(entity.getUpdateTime());
        }
        return repository.save(entity);
    }

    private MeetingConfigV2Entity buildDefault() {
        MeetingConfigV2Entity e = new MeetingConfigV2Entity();
        e.setAllMic(true);
        e.setAllMute(false);
        e.setStepConsumptionToken(new java.math.BigDecimal("0.08"));
        e.setTimeZone("UTC+8");

        List<Map<String, Object>> tier = new ArrayList<>();
        tier.add(entry(100, "100人以下"));
        tier.add(entry(300, "300人以下"));
        tier.add(entry(600, "600人以下"));
        tier.add(entry(1000, "1000人以下"));
        e.setUserTierOptions(tier);

        List<Map<String, Object>> time = new ArrayList<>();
        time.add(option("30分", 30));
        time.add(option("1小时", 60));
        time.add(option("1小时30分", 90));
        time.add(option("2小时", 120));
        time.add(option("2小时30分", 150));
        time.add(option("3小时", 180));
        e.setTimeOline(time);

        e.setIsNonPayPwd(true);

        e.setRenewalRules("续费规则：每30钟收费"+e.getStepConsumptionToken()+"WebX，不足30分钟按30分钟计费。");

        long now = System.currentTimeMillis();
        e.setCreateTime(now);
        e.setUpdateTime(now);
        return e;
    }

    private Map<String, Object> entry(int value, String name) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("value", value);
        return m;
    }

    private Map<String, Object> option(String name, int value) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("value", value);
        return m;
    }
}


