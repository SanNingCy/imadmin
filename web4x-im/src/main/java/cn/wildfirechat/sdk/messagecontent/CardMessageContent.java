/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

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

public class CardMessageContent extends MessageContent {
    /**
     * 0，用户；1，群组；2，聊天室；3，频道
     */
    private int type;
    private String target;
    // 用户名，一般是type为用户时使用
    private String name;
    private String displayName;
    private String portrait;
    private String from;

    public CardMessageContent() {
    }

    public CardMessageContent(int type, String target, String displayName, String portrait, String from) {
        this.type = type;
        this.target = target;
        this.displayName = displayName;
        this.portrait = portrait;
        this.from = from;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Name_Card;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        payload.setContent(target);
            JSONObject objWrite = new JSONObject();
            objWrite.put("t", type);
            objWrite.put("n", name);
            objWrite.put("d", displayName);
            objWrite.put("p", portrait);
            objWrite.put("f", from);

            payload.setBase64edData(Base64.getEncoder().encodeToString(objWrite.toString().getBytes()));
        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        target = payload.getContent();
        try {
            if (!StringUtil.isNullOrEmpty(payload.getBase64edData())) {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(payload.getBase64edData())));
                type = (int) jsonObject.get("t");
                name = (String) jsonObject.get("n");
                displayName = (String) jsonObject.get("d");
                portrait = (String) jsonObject.get("p");
                from = (String) jsonObject.get("f");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
