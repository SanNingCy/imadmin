package com.seekweb4.chat.modules.creditScore.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Data
public class CreditScoreConfigQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private Long id;

    private String orderBy;

    private static final Map<String, String> ORDER_BY_COLUMNS = new HashMap<>();
    static {
        ORDER_BY_COLUMNS.put("id", "id");
        ORDER_BY_COLUMNS.put("createTime", "create_time");
        ORDER_BY_COLUMNS.put("updateTime", "update_time");
        ORDER_BY_COLUMNS.put("initScore", "init_score");
        ORDER_BY_COLUMNS.put("vipBonusRate", "vip_bonus_rate");
        ORDER_BY_COLUMNS.put("lianghaoBonusRate", "lianghao_bonus_rate");
        ORDER_BY_COLUMNS.put("price", "price");
        ORDER_BY_COLUMNS.put("scoreInfo", "score_info");
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = normalizeOrderBy(orderBy);
    }

    private String normalizeOrderBy(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(", ");
        String[] items = raw.split(",");
        for (String item : items) {
            if (item == null || item.trim().isEmpty()) {
                continue;
            }
            String[] parts = item.trim().split("\\s+");
            if (parts.length == 0) {
                continue;
            }
            String column = ORDER_BY_COLUMNS.get(parts[0]);
            if (column == null) {
                continue;
            }
            String direction = "desc";
            if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1])) {
                direction = "asc";
            }
            joiner.add(column + " " + direction);
        }
        return joiner.length() == 0 ? null : joiner.toString();
    }
}
