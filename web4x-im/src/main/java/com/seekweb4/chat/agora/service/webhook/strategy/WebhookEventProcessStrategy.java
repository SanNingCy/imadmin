package com.seekweb4.chat.agora.service.webhook.strategy;

import com.seekweb4.chat.agora.bean.enums.ChannelEventTypeEnum;
import com.seekweb4.chat.agora.bean.req.v2.EventCallBackReq;


/**
 * Webhook事件处理策略接口
 * 
 * <p>定义webhook事件处理的标准策略接口，基于策略模式实现不同事件类型的处理逻辑。</p>
 * <p>每个具体的事件类型都应该实现此接口，提供专门的处理逻辑。</p>
 * 
 * <p><b>设计原则：</b></p>
 * <ul>
 *   <li><b>单一职责</b> - 每个策略类只处理一种或一类相关的事件类型</li>
 *   <li><b>开闭原则</b> - 新增事件类型时只需添加新策略，无需修改现有代码</li>
 *   <li><b>依赖倒置</b> - 高层模块依赖抽象接口，不依赖具体实现</li>
 *   <li><b>可扩展性</b> - 支持复杂的业务逻辑处理和数据持久化</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 */
public interface WebhookEventProcessStrategy {
    
    /**
     * 处理webhook事件
     * 
     * <p>执行具体的事件处理逻辑，包括数据验证、业务处理、数据持久化等。</p>
     * 
     * @param event webhook通知请求对象
     * @return 处理结果，包含处理状态和相关数据
     * @throws Exception 处理过程中的任何异常
     */
    Boolean process(EventCallBackReq event);
    
    /**
     * 获取支持的事件类型
     * 
     * <p>返回该策略支持处理的事件类型，用于策略工厂进行路由。</p>
     * 
     * @return 支持的事件类型枚举
     */
    ChannelEventTypeEnum getSupportedEventType();

}