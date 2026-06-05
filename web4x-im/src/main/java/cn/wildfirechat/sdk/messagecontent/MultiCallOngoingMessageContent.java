/*
 * Copyright (c) 2022 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MultiCallOngoingMessageContent extends MessageContent {
    private String callId;
    private String initiator;
    private boolean audioOnly;
    private List<String> targets;

    public MultiCallOngoingMessageContent() {
    }

    public MultiCallOngoingMessageContent(String callId, String initiator, boolean audioOnly, List<String> targets) {
        this.callId = callId;
        this.initiator = initiator;
        this.audioOnly = audioOnly;
        this.targets = targets;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Call_Multi_Call_Ongoing;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Transparent;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        payload.setContent(callId);
        JSONObject object = new JSONObject();
        object.put("initiator", this.initiator);
        JSONArray arr = new JSONArray();
        for (int i = 0; i < targets.size(); i++) {
            arr.add(i, targets.get(i));
        }
        object.put("targets", arr);
        object.put("audioOnly", this.audioOnly ? 1 : 0);
        payload.setBase64edData(Base64.getEncoder().encodeToString(object.toString().getBytes()));

        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        this.callId = payload.getContent();
        try {
            JSONObject object = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(payload.getBase64edData())));
            this.initiator = (String) object.get("initiator");
            this.targets = new ArrayList<>();
            JSONArray array = (JSONArray) object.get("targets");
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    targets.add((String) array.get(i));
                }
            }
            this.audioOnly = (int)object.get("audioOnly") == 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public boolean isAudioOnly() {
        return audioOnly;
    }

    public void setAudioOnly(boolean audioOnly) {
        this.audioOnly = audioOnly;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }
}
