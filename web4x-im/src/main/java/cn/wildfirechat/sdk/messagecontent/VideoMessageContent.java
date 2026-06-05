/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Base64;


/**
 * Created by heavyrain lee on 2017/12/6.
 *
 * @refactor dhl
 * 添加小视频的宽高，时长
 */


public class VideoMessageContent extends MediaMessageContent {
    private byte[] thumbnailBytes;
    private long duration ;

    //必须有个空的构造函数
    public VideoMessageContent() {
    }

    public VideoMessageContent setThumbnailBytes(byte[] thumbnailBytes) {
        this.thumbnailBytes = thumbnailBytes;
        return this;
    }

    public byte[] getThumbnailBytes() {
        return thumbnailBytes;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Video;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        payload.setSearchableContent("[视频]");

        payload.setBase64edData(Base64.getEncoder().encodeToString(thumbnailBytes));

        JSONObject objWrite = new JSONObject();
        objWrite.put("d", duration);
        objWrite.put("duration", duration);
        payload.setContent(objWrite.toJSONString());

        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decodeBase(payload);
        thumbnailBytes = Base64.getDecoder().decode(payload.getBase64edData());
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(payload.getContent());
            if(jsonObject.containsKey("d")) {
                duration = (long) jsonObject.get("d");
            } else {
                duration = (long) jsonObject.get("duration");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected int getMediaType() {
        return ProtoConstants.MessageMediaType.VIDEO;
    }
}
