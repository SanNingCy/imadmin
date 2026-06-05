/*
 * Copyright (c) 2021 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;


import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;

public class PTTSoundMessageContent extends SoundMessageContent {

    public PTTSoundMessageContent() {
        super();
    }


    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.setSearchableContent("[对讲语音]");

        return payload;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Ptt_Voice;
    }
}
