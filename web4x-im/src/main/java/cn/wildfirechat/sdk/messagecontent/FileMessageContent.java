/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;


import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

public class FileMessageContent extends MediaMessageContent {
    private String name;
    private int size;
    private static final String FILE_NAME_PREFIX = "[文件] ";


    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.File;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        payload.setSearchableContent(name);
        payload.setContent(size + "");

        return payload;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decodeBase(payload);

        if (payload.getSearchableContent().startsWith(FILE_NAME_PREFIX)) {
            name = payload.getSearchableContent().substring(payload.getSearchableContent().indexOf(FILE_NAME_PREFIX) + FILE_NAME_PREFIX.length());
        } else {
            name = payload.getSearchableContent();
        }
        size = Integer.parseInt(payload.getContent());
    }

    @Override
    protected int getMediaType() {
        return ProtoConstants.MessageMediaType.FILE;
    }
}
