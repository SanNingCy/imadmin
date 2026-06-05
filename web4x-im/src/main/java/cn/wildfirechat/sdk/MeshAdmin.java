package cn.wildfirechat.sdk;

import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.pojos.mesh.*;
import cn.wildfirechat.sdk.model.IMResult;
import cn.wildfirechat.sdk.utilities.AdminHttpUtils;

import java.util.List;

public class MeshAdmin {
    public static IMResult<Void> createDomain(InputOutputDomainInfo domainInfo) throws Exception {
        String path = APIPath.Create_Domain;
        return AdminHttpUtils.httpJsonPost(path, domainInfo, Void.class);
    }

    public static IMResult<InputOutputDomainInfo> getDomain(String domainId) throws Exception {
        String path = APIPath.Get_Domain;
        InputStringValue inputId = new InputStringValue();
        inputId.setValue(domainId);
        return AdminHttpUtils.httpJsonPost(path, inputId, InputOutputDomainInfo.class);
    }

    public static IMResult<Void> deleteDomain(String domainId) throws Exception {
        String path = APIPath.Destroy_Domain;
        InputStringValue inputId = new InputStringValue();
        inputId.setValue(domainId);
        return AdminHttpUtils.httpJsonPost(path, inputId, Void.class);
    }

    public static IMResult<InputOutputDomainInfoList> getAllDomain() throws Exception {
        String path = APIPath.List_Domain;
        return AdminHttpUtils.httpJsonPost(path, null, InputOutputDomainInfoList.class);
    }

    public static IMResult<OutputUserInfoList> getBatchUserInfos(List<String> userIds) throws Exception {
        String path = APIPath.User_Batch_Get_Infos;
        OutputStringList getUserInfo = new OutputStringList(userIds);
        return AdminHttpUtils.httpJsonPost(path, getUserInfo, OutputUserInfoList.class);
    }

    public static IMResult<PojoSearchUserRes> searchUser(String keyword, int searchType, int page) throws Exception {
        String path = APIPath.Search_User;
        PojoSearchUserReq req = new PojoSearchUserReq();
        req.keyword = keyword;
        req.searchType = searchType;
        req.page = page;
        return AdminHttpUtils.httpJsonPost(path, req, PojoSearchUserRes.class);
    }

    public static IMResult<Void> sendFriendRequest(String userId, String targetId, String reason) throws Exception {
        String path = APIPath.Friend_Send_Request;
        InputAddFriendRequest input = new InputAddFriendRequest();
        input.setUserId(userId);
        input.setFriendUid(targetId);
        input.setReason(reason);
        input.setForce(false);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    public static IMResult<Void> handleFriendRequest(String userId, String targetId, int status) throws Exception {
        String path = APIPath.Handle_Friend_Send_Request;
        InputHandleFriendRequest input = new InputHandleFriendRequest();
        input.setUserId(userId);
        input.setFriendUid(targetId);
        input.setStatus(status);
        return AdminHttpUtils.httpJsonPost(path, input, Void.class);
    }

    public static IMResult<SendMessageResult> sendMessage(String sender, Conversation conversation, MessagePayload payload, List<String> toUsers) throws Exception {
        String path = APIPath.Msg_Send;
        SendMessageData messageData = new SendMessageData();
        messageData.setSender(sender);
        messageData.setConv(conversation);
        messageData.setPayload(payload);
        messageData.setToUsers(toUsers);
        messageData.setMeshMessage(true);
        if (payload.getType() == 1 && (payload.getSearchableContent() == null || payload.getSearchableContent().isEmpty())) {
            System.out.println("Payload错误，Payload格式应该跟客户端消息encode出来的Payload对齐，这样客户端才能正确识别。比如文本消息，文本需要放到searchableContent属性。请与客户端同事确认Payload的格式，或则去 https://gitee.com/wfchat/android-chat/tree/master/client/src/main/java/cn/wildfirechat/message 找到消息encode的实现方法！");
        }
        return AdminHttpUtils.httpJsonPost(path, messageData, SendMessageResult.class);
    }

    public static IMResult<SendMessageResult> publishMessage(SendMessageData messageData, List<String> receivers) throws Exception {
        String path = APIPath.Msg_Publish;
        PojoPublishMessageReq req = new PojoPublishMessageReq();
        req.messageData = messageData;
        req.receivers = receivers;
        return AdminHttpUtils.httpJsonPost(path, req, SendMessageResult.class);
    }

    public static IMResult<String> recallMessage(String operator, long messageId) throws Exception {
        String path = APIPath.Msg_Recall;
        RecallMessageData req = new RecallMessageData();
        req.setOperator(operator);
        req.setMessageUid(messageId);
        req.setUserRecall(true);
        return AdminHttpUtils.httpJsonPost(path, req, String.class);
    }

    public static IMResult<Void> updateMessageContent(String operator, long messageUid, MessagePayload payload, boolean distribute) throws Exception {
        String path = APIPath.Msg_Update;
        UpdateMessageContentData updateMessageContentData = new UpdateMessageContentData();
        updateMessageContentData.setOperator(operator);
        updateMessageContentData.setMessageUid(messageUid);
        updateMessageContentData.setPayload(payload);
        updateMessageContentData.setDistribute(distribute?1:0);
        updateMessageContentData.setUpdateTimestamp(0);
        updateMessageContentData.setMeshLocal(1);
        return AdminHttpUtils.httpJsonPost(path, updateMessageContentData, Void.class);
    }

    public static IMResult<Void> syncGroup(PojoGroupInfo group_info, List<PojoGroupMember> members) throws Exception {
        String path = APIPath.Sync_Group;
        PojoGroup pojoGroup = new PojoGroup();
        pojoGroup.setGroup_info(group_info);
        pojoGroup.setMembers(members);

        return AdminHttpUtils.httpJsonPost(path, pojoGroup, Void.class);
    }

    public static IMResult<PojoGroupInfo> getGroupInfo(String groupId) throws Exception {
        return GroupAdmin.getGroupInfo(groupId);
    }

    public static IMResult<OutputGroupMemberList> getGroupMembers(String groupId) throws Exception {
        return GroupAdmin.getGroupMembers(groupId);
    }

    public static IMResult<Void> addGroupMembers(String operator, String groupId, List<PojoGroupMember> groupMembers, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Member_Add;
        InputAddGroupMember addGroupMember = new InputAddGroupMember();
        addGroupMember.setGroup_id(groupId);
        addGroupMember.setMembers(groupMembers);
        addGroupMember.setOperator(operator);
        addGroupMember.setTo_lines(to_lines);
        addGroupMember.setNotify_message(notify_message);
        addGroupMember.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, addGroupMember, Void.class);
    }

