/*
 * Copyright (c) 2024 WildFireChat. All rights reserved.
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

public class NotDeliveredMessageContent extends MessageContent {
    // 请求的类型，1 发送消息，2 撤回消息，3 删除消息
    private int type;

    // 发送的消息 uid
    private long messageUid;

    // 是全部失败，还是部分失败
    private boolean allFailure;

    // 部分失败时，失败的用户 id 列表
    private List<String> userIds;

    // 归属IM服务请求桥接服务出现的错误，有可能是桥接服务没有配置，或者不可用。
    private int localImErrorCode;

    // 归属桥接服务出现的错误
    private int localBridgeErrorCode;

    // 远端桥接服务出现的错误
    private int remoteBridgeErrorCode;

    // 远端IM服务出现的错误
    private int remoteServerErrorCode;

    // 错误提示信息
    private String errorMessage;


    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Not_Delivered;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        JSONObject obj = new JSONObject();
        obj.put("mid", this.messageUid);
        obj.put("all", this.allFailure);
        obj.put("us", this.userIds);
        obj.put("lme", this.localImErrorCode);
        obj.put("lbe", this.localBridgeErrorCode);
        obj.put("rbe", this.remoteBridgeErrorCode);
        obj.put("rme", this.remoteServerErrorCode);
        obj.put("em", this.errorMessage);
        payload.setBase64edData(Base64.getEncoder().encodeToString(obj.toString().getBytes()));
        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        try {

                JSONObject obj = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(payload.getBase64edData())));
                this.messageUid = (long) obj.get("mid");
                this.allFailure = (boolean) obj.get("all");
                this.userIds = new ArrayList<>();
                JSONArray arr = (JSONArray) obj.get("us");
                if (arr != null) {
                    for (int i = 0; i < arr.size(); i++) {
                        this.userIds.add((String) arr.get(i));
                    }
                }
                this.localImErrorCode = (int) obj.get("lme");
                this.localBridgeErrorCode = (int) obj.get("lbe");
                this.remoteBridgeErrorCode = (int) obj.get("rbe");
                this.remoteServerErrorCode = (int) obj.get("rme");
                this.errorMessage = (String) obj.get("em");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
