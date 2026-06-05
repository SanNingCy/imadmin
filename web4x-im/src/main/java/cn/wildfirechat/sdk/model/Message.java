package cn.wildfirechat.sdk.model;

import cn.wildfirechat.pojos.Conversation;
import cn.wildfirechat.sdk.messagecontent.MessageContent;

import java.util.List;

public class Message {
    public Conversation conversation;
    public MessageContent content;
    public long messageUid;
    public String sender;
    public long serverTime;
    public List<String> toUsers;
}
