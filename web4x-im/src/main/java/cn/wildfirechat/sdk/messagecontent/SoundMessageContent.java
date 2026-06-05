package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import org.json.simple.JSONObject;

public class SoundMessageContent extends MediaMessageContent {
    private int duration;

    //必须有个空的构造函数
    public SoundMessageContent() {
    }

    public SoundMessageContent(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Voice;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public void decode(MessagePayload payload) {
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = encodeBase();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("duration", duration);
        payload.setContent(jsonObject.toJSONString());
        return payload;
    }

    @Override
    protected int getMediaType() {
        return ProtoConstants.MessageMediaType.VOICE;
    }
}
