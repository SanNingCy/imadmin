package cn.wildfirechat.pojos.mesh;

import cn.wildfirechat.pojos.SendMessageData;

import java.util.List;

public class PojoPublishMessageReq {
    public SendMessageData messageData;
    public List<String> receivers;
    public String domainId;
    public long messageId;
    public boolean republish;
}
