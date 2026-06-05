/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import io.netty.util.internal.StringUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created by heavyrain lee on 2017/12/6.
 */

public class CallStartMessageContent extends MessageContent {
    private String callId;
    // 多人视音频是有效，不包含自己，一对一忽略此参数
    private List<String> targetIds;
    private long connectTime;
    private long endTime;
    private boolean audioOnly;
    private String pin;


    /**
     * 0, UnKnown,
     * 1, Busy,
     * 2, SignalError,
     * 3, Hangup,
     * 4, MediaError,
     * 5, RemoteHangup,
     * 6, OpenCameraFailure,
     * 7, Timeout,
     * 8, AcceptByOtherClient
     */
    private int status;

    /**
     * 0，未知；1，多人版音视频；2，高级版音视频
     */
    private int type;

    public CallStartMessageContent() {
    }

    public CallStartMessageContent(String callId, List<String> targetIds, boolean audioOnly) {
        this.callId = callId;
        this.audioOnly = audioOnly;
        this.targetIds = targetIds;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isAudioOnly() {
        return audioOnly;
    }

    public void setAudioOnly(boolean audioOnly) {
        this.audioOnly = audioOnly;
    }

    public List<String> getTargetIds() {
        return targetIds;
    }

    public void setTargetIds(List<String> targetIds) {
        this.targetIds = targetIds;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Call_Start;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        payload.setContent(callId);
        payload.setPushContent("音视频通话邀请");

            JSONObject objWrite = new JSONObject();
            if (connectTime > 0) {
                objWrite.put("c", connectTime);
            }

            if (endTime > 0) {
                objWrite.put("e", endTime);
            }

            if (status > 0) {
                objWrite.put("s", status);
            }

            objWrite.put("t", targetIds.get(0));
            JSONArray ts = new JSONArray();
            ts.addAll(targetIds);
            objWrite.put("ts", ts);
            objWrite.put("a", audioOnly ? 1 : 0);
            objWrite.put("p", pin);
            if (this.type > 0) {
                objWrite.put("ty", this.type);
            }

            payload.setBase64edData(Base64.getEncoder().encodeToString(objWrite.toString().getBytes()));

            JSONObject pushDataWrite = new JSONObject();
            pushDataWrite.put("callId", callId);
            pushDataWrite.put("audioOnly", audioOnly);
            if (targetIds != null && targetIds.size() > 0) {
                pushDataWrite.put("participants", targetIds);
            }
            payload.setPushData(pushDataWrite.toString());
        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        callId = payload.getContent();

        try {
            if (!StringUtil.isNullOrEmpty(payload.getBase64edData())) {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(payload.getBase64edData())));
                connectTime = (long) jsonObject.get("c");
                endTime = (long) jsonObject.get("e");
                status = (int) jsonObject.get("s");
                pin = (String) jsonObject.get("p");
                type = (int) jsonObject.get("ty");
                JSONArray array = (JSONArray) jsonObject.get("ts");
                targetIds = new ArrayList<>();
                if (array == null) {
                    targetIds.add((String) jsonObject.get("t"));
                } else {
                    for (int i = 0; i < array.size(); i++) {
                        if (array.get(i) instanceof String) {
                            targetIds.add((String) array.get(i));
                        }
                    }
                }
                audioOnly = (int)jsonObject.get("a") > 0;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
