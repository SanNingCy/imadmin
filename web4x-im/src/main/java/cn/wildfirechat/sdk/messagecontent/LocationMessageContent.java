/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import io.netty.util.internal.StringUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Base64;

public class LocationMessageContent extends MessageContent {
    private String title;
    public byte[] thumbnailByte;
    private double longitude;
    private double latitude;

    public LocationMessageContent() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getThumbnailByte() {
        return thumbnailByte;
    }

    public void setThumbnailByte(byte[] thumbnailByte) {
        this.thumbnailByte = thumbnailByte;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Location;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        payload.setSearchableContent(title);
        payload.setBase64edData(Base64.getEncoder().encodeToString(this.thumbnailByte));

        JSONObject objWrite = new JSONObject();
        objWrite.put("lat", latitude);
        objWrite.put("long", longitude);

        payload.setContent(objWrite.toString());

        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        if (!StringUtil.isNullOrEmpty(payload.getBase64edData())) {
            this.thumbnailByte = Base64.getDecoder().decode(payload.getBase64edData());
        }
        title = payload.getSearchableContent();
        try {
            if (payload.getContent() != null) {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(payload.getContent());
                latitude = (double) jsonObject.get("lat");
                longitude = (double) jsonObject.get("long");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
