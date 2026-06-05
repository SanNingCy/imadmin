package cn.wildfirechat.sdk;

import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import cn.wildfirechat.sdk.messagecontent.*;
import cn.wildfirechat.sdk.model.IMResult;
import com.google.gson.GsonBuilder;
import io.netty.util.internal.StringUtil;

import java.util.*;

import static cn.wildfirechat.pojos.MyInfoType.Modify_DisplayName;
import static cn.wildfirechat.proto.ProtoConstants.ChannelState.*;
import static cn.wildfirechat.proto.ProtoConstants.SystemSettingType.Group_Max_Member_Count;

public class Main {
    private static boolean commercialServer = false;
    private static boolean advanceVoip = false;
    //管理端口是8080
    private static String AdminUrl = "http://localhost:18080";
    private static String AdminSecret = "123456";

    //机器人和频道使用IM服务的公开端口80，注意不是18080
    private static String IMUrl = "http://localhost";


    public static void main(String[] args) throws Exception {
        if (args.length == 5) {
            AdminUrl = args[0];
            AdminSecret = args[1];
            IMUrl = args[2];
            commercialServer = Boolean.parseBoolean(args[3]);
            advanceVoip = Boolean.parseBoolean(args[4]);
        } else {
            if(args.length == 1 && (args[0].equals("-h") || args[0].equals("--help") || args[0].equals("-help"))) {
                System.out.println("Usage: java -jar checker.jar adminUrl adminSecret imUrl commercialServer advanceVoip \n      e.g. java -jar checker.jar http://192.168.1.80:18080 123456 http://192.168.1.80 false false");
                return;
            }
            System.out.println("Usage: java -jar checker.jar adminUrl adminSecret imUrl commercialServer advanceVoip \n      e.g. java -jar checker.jar http://192.168.1.80:18080 123456 http://192.168.1.80 false false");
            System.out.println();
            System.out.println();
            System.out.println("Use default value: java -jar checker.jar http://127.0.0.1:18080 123456 http://127.0.0.1 false false");
        }


        //admin使用的是18080端口，超级管理接口，理论上不能对外开放端口，也不能让非内部服务知悉密钥。
        testAdmin();

        //测试解析数据库中消息内容
        testReadMessageContentFromDB();

        //Robot和Channel都是使用的80端口，第三方可以创建或者为第三方创建，第三方可以使用robot或者channel与IM系统进行对接。
        testRobot();
        testChannel();
    }


    static void testAdmin() throws Exception {
        //初始化服务API
        AdminConfig.initAdmin(AdminUrl, AdminSecret);

        testUser();
        testUserRelation();
        testGroup();
        testChatroom();
        testMessage();
        testMessageContent();
        testChannelApi();
        testGeneralApi();
        testSensitiveApi();
        if (commercialServer) {
            testDevice();
        }
        if(advanceVoip) {
            testConference();
        }

        System.out.println("Congratulation, all admin test case passed!!!!!!!");
    }

    //***********************************************
    //****  用户相关的API
    //***********************************************
    static void testUser() throws Exception {
        InputOutputUserInfo userInfo = new InputOutputUserInfo();
        //用户ID，必须保证唯一性
        userInfo.setUserId("userId1");
        //用户名，一般是用户登录帐号，也必须保证唯一性。也就是说所有用户的userId必须不能重复，所有用户的name必须不能重复，但可以同一个用户的userId和name是同一个，一般建议userId使用一个uuid，name是"微信号"且可以修改，
        userInfo.setName("user1");
        userInfo.setMobile("13900000000");
        userInfo.setDisplayName("user 1");

        IMResult<OutputCreateUser> resultCreateUser = UserAdmin.createUser(userInfo);
        if (resultCreateUser != null && resultCreateUser.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Create user " + resultCreateUser.getResult().getName() + " success");
        } else {
            System.out.println("Create user failure");
            System.exit(-1);
        }

        InputCreateRobot createRobot = new InputCreateRobot();
        createRobot.setUserId("robot1");
        createRobot.setName("robot1");
        createRobot.setDisplayName("机器人");
        createRobot.setOwner("userId1");
        createRobot.setSecret("123456");
        createRobot.setCallback("http://127.0.0.1:8883/robot/recvmsg");
        IMResult<OutputCreateRobot> resultCreateRobot = UserAdmin.createRobot(createRobot);
        if (resultCreateRobot != null && resultCreateRobot.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Create robot " + resultCreateRobot.getResult().getUserId() + " success");
        } else {
            System.out.println("Create robot failure");
            System.exit(-1);
        }

        IMResult<OutputRobot> outputRobotIMResult = UserAdmin.getRobotInfo("robot1");
        if(outputRobotIMResult != null && outputRobotIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Get robot success");
        } else {
            System.out.println("Get robot failure");
            System.exit(-1);
        }

        IMResult<Void> destroyResult = UserAdmin.destroyRobot("robot1");
        if(destroyResult != null && destroyResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("success");
        } else {
            System.out.println("destroy user failure");
            System.exit(-1);
        }

        IMResult<InputOutputUserInfo> resultGetUserInfo1 = UserAdmin.getUserByName(userInfo.getName());
        if (resultGetUserInfo1 != null && resultGetUserInfo1.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (userInfo.getUserId().equals(resultGetUserInfo1.getResult().getUserId())
                && userInfo.getName().equals(resultGetUserInfo1.getResult().getName())
                && userInfo.getMobile().equals(resultGetUserInfo1.getResult().getMobile())
                && userInfo.getDisplayName().equals(resultGetUserInfo1.getResult().getDisplayName())) {
                System.out.println("get user info success");
            } else {
                System.out.println("get user info by name failure");
                System.exit(-1);
            }
        } else {
            System.out.println("get user info by name failure");
            System.exit(-1);
        }

        IMResult<InputOutputUserInfo> resultGetUserInfo2 = UserAdmin.getUserByMobile(userInfo.getMobile());
        if (resultGetUserInfo2 != null && resultGetUserInfo2.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (userInfo.getUserId().equals(resultGetUserInfo2.getResult().getUserId())
                && userInfo.getName().equals(resultGetUserInfo2.getResult().getName())
                && userInfo.getMobile().equals(resultGetUserInfo2.getResult().getMobile())
                && userInfo.getDisplayName().equals(resultGetUserInfo2.getResult().getDisplayName())) {
                System.out.println("get user info success");
            } else {
                System.out.println("get user info by mobile failure");
                System.exit(-1);
            }
        } else {
            System.out.println("get user info by mobile failure");
            System.exit(-1);
        }

        IMResult<InputOutputUserInfo> resultGetUserInfo3 = UserAdmin.getUserByUserId(userInfo.getUserId());
        if (resultGetUserInfo3 != null && resultGetUserInfo3.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (userInfo.getUserId().equals(resultGetUserInfo3.getResult().getUserId())
                && userInfo.getName().equals(resultGetUserInfo3.getResult().getName())
                && userInfo.getMobile().equals(resultGetUserInfo3.getResult().getMobile())
                && userInfo.getDisplayName().equals(resultGetUserInfo3.getResult().getDisplayName())) {
                System.out.println("get user info success");
            } else {
                System.out.println("get user info by userId failure");
                System.exit(-1);
            }
        } else {
            System.out.println("get user info by userId failure");
            System.exit(-1);
        }

        IMResult<OutputUserInfoList> userInfoListIMResult = UserAdmin.getUserByEmail("13900000001@139.com");
        if(userInfoListIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS || userInfoListIMResult.getErrorCode() == ErrorCode.ERROR_CODE_NOT_EXIST) {
            System.out.println("getUserByEmail success");
        } else {
            System.out.println("getUserByEmail failure");
            System.exit(-1);
        }

        InputOutputUserInfo updateUserInfo = new InputOutputUserInfo();
        updateUserInfo.setUserId(System.currentTimeMillis()+"");
        updateUserInfo.setDisplayName("updatedUserName");
        updateUserInfo.setPortrait("updatedUserPortrait");
        int updateUserFlag = ProtoConstants.UpdateUserInfoMask.Update_User_DisplayName | ProtoConstants.UpdateUserInfoMask.Update_User_Portrait;
        IMResult<Void> result = UserAdmin.updateUserInfo(updateUserInfo, updateUserFlag);
        if(result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_NOT_EXIST) {
            System.out.println("updateUserInfo success");
        } else {
            System.out.println("updateUserInfo failure");
            System.exit(-1);
        }

        updateUserInfo.setUserId(userInfo.getUserId());
        result = UserAdmin.updateUserInfo(updateUserInfo, updateUserFlag);
        if(result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("updateUserInfo success");
        } else {
            System.out.println("updateUserInfo failure");
            System.exit(-1);
        }

        IMResult<InputOutputUserInfo> resultGetUserInfo4 = UserAdmin.getUserByUserId(userInfo.getUserId());
        if (resultGetUserInfo4 != null && resultGetUserInfo4.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (userInfo.getUserId().equals(resultGetUserInfo4.getResult().getUserId())
                && updateUserInfo.getDisplayName().equals(resultGetUserInfo4.getResult().getDisplayName())
                && updateUserInfo.getPortrait().equals(resultGetUserInfo4.getResult().getPortrait())) {
                System.out.println("get user info success");
            } else {
                System.out.println("get user info by userId failure");
                System.exit(-1);
            }
        } else {
            System.out.println("get user info by userId failure");
            System.exit(-1);
        }

        IMResult<OutputGetIMTokenData> resultGetToken = UserAdmin.getUserToken(userInfo.getUserId(), "client111", ProtoConstants.Platform.Platform_Android);
        if (resultGetToken != null && resultGetToken.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get token success: " + resultGetToken.getResult().getToken());
        } else {
            System.out.println("get user token failure");
            System.exit(-1);
        }

        IMResult<Void> resultVoid =UserAdmin.updateUserBlockStatus(userInfo.getUserId(), 2);
        if (resultVoid != null && resultVoid.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("block user done");
        } else {
            System.out.println("block user failure");
            System.exit(-1);
        }

        IMResult<OutputUserStatus> resultCheckUserStatus = UserAdmin.checkUserBlockStatus(userInfo.getUserId());
        if (resultCheckUserStatus != null && resultCheckUserStatus.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (resultCheckUserStatus.getResult().getStatus() == 2) {
                System.out.println("check user status success");
            } else {
                System.out.println("user status not correct");
                System.exit(-1);
            }
        } else {
            System.out.println("block user failure");
            System.exit(-1);
        }

        IMResult<OutputUserBlockStatusList> resultBlockStatusList = UserAdmin.getBlockedList();
        if (resultBlockStatusList != null && resultBlockStatusList.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            boolean success = false;
            for (InputOutputUserBlockStatus blockStatus : resultBlockStatusList.getResult().getStatusList()) {
                if (blockStatus.getUserId().equals(userInfo.getUserId()) && blockStatus.getStatus() == 2) {
                    System.out.println("get block list done");
                    success = true;
                    break;
                }
            }
            if (!success) {
                System.out.println("block user status is not expected");
                System.exit(-1);
            }
        } else {
            System.out.println("block user failure");
            System.exit(-1);
        }

        resultVoid =UserAdmin.updateUserBlockStatus(userInfo.getUserId(), 0);
        if (resultVoid != null && resultVoid.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("block user done");
        } else {
            System.out.println("block user failure");
            System.exit(-1);
        }

        resultCheckUserStatus = UserAdmin.checkUserBlockStatus(userInfo.getUserId());
        if (resultCheckUserStatus != null && resultCheckUserStatus.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (resultCheckUserStatus.getResult().getStatus() == 0) {
                System.out.println("check user status success");
            } else {
                System.out.println("user status not correct");
                System.exit(-1);
            }
        } else {
            System.out.println("block user failure");
            System.exit(-1);
        }

        IMResult<OutputCheckUserOnline> outputCheckUserOnline = UserAdmin.checkUserOnlineStatus(userInfo.getUserId());
        if (outputCheckUserOnline != null && outputCheckUserOnline.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("check user online status success:" + outputCheckUserOnline.getResult().getSessions().size());
        } else {
            System.out.println("block user online failure");
            System.exit(-1);
        }

        //慎用，这个方法可能功能不完全，如果用户不在需要，建议使用block功能屏蔽用户
        IMResult<Void> voidIMResult = UserAdmin.destroyUser("user11");
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("destroy user success");
        } else {
            System.out.println("destroy user failure");
            System.exit(-1);
        }

