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

public class LinkMessageContent extends MessageContent {
    private String title;
    private String contentDigest;
    private String url;
    private String thumbnailUrl;

    public LinkMessageContent() {
    }

    public LinkMessageContent(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getContentDigest() {
        return contentDigest;
    }

    public void setContentDigest(String contentDigest) {
        this.contentDigest = contentDigest;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Link;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();


        payload.setSearchableContent(title);
        JSONObject objWrite = new JSONObject();
        objWrite.put("d", contentDigest);
        objWrite.put("u", url);
        objWrite.put("t", thumbnailUrl);
        payload.setBase64edData(Base64.getEncoder().encodeToString(objWrite.toString().getBytes()));

        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        title = payload.getSearchableContent();


        try {
            if (!StringUtil.isNullOrEmpty(payload.getBase64edData())) {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(payload.getBase64edData())));
                contentDigest = (String) jsonObject.get("d");
                url = (String) jsonObject.get("u");
                thumbnailUrl = (String) jsonObject.get("t");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
