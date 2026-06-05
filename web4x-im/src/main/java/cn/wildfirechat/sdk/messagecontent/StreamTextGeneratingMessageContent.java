package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

public class StreamTextGeneratingMessageContent extends MessageContent {
    private String text;
    private String streamId;

    //必须有个空的构造函数
    public StreamTextGeneratingMessageContent() {
    }

    public StreamTextGeneratingMessageContent(String text, String streamId) {
        this.text = text;
        this.streamId = streamId;
    }

    public StreamTextGeneratingMessageContent text(String text) {
        this.text = text;
        return this;
    }

    public StreamTextGeneratingMessageContent streamId(String streamId) {
        this.streamId = streamId;
        return this;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.StreamingText_Generationg;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Transparent;
    }

    @Override
    public void decode(MessagePayload payload) {
        streamId = payload.getContent();
        text = payload.getSearchableContent();
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = encodeBase();
        payload.setSearchableContent(text);
        payload.setContent(streamId);
        return payload;
    }
}
