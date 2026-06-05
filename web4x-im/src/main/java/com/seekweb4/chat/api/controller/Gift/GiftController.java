//package com.seekweb4.chat.modules.roomgift.controller;
package com.seekweb4.chat.api.controller.Gift;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
import com.seekweb4.chat.modules.roomgift.dao.GiftResponseDto;
import com.seekweb4.chat.modules.roomgift.service.GiftService;
import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 礼物配置移动端Controller接口
 * @author cycdev
 * @version 2025-10-23
 */
@Slf4j
@RestController
@RequestMapping("/gift")
public class GiftController {
    @Autowired
    private GiftService giftService;
    @Autowired
    private RedisDelayedQueue redisDelayedQueue;

    /**
     * 获取礼物配置信息
     * @return 礼物配置
     */
    @GetMapping("/config")
    public AjaxJson getGiftConfig() {
        try {
            GiftResponseDto config = giftService.getGiftConfig();
            return AjaxJson.success().put("data", config);
        } catch (Exception e) {
            log.error("获取礼物配置失败", e);
            return AjaxJson.error("获取礼物配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取免费礼物列表
     * @return 免费礼物列表
     */
    @GetMapping("/free")
    public AjaxJson getFreeGifts() {
        try {
            return AjaxJson.success().setDataList(giftService.getGiftsByType("1"));
        } catch (Exception e) {
            log.error("获取免费礼物失败", e);
            return AjaxJson.error("获取免费礼物失败: " + e.getMessage());
        }
    }

    /**
     * 获取付费礼物列表
     * @return 付费礼物列表
     */
    @GetMapping("/paid")
    public AjaxJson getPaidGifts() {
        try {
            return AjaxJson.success().setDataList(giftService.getGiftsByType("2"));
        } catch (Exception e) {
            log.error("获取付费礼物失败", e);
            return AjaxJson.error("获取付费礼物失败: " + e.getMessage());
        }
    }

    /**
     * 礼物扣费接口
     * @param body 请求参数，包含userId和price
     * @return 扣费结果
     */
    @PostMapping("/deduct")
    public AjaxJson deductGift(@RequestBody Map<String, Object> body) {
        try {
            String userId = body.get("ownerId") == null ? null : String.valueOf(body.get("ownerId"));
            String priceStr = body.get("price") == null ? null : String.valueOf(body.get("price"));
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                return AjaxJson.error("用户ID不能为空");
            }
            if (priceStr == null || priceStr.trim().isEmpty()) {
                return AjaxJson.error("价格不能为空");
            }
            
            BigDecimal price;
            try {
                price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    return AjaxJson.error("价格必须大于0");
                }
            } catch (NumberFormatException e) {
                return AjaxJson.error("价格格式不正确");
            }
            
            // 调用扣费服务
            Map<String, BigDecimal> result = giftService.deductGiftPrice(userId, price);
            
            Map<String, Object> response = new HashMap<>();
            response.put("deductPrice", result.get("deductPrice"));
            response.put("remainBalance", result.get("remainBalance"));
            
            return AjaxJson.success().setData(response);
            
        } catch (IllegalArgumentException e) {
            return AjaxJson.error(e.getMessage());
        } catch (Exception e) {
            log.error("礼物扣费失败", e);
            return AjaxJson.error(e.getMessage());
        }
    }

    @PostMapping("/deductGiftliwu")
    public AjaxJson deductGiftliwu(@RequestBody Map<String, Object> body) {
        try {
            String userId = body.get("ownerId") == null ? null : String.valueOf(body.get("ownerId"));
            String giftId = body.get("giftId") == null ? null : String.valueOf(body.get("giftId"));
            String quantityStr = body.get("quantity") == null ? "1" : String.valueOf(body.get("quantity"));
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                return AjaxJson.error("用户ID不能为空");
            }
            if (giftId == null || giftId.trim().isEmpty()) {
                return AjaxJson.error("礼物ID不能为空");
            }

            Integer quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    return AjaxJson.error("礼物数量必须大于0");
                }
            } catch (NumberFormatException e) {
                return AjaxJson.error("礼物数量格式不正确");
            }

            // 调用扣费服务
            Map<String, BigDecimal> result = giftService.deductGiftPriceLiwu(userId, giftId,quantity);

            Map<String, Object> response = new HashMap<>();
            response.put("deductPrice", result.get("deductPrice"));
            response.put("remainBalance", result.get("remainBalance"));

            return AjaxJson.success().setData(response);

        } catch (IllegalArgumentException e) {
            return AjaxJson.error(e.getMessage());
        } catch (Exception e) {
            log.error("礼物扣费失败", e);
            return AjaxJson.error(e.getMessage());
        }
    }


    /**
     * 赠送礼物接口
     * @param body 请求参数，包含赠送者、接收者、礼物ID等信息
     * @return 赠送结果
     */
    @PostMapping("/send")
    public AjaxJson sendGift(@RequestBody Map<String, Object> body) {
        try {
            String senderId = body.get("senderId") == null ? null : String.valueOf(body.get("senderId"));
            String receiverId = body.get("receiverId") == null ? null : String.valueOf(body.get("receiverId"));
            String giftId = body.get("giftId") == null ? null : String.valueOf(body.get("giftId"));
            String quantityStr = body.get("quantity") == null ? "1" : String.valueOf(body.get("quantity"));

            // 参数验证
            if (senderId == null || senderId.trim().isEmpty()) {
                return AjaxJson.error("赠送者ID不能为空");
            }
            if (receiverId == null || receiverId.trim().isEmpty()) {
                return AjaxJson.error("接收者ID不能为空");
            }
            if (giftId == null || giftId.trim().isEmpty()) {
                return AjaxJson.error("礼物ID不能为空");
            }
            if (senderId.equals(receiverId)) {
                return AjaxJson.error("不能给自己赠送礼物");
            }

            Integer quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    return AjaxJson.error("赠送数量必须大于0");
                }
            } catch (NumberFormatException e) {
                return AjaxJson.error("赠送数量格式不正确");
            }
            
            // 调用赠送服务
            Map<String, Object> result = giftService.sendGift(senderId, receiverId, giftId, quantity);

            return AjaxJson.success().setData(result);
            
        } catch (IllegalArgumentException e) {
            return AjaxJson.error(e.getMessage());
        } catch (Exception e) {
            log.error("赠送礼物失败", e);
            return AjaxJson.error("赠送礼物失败: " + e.getMessage());
        }
    }
}
