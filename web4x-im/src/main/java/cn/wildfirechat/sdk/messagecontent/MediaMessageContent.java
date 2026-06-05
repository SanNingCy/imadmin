package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;

abstract public class MediaMessageContent extends MessageContent {
    private String remoteMediaUrl;

    public String getRemoteMediaUrl() {
        return remoteMediaUrl;
    }

    public void setRemoteMediaUrl(String remoteMediaUrl) {
        this.remoteMediaUrl = remoteMediaUrl;
    }

    @Override
    protected MessagePayload encodeBase() {
        MessagePayload payload = super.encodeBase();
        payload.setRemoteMediaUrl(remoteMediaUrl);
        payload.setMediaType(getMediaType());
        return payload;
    }

    protected abstract int getMediaType();
}
