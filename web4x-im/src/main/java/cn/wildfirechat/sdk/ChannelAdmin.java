package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

public class ChannelAdmin {
    public static IMResult<OutputCreateChannel> createChannel(InputCreateChannel inputCreateChannel) throws Exception {
        String path = APIPath.Create_Channel;
        return AdminHttpUtils.httpJsonPost(path, inputCreateChannel, OutputCreateChannel.class);
    }

    public static IMResult<Void> destroyChannel(String channelId) throws Exception {
        String path = APIPath.Destroy_Channel;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, Void.class);
    }

    public static IMResult<OutputGetChannelInfo> getChannelInfo(String channelId) throws Exception {
        String path = APIPath.Get_Channel_Info;
        InputChannelId inputChannelId = new InputChannelId(channelId);
        return AdminHttpUtils.httpJsonPost(path, inputChannelId, OutputGetChannelInfo.class);
    }

    public static IMResult<Void> subscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 1);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    public static IMResult<Void> unsubscribeChannel(String channelId, String userId) throws Exception {
        String path = APIPath.Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    public static IMResult<OutputBooleanValue> isUserSubscribedChannel(String userId, String channelId) throws Exception {
        String path = APIPath.Check_User_Subscribe_Channel;
        InputSubscribeChannel input = new InputSubscribeChannel(channelId, userId, 0);
        return AdminHttpUtils.httpJsonPost(path, input, OutputBooleanValue.class);
    }
}
