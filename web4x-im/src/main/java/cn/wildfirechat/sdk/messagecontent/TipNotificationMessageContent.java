/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;


import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

public class TipNotificationMessageContent extends MessageContent {
    public String tip;

    public TipNotificationMessageContent() {
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        payload.setContent(tip);

        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decodeBase(payload);
        tip = payload.getContent();
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Tip;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist;
    }
}
