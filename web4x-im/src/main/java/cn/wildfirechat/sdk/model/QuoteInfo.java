/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.model;

import org.json.simple.JSONObject;

public class QuoteInfo {
    private long messageUid;
    private String userId;
    private String userDisplayName;
    private String messageDigest;

    public long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getMessageDigest() {
        return messageDigest;
    }

    public void setMessageDigest(String messageDigest) {
        this.messageDigest = messageDigest;
    }

    public JSONObject encode() {
        JSONObject object = new JSONObject();
        object.put("u", messageUid);
        object.put("i", userId);
        object.put("n", userDisplayName);
        object.put("d", messageDigest);
        return object;
    }

    public void decode(JSONObject object) {
        messageUid = (long) object.get("u");
        userId = (String) object.get("i");
        userDisplayName = (String) object.get("n");
        messageDigest = (String) object.get("d");
    }
}