        IMResult<OutputGetUserList> getUserListIMResult = UserAdmin.getAllUsers(100, 0);
        if (getUserListIMResult != null && getUserListIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("getUserListIMResult success");
        } else {
            System.out.println("getUserListIMResult failure");
            System.exit(-1);
        }

        if (commercialServer) {
            IMResult<GetOnlineUserCountResult> getOnlineUserCountResultIMResult = UserAdmin.getOnlineUserCount();
            if (getOnlineUserCountResultIMResult != null && getOnlineUserCountResultIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("get user online count success");
            } else {
                System.out.println("get user online count failure");
                System.exit(-1);
            }

            IMResult<GetOnlineUserResult> getOnlineUserResultIMResult = UserAdmin.getOnlineUser(1, 0, 100);
            if (getOnlineUserResultIMResult != null && getOnlineUserResultIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("get user online success");
            } else {
                System.out.println("get user online failure");
                System.exit(-1);
            }

            IMResult<GetUserSessionResult> getUserSessionResultIMResult = UserAdmin.getUserSession("hygqmws2k");
            if (getUserSessionResultIMResult != null && getUserSessionResultIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("get user session success");
            } else {
                System.out.println("get user session failure");
                System.exit(-1);
            }
        }
    }

    //***********************************************
    //****  用户关系相关的API
    //***********************************************
    static void testUserRelation() throws Exception {

        //先创建2个用户
        InputOutputUserInfo userInfo = new InputOutputUserInfo();
        userInfo.setUserId("ff1");
        userInfo.setName("ff1");
        userInfo.setMobile("13800000000");
        userInfo.setDisplayName("ff1");

        IMResult<OutputCreateUser> resultCreateUser = UserAdmin.createUser(userInfo);
        if (resultCreateUser != null && resultCreateUser.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Create user " + resultCreateUser.getResult().getName() + " success");
        } else {
            System.out.println("Create user failure");
            System.exit(-1);
        }

        userInfo = new InputOutputUserInfo();
        userInfo.setUserId("ff2");
        userInfo.setName("ff2");
        userInfo.setMobile("13800000001");
        userInfo.setDisplayName("ff2");

        resultCreateUser = UserAdmin.createUser(userInfo);
        if (resultCreateUser != null && resultCreateUser.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Create user " + resultCreateUser.getResult().getName() + " success");
        } else {
            System.out.println("Create user failure");
            System.exit(-1);
        }

        IMResult<Void> result = RelationAdmin.sendFriendRequest("ff1", "ff2", "hello", true);
        if (result != null && (result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS || result.getErrorCode() == ErrorCode.ERROR_CODE_ALREADY_FRIENDS)) {
            System.out.println("send friend request success");
        } else {
            System.out.println("failure");
            System.exit(-1);
        }

        IMResult<Void> updateFriendStatusResult = RelationAdmin.setUserFriend("ff1", "ff2", true, "{\"from\":1}");
        if (updateFriendStatusResult != null && updateFriendStatusResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("update friend status success");
        } else {
            System.out.println("update friend status failure");
            System.exit(-1);
        }

        IMResult<OutputStringList> resultGetFriendList = RelationAdmin.getFriendList("ff1");
        if (resultGetFriendList != null && resultGetFriendList.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && resultGetFriendList.getResult().getList().contains("ff2")) {
            System.out.println("get friend status success");
        } else {
            System.out.println("get friend status failure");
            System.exit(-1);
        }

        updateFriendStatusResult = RelationAdmin.setUserFriend("ff1", "ff2", false, null);
        if (updateFriendStatusResult != null && updateFriendStatusResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("update friend status success");
        } else {
            System.out.println("update friend status failure");
            System.exit(-1);
        }

        resultGetFriendList = RelationAdmin.getFriendList("ff1");
        if (resultGetFriendList != null && resultGetFriendList.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && !resultGetFriendList.getResult().getList().contains("ff2")) {
            System.out.println("get friend status success");
        } else {
            System.out.println("get friend status failure");
            System.exit(-1);
        }


        IMResult<Void> updateBlacklistStatusResult = RelationAdmin.setUserBlacklist("ff1", "ff2", true);
        if (updateBlacklistStatusResult != null && updateBlacklistStatusResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("update blacklist status success");
        } else {
            System.out.println("update blacklist status failure");
            System.exit(-1);
        }

        resultGetFriendList = RelationAdmin.getUserBlacklist("ff1");
        if (resultGetFriendList != null && resultGetFriendList.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && resultGetFriendList.getResult().getList().contains("ff2")) {
            System.out.println("get blacklist status success");
        } else {
            System.out.println("get blacklist status failure");
            System.exit(-1);
        }

        String alias = "hello" + System.currentTimeMillis();
        IMResult<Void> updateFriendAlias = RelationAdmin.updateFriendAlias("ff1", "ff2", alias);
        if (updateFriendAlias != null && updateFriendAlias.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("update friend alias success");
        } else {
            System.out.println("update friend alias failure");
            System.exit(-1);
        }

        IMResult<OutputGetAlias> getFriendAlias = RelationAdmin.getFriendAlias("ff1", "ff2");
        if (getFriendAlias != null && getFriendAlias.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && getFriendAlias.getResult().getAlias().equals(alias)) {
            System.out.println("get friend alias success");
        } else {
            System.out.println("get friend alias failure");
            System.exit(-1);
        }

        String friendExtra = "hello friend extra";
        IMResult<Void> setExtraResult = RelationAdmin.updateFriendExtra("ff1", "ff2", friendExtra);
        if (setExtraResult != null && setExtraResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("set friend extra success");
        } else {
            System.out.println("set friend extra failure");
            System.exit(-1);
        }

        IMResult<RelationPojo> getRelation = RelationAdmin.getRelation("ff1", "ff2");
        if (getRelation != null && getRelation.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get friend relation success");
        } else {
            System.out.println("get friend relation failure");
            System.exit(-1);
        }

        if(!friendExtra.equals(getRelation.getResult().extra)) {
            System.out.println("set friend extra failure");
            System.exit(-1);
        }
    }
    //***********************************************
    //****  群组相关功能
    //***********************************************
    static void testGroup() throws Exception {

        IMResult<Void> voidIMResult1 = GroupAdmin.dismissGroup("user1", "groupId1", null, null);

        PojoGroupInfo groupInfo = new PojoGroupInfo();
        groupInfo.setTarget_id("groupId1");
        groupInfo.setOwner("user1");
        groupInfo.setName("test_group");
        groupInfo.setExtra("hello extra");
        groupInfo.setType(2);
        groupInfo.setPortrait("http://portrait");
        List<PojoGroupMember> members = new ArrayList<>();
        PojoGroupMember member1 = new PojoGroupMember();
        member1.setMember_id(groupInfo.getOwner());
        members.add(member1);

        PojoGroupMember member2 = new PojoGroupMember();
        member2.setMember_id("user2");
        members.add(member2);

        PojoGroupMember member3 = new PojoGroupMember();
        member3.setMember_id("user3");
        members.add(member3);

        IMResult<OutputCreateGroupResult> resultCreateGroup = GroupAdmin.createGroup(groupInfo.getOwner(), groupInfo, members, null, null);
        if (resultCreateGroup != null && resultCreateGroup.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("create group success");
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        IMResult<PojoGroupInfo> resultGetGroupInfo = GroupAdmin.getGroupInfo(groupInfo.getTarget_id());
        if (resultGetGroupInfo != null && resultGetGroupInfo.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (groupInfo.getExtra().equals(resultGetGroupInfo.getResult().getExtra())
                && groupInfo.getName().equals(resultGetGroupInfo.getResult().getName())
                && groupInfo.getOwner().equals(resultGetGroupInfo.getResult().getOwner())) {
                System.out.println("get group success");
            } else {
                System.out.println("group info is not expected");
                System.exit(-1);
            }
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        IMResult<Void> voidIMResult = GroupAdmin.transferGroup(groupInfo.getOwner(), groupInfo.getTarget_id(), "user2", null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("transfer success");
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        voidIMResult = GroupAdmin.modifyGroupInfo(groupInfo.getOwner(), groupInfo.getTarget_id(), ProtoConstants.ModifyGroupInfoType.Modify_Group_Name,"HelloWorld", null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("transfer success");
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        voidIMResult = GroupAdmin.modifyGroupInfo(groupInfo.getOwner(), groupInfo.getTarget_id(), ProtoConstants.ModifyGroupInfoType.Modify_Group_Extra,"HelloWorld2", null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("modify group extra success");
        } else {
            System.out.println("modify group extra failure");
            System.exit(-1);
        }

        resultGetGroupInfo = GroupAdmin.getGroupInfo(groupInfo.getTarget_id());
        if (resultGetGroupInfo != null && resultGetGroupInfo.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if ("user2".equals(resultGetGroupInfo.getResult().getOwner())) {
                groupInfo.setOwner("user2");
            } else {
                System.out.println("group info is not expected");
                System.exit(-1);
            }
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        IMResult<OutputGroupMemberList> resultGetMembers = GroupAdmin.getGroupMembers(groupInfo.getTarget_id());
        if (resultGetMembers != null && resultGetMembers.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get group member success");
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        PojoGroupMember m = new PojoGroupMember();
        m.setMember_id("user1");
        m.setAlias("hello user1");

        voidIMResult = GroupAdmin.addGroupMembers("user1", groupInfo.getTarget_id(), Arrays.asList(m), null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("add group member success");
        } else {
            System.out.println("add group member failure");
            System.exit(-1);
        }

        IMResult<PojoGroupMember> groupMemberIMResult = GroupAdmin.getGroupMember(groupInfo.getTarget_id(), "user1");
        if (groupMemberIMResult != null && groupMemberIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get group member success");
        } else {
            System.out.println("get group member failure");
            System.exit(-1);
        }

        voidIMResult = GroupAdmin.kickoffGroupMembers("user1", groupInfo.getTarget_id(), Arrays.asList("user3"), null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("kickoff group member success");
        } else {
            System.out.println("kickoff group member failure");
            System.exit(-1);
        }

        voidIMResult = GroupAdmin.setGroupMemberAlias("user1", groupInfo.getTarget_id(), "user3", "test user3", null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("set group member alias success");
        } else {
            System.out.println("set group member alias failure");
            System.exit(-1);
        }

        voidIMResult = GroupAdmin.setGroupMemberExtra(groupInfo.getOwner(), groupInfo.getTarget_id(), groupInfo.getOwner(), "hello member extra2", null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("set group member extra success");
        } else {
            System.out.println("set group member extra failure");
            System.exit(-1);
        }

        if(commercialServer) {
            voidIMResult = GroupAdmin.setGroupManager("user1", groupInfo.getTarget_id(), Arrays.asList("user4", "user5"), true, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("set group manager success");
            } else {
                System.out.println("set group manager failure");
                System.exit(-1);
            }

            voidIMResult = GroupAdmin.setGroupManager("user1", groupInfo.getTarget_id(), Arrays.asList("user4", "user5"), false, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("cancel group manager success");
            } else {
                System.out.println("cancel group manager failure");
                System.exit(-1);
            }
        }

        voidIMResult = GroupAdmin.quitGroup("user4", groupInfo.getTarget_id(), null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("quit group success");
        } else {
            System.out.println("quit group failure");
            System.exit(-1);
        }

        IMResult<OutputGroupIds> groupIdsIMResult = GroupAdmin.getUserGroups("user1");
        if (groupIdsIMResult != null && groupIdsIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (groupIdsIMResult.getResult().getGroupIds().contains(groupInfo.getTarget_id())) {
                System.out.println("get user groups success");
            } else {
                System.out.println("get user groups failure");
                System.exit(-1);
            }
        } else {
            System.out.println("get user groups failure");
            System.exit(-1);
        }

        groupIdsIMResult = GroupAdmin.getUserGroupsByType("user2", Arrays.asList(ProtoConstants.GroupMemberType.GroupMemberType_Manager, ProtoConstants.GroupMemberType.GroupMemberType_Owner));
        if (groupIdsIMResult != null && groupIdsIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get user groups by type success");
        } else {
            System.out.println("get user groups by type failure");
            System.exit(-1);
        }

        groupIdsIMResult = GroupAdmin.getCommonGroups("user1", "user2");
        if (groupIdsIMResult != null && groupIdsIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get user common groups success");
        } else {
            System.out.println("get user common groups failure");
            System.exit(-1);
        }

        //仅专业版支持
        if (commercialServer) {
            //开启群成员禁言
            voidIMResult = GroupAdmin.muteGroupMemeber("user1", groupInfo.getTarget_id(), Arrays.asList("user5"), true, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("mute group member success");
            } else {
                System.out.println("mute group member failure");
                System.exit(-1);
            }
            //关闭群成员禁言
            voidIMResult = GroupAdmin.muteGroupMemeber("user1", groupInfo.getTarget_id(), Arrays.asList("user5"), false, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("unmute group member success");
            } else {
                System.out.println("unmute group member failure");
                System.exit(-1);
            }

            //开启群成员白名单，当群全局禁言时，白名单用户可以发言
            voidIMResult = GroupAdmin.allowGroupMemeber("user1", groupInfo.getTarget_id(), Arrays.asList("user5"), true, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("allow group member success");
            } else {
                System.out.println("allow group member failure");
                System.exit(-1);
            }

            //关闭群成员白名单
            voidIMResult = GroupAdmin.allowGroupMemeber("user1", groupInfo.getTarget_id(), Arrays.asList("user5"), false, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("unallow group member success");
            } else {
                System.out.println("unallow group member failure");
                System.exit(-1);
            }
        }
    }

    //***********************************************
    //****  消息相关功能
    //***********************************************
    static void testMessage() throws Exception {
        Conversation conversation = new Conversation();
        conversation.setTarget("ff2");
        conversation.setType(ProtoConstants.ConversationType.ConversationType_Private);
        TextMessageContent textMessageContent = new TextMessageContent("Hello world");
        MessagePayload payload = textMessageContent.encode();

        IMResult<SendMessageResult> resultSendMessage = MessageAdmin.sendMessage("ff1", conversation, payload, null);
        if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("send message success");
        } else {
            System.out.println("send message failure");
            System.exit(-1);
        }

        IMResult<OutputMessageData> outputMessageDataIMResult = MessageAdmin.getMessage(resultSendMessage.result.getMessageUid());
        if(outputMessageDataIMResult != null && outputMessageDataIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && outputMessageDataIMResult.getResult().getMessageId() == resultSendMessage.getResult().getMessageUid()) {
            System.out.println("get message success");
        } else {
            System.out.println("get message failure");
            System.exit(-1);
        }

        IMResult<String> stringIMResult = MessageAdmin.recallMessage("user1", resultSendMessage.getResult().getMessageUid());
        if (stringIMResult != null && stringIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("recall message success");
        } else {
            System.out.println("recall message failure");
            System.exit(-1);
        }

        if (commercialServer) {
            IMResult<Void> voidIMResult = MessageAdmin.deleteMessage(resultSendMessage.getResult().getMessageUid());
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("delete message success");
            } else {
                System.out.println("delete message failure");
                System.exit(-1);
            }

            payload.setSearchableContent("hello world2");
            resultSendMessage = MessageAdmin.sendMessage("user1", conversation, payload, null);
            if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("send message success");
            } else {
                System.out.println("send message failure");
                System.exit(-1);
            }

            payload.setSearchableContent("hello world3");
            voidIMResult = MessageAdmin.updateMessageContent("user1", resultSendMessage.getResult().getMessageUid(), payload, true);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("update message success");
            } else {
                System.out.println("update message failure");
                System.exit(-1);
            }

            IMResult<BroadMessageResult> resultBroadcastMessage = MessageAdmin.broadcastMessage("user1", 0, payload);
            if (resultBroadcastMessage != null && resultBroadcastMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("broad message success, send message to " + resultBroadcastMessage.getResult().getCount() + " users");
            } else {
                System.out.println("broad message failure");
                System.exit(-1);
            }

            voidIMResult = MessageAdmin.recallBroadCastMessage("user1", resultBroadcastMessage.result.getMessageUid());
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("Success");
            } else {
                System.out.println("failure");
            }

            IMResult<OutputTimestamp> timestampResult = MessageAdmin.getConversationReadTimestamp("57gqmws2k", new Conversation(0, "admin", 0));
            if(timestampResult != null && timestampResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("Get conversation read time success");
            } else {
                System.out.println("Get conversation read time failure");
            }

            timestampResult = MessageAdmin.getMessageDelivery("57gqmws2k");
            if(timestampResult != null && timestampResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("Get message delivery success");
            } else {
                System.out.println("Get message delivery failure");
            }

            conversation = new Conversation();
            conversation.setTarget("user1");
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Private);
            conversation.setLine(0);
            IMResult<Void> clearConversationResult = MessageAdmin.clearConversation("user2", conversation);
            if (clearConversationResult != null && clearConversationResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("clear conversation success");
            } else {
                System.out.println("clear conversation failure");
            }
        }

        List<String> multicastReceivers = Arrays.asList("user2", "user3", "user4");
        IMResult<MultiMessageResult> resultMulticastMessage = MessageAdmin.multicastMessage("user1", multicastReceivers, 0, payload);
        if (resultMulticastMessage != null && resultMulticastMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("multi message success, messageid is " + resultMulticastMessage.getResult().getMessageUid());
        } else {
            System.out.println("multi message failure");
            System.exit(-1);
        }

        IMResult<Void> voidIMResult = MessageAdmin.recallMultiCastMessage("user1", resultMulticastMessage.result.getMessageUid(), multicastReceivers);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Success");
        } else {
            System.out.println("failure");
        }

    }

    //***********************************************
    //****  发送各种消息。
    //***********************************************
    static void testMessageContent() throws Exception {
        String sender = "userId2";
        Conversation conversation = new Conversation();
        conversation.setTarget("3ygqmws2k");
        conversation.setType(ProtoConstants.ConversationType.ConversationType_Private);

        //测试发送文本消息
        TextMessageContent textMessageContent = new TextMessageContent("测试文本消息");
        //消息转成Payload并发送
        MessagePayload payload = textMessageContent.encode();
        IMResult<SendMessageResult> resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试发送语音消息
        SoundMessageContent soundMessageContent = new SoundMessageContent();
        //语音文件时长，单位秒
        soundMessageContent.setDuration(7);
        //语音文件格式为amr或者mp3，需要先上传到对象存储服务。
        soundMessageContent.setRemoteMediaUrl("https://media.wfcoss.cn/firechat/voice_message_sample.amr");
        //消息转成Payload并发送
        payload = soundMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试发送图片消息
        ImageMessageContent imageMessageContent = new ImageMessageContent();
        //base64edData为图片的缩略图。缩略图的生成规则是，把图片压缩到120X120大小的方框内，45%的质量压缩为JPG格式，再把二进制做base64编码得到字符串。
        String thumbnailBase64edData = "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";
        imageMessageContent.setThumbnailBytes(Base64.getDecoder().decode(thumbnailBase64edData));
        //图片地址，发送前需要先上传到对象存储服务。
        imageMessageContent.setRemoteMediaUrl("https://media.wfcoss.cn/firechat/image_message_sample.jpg");
        //消息转成Payload并发送
        payload = imageMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试发送视频消息
        VideoMessageContent videoMessageContent = new VideoMessageContent();
        //base64edData为视频的首帧的缩略图。缩略图的生成规则是，把图片压缩到120X120大小的方框内，45%的质量压缩为JPG格式，再把二进制做base64编码得到字符串。
        thumbnailBase64edData = "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";
        videoMessageContent.setThumbnailBytes(Base64.getDecoder().decode(thumbnailBase64edData));
        //视频时长，单位秒
        videoMessageContent.setDuration(3);
        //视频地址，发送前需要先上传到对象存储服务。
        videoMessageContent.setRemoteMediaUrl("https://media.wfcoss.cn/firechat/video_message_sample.mov");
        //消息转成Payload并发送
        payload = videoMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试发送位置消息
        LocationMessageContent locationMessageContent = new LocationMessageContent();
        //base64edData为位置图片的缩略图。缩略图的生成规则是，把图片压缩到120X120大小的方框内，45%的质量压缩为JPG格式，再把二进制做base64编码得到字符串。
        thumbnailBase64edData = "/9j/4AAQSkZJRgABAQAASABIAAD/4QCMRXhpZgAATU0AKgAAAAgABQESAAMAAAABAAEAAAEaAAUAAAABAAAASgEbAAUAAAABAAAAUgEoAAMAAAABAAIAAIdpAAQAAAABAAAAWgAAAAAAAABIAAAAAQAAAEgAAAABAAOgAQADAAAAAQABAACgAgAEAAAAAQAAALSgAwAEAAAAAQAAAHgAAAAA/8AAEQgAeAC0AwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/bAEMAAwMDAwMDBQMDBQcFBQUHCQcHBwcJDAkJCQkJDA4MDAwMDAwODg4ODg4ODhERERERERQUFBQUFhYWFhYWFhYWFv/bAEMBAwQEBgUGCgUFChcQDRAXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXFxcXF//dAAQADP/aAAwDAQACEQMRAD8A/SGR0ZcRx7TkEHI7HPrUE5keJgeeD3qzG8boryksSAc7sD8BkcUxxB5MpJGcHbls9vrXJc2sPI+Y71UHP9//AOtRtT+6v/ff/wBaozLIufnQ89SB/jWbrGtWukWBvrpTJtdFVIgC7u52qqjPUk9zj1pt8qu3oOKcmoxWp0EewcIeB0qWuGtvFGkXKSTXoeykjkEEkNwgMiPwcHZuGMMpyDjB61JP4k8OxNKn2gfufvko+B8wUYwpzknt25rLmi9eY1dOaduU7GX7h9cH+VVYpNvy4+9iufj1zQ50jWG4w865RSHGeGPoOcKeDzT08Q6KwtpEuNwupPLiwj/M64yPu8Yz3q4uNtyHTl2OmJaKxjDYVlJHB+lUo3SPLIxBY5YgKcn6kVkS+NfDqRPH9r+aJyrBUdiGIOOAp/utz0rShvDcwpcQszJIodG8scg8g8pmtE4S0uQ4Sjq0WYEywKOzAIVw2MA8Y6ev9Kesc1upclD0HQ9yB61JGZfKPmYA6klVHHrwO1VZTCRIAzsNg25Zsbuf/rUn2Qjzrxxrer6LexR2sz28P2ZpYmS1+0fabkMdsPPC8Y7gnOQeKh1K8uZr+4W4vJYLp7eNns13lEd4jIUBQk4wpOQu7dxXp4ikBKg4xwfmYf1qg+l6dPcNcSpEZA/3nwTkJt6n2JH0rldKV22ztVeKily6rr3PJ9K1hra3sBcanJ8szblw/wC96R4YSYbGT/DwDn6U5dRktdPeEardEK8cbShGLZQyM45yMcE8nJxzgc16auiaPCAsdtERCpEZUcJkHOMdzgZpr6No0nMkETZ5O7acnk5I7k7jk9TnmkqT7luvBu9vyM3R9Nv21R9We9eezuogywuXAjLYIwrZHA6HIPJzXQSxp5VyM7go4P4CliEcUIt4yiogCKOOABgDJOTSSFzaT4YYC+nX8c11QXKcU5czuVdGk8uKY4J75AyPvN1rQz5rE+SrE8k7RWdo/KOh6M+0/TL1pxJuY+SxUbQfm7/qa0lbmZktiUoQpRUAIxwv19KjliieRgfvE7fmQHGecjNWQVViHIJHFKWQ4AI96zuUUBsT5fN6f7P/ANel3J/z2/8AHf8A69Wvs5PKlcf7oo+zN6r/AN8iquu4WP/Q/SqGMAHdGq/Tmo5olyGTBYHOCQOxpCzu2yTK4GcqAc5+mfSlQM6GQsuQTjKjscCuS3U2uMZdhXBXo3I7dPrVPWdNstXsm0++3NFIUJCMVYEMCGVgcqwPQirY+fZsBJbJOTjqM+9NZW2namMMFOW75HtSkk7qQ4yad4mLZeF/D1jaCwjgE6PKbh3nJlkeU4Jdnbkk7QPTAxTIfCGhxXFzdurSLcLsdWLEY3BiOuCMjpjpxXQshjQu0ecf7X/1qQyGLMTR55z97/61EYxS5Yr8EVKcpS5pSd/VmH/wjuiiVZYYnEiqEB3sPlAx2PbtUX/CN6KI4YxaqscLGSNASFVmAyQM8Zxn6810SyYUusYHb73/ANaodqoFAUfdB6e1NQjfYbqS/mM5vDeh3Ns7T2yMZnMrk5+Z9oXnB6YJ46c1eigS3t4bVGxEihUUksVVRgZ6n86vIEe0Vii8KxAxnsvrmqt9Pa2NkL+5AWOFS7lRzgKScYxUc0VLkS1E+Zxu3oOjjk2kqPlkU4x3GOCfTNRMyFWDZwV7g1g2/iS3jSYaik1p5CK+xiHJR87SNueeCCOxFTS+JtFgk+zvM+XiaUZjJyigk4xtOflPHXIrezMzdaOFbjy/l+93x9aEMDM7CPOW4PA4wPUiufj8V+HbiKNzJIRKxQAh/vL14PNdIZEQbUUH5sZyD+fWpdwK7ySFXQLgEkrx6+4P9K0DDGMfe5PY1AksjPsPAwSMH0/ClLyEhEJG4989hmiwHNeJPEv/AAjjxK9u8qSQzyB/NSMZhCnb855J3dv1rmtd8eGxv7LRpLBjNfRRum6Zesg4ThSN2SR15xkCu71HRrLU4ydSUy+WkiptkdCA4w4OxlyCAODWJfaFo9xILu4tUeaJNiu2SQoUqB1xwDgenXrVqxLuUdM1W4S5EYt3Fubv7MZQyffzIAdp525yK6yIYQoDyUHP4r/jXNaZoumtPJqJhHnRyecG3P8A6wlxu27tuce1dIVkA/dnJ2/h2Pc+1E92C2JiZFlMTFW+YDLDPX8aeFkYbgqnPov/ANehx5imcSAsibhhf/r08SiNVVScc/w5/rWdyiLy5v7v6H/Gjy5v7v6H/Gn+cys2OckHOD6Cl+0Seh/L/wCvVXA//9H9J5VAnwuB8g/iK9z6EVCqgRsxA4ZuS/oT7Gr7okn38H/vr/Cq4t7eNSzheCTnB6dfSuVGoiLMqxsyg7R0XGeR+FR73KSfuyMPuJJUdCCe9XQFXgH/ANC/wpjwr5ZRGHzryfm7/hS5b6juVXulcAYGNwJ+Zeg59ajkkR2Levuv+NXJl2Qu8Mau6qSicjcQOBnHGTXNLqOvtaCRtKRZzCz+XvJw4chV7cFec5qoxtsJs2PMG3aPXPVf/iqmj8qQAMMEKB94c4HsadZebNZxS3cKwzOil4+TtbHIz7GrXlxA/dB4ByFPfmk1Jp2Gmr6kYwI4l4CsSrD6gf1qrKkMkL2l6hdDkbT0KkYx9CKvSRq0YUkqpJBG3GRx3JqI+SATvZQpxnJxnGe7EVxyhJSTvqbRcWmraGJaaBosEc6pCWjkChg7s5I54yxJwM8DNOu9I0K6/eSWyNIFKBiCDtI2kcHpgnjtWwgQu+243A88Y6KPoafsjPV2P4n+gq2qrWjBOC3RyaeHtERfKS2QKhyAC2Mk59fUA1rkAsSc9s44P3hWgba1Lbgx5Vc4JHOOe1PjtoEbzFYkkEc5NTGlP2nNJ6IHOPLZIqxsiPlVYkgjlgcUpuAjodp6k/Mw9DVlzGZACwBQ8gjHUU0RRy7ZY5MAAnpnIPHfNdtrnPcZ5iyHaQqK33iDkn26DrWfOU8qXeMny+OM85FaRtywAMo5BP3R2/D0FV7iJRayOkhA2lTuGeM/hjmmlYLmVpW7yZgMYYgHPX7z1tMYhFtR0Jxnk+2OKoaKkZWRQwYFh1U/3m/rx+Fahtrdju5BHTA/POTVTXvMUXoVWMZV2EgxtPBPOcYqQloyUD7cds5/pUrW0JUjJGRjP+Wp+Iv75/P/AOyqLFFVfN2gJLwBj8v+A0v7/wD56/p/9jVnbH/eP5//AGVGI/7x/P8A+yosFz//0v0iDtuClQM9SM5/rRukkhcsexAX19qiJ2HKhEyOM9cfmK80l8PeJJ/FYv4YmZWvIpUvzcMFjtV/1kIhDjJbkfdKnOc5rgqVHBK0bnZSpqbak7aHpH9raarOWvYdkZAc+YnysTgBueCSDxST61piMqLdQ/NH5hIkThRzu69MHOeK8Q1CwgEOoJLY3CiS/iBO8YMmZHz/AKs52hhnr29KtCSzS/umTT5B5Nmzq5mB3/uUJUDyyemQMdTzjHFY+3fVHY8Muj/I9ohvrW4RJIJ/MWQkIUIYEr1AwTnHeokv7ectLZzLMEXDFTnB64O3OK8r0+4CxaU50t5MmUxtlf3e12EpI8vnKqc56Z4wea6DwPJa/Y7/AMmy+zMzq7gvk/OCRgFUwF6DGe/fNXGrzNIxqYflTl2/zPQDI6NhiTjrtBP5Upmlkj8xVcKox2/h4PGR6UFolY5UuPYY/ma4hb3Wv7XeFTOENwytCY/3AtiPvCTGN/Q/ezk4xXV6HDc7diGhKShs53cgHI6evvUI2gYXIH4D/wBmrj7bU/FxN0rWSb4yoiBRwGUglzkbgSMD8Tjqat22o+KpNRnha0Jt1jZoiY9hZwAQNz4GCc9QDWboRbbkXGo1ojrLdSZDk/LtYHv2PvTnht9wbIBJ4BUYz+dclqd540XRrubTLZY71IwYQ+05JiJYgAnJD9B6eteS/DTXPHB16+Pi6K5i05idiX3zuk68FVkYKcHJJyNvQDmsHUVKUaSi3e/oexh8ulicNVxntEuS2jeru7aLqfRDQIxyAg+mB/PFMlby2SIHam0kkc859vaooissYljQFeoICf40sCu0xQZCggkEDngfX+ddd0eJYVltjj595JwAMZ/XFIiSRShowUz8obA7kD6dKn8ozFtqqAG28g56CkBkgDKcYUKRjpySOlK7HYk3SspImyF4IAz+lZ1ww8iZjxlCDhSMkkdePbvUkxiLEna5yCeDuHHuMfrUDOFs5kCtjZ1xxyapNvcCvo4zBMPUgf8AjzVoi2VdwkZgFbaCSee471m6MT5cqhWPIPHszVoyyS5LSRlVJ9R/WnJ+8yVsKYtsZKMRz1bv/OmqjNhckn2I/wDiaebslxHBjByScE/oKVp59p2spbBwAvP86i7ZRAY5QAPmzgZ4HXv2pNk3q35D/CrNvHLLEH8x+fc/41P9nl/56P8Amf8AGq+YH//T/SaKQZJbrgdPQf8A66ebuAHac5J2j5TyarebL321AihwG8xVKvuwSOCDXM0upqmW3lhkZPmIwxONp/wqy0ieWHHQrkZ79azwoDFtwdQTgjHX8KmdmWKFCoOUHcjpn29qElYL6k9sSY8EYwSB7D0qSWISIy4GSCMmqKyPs8tlVh179STTJP8AVtiNQcHkE1FmVctuLgZYttA5/hwPzFVWdA4d3JJHUYOCPbjtT3TKNiMAgZ4J/rQ7+ZJkK2WPqPTPr7VaJsNwJB8hGMdX4zyPTPpUezkjarY4yMkfyp4ZVXYRyWJDZFOiXcwj4H3iW4PcYHt1ouFijPeQWCTXdyyQQW0Mks0jZ/dxqpLNjHIwK8a8P+J/CnxE1a7j0ua4862U3ZhnXZvgdgN6bS3TI3A4IzXsd9p8N4t1ZXUYnt7iFoJo243xurBlyOnHftXmfg74aeEvh9JfahoYufMuYykk946ERQAh2VNiqMHaMsecCvdwqwP1Wq8Q37TTltt8zycQ8X9Ypqglyfavv8j1SzUxQLDGQI1GMEnOPxFSExySNHtJIdRnjoQv41ylh4w0bULW5ksjIWtYfP2TIYTJGeFdPMwGUnjP6cjMMXibffWsUdsWF2FdmWQfuyDjB2hvT1B5AxnivnvawavGR7joVE7NWO4lEK+YixgEA8g45x1p0yW4jYD72PU9ua8+u/G32SwN/daXICJTGyGYk7Rn5yQPTqD0JGeoqxc+Lo0+3rHZ7hZIGJEuc7m2gYCMc+3NT7WHcv6vU7HaTPaWz7XYoTzyzc9qrXskb2LSRMSrHbnJx/niuKuvEyX+rQWRtyhljDK4fK4ZS/GVUnpg11DH/iTr/v4/U1zUq8pVXTa0OFucajpzVla6JdFYBJFbozYP4Fz/AErYcWwBwQxH8Oea5vRrq2cyRxyJIyuQyqwJHMgOcdPxrdlTbGZETkDcDuHUc9K9KbtJmkdh5YjaUUqVzg9evXingsku92dx/EoHByO3PFMX7SW5OPzxU6icfeOal6Mdhn2WLt8o7AquQPzo+zRf3l/75X/GobqGWR1IJHy47epqt9ll9T+lLm8gsf/U+5V1+5l1gBbqH7H5BdkBiWThC25QWYgdDyxwOtU5vEHiD/iXjTriMqXK3Cu0JZm8wKwG3PABI45yRW7F4F0aa4a4lmuJiqGA73U5QxeWckKCTgnJ7nrSweFrG3eF4muM28pkTDJjcG+X+HouMAdhx6V5dpy0Z6vPSVml+BiX3iDV5ra6ksby185Z1WMDZwh3AI+XbDjBZh2Hc843tPl8TnVj/aCpJpxt0MLqUALlRkjaS3JDnnjBFVF8F6V5MiB5lEjBXGV+YKG+98vJO85PXgV2m0rDDGSX2xcM3UkcZP1zWkIS3kzOpONrQX4BApd/mJX5ONpI6E9amlhHlONz/dP8R9PrVYPJHtZAvIIOc/3h/jUqtJPCzucLg8KMZ49ck4/Kre5yo4DxuNfSW1ew/tCW0MMmVsGCyfaePK8w4J8v8CufvdqyL67uLa/sf7ZZjfiyC3CADywxhkZ8HzEBJweAuB154r1QxxiNQikEAdM1DNY6fPIjzQJIckEuu7Pyn1z6Vg6Lu5X3OyNdKKi1seOaTq2m2mm2kdtC7hbp5P3sKuQyiNcZEgADbxt7lhjr1dDbafaLqtwXuHjldEnVMB/nkdiVZWHTnPXA4Iz09mj0/TymwW0IUNu2iNcZPU4x14pX0+xUNi3iG45bCKM855wPWpVJ7Mt4latL+rnmcGmWGseI5ImklSWezKFwQV+aIBmxubkqwABbPU8gcdnpOk2+n6INAmY3cbK6OzqUDCXO4cZwDk4reit7QXAmSBFl67wqhueOuM9OKhnYrIV27gyqTk46nHp7VvGmrXe+pzTrOVkttDhdN+H9joMV0NLuJku5oVhimkYP5UKNkRJtCEKemeT6GrtpoOsR6hZ3T6m/k2ykTKSQsjdQMHORzyScnA/Dqg6ryyEjjOD+XJ+tA+ZztXGSSB17D/69TGjGC5Yqw5Yicm3J3OLi0PXJXeSbUVUSIu3EjHb8i7hsJAO5wTkYIycVUk8O+IEu7SX+0hIkEQ8z946lpASw6Zz1AyTz1NejqgKhvMxUfkxFXO1Rlz1FN013Gq0vIadPWWOM3QDyqgVmHGTjk8epqK9t44NPZEGAGUj2O4U+SNVOMKeM9KZdKPskqRj+JDgD2HpVqmlJStqclo3cranj/wAO4oIPFmvKdLuLWQknzpJQySgysPlURIByc/ebgjr29icB0KqvJGBlv/rV5R4AvdOufEviKGzvri5dHBeKREWKIiVx8jIx39QASBwOO9ewLGXhXbgEE1vLe4I8p8a+Pta8Pa4NNtIo0VIopI43Qu940jkNHEwddpUD0bn8AesvvEz2d5eWi24ZraLemZAC7YUlAMHnnt6c9RXUCIfaleUBmC5UkZxj09OtXa4FTlGUnzaM7XUpyjFcu3nucGPGNwLaCWW0jRpY95VpGyPmYdk74zzzzSf8JnL/AM+0X/f1/wD43XZvcCNtm3djvTPtY/55it+WRnzR7H//1f0oigli3BWXDNu+6ep/GqqqduTgkkn7p7k/7VXUScqCzkHuCo4PcfhTBDcIAiScDpkDP8q4lvqdBVVSpwOhJOMYHP4mpmVHgifaGOwDOM8c1Myy7QHIPPWmBiIIQoYfKMlOvU9c1qnoQ9xhW2PWL9KVVt87Vj42kkd+o/xqDJZd5L53Eck9Bj0xTh5j/KozhQPwzU8vULkogEmWiYqAcYyRj9aiUhXVpHYgNjkkj7poR5I4mjJRM7vvdee/btTirjZ8uCSe/YDrn8aGtLATrLGv3WDfSpA3mj5e1VPOWJsueR2LCkNwjMWVgM9gy/40WQFm3yZ2BPAYAfTIqrNuklJRd67VXIOOhz2qzaAZ3bgSWyRkH+L2zUMituQ7COwxg9vqKpPQTRWIUOV38BxgM3PGOOtTgDzkUPjgZ2kH+99aVUkdWU527mypIGOen5e9SeSwkCAIQ6nIPTqPSpb0GhrQlMqgds45OP6CkPTkEje3SmqkzqGESkH/AGmP9ajljdFB8sD5gf4hn9aE3oBIVy2drgdqZdEi2lZSR8yg4OOgGajZnALeWnAzzk9PrT7ySIwvEjLgAEBSPr2qtboNLHlvgO8ubvxBrkdxewXBgCJ5UQcPGRLLw+5F+nBIyPxPrXPkLjPU9CR/KvJ/h9pmqQa7rFxqOmQ28Mh/dSJgPKRK5y2Hc8A+wzXr2XUKqjYOgyN3J/EU5vWwkVo1ZJlZtx+U9STxketXPNHpVWVpUm+8CcBeBjrk9z7UbrgfNsz342n+RrO1x3HvbGRt+cZ7U37Gf7wqi7tNIzkHqAPyHvSbD6foKq0guf/W+/dN8Sx61ZpqenXAlglZwrlAuSjFG4dAeGUjpV4anc5x5iE+m2P/AAFeX/Db/kR7D/rref8ApVLXYr/r61SRNzpP7SvO4Q/8AH9DUV1rDwWzzyrsihjLPsU/dQFiQASenaokqhq//IH1D/r1n/8ARRpOKGmzKt/HWjOtlIsk0bag5jhidJFkLbwhLLg7QCRnPqK1NC8Z6f4i+0S6U7ubZ1ilEimMqxG4DDIOx614i3/Id8M/9fE3/pRBW78Kfv8AiL/r9i/9FCo9nEttnoOv/ENNG1BdJSymupWiDsIwDjOF9upIxVXTPiYl7fW2mXOl3Ns11xE8owp2gZ/9CHcd+4IrkNf/AOR8/wC3aH/0ZHST/wDIe8O/WT+lfPzxE1WcU9D9Xw+R4OeXqvKHvcl73e9rntS3ygsWizuOf0x6077dAesP+fyqgOgpK9zkR+UXZqw39ssi4jKZYZPPTP0qFLuIOXldyckADgAduDVE/eX8P51DL/rG/CjlVhKTNk3dixyxOf8AaCn+dN8/Sc4LKD+Fc9L96qEn+srJotHeC3t9qlGUbgG6jv04JHao7iB4YHnKuViUuWUnOFGenIqt2i/3I/5Vv3n/ACCbr/rhJ/6BW0YptXIcnqea3/i4WEcQuLW48yd/LMeV3AYQ7sNgkYcdB1/Ona3rF0llfQWcKx3KTQQpJP5gRjNIq5I2pnarA8E5zXN+M/8AkP2H+f4beuh8W/cl/wCv6x/9Ct6+ojgqPNB8vX9TwKmLq2lr/VmVfBeo3Wp3N35kttdwiKKWCW2V1yJJJVbIYnoyHFehY/eIUVgRk4J64xXj3wT/AOPD/txg/wDSm6r2j/lvH9G/pXlZzRjSxUoU1Zafkd+W1JVMPGcnr/wSs7MzvvUYJHUnsBx0P1qLyo2OAq8+h/8AsRU8vf8A3/8A2QVFF94V46PUGQyxbMvCTk5ByOnapfNtv+eB/MVVj/1Uf+6KdRyom5//2Q==";
        locationMessageContent.setThumbnailByte(Base64.getDecoder().decode(thumbnailBase64edData));
        locationMessageContent.setTitle("中国北京市海淀区阜成路北二街131号");
        //设置经纬度
        locationMessageContent.setLatitude(39.926537312885166);
        locationMessageContent.setLongitude(116.32154022158235);
        //消息转成Payload并发送
        payload = locationMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试发送文件消息
        FileMessageContent fileMessageContent = new FileMessageContent();
        //设置文件名
        fileMessageContent.setName("野火产品简介.pptx");
        //设置文件大小
        fileMessageContent.setSize(38394);
        //设置文件链接
        fileMessageContent.setRemoteMediaUrl("https://media.wfcoss.cn/firechat/file_message_sample.pptx");
        //消息转成Payload并发送
        payload = fileMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试动态表情消息
        StickerMessageContent stickerMessageContent = new StickerMessageContent();
        stickerMessageContent.setWidth(753);
        stickerMessageContent.setHeight(960);
        stickerMessageContent.setRemoteMediaUrl("https://media.wfcoss.cn/firechat/sticker_message_sample.jpg");
        //消息转成Payload并发送
        payload = stickerMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试链接消息
        LinkMessageContent linkMessageContent = new LinkMessageContent();
        linkMessageContent.setTitle("野火IM开发手册");
        linkMessageContent.setUrl("https://docs.wildfirechat.cn");
        linkMessageContent.setThumbnailUrl("https://docs.wildfirechat.cn/favicon.ico");
        linkMessageContent.setContentDigest("野火IM开发手册，关于野火的所有知识都在这里！");
        //消息转成Payload并发送
        payload = linkMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试名片消息
        CardMessageContent cardMessageContent = new CardMessageContent();
        //类型：0，用户；1，群组；2，聊天室；3，频道
        cardMessageContent.setType(0);
        cardMessageContent.setTarget("FireRobot");
        cardMessageContent.setName("FireRobot");
        cardMessageContent.setPortrait("https://cdn2.wildfirechat.net/robot.png");
        cardMessageContent.setDisplayName("小火");
        cardMessageContent.setFrom(sender);
        //消息转成Payload并发送
        payload = cardMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试提醒消息
        TipNotificationMessageContent tipNotificationMessageContent = new TipNotificationMessageContent();
        tipNotificationMessageContent.setTip("这是一个提醒小灰条消息");
        //消息转成Payload并发送
        payload = tipNotificationMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试富通知消息
        RichNotificationMessageContent richNotificationMessageContent = new RichNotificationMessageContent("产品审核通知", "您好，您的SSL证书以审核通过并成功办理，请关注", "https://www.wildfirechat.cn")
            .remark("谢谢惠顾")
            .exName("证书小助手")
            .appId("1234567890")
            .addItem("登陆账户", "野火IM", "#173177")
            .addItem("产品名称", "域名wildifrechat.cn申请的免费SSL证书", "#173177")
            .addItem("审核通过", "通过", "#173177")
            .addItem("说明", "请登陆账户查看处理", "#173177");
        //消息转成Payload并发送
        payload = richNotificationMessageContent.encode();
        resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
        checkSendMessageResult(resultSendMessage);

        //测试流式文本消息
        testStreamingText(sender, conversation);
    }

    static void testReadMessageContentFromDB() throws Exception {
        //从数据库t_messages_x表中读取到消息内容字段_data的二进制数据为
        byte[] data = {8,1,18,5,72,101,108,108,111,64,3};
        //1. 先把二进制数据转化为协议栈消息内容。
        WFCMessage.MessageContent protoContent = WFCMessage.MessageContent.parseFrom(data);
        //2. 调用MessageContentFactory接口解析为消息内容。
        MessageContent messageContent = MessageContentFactory.decodeMessageContent(protoContent);
        //3. 检查是哪种消息，如果没有定义，会回落到UnknownMessageContent。
        if(messageContent instanceof TextMessageContent) {
            TextMessageContent txt = (TextMessageContent)messageContent;
            System.out.println("读取到的是文本消息，内容为：" + txt.getText());
        } else if(messageContent instanceof ImageMessageContent) {
            ImageMessageContent img = (ImageMessageContent) messageContent;
            System.out.println("读取到的是图片消息，图片链接为：" + img.getRemoteMediaUrl());
        } else if(messageContent instanceof SoundMessageContent) {
            SoundMessageContent sound = (SoundMessageContent) messageContent;
            System.out.println("读取到的是声音消息，声音链接为：" + sound.getRemoteMediaUrl());
        }
    }

    static void checkSendMessageResult(IMResult<SendMessageResult> resultSendMessage) {
        if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("send message success");
        } else {
            System.out.println("send message failure");
            System.exit(-1);
        }
    }

    static void testStreamingText(String sender, Conversation conversation) throws Exception {
        String fullText = "北京野火无限网络科技有限公司是成立于2019年底的一家科技创新企业，公司的主要目标是为广大企业和单位提供优质可控、私有部署的即时通讯和实时音视频能力，为社会信息化水平提高作出自己的贡献。\n" +
            "\n" +
            "野火IM是公司研发一套自主可控的即时通讯组件，具有全部私有化、功能齐全、协议稳定可靠、全平台支持、安全性高和支持国产化等技术特点。客户端分层设计，既可开箱即用，也可与现有系统深度融合。具有完善的服务端API和自定义消息功能，可以任意扩展功能。代码开源率高，方便二次开发和使用。支持多人实时音视频和会议功能，线上沟通更通畅。\n" +
            "\n" +
            "公司致力于开源项目，在Github上开源项目广受好评，其中Server项目有超过7.1K个Star，组织合计Star超过1万个。有大量的技术公司受益于我们的开源，为自己的产品添加了即时通讯能力，这也算是我们公司为社会信息化建设做出的一点点贡献吧。\n" +
            "\n" +
            "公司以即时通讯技术为核心，持续努力优化和完善即时通讯和实时音视频产品，努力为客户提供最优质的即时通讯和实时音视频能力。";
        int i = 0;
        String streamId = UUID.randomUUID().toString();
        while (i < fullText.length()) {
            i+= 15;

            boolean finish = i >= fullText.length();
            String partText = finish?fullText:fullText.substring(0, i);

            MessageContent messageContent;
            if(finish) {
                messageContent = new StreamTextGeneratedMessageContent(partText, streamId);
            } else {
                messageContent = new StreamTextGeneratingMessageContent(partText, streamId);
            }
            //消息转成Payload并发送
            MessagePayload payload = messageContent.encode();

            IMResult<SendMessageResult> resultSendMessage = MessageAdmin.sendMessage(sender, conversation, payload, null);
            if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("send message success");
            } else {
                System.out.println("send message failure");
                System.exit(-1);
            }

            Thread.sleep(500);
        }
    }


    //***********************************************
    //****  测试频道功能
    //***********************************************
    static void testChannelApi() throws Exception {
        String channelName = "MyChannel";
        String channelOwner = "user1";
        InputCreateChannel inputCreateChannel = new InputCreateChannel();
        inputCreateChannel.setName(channelName);
        inputCreateChannel.setOwner(channelOwner);
        IMResult<OutputCreateChannel> resultCreateChannel = ChannelAdmin.createChannel(inputCreateChannel);
        if (resultCreateChannel != null && resultCreateChannel.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("success");
            inputCreateChannel.setTargetId(resultCreateChannel.result.getTargetId());
        } else {
            System.out.println("create channel failure");
            System.exit(-1);
        }

        IMResult<OutputGetChannelInfo> resultGetChannel = ChannelAdmin.getChannelInfo(inputCreateChannel.getTargetId());
        if(resultGetChannel != null && resultGetChannel.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS
            && resultGetChannel.getResult().getName().equals(channelName)
            && resultGetChannel.getResult().getOwner().equals(channelOwner)) {
            System.out.println("success");
        } else {
            System.out.println("get channel failure");
            System.exit(-1);
        }

        IMResult<Void> voidIMResult = ChannelAdmin.destroyChannel(inputCreateChannel.getTargetId());
        if(voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("success");
        } else {
            System.out.println("destroy channel failure");
            System.exit(-1);
        }

        resultGetChannel = ChannelAdmin.getChannelInfo(inputCreateChannel.getTargetId());
        if(resultGetChannel != null && resultGetChannel.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && (resultGetChannel.getResult().getState() & ProtoConstants.ChannelState.Channel_State_Mask_Deleted) > 0) {
            System.out.println("success");
        } else {
            System.out.println("get channel failure");
            System.exit(-1);
        }
    }

    //***********************************************
    //****  一些其它的功能，比如创建频道，更新用户设置等
    //***********************************************
    static void testGeneralApi() throws Exception {
        IMResult<SystemSettingPojo> resultGetSystemSetting  =  GeneralAdmin.getSystemSetting(Group_Max_Member_Count);
        if (resultGetSystemSetting != null && resultGetSystemSetting.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("success");
        } else {
            System.out.println("get system setting failure");
            System.exit(-1);
        }

        IMResult<Void> resultSetSystemSetting = GeneralAdmin.setSystemSetting(Group_Max_Member_Count, "2000", "最大群人数为2000");
        if (resultSetSystemSetting != null && resultSetSystemSetting.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("success");
        } else {
            System.out.println("get system setting failure");
            System.exit(-1);
        }

        resultGetSystemSetting  =  GeneralAdmin.getSystemSetting(Group_Max_Member_Count);
        if (resultGetSystemSetting != null && resultGetSystemSetting.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && resultGetSystemSetting.getResult().value.equals("2000")) {
            System.out.println("success");
        } else {
            System.out.println("get system setting failure");
            System.exit(-1);
        }

        IMResult<HealthCheckResult> health = GeneralAdmin.healthCheck();
        if(health != null && health.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println(health.result);
        } else {
            System.out.println("health check failure");
            System.exit(-1);
        }
    }

    static void testChatroom() throws Exception {
        String chatroomId = "chatroomId1";
        String chatroomTitle = "TESTCHATROM";
        String chatroomDesc = "this is a test chatroom";
        String chatroomPortrait = "http://pic.com/test123.png";
        String chatroomExtra = "{\'managers:[\"user1\",\"user2\"]}";
        IMResult<OutputCreateChatroom> chatroomIMResult = ChatroomAdmin.createChatroom(chatroomId,chatroomTitle, chatroomDesc, chatroomPortrait,chatroomExtra,ProtoConstants.ChatroomState.Chatroom_State_Normal);
        if (chatroomIMResult != null && chatroomIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && chatroomIMResult.getResult().getChatroomId().equals(chatroomId)) {
            System.out.println("create chatroom success");
        } else {
            System.out.println("create chatroom failure");
            System.exit(-1);
        }

        IMResult<OutputGetChatroomInfo> getChatroomInfoIMResult = ChatroomAdmin.getChatroomInfo(chatroomId);
        if (getChatroomInfoIMResult != null && getChatroomInfoIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (!getChatroomInfoIMResult.getResult().getChatroomId().equals(chatroomId)
                || !getChatroomInfoIMResult.getResult().getTitle().equals(chatroomTitle)
                || !getChatroomInfoIMResult.getResult().getDesc().equals(chatroomDesc)
                || !getChatroomInfoIMResult.getResult().getPortrait().equals(chatroomPortrait)
                || !getChatroomInfoIMResult.getResult().getExtra().equals(chatroomExtra)
                || getChatroomInfoIMResult.getResult().getState() != ProtoConstants.ChatroomState.Chatroom_State_Normal) {
                System.out.println("chatroom info incorrect");
                System.exit(-1);
            } else {
                System.out.println("chatroom info correct");
            }
        } else {
            System.out.println("get chatroom info failure");
            System.exit(-1);
        }

        IMResult<OutputStringList> memberList = ChatroomAdmin.getChatroomMembers(chatroomId);
        if (memberList != null && memberList.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get chatroom member success");
        } else {
            System.out.println("get chatroom member failure: " + memberList.getErrorCode().msg);
        }

        IMResult<Void> voidIMResult = ChatroomAdmin.destroyChatroom(chatroomId);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("destroy chatroom done!");
        } else {
            System.out.println("destroy chatroom failure");
            System.exit(-1);
        }

        Thread.sleep(1000);
        getChatroomInfoIMResult = ChatroomAdmin.getChatroomInfo(chatroomId);
        if (getChatroomInfoIMResult != null && getChatroomInfoIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && getChatroomInfoIMResult.getResult().getState() == ProtoConstants.ChatroomState.Chatroom_State_End) {
            System.out.println("chatroom destroyed!");
        } else {
            System.out.println("chatroom not destroyed!");
            System.exit(-1);
        }

        IMResult<OutputUserChatroom>  userChatroomIMResult = ChatroomAdmin.getUserChatroom("uygqmws2k");
        if(userChatroomIMResult != null && (userChatroomIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS || userChatroomIMResult.getErrorCode() == ErrorCode.ERROR_CODE_NOT_EXIST)) {
            System.out.println("get user chatroom success");
        } else {
            System.out.println("get user chatroom failure");
            System.exit(-1);
        }

        //下面仅专业版支持
        if(commercialServer) {
            //设置用户聊天室黑名单。0正常；1禁言；2禁止加入。
            IMResult<Void> voidIMResult1 = ChatroomAdmin.setChatroomBlacklist("chatroom1", "oto9o9__", 1);
            if (voidIMResult1 != null && voidIMResult1.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("add chatroom black success");
            } else {
                System.out.println("add chatroom black failure");
                System.exit(-1);
            }

            //获取聊天室黑名单
            IMResult<OutputChatroomBlackInfos> blackInfos = ChatroomAdmin.getChatroomBlacklist("chatroom1");
            if (blackInfos != null && blackInfos.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && !blackInfos.getResult().infos.isEmpty()) {
                boolean success = false;
                for (OutputChatroomBlackInfos.OutputChatroomBlackInfo info : blackInfos.getResult().infos) {
                    if (info.userId.equals("oto9o9__")) {
                        success = true;
                        break;
                    }
                }
                if (success) {
                    System.out.println("add chatroom black success");
                } else {
                    System.out.println("add chatroom black failure");
                    System.exit(-1);
                }
            } else {
                System.out.println("add chatroom black failure");
                System.exit(-1);
            }

            //取消用户聊天室黑名单。0正常；1禁言；2禁止加入。
            voidIMResult1 = ChatroomAdmin.setChatroomBlacklist("chatroom1", "oto9o9__", 0);
            if (voidIMResult1 != null && voidIMResult1.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("remove chatroom black success");
            } else {
                System.out.println("remove chatroom black failure");
                System.exit(-1);
            }

            //获取聊天室黑名单
            blackInfos = ChatroomAdmin.getChatroomBlacklist("chatroom1");
            if (blackInfos != null && blackInfos.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                boolean success = true;
                for (OutputChatroomBlackInfos.OutputChatroomBlackInfo info : blackInfos.getResult().infos) {
                    if (info.userId.equals("oto9o9__")) {
                        success = false;
                        break;
                    }
                }
                if (success) {
                    System.out.println("remove chatroom black success");
                } else {
                    System.out.println("remove chatroom black failure");
                    System.exit(-1);
                }
            } else {
                System.out.println("remove chatroom black failure");
                System.exit(-1);
            }

            //设置聊天室管理员
            IMResult<Void> voidIMResult2 = ChatroomAdmin.setChatroomManager("chatroom1", "UserId1", 1);
            if (voidIMResult2 != null && voidIMResult2.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("add chatroom manager success");
            } else {
                System.out.println("add chatroom black failure");
                System.exit(-1);
            }

            //获取聊天室管理员
            IMResult<OutputStringList> managers = ChatroomAdmin.getChatroomManagerList("chatroom1");
            if (managers != null && managers.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && !managers.getResult().getList().isEmpty() && managers.getResult().getList().contains("UserId1")) {
                System.out.println("add chatroom black success");
            } else {
                System.out.println("add chatroom black failure");
                System.exit(-1);
            }

            //取消聊天室管理员
            IMResult<Void> voidIMResult3 = ChatroomAdmin.setChatroomManager("chatroom1", "UserId1", 0);
            if (voidIMResult3 != null && voidIMResult3.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("add chatroom manager success");
            } else {
                System.out.println("add chatroom black failure");
                System.exit(-1);
            }

            //获取聊天室管理员
            IMResult<OutputStringList> managers2 = ChatroomAdmin.getChatroomManagerList("chatroom1");
            if (managers2 != null && managers2.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && !managers2.getResult().getList().contains("UserId1")) {
                System.out.println("add chatroom black success");
            } else {
                System.out.println("add chatroom black failure");
                System.exit(-1);
            }
        }
    }

    static void testRobot() throws Exception {
        String robotId = "robot1";
        String robotSecret = "123456";
        //初始化服务API
        AdminConfig.initAdmin(AdminUrl, AdminSecret);
        //创建机器人
        InputCreateRobot createRobot = new InputCreateRobot();
        createRobot.setUserId(robotId);
        createRobot.setName(robotId);
        createRobot.setDisplayName("机器人");
        createRobot.setOwner("userId1");
        createRobot.setSecret(robotSecret);
        createRobot.setCallback("http://127.0.0.1:8883/robot/recvmsg");
        IMResult<OutputCreateRobot> resultCreateRobot = UserAdmin.createRobot(createRobot);
        if (resultCreateRobot != null && resultCreateRobot.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Create robot " + resultCreateRobot.getResult().getUserId() + " success");
        } else {
            System.out.println("Create robot failure");
            System.exit(-1);
        }

        RobotService robotService = new RobotService(IMUrl, robotId, robotSecret);

        //***********************************************
        //****  机器人API
        //***********************************************

        IMResult<OutputRobot> robotProfileIMResult = robotService.getProfile();
        if(robotProfileIMResult != null && robotProfileIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get profile success");
        } else {
            System.out.println("get profile failure");
            System.exit(-1);
        }

        String displayName = "testrobot"+System.currentTimeMillis();
        IMResult<Void> voidIMResult1 = robotService.updateProfile(Modify_DisplayName, displayName);
        if(voidIMResult1 != null && voidIMResult1.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("modify profile success");
        } else {
            System.out.println("modify profile failure");
            System.exit(-1);
        }
        robotProfileIMResult = robotService.getProfile();
        if(robotProfileIMResult != null && robotProfileIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && displayName.equals(robotProfileIMResult.getResult().getDisplayName())) {
            System.out.println("get profile success");
        } else {
            System.out.println("get profile failure");
            System.exit(-1);
        }

        String robotCallback = "http://hellow123";
        voidIMResult1 = robotService.setCallback(robotCallback);
        if(voidIMResult1 != null && voidIMResult1.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("set callback success");
        } else {
            System.out.println("set callback failure");
            System.exit(-1);
        }

        IMResult<RobotCallbackPojo> callbackPojoIMResult = robotService.getCallback();
        if(callbackPojoIMResult != null && callbackPojoIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && robotCallback.equals(callbackPojoIMResult.getResult().getUrl())) {
            System.out.println("get callback success");
        } else {
            System.out.println("get callback failure");
            System.exit(-1);
        }

        voidIMResult1 = robotService.deleteCallback();
        if(voidIMResult1 != null && voidIMResult1.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("delete callback success");
        } else {
            System.out.println("delete callback failure");
            System.exit(-1);
        }

        callbackPojoIMResult = robotService.getCallback();
        if(callbackPojoIMResult != null && callbackPojoIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && StringUtil.isNullOrEmpty(callbackPojoIMResult.getResult().getUrl())) {
            System.out.println("get callback success");
        } else {
            System.out.println("get callback failure");
            System.exit(-1);
        }

        Conversation conversation = new Conversation();
        conversation.setTarget("user2");
        conversation.setType(ProtoConstants.ConversationType.ConversationType_Private);
        MessagePayload payload = new MessagePayload();
        payload.setType(1);
        payload.setSearchableContent("hello world");

        IMResult<SendMessageResult> resultRobotSendMessage = robotService.sendMessage(robotService.getRobotId(), conversation, payload);
        if (resultRobotSendMessage != null && resultRobotSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("robot send message success");
        } else {
            System.out.println("robot send message failure");
            System.exit(-1);
        }

        if(commercialServer) {
            Thread.sleep(1000);
            IMResult<String> recallMessageResult = robotService.recallMessage(resultRobotSendMessage.result.getMessageUid());
            if (recallMessageResult != null && recallMessageResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("robot recall message success");
            } else {
                System.out.println("robot recall message failure");
                System.exit(-1);
            }

            payload.setSearchableContent("hello world, hello world");
            resultRobotSendMessage = robotService.sendMessage(robotService.getRobotId(), conversation, payload);
            if (resultRobotSendMessage != null && resultRobotSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("robot send message success");
            } else {
                System.out.println("robot send message failure");
                System.exit(-1);
            }

            Thread.sleep(1000);
            payload.setSearchableContent("hello world, updated message content");
            IMResult<Void> updateMessageResult = robotService.updateMessage(resultRobotSendMessage.result.getMessageUid(), payload);
            if (updateMessageResult != null && updateMessageResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("robot update message success");
            } else {
                System.out.println("robot update message failure");
                System.exit(-1);
            }
        }


        IMResult<InputOutputUserInfo> resultRobotGetUserInfo = robotService.getUserInfo("userId1");
        if (resultRobotGetUserInfo != null && resultRobotGetUserInfo.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("robot get user info success");
        } else {
            System.out.println("robot get user info by userId failure");
            System.exit(-1);
        }

        String groupId = "robot_group" + System.currentTimeMillis();
        PojoGroupInfo groupInfo = new PojoGroupInfo();
        groupInfo.setTarget_id(groupId);
        groupInfo.setName("test_group");
        groupInfo.setType(2);
        groupInfo.setExtra("hello extra");
        groupInfo.setPortrait("http://portrait");
        List<PojoGroupMember> members = new ArrayList<>();

        PojoGroupMember member1 = new PojoGroupMember();
        member1.setMember_id("user1");
        members.add(member1);

        PojoGroupMember member2 = new PojoGroupMember();
        member2.setMember_id("user2");
        members.add(member2);

        PojoGroupMember member3 = new PojoGroupMember();
        member3.setMember_id("user3");
        members.add(member3);

        IMResult<OutputCreateGroupResult> resultCreateGroup = robotService.createGroup(groupInfo, members, null, null);
        if (resultCreateGroup != null && resultCreateGroup.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("create group success");
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        IMResult<PojoGroupInfo> resultGetGroupInfo = robotService.getGroupInfo(groupInfo.getTarget_id());
        if (resultGetGroupInfo != null && resultGetGroupInfo.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if (groupInfo.getExtra().equals(resultGetGroupInfo.getResult().getExtra())
                && groupInfo.getName().equals(resultGetGroupInfo.getResult().getName())
                && robotId.equals(resultGetGroupInfo.getResult().getOwner())) {
                System.out.println("get group success");
            } else {
                System.out.println("group info is not expected");
                System.exit(-1);
            }
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        IMResult<Void> voidIMResult = robotService.modifyGroupInfo(groupInfo.getTarget_id(), ProtoConstants.ModifyGroupInfoType.Modify_Group_Name,"HelloWorld", null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("modify group success");
        } else {
            System.out.println("modify group failure");
            System.exit(-1);
        }


        IMResult<OutputGroupMemberList> resultGetMembers = robotService.getGroupMembers(groupInfo.getTarget_id());
        if (resultGetMembers != null && resultGetMembers.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get group member success");
        } else {
            System.out.println("get group member failure");
            System.exit(-1);
        }

        PojoGroupMember m = new PojoGroupMember();
        m.setMember_id("user0");
        m.setAlias("hello user0");

        voidIMResult = robotService.addGroupMembers(groupInfo.getTarget_id(), Arrays.asList(m), null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("add group member success");
        } else {
            System.out.println("add group member failure");
            System.exit(-1);
        }

        IMResult<PojoGroupMember> groupMemberIMResult = robotService.getGroupMember(groupInfo.getTarget_id(), "user0");
        if (groupMemberIMResult != null && groupMemberIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get group member success");
        } else {
            System.out.println("get group member failure");
            System.exit(-1);
        }

        voidIMResult = robotService.kickoffGroupMembers(groupInfo.getTarget_id(), Arrays.asList("user3"), null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("kickoff group member success");
        } else {
            System.out.println("kickoff group member failure");
            System.exit(-1);
        }

        voidIMResult = robotService.setGroupMemberAlias(groupInfo.getTarget_id(), "user3", "test user3", null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("set group member alias success");
        } else {
            System.out.println("set group member alias failure");
            System.exit(-1);
        }

        if(commercialServer) {
            voidIMResult = robotService.setGroupManager(groupInfo.getTarget_id(), Arrays.asList("user4", "user5"), true, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("set group manager success");
            } else {
                System.out.println("set group manager failure");
                System.exit(-1);
            }

            voidIMResult = robotService.setGroupManager(groupInfo.getTarget_id(), Arrays.asList("user4", "user5"), false, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("cancel group manager success");
            } else {
                System.out.println("cancel group manager failure");
                System.exit(-1);
            }

            OutputApplicationConfigData config = robotService.getApplicationSignature();
            System.out.println(config);
        }



        //仅专业版支持
        if (commercialServer) {
            //开启群成员禁言
            voidIMResult = robotService.muteGroupMember(groupInfo.getTarget_id(), Arrays.asList("user5"), true, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("mute group member success");
            } else {
                System.out.println("mute group member failure");
                System.exit(-1);
            }
            //关闭群成员禁言
            voidIMResult = robotService.muteGroupMember(groupInfo.getTarget_id(), Arrays.asList("user5"), false, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("unmute group member success");
            } else {
                System.out.println("unmute group member failure");
                System.exit(-1);
            }

            //开启群成员白名单，当群全局禁言时，白名单用户可以发言
            voidIMResult = robotService.allowGroupMember(groupInfo.getTarget_id(), Arrays.asList("user5"), true, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("allow group member success");
            } else {
                System.out.println("allow group member failure");
                System.exit(-1);
            }

            //关闭群成员白名单
            voidIMResult = robotService.allowGroupMember(groupInfo.getTarget_id(), Arrays.asList("user5"), false, null, null);
            if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("unallow group member success");
            } else {
                System.out.println("unallow group member failure");
                System.exit(-1);
            }
        }
        voidIMResult = robotService.transferGroup(groupInfo.getTarget_id(), "user2", null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("transfer success");
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        resultGetGroupInfo = robotService.getGroupInfo(groupInfo.getTarget_id());
        if (resultGetGroupInfo != null && resultGetGroupInfo.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            if ("user2".equals(resultGetGroupInfo.getResult().getOwner())) {
                groupInfo.setOwner("user2");
            } else {
                System.out.println("group info is not expected");
                System.exit(-1);
            }
        } else {
            System.out.println("create group failure");
            System.exit(-1);
        }

        voidIMResult = robotService.quitGroup(groupInfo.getTarget_id(), null, null);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("quit group success");
        } else {
            System.out.println("quit group failure");
            System.exit(-1);
        }

    }

    //***测试频道API功能，仅专业版支持***
    static void testChannel() throws Exception {
        //初始化服务API
        AdminConfig.initAdmin(AdminUrl, AdminSecret);

        //先创建3个用户
        InputOutputUserInfo userInfo = new InputOutputUserInfo();
        userInfo.setUserId("userId1");
        userInfo.setName("user1");
        userInfo.setMobile("13900000000");
        userInfo.setDisplayName("user 1");

        IMResult<OutputCreateUser> resultCreateUser = UserAdmin.createUser(userInfo);
        if (resultCreateUser != null && resultCreateUser.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Create user " + resultCreateUser.getResult().getName() + " success");
        } else {
            System.out.println("Create user failure");
            System.exit(-1);
        }

        userInfo = new InputOutputUserInfo();
        userInfo.setUserId("userId2");
        userInfo.setName("user2");
        userInfo.setMobile("13900000002");
        userInfo.setDisplayName("user 2");

        resultCreateUser = UserAdmin.createUser(userInfo);
        if (resultCreateUser != null && resultCreateUser.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Create user " + resultCreateUser.getResult().getName() + " success");
        } else {
            System.out.println("Create user failure");
            System.exit(-1);
        }

        userInfo = new InputOutputUserInfo();
        userInfo.setUserId("userId3");
        userInfo.setName("user3");
        userInfo.setMobile("13900000003");
        userInfo.setDisplayName("user 3");

        resultCreateUser = UserAdmin.createUser(userInfo);
        if (resultCreateUser != null && resultCreateUser.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Create user " + resultCreateUser.getResult().getName() + " success");
        } else {
            System.out.println("Create user failure");
            System.exit(-1);
        }

        //1. 先使用admin api创建频道
        InputCreateChannel inputCreateChannel = new InputCreateChannel();
        inputCreateChannel.setName("testChannel");
        inputCreateChannel.setOwner("userId1");
        String secret = "channelsecret";
        String channelId = "channelId123";
        inputCreateChannel.setSecret(secret);
        inputCreateChannel.setTargetId(channelId);
        inputCreateChannel.setAuto(1);
        inputCreateChannel.setCallback("http://192.168.1.81:8088/wf/channelId123");
        inputCreateChannel.setState(Channel_State_Mask_FullInfo | Channel_State_Mask_Unsubscribed_User_Access | Channel_State_Mask_Active_Subscribe | Channel_State_Mask_Message_Unsubscribed);
        IMResult<OutputCreateChannel> resultCreateChannel = ChannelAdmin.createChannel(inputCreateChannel);
        if (resultCreateChannel != null && resultCreateChannel.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("create channel success");
        } else {
            System.out.println("create channel failure");
            System.exit(-1);
        }


        //2. 初始化api，注意端口是80，不是18080
        ChannelServiceApi channelServiceApi = new ChannelServiceApi(IMUrl, channelId, secret);


        //3. 测试channel api功能
        IMResult<Void> resultVoid = channelServiceApi.subscribe("userId2");
        if (resultVoid != null && resultVoid.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("subscribe success");
        } else {
            System.out.println("subscribe failure");
            System.exit(-1);
        }

        resultVoid = channelServiceApi.subscribe("userId3");
        if (resultVoid != null && resultVoid.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("subscribe done");
        } else {
            System.out.println("subscribe failure");
            System.exit(-1);
        }

        resultVoid = ChannelAdmin.subscribeChannel(channelId, "userId4");
        if (resultVoid != null && resultVoid.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("subscribe done");
        } else {
            System.out.println("subscribe failure");
            System.exit(-1);
        }

        IMResult<OutputStringList> resultStringList = channelServiceApi.getSubscriberList();
        if (resultStringList != null && resultStringList.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && resultStringList.getResult().getList().contains("userId2") && resultStringList.getResult().getList().contains("userId3") && resultStringList.getResult().getList().contains("userId4")) {
            System.out.println("get subscriber done");
        } else {
            System.out.println("get subscriber failure");
            System.exit(-1);
        }

        IMResult<Boolean> booleanIMResult = channelServiceApi.isSubscriber("userId2");
        if(booleanIMResult != null && booleanIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && booleanIMResult.getResult()) {
            System.out.println("is subscriber success");
        } else {
            System.out.println("is subscriber failure");
            System.exit(-1);
        }

        resultVoid = channelServiceApi.unsubscribe("userId2");
        if (resultVoid != null && resultVoid.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("unsubscriber done");
        } else {
            System.out.println("unsubscriber failure");
            System.exit(-1);
        }

        resultStringList = channelServiceApi.getSubscriberList();
        if (resultStringList != null && resultStringList.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && resultStringList.getResult().getList().contains("userId3") && !resultStringList.getResult().getList().contains("userId2")) {
            System.out.println("get subscriber done");
        } else {
            System.out.println("get subscriber failure");
            System.exit(-1);
        }

        IMResult<InputOutputUserInfo> resultGetUserInfo1 = channelServiceApi.getUserInfo("userId3");
        if (resultGetUserInfo1 != null && resultGetUserInfo1.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get user info success");
        } else {
            System.out.println("get user info failure");
            System.exit(-1);
        }

        MessagePayload payload = new MessagePayload();
        payload.setType(1);
        payload.setSearchableContent("hello world");

        IMResult<SendMessageResult> resultSendMessage = channelServiceApi.sendMessage(0, null, payload);
        if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("send message to all the subscriber success");
        } else {
            System.out.println("send message to all the subscriber  failure");
            System.exit(-1);
        }

        ArticleContent articleContent = new ArticleContent("article1", "https://media.wfcoss.cn/channel-assets/20220816/2dd76540daa9444dae44e942aa1c2bbc.png", "这是一个测试文章", "测试一下文章的功能", "https://mp.weixin.qq.com/s/W6tanLbALd3qqZM8r3MTgA", true);
        articleContent.addSubArticle("article2", "https://media.wfcoss.cn/channel-assets/20220816/2dd76540daa9444dae44e942aa1c2bbc.png", "这是第二个测试文章", "测试一下文章的功能", "https://mp.weixin.qq.com/s/W6tanLbALd3qqZM8r3MTgA", false);
        articleContent.addSubArticle("article3", "https://media.wfcoss.cn/channel-assets/20220816/2dd76540daa9444dae44e942aa1c2bbc.png", "这是第三个测试文章", "测试一下文章的功能", "https://mp.weixin.qq.com/s/W6tanLbALd3qqZM8r3MTgA", false);
        payload = articleContent.toPayload();

        resultSendMessage = channelServiceApi.sendMessage(0, null, payload);
        if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("send message to all the subscriber success");
        } else {
            System.out.println("send message to all the subscriber  failure");
            System.exit(-1);
        }

        payload.setSearchableContent("hello to user2");

        resultSendMessage = channelServiceApi.sendMessage(0, Arrays.asList("userId2"),payload);
        if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("send message to user2 success");
        } else {
            System.out.println("send message to user2 failure");
            System.exit(-1);
        }

        IMResult<Void> voidIMResult = channelServiceApi.modifyChannelInfo(ProtoConstants.ModifyChannelInfoType.Modify_Channel_Desc, "this is a test channel, update at:" + new Date().toString());
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("modify channel profile success");
        } else {
            System.out.println("modify channel profile failure");
            System.exit(-1);
        }

        List<OutputGetChannelInfo.OutputMenu> menus = new ArrayList<>();
        OutputGetChannelInfo.OutputMenu menu1 = new OutputGetChannelInfo.OutputMenu();
        menu1.menuId = UUID.randomUUID().toString();
        menu1.type = "view";
        menu1.name = "一级菜单1";
        menu1.key = "key1";
        menu1.url = "http://www.baidu.com";
        menus.add(menu1);

        OutputGetChannelInfo.OutputMenu menu2 = new OutputGetChannelInfo.OutputMenu();
        menu2.menuId = UUID.randomUUID().toString();
        menu2.type = "view";
        menu2.name = "一级菜单2";
        menu2.key = "key2";
        menu2.url = "http://www.sohu.com";
        menu2.subMenus = new ArrayList<>();
        menus.add(menu2);

        OutputGetChannelInfo.OutputMenu menu21 = new OutputGetChannelInfo.OutputMenu();
        menu21.menuId = UUID.randomUUID().toString();
        menu21.type = "click";
        menu21.name = "二级菜单21";
        menu21.key = "key21";
        menu21.url = "http://www.sohu.com";
        menu2.subMenus.add(menu21);

        String menuStr = new GsonBuilder().disableHtmlEscaping().create().toJson(menus);
        voidIMResult = channelServiceApi.modifyChannelInfo(ProtoConstants.ModifyChannelInfoType.Modify_Channel_Menu, menuStr);
        if (voidIMResult != null && voidIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("modify channel menu success");
        } else {
            System.out.println("modify channel menu failure");
            System.exit(-1);
        }

        IMResult<OutputGetChannelInfo> outputGetChannelInfoIMResult = channelServiceApi.getChannelInfo();
        if (outputGetChannelInfoIMResult != null && outputGetChannelInfoIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get channel info success");
        } else {
            System.out.println("get channel info failure");
            System.exit(-1);
        }
        OutputApplicationConfigData config = channelServiceApi.getApplicationSignature();
        System.out.println(config);
    }
    static void testSensitiveApi() throws Exception {
        List<String> words = Arrays.asList("a","b","c");
        IMResult<Void> addResult = SensitiveAdmin.addSensitives(words);
        if (addResult != null && addResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Add sensitive word response success");
        } else {
            System.out.println("Add sensitive word response error");
            System.exit(-1);
        }

        Thread.sleep(100);

        IMResult<InputOutputSensitiveWords> swResult = SensitiveAdmin.getSensitives();
        if (swResult != null && swResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && swResult.getResult().getWords().containsAll(words)) {
            System.out.println("Sensitive word added");
        } else {
            System.out.println("Sensitive word not added");
            System.exit(-1);
        }

        IMResult<Void> removeResult = SensitiveAdmin.removeSensitives(words);
        if (removeResult != null && removeResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Remove sensitive word response success");
        } else {
            System.out.println("Remove sensitive word response error");
            System.exit(-1);
        }

        Thread.sleep(100);
        swResult = SensitiveAdmin.getSensitives();
        if (swResult != null && swResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && !swResult.getResult().getWords().containsAll(words)) {
            System.out.println("Sensitive word removed");
        } else {
            System.out.println("Sensitive word not removed");
            System.exit(-1);
        }
    }

    //***********************************************
    //****  物联网相关的API，仅专业版支持
    //***********************************************
    static void testDevice() throws Exception {
        InputCreateDevice createDevice = new InputCreateDevice();
        createDevice.setDeviceId("deviceId1");
        createDevice.setOwners(Arrays.asList("opoGoG__", "userId1"));
        IMResult<OutputCreateDevice> resultCreateDevice = UserAdmin.createOrUpdateDevice(createDevice);
        if (resultCreateDevice != null && resultCreateDevice.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("Create device " + resultCreateDevice.getResult().getDeviceId() + " success");
        } else {
            System.out.println("Create device failure");
            System.exit(-1);
        }

        IMResult<OutputDevice> getDevice = UserAdmin.getDevice("deviceId1");
        if (getDevice != null && getDevice.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && getDevice.getResult().getDeviceId().equals("deviceId1") && getDevice.getResult().getOwners().contains("opoGoG__")) {
            System.out.println("Get device " + resultCreateDevice.getResult().getDeviceId() + " success");
        } else {
            System.out.println("Get device failure");
            System.exit(-1);
        }

        IMResult<OutputDeviceList> getUserDevices = UserAdmin.getUserDevices("userId1");
        if (getUserDevices != null && getUserDevices.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
            boolean success = false;
            for (OutputDevice outputDevice : getUserDevices.getResult().getDevices()) {
                if (outputDevice.getDeviceId().equals("deviceId1")) {
                    success = true;
                    break;
                }
            }
            if (success) {
                System.out.println("Get user device success");
            } else {
                System.out.println("Get user device failure");
                System.exit(-1);
            }
        } else {
            System.out.println("Get device failure");
            System.exit(-1);
        }
    }

    /*
    会议相关接口，仅音视频高级版服务支持
     */
    public static void testConference() throws Exception {
        IMResult<PojoConferenceInfoList> listResult = ConferenceAdmin.listConferences();
        if(listResult == null || listResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get conference list failure");
            System.exit(-1);
        } else {
            System.out.println("conference list " + listResult.getResult().conferenceInfoList);
        }

        for (PojoConferenceInfo conferenceInfo:listResult.getResult().conferenceInfoList) {
            IMResult<Void> destroyResult = ConferenceAdmin.destroy(conferenceInfo.roomId, conferenceInfo.advance);
            if(destroyResult == null || destroyResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("destroy room failure");
                System.exit(-1);
            } else {
                System.out.println("destroy room success");
            }
        }

        listResult = ConferenceAdmin.listConferences();
        if(listResult == null || listResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get conference list failure");
            System.exit(-1);
        } else {
            System.out.println("conference list " + listResult.getResult().conferenceInfoList);
        }

        IMResult<Void> voidIMResult = ConferenceAdmin.createRoom("helloroomid", "hello room description", "123456", 9, false, 0, false, true);
        if(voidIMResult == null || voidIMResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("create conference failure");
            System.exit(-1);
        } else {
            System.out.println("create conference");
        }

        voidIMResult = ConferenceAdmin.createRoom("helloroomid2", "hello room description advanced", "123456", 20, true, 0, false, true);
        if(voidIMResult == null || voidIMResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("create conference failure");
            System.exit(-1);
        } else {
            System.out.println("create conference");
        }

        IMResult<Boolean> booleanIMResult = ConferenceAdmin.existsConferences("helloroomid2");
        if(booleanIMResult == null || booleanIMResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("exist conference failure");
            System.exit(-1);
        } else {
            System.out.println("exit conference success");
        }

        voidIMResult = ConferenceAdmin.enableRecording("helloroomid2", true, true);
        if(voidIMResult == null || voidIMResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("recording conference failure");
            System.exit(-1);
        } else {
            System.out.println("recording conference success");
        }

        listResult = ConferenceAdmin.listConferences();
        if(listResult == null || listResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("get conference list failure");
            System.exit(-1);
        } else {
            System.out.println("conference list " + listResult.getResult().conferenceInfoList);
        }

        for (PojoConferenceInfo conferenceInfo:listResult.getResult().conferenceInfoList) {
            IMResult<PojoConferenceParticipantList> listParticipantsResult = ConferenceAdmin.listParticipants(conferenceInfo.roomId, conferenceInfo.advance);
            if(listParticipantsResult == null || listParticipantsResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
                System.out.println("list participants failure");
                System.exit(-1);
            } else {
                System.out.println("list participants success");
            }
        }

        IMResult<PojoConferenceRtpForwarders> listForwarderResult = ConferenceAdmin.listRtpForwarders("6366963312");
        if(listForwarderResult == null || listForwarderResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("list rtp forward failure");
        } else {
            System.out.println("list rtp forward success");
            listForwarderResult.getResult().forwarders.forEach(rtpForwarder -> rtpForwarder.streams.forEach(rtpStream -> {
                try {
                    IMResult<Void> stopRtpForwardResult = ConferenceAdmin.stopRtpForward(listForwarderResult.getResult().roomId, rtpForwarder.publisherId, rtpStream.streamId);
                    if(stopRtpForwardResult == null || stopRtpForwardResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
                        System.out.println("rtp forward stop failure");
                    } else {
                        System.out.println("rtp forward stop success");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }

        IMResult<Void> rtpForwardResult = ConferenceAdmin.rtpForward("6366963312", "cygqmws2k", "192.168.1.81", 10000, 111, 0, 10005, 98, 0);
        if(rtpForwardResult == null || rtpForwardResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
            System.out.println("rtp forward failure");
        } else {
            System.out.println("rtp forward success");
        }
    }
}
