package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.Conversation;
import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.WFCMessage;
import cn.wildfirechat.sdk.model.Message;
import cn.wildfirechat.sdk.utilities.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageContentFactory {
    private static final Map<Integer, Class<MessageContent>> contentClassMap = new ConcurrentHashMap<>();

    static  {
        registerAllMessageContent();
    }

    public static Message decodeMessage(WFCMessage.Message protoMessage) {
        Message message = new Message();
        message.content = decodeMessageContent(protoMessage.getContent());
        WFCMessage.Conversation protoConversation = protoMessage.getConversation();
        message.conversation = new Conversation(protoConversation.getType(), protoConversation.getTarget(), protoConversation.getLine());
        message.messageUid = protoMessage.getMessageId();
        message.sender = protoMessage.getFromUser();
        message.serverTime = protoMessage.getServerTimestamp();
        message.toUsers = protoMessage.getToList();
        return message;
    }

    public static MessageContent decodeMessageContent(WFCMessage.MessageContent protoMessageContent) {
        MessagePayload payload = MessagePayload.fromProtoMessageContent(protoMessageContent);
        return decodeMessageContent(payload);
    }

    public static MessageContent decodeMessageContent(MessagePayload messagePayload) {
        Class<MessageContent> cls = contentClassMap.get(messagePayload.getType());
        MessageContent messageContent;
        if(cls != null) {
            try {
                messageContent = cls.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            messageContent = new UnknownMessageContent();
        }
        messageContent.decodeBase(messagePayload);
        messageContent.decode(messagePayload);
        return messageContent;
    }

    private static void registerAllMessageContent() {
        try {
            for (Class cls : ClassUtil.getAllAssignedClass(MessageContent.class)) {
                if(!Modifier.isAbstract(cls.getModifiers())) {
                    try {
                        MessageContent content = (MessageContent)cls.newInstance();
                        contentClassMap.put(content.getContentType(), cls);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
