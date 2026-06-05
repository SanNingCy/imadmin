package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import io.netty.util.internal.StringUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RichNotificationMessageContent extends MessageContent {
    private String title;
    private String desc;
    private String remark;
    private JSONArray datas;
    private String exName;
    private String exPortrait;
    private String exUrl;
    private String appId;

    //必须有个空的构造函数
    public RichNotificationMessageContent() {
    }

    public RichNotificationMessageContent(String title, String desc, String exUrl) {
        this.title = title;
        this.desc = desc;
        this.exUrl = exUrl;
    }

    public RichNotificationMessageContent title(String title) {
        this.title = title;
        return this;
    }

    public RichNotificationMessageContent desc(String desc) {
        this.desc = desc;
        return this;
    }

    public RichNotificationMessageContent exUrl(String exUrl) {
        this.exUrl = exUrl;
        return this;
    }

    public RichNotificationMessageContent remark(String remark) {
        this.remark = remark;
        return this;
    }
    public RichNotificationMessageContent exName(String exName) {
        this.exName = exName;
        return this;
    }
    public RichNotificationMessageContent exPortrait(String exPortrait) {
        this.exPortrait = exPortrait;
        return this;
    }
    public RichNotificationMessageContent appId(String appId) {
        this.appId = appId;
        return this;
    }
    public RichNotificationMessageContent addItem(String key, String value) {
        return addItem(key, value, null);
    }
    public RichNotificationMessageContent addItem(String key, String value, String color) {
        if(this.datas == null) {
            this.datas = new JSONArray();
        }
        JSONObject item = new JSONObject();
        item.put("key", key);
        item.put("value", value == null ? "" : value);
        if(!StringUtil.isNullOrEmpty(color)) {
            item.put("color", color);
        }
        this.datas.add(item);
        return this;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Rich_Notification;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = encodeBase();
        payload.setPushContent(title);
        payload.setContent(desc);
        JSONObject jsonObject = new JSONObject();
        if(!StringUtil.isNullOrEmpty(remark))
            jsonObject.put("remark", remark);
        if(!StringUtil.isNullOrEmpty(exName))
            jsonObject.put("exName", exName);
        if(!StringUtil.isNullOrEmpty(exPortrait))
            jsonObject.put("exPortrait", exPortrait);
        if(!StringUtil.isNullOrEmpty(exUrl))
            jsonObject.put("exUrl", exUrl);
        if(!StringUtil.isNullOrEmpty(appId))
            jsonObject.put("appId", appId);
        if(datas != null && !datas.isEmpty())
            jsonObject.put("datas", datas);

        payload.setBase64edData(Base64.getEncoder().encodeToString(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8)));

        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
    }
}
