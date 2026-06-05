/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

public class TypingMessageContent extends MessageContent {
    public static final int TYPING_TEXT = 0;
    public static final int TYPING_VOICE = 1;
    public static final int TYPING_CAMERA = 2;
    public static final int TYPING_LOCATION = 3;
    public static final int TYPING_FILE = 4;

    private int typingType;

    public TypingMessageContent() {
    }

    public TypingMessageContent(int typingType) {
        this.typingType = typingType;
    }

    public int getTypingType() {
        return typingType;
    }

    public void setTypingType(int typingType) {
        this.typingType = typingType;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Typing;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Transparent;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        payload.setContent(typingType + "");
        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decodeBase(payload);
        typingType = Integer.parseInt(payload.getContent());
    }
}