    public static IMResult<PojoGroupInfoList> batchGroupInfos(List<String> groupIds) throws Exception {
        return GroupAdmin.batchGroupInfos(groupIds);
    }

    public static IMResult<Void> quitGroup(String operator, String groupId, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Member_Quit;
        InputQuitGroup quitGroup = new InputQuitGroup();
        quitGroup.setGroup_id(groupId);
        quitGroup.setOperator(operator);
        quitGroup.setTo_lines(to_lines);
        quitGroup.setNotify_message(notify_message);
        quitGroup.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, quitGroup, Void.class);
    }


    public static IMResult<Void> dismissGroup(String operator, String groupId, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Dismiss;
        InputDismissGroup dismissGroup = new InputDismissGroup();
        dismissGroup.setOperator(operator);
        dismissGroup.setGroup_id(groupId);
        dismissGroup.setTo_lines(to_lines);
        dismissGroup.setNotify_message(notify_message);
        dismissGroup.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, dismissGroup, Void.class);
    }

    public static IMResult<Void> kickoffGroupMembers(String operator, String groupId, List<String> groupMemberIds, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Member_Kickoff;
        InputKickoffGroupMember kickoffGroupMember = new InputKickoffGroupMember();
        kickoffGroupMember.setGroup_id(groupId);
        kickoffGroupMember.setMembers(groupMemberIds);
        kickoffGroupMember.setOperator(operator);
        kickoffGroupMember.setTo_lines(to_lines);
        kickoffGroupMember.setMeshMessage(true);
        kickoffGroupMember.setNotify_message(notify_message);
        return AdminHttpUtils.httpJsonPost(path, kickoffGroupMember, Void.class);
    }

    public static IMResult<Void> transferGroup(String operator, String groupId, String newOwner, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Transfer;
        InputTransferGroup transferGroup = new InputTransferGroup();
        transferGroup.setGroup_id(groupId);
        transferGroup.setNew_owner(newOwner);
        transferGroup.setOperator(operator);
        transferGroup.setTo_lines(to_lines);
        transferGroup.setNotify_message(notify_message);
        transferGroup.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, transferGroup, Void.class);
    }

    public static IMResult<Void> modifyGroupInfo(String operator, String groupId, /*ModifyGroupInfoType*/int type, String value, List<Integer> to_lines, MessagePayload  notify_message) throws Exception {
        String path = APIPath.Group_Modify_Info;
        InputModifyGroupInfo modifyGroupInfo = new InputModifyGroupInfo();
        modifyGroupInfo.setGroup_id(groupId);
        modifyGroupInfo.setOperator(operator);
        modifyGroupInfo.setTo_lines(to_lines);
        modifyGroupInfo.setType(type);
        modifyGroupInfo.setValue(value);
        modifyGroupInfo.setNotify_message(notify_message);
        modifyGroupInfo.setMeshMessage(true);
        return AdminHttpUtils.httpJsonPost(path, modifyGroupInfo, Void.class);
    }

    public static IMResult<PojoUserConferenceResponse> userConferenceRequest(String clientID, String fromUser, String request, long sessionId, String roomId, String data, boolean advanced) throws Exception {
        String path = APIPath.Conference_User_Request;
        PojoUserConferenceRequest conferenceRequest = new PojoUserConferenceRequest();
        conferenceRequest.clientID = clientID;
        conferenceRequest.fromUser = fromUser;
        conferenceRequest.request = request;
        conferenceRequest.sessionId = sessionId;
        conferenceRequest.roomId = roomId;
        conferenceRequest.data = data;
        conferenceRequest.advanced = advanced;
        return AdminHttpUtils.httpJsonPost(path, conferenceRequest, PojoUserConferenceResponse.class);
    }

    public static IMResult<Void> userConferenceEvent(String data, String userId, String clientId, boolean isRobot) throws Exception {
        String path = APIPath.Conference_User_Event;
        PojoUserConferenceEvent event = new PojoUserConferenceEvent();
        event.data = data;
        event.userId = userId;
        event.clientId = clientId;
        event.isRobot = isRobot;
        return AdminHttpUtils.httpJsonPost(path, event, Void.class);
    }

}
