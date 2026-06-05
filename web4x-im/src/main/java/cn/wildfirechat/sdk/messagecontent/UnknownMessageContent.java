package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

public class UnknownMessageContent extends MessageContent {
    private MessagePayload orignalPayload;

    //必须有个空的构造函数
    public UnknownMessageContent() {
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Text;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        return orignalPayload;
    }

    @Override
    public void decode(MessagePayload payload) {
        this.orignalPayload = payload;
    }
}
