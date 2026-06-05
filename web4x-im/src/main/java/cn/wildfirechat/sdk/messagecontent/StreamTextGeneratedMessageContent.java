package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

public class StreamTextGeneratedMessageContent extends MessageContent {
    private String text;
    private String streamId;

    //必须有个空的构造函数
    public StreamTextGeneratedMessageContent() {
    }

    public StreamTextGeneratedMessageContent(String text, String streamId) {
        this.text = text;
        this.streamId = streamId;
    }

    public StreamTextGeneratedMessageContent text(String text) {
        this.text = text;
        return this;
    }

    public StreamTextGeneratedMessageContent streamId(String streamId) {
        this.streamId = streamId;
        return this;
    }

    @Override
    public int getContentType() {
            return ProtoConstants.ContentType.StreamingText_Generated;
    }

    @Override
    public int getPersistFlag() {
            return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public void decode(MessagePayload payload) {
        this.streamId = payload.getContent();
        this.text = payload.getSearchableContent();
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = encodeBase();
        payload.setSearchableContent(text);
        payload.setContent(streamId);
        return payload;
    }
}
