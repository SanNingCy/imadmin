package com.seekweb4.chat.modules.roomgift.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.roomgift.dao.GiftResponseDto;
import com.seekweb4.chat.modules.roomgift.entity.Gift;
import com.seekweb4.chat.modules.roomgift.mapper.GiftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GiftService {
    @Autowired
    private GiftMapper giftMapper;

    @Autowired
    private GiftConfigService giftConfigService;

    @Autowired
    private MemberUserBalanceService userBalanceService;

    /**
     * 获取礼物配置信息
     *
     * @return 礼物配置响应
     */
    public GiftResponseDto getGiftConfig() {
        GiftResponseDto response = new GiftResponseDto();

        // 获取启用的礼物列表
        List<Gift> gifts = giftMapper.findEnabledGifts();
        List<GiftResponseDto.GiftDto> giftDtos = gifts.stream().map(gift -> {
            GiftResponseDto.GiftDto dao = new GiftResponseDto.GiftDto();
            dao.setId(gift.getId());
            dao.setImg(gift.getImg());
            dao.setName(gift.getName());
            // 根据礼物类型设置价值
            if ("1".equals(gift.getType())) {
                dao.setValue("0");
            } else {
                dao.setValue(gift.getValue());
            }
            return dao;
        }).collect(Collectors.toList());

        response.setRewardGifts(giftDtos); // 礼物列表

        String guidelines = giftConfigService.getGiftGuidelines();
        response.setGiftGuidelines(guidelines);

        // 获取免密支付配置
        Boolean isNonPayPwd = giftConfigService.getIsNonPayPwd();
        response.setIsNonPayPwd(isNonPayPwd);

        return response;
    }

    /**
     * 根据类型获取礼物列表
     *
     * @param type 礼物类型
     * @return 礼物列表
     */
    public List<Gift> getGiftsByType(String type) {
        return giftMapper.findGiftsByType(type);
    }

    /**
     * 根据ID获取礼物
     *
     * @param id 礼物ID
     * @return 礼物信息
     */
    public Gift get(String id) {
        return giftMapper.get(id);
    }

    /**
     * 获取礼物列表
     *
     * @param gift 查询条件
     * @return 礼物列表
     */

    public List<Gift> findList(Gift gift) {
        return giftMapper.findList(gift);
    }

    /**
     * 保存礼物x`x`
     *
     * @param gift 礼物信息
     */
    @Transactional(readOnly = false)
    public void save(Gift gift) {
        if (gift.getId() == null || gift.getId().isEmpty()) {
            gift.preInsert();
            giftMapper.insert(gift);
        }
    }

    @Transactional(readOnly = false)
    public void update(Gift gift) {
        if (gift.getId() != null) {
            gift.preUpdate();
            giftMapper.update(gift);
        }
    }

    /**
     * 删除礼物
     *
     * @param gift 礼物信息
     */
    @Transactional(readOnly = false)
    public void delete(Gift gift) {
        giftMapper.delete(gift);
    }

    /**
     * 分页查询礼物列表
     *
     * @param page 分页对象
     * @param gift 查询条件
     * @return 分页结果
     */
    public Page<Gift> findPage(Page<Gift> page, Gift gift) {
        gift.setPage(page);
        List<Gift> list = giftMapper.findPage(gift);
        page.setList(list);
        return page;
    }

    /**
     * 礼物扣费
     *
     * @param ownerId 用户ID
     * @param price 扣费金额
     * @return 扣费结果
     */
    @Transactional(readOnly = false)
    public Map<String, BigDecimal> deductGiftPrice(String ownerId, BigDecimal price) throws Exception {
        try {
            if (price.compareTo(BigDecimal.ZERO) != 0) {
                // 扣除用户余额
                userBalanceService.deductUserBalance(ownerId, price);
                
                // 记录余额日志
                userBalanceService.recordBalanceLog(ownerId, "礼物扣费", price, "0", "6", "礼物购买扣费");
            }

            // 获取扣费后的余额
            BigDecimal remainBalance = userBalanceService.getUserBalance(ownerId);
            Map<String, BigDecimal> result = new HashMap<>();
            result.put("deductPrice", price.setScale(2, RoundingMode.HALF_UP));
            result.put("remainBalance", remainBalance.setScale(2, RoundingMode.HALF_UP));

            return result;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 礼物ID扣费
     *
     * @param ownerId 用户ID
     * @param giftId 礼物ID
     * @param quantity 礼物数量
     * @return 扣费结果
     */
    @Transactional(readOnly = false)
    public Map<String, BigDecimal> deductGiftPriceLiwu(String ownerId,  String giftId, Integer quantity) throws Exception {
        try {
            // 1. 获取礼物信息
            Gift gift = giftMapper.get(giftId);
            if (gift == null) {
                throw new IllegalArgumentException("礼物不存在");
            }
            if (!"1".equals(gift.getStatus())) {
                throw new IllegalArgumentException("礼物已禁用");
            }


            // 付费礼物需要扣费
            BigDecimal totalCost = null;
            // 2. 检查是否为付费礼物
            if ("2".equals(gift.getType())) {
                // 付费礼物需要扣费
                totalCost = new BigDecimal(gift.getValue()).multiply(new BigDecimal(quantity));

                // 扣除赠送者余额
                userBalanceService.deductUserBalance(ownerId, totalCost);

                String receiverInfo = "购买礼物" + gift.getName() + "x" + quantity + "扣费" + totalCost;
                userBalanceService.recordBalanceLog(ownerId, "礼物扣费", totalCost, "0", "6", receiverInfo);
            }


            // 获取扣费后的余额
            BigDecimal remainBalance = userBalanceService.getUserBalance(ownerId);
            Map<String, BigDecimal> result = new HashMap<>();
            result.put("deductPrice", totalCost.setScale(2, RoundingMode.HALF_UP));
            result.put("remainBalance", remainBalance.setScale(2, RoundingMode.HALF_UP));

            return result;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 赠送礼物
     *
     * @param senderId 赠送者ID
     * @param receiverId 接收者ID
     * @param giftId 礼物ID
     * @param quantity 赠送数量
     * @return 赠送结果
     */
    @Transactional(readOnly = false)
    public Map<String, Object> sendGift(String senderId, String receiverId, String giftId, Integer quantity) throws Exception {
        try {
            // 1. 获取礼物信息
            Gift gift = giftMapper.get(giftId);
            if (gift == null) {
                throw new IllegalArgumentException("礼物不存在");
            }
            if (!"1".equals(gift.getStatus())) {
                throw new IllegalArgumentException("礼物已禁用");
            }

            // 2. 检查是否为付费礼物
            if ("2".equals(gift.getType())) {
                // 付费礼物需要扣费
                BigDecimal totalCost = new BigDecimal(gift.getValue()).multiply(new BigDecimal(quantity));
                
                // 扣除赠送者余额
                userBalanceService.deductUserBalance(senderId, totalCost);
                
                // 记录赠送者扣费日志
                String senderInfo = "赠送" + gift.getName() + "x" + quantity + "给用户" + receiverId;
                userBalanceService.recordBalanceLog(senderId, "赠送礼物", totalCost, "0", "6", senderInfo);
            }

            // 3. 计算接收者获得的代币（根据转换比例）
            Double conversionRate = giftConfigService.getConversionRate();
            BigDecimal giftValue = new BigDecimal(gift.getValue());
            BigDecimal totalGiftValue = giftValue.multiply(new BigDecimal(quantity));
            BigDecimal convertedTokens = totalGiftValue.multiply(new BigDecimal(conversionRate))
                    .setScale(2, RoundingMode.HALF_UP);

            // 4. 给接收者增加代币
            userBalanceService.addUserBalance(receiverId, convertedTokens);

            // 5. 记录接收者获得代币日志
            String receiverInfo = "收到" + senderId + "赠送的" + gift.getName() + "x" + quantity + "，价值" + totalGiftValue + "，到手" + convertedTokens;
            userBalanceService.recordBalanceLog(receiverId, "收到礼物", convertedTokens, "1", "6", receiverInfo);

            // 6. 获取赠送者剩余余额
            BigDecimal senderRemainBalance = userBalanceService.getUserBalance(senderId);

            // 7. 获取接收者当前余额
            BigDecimal receiverBalance = userBalanceService.getUserBalance(receiverId);

            Map<String, Object> result = new HashMap<>();
            result.put("giftId", gift.getId());
            result.put("giftName", gift.getName());
            result.put("giftImg", gift.getImg());
            result.put("quantity", quantity);
            result.put("giftValue", totalGiftValue.setScale(2, RoundingMode.HALF_UP));
            result.put("convertedTokens", convertedTokens);
            result.put("conversionRate", conversionRate);
            result.put("senderRemainBalance", senderRemainBalance.setScale(2, RoundingMode.HALF_UP));
            result.put("receiverBalance", receiverBalance.setScale(2, RoundingMode.HALF_UP));

            return result;

        } catch (IllegalArgumentException e) {
            // 对于业务异常（如余额不足），直接重新抛出，保持原始错误信息
            throw e;
        } catch (Exception e) {
            throw new Exception("赠送礼物失败: " + e.getMessage());
        }
    }
}
