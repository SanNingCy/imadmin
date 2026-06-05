package com.seekweb4.chat.api.utils.yehuo;

import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.sdk.*;
import cn.wildfirechat.sdk.messagecontent.*;
import cn.wildfirechat.sdk.model.IMResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.image.service.WechatGroupAvatarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * im工具类
 */
@Slf4j
@Component
public class ImUtils implements InitializingBean {

    private static boolean commercialServer = false;
    private static boolean advanceVoip = false;
    //管理端口是8080
    private static String imAdminHost;
    private static String imAdminPassword;
    public static String robot_id = "robot001";

    @Value("${im.admin.host}")
    public void setImAdminHost(String imAdminHost) {
        ImUtils.imAdminHost = imAdminHost;
    }

    @Value("${im.admin.password}")
    public void setImAdminPassword(String imAdminPassword) {
        ImUtils.imAdminPassword = imAdminPassword;
    }

    /**
     * 获取token
     *
     * @param userID
     * @param clientId 客户端ID
     * @param platform 平台类型iOS 1, Android 2, Windows 3, OSX 4, WEB 5, 小程序 6，linux 7
     * @return
     */
    public static String getToken(String userID, String clientId, int platform) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<OutputGetIMTokenData> result = UserAdmin.getUserToken(userID, clientId, platform);
            if (result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && result.getResult() != null) {
                return result.getResult().getToken();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "";
    }

    /**
     * 强迫用户下线，需要用户重新获取token才能进行连接。
     * userId 必须有效；clientId 可为空：
     * - 为空：踢掉用户所有客户端
     * - 非空：只踢掉对应客户端
     */
    public static void kickoffUserClient(String userId, String clientId) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = UserAdmin.kickoffUserClient(userId, clientId);
            if (result != null) {
                log.debug("IM--强制下线: userId={}, clientId={}, code={}, msg={}",
                        userId, clientId, result.getErrorCode(), result.getMsg());
            } else {
                log.warn("IM--强制下线返回为空: userId={}, clientId={}", userId, clientId);
            }
        } catch (Exception e) {
            log.error("IM--强制下线异常: userId={}, clientId={}, err={}", userId, clientId, e.getMessage(), e);
        }
    }

    /**
     * 封禁/解封用户（让旧 imtoken/会话无法正常连接）
     *
     * @param userId 野火用户ID
     * @param block  0=解除封禁, 1=封禁
     */
    public static void updateUserBlockStatus(String userId, int block) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = UserAdmin.updateUserBlockStatus(userId, block);
            if (result != null) {
                log.debug("IM--更新封禁状态: userId={}, block={}, code={}, msg={}",
                        userId, block, result.getErrorCode(), result.getMsg());
            } else {
                log.warn("IM--更新封禁状态返回为空: userId={}, block={}", userId, block);
            }
        } catch (Exception e) {
            log.error("IM--更新封禁状态异常: userId={}, block={}, err={}",
                    userId, block, e.getMessage(), e);
        }
    }

    /**
     * 用户注册
     *
     * @param userID
     * @param acount
     * @param nickname
     * @param icon
     * @return
     */
    public static JSONObject register(String userID, String acount, String nickname, String icon) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        JSONObject obj = new JSONObject();
        try {
            InputOutputUserInfo userInfo = new InputOutputUserInfo();
            //用户ID，必须保证唯一性
            userInfo.setUserId(userID);
            //用户名，一般是用户登录帐号，也必须保证唯一性。也就是说所有用户的userId必须不能重复，所有用户的name必须不能重复，但可以同一个用户的userId和name是同一个，一般建议userId使用一个uuid，name是"微信号"且可以修改，
            if (StringUtils.isNotBlank(acount)) {
                userInfo.setName(acount);
            } else {
                userInfo.setName(userID);
            }
            userInfo.setDisplayName(nickname);
            userInfo.setPortrait(icon);
            IMResult<OutputCreateUser> result = UserAdmin.createUser(userInfo);
            log.info("IM--用户注册:{}", JSON.toJSONString(result));
            if (result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                obj = JSONObject.parseObject(JSON.toJSONString(result));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            obj.put("msg", "请求失败");
        }
        return obj;
    }

    /**
     * 修改用户资料
     *
     * @param userID
     * @param nickname
     * @param icon
     */
    public static void editUser(String userID, String nickname, String icon) {
        if (StringUtils.isBlank(userID)) {
            return;
        }
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            InputOutputUserInfo userInfo = new InputOutputUserInfo();
            //用户ID，必须保证唯一性
            userInfo.setUserId(userID);
            userInfo.setDisplayName(nickname);
            userInfo.setPortrait(icon);
            int mask = ProtoConstants.UpdateUserInfoMask.Update_User_DisplayName
                    | ProtoConstants.UpdateUserInfoMask.Update_User_Portrait;
            IMResult<Void> result = UserAdmin.updateUserInfo(userInfo, mask);
            if (result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_NOT_EXIST) {
                // 业务库有用户但 IM 未创建：先补建用户，避免 253 not exist 导致头像不生效
                InputOutputUserInfo create = new InputOutputUserInfo();
                create.setUserId(userID);
                create.setName(userID);
                create.setDisplayName(StringUtils.isNotBlank(nickname) ? nickname : userID);
                create.setPortrait(icon);
                IMResult<OutputCreateUser> created = UserAdmin.createUser(create);
                log.info("IM--用户不存在，已尝试创建: userId={}, result={}", userID, JSON.toJSONString(created));
                return;
            }
            if (result == null || result.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
                log.warn("IM--用户资料修改未成功: userId={}, result={}", userID, JSON.toJSONString(result));
            } else {
                log.info("IM--用户资料修改成功: userId={}", userID);
            }
        } catch (Exception e) {
            log.error("IM--用户资料修改异常: userId={}", userID, e);
        }
    }

    /**
     * 删除用户
     *
     * @param userID
     */
    public static void deleteUser(String userID) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            InputOutputUserInfo userInfo = new InputOutputUserInfo();
            IMResult<Void> result = UserAdmin.destroyUser(userID);
            log.debug("IM--用户销毁:" + result.getMsg());
            log.info("IM--用户销毁:{}", JSON.toJSONString(result));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 设置用户好友关系
     *
     * @param uid
     * @param uid2
     * @param state true 好友 false 陌生人
     */
    public static void addFrinend(String uid, String uid2, boolean state) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = RelationAdmin.setUserFriend(uid, uid2, state, "");
            log.info("IM--设置好友关系:" + result.getMsg());
            log.info("IM--用户销毁:{}", JSON.toJSONString(result));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 设置黑名单
     *
     * @param uid
     * @param uid2
     * @param state true 设为黑名单 false 取消黑名单
     */
    public static void addBlack(String uid, String uid2, boolean state) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = RelationAdmin.setUserBlacklist(uid, uid2, state);
            log.info("IM--设置黑名单:" + result.getMsg());
            log.info("IM--设置黑名单::{}", JSON.toJSONString(result));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 设置备注
     *
     * @param uid
     * @param uid2
     * @param alias 备注
     */
    public static void setBeizhu(String uid, String uid2, String alias) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = RelationAdmin.updateFriendAlias(uid, uid2, alias);
//            log.debug("IM--设置备注:" + result.getMsg());
            log.info("IM--设置备注:{}", JSON.toJSONString(result));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 建群
     *
     * @param userID
     * @param name
     * @param ids
     * @return
     */
//    public static String creatrGroup(String userID, String name, List<String> ids, String portrait) {
//        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
//        try {
//            PojoGroupInfo groupInfo = new PojoGroupInfo();
//            groupInfo.setOwner(userID);
//            groupInfo.setName(name);
//            groupInfo.setExtra("建群成功");
//            groupInfo.setType(2);
//            if (portrait != null) {
//                groupInfo.setPortrait(portrait);
//            }
//            List<PojoGroupMember> members = new ArrayList<>();
//            PojoGroupMember member1 = new PojoGroupMember();
//            member1.setMember_id(groupInfo.getOwner());
//            members.add(member1);
//            for (String id : ids) {
//                PojoGroupMember member2 = new PojoGroupMember();
//                member2.setMember_id(id);
//                members.add(member2);
//            }
//            IMResult<OutputCreateGroupResult> result = GroupAdmin.createGroup(groupInfo.getOwner(), groupInfo, members, null, null);
//            log.info("IM--建群:" + result.toString());
//            if (result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && result.getResult() != null) {
//                return result.getResult().getGroup_id();
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return "";
//    }

    public static String creatrGroup(String userID,String name, List<String> ids) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            PojoGroupInfo groupInfo = new PojoGroupInfo();
            groupInfo.setOwner(userID);
            groupInfo.setName(name);
            groupInfo.setExtra("建群成功");
            groupInfo.setType(2);
            //groupInfo.setPortrait("http://portrait");
            List<PojoGroupMember> members = new ArrayList<>();
            PojoGroupMember member1 = new PojoGroupMember();
            member1.setMember_id(groupInfo.getOwner());
            members.add(member1);
            for(String id:ids){
                PojoGroupMember member2 = new PojoGroupMember();
                member2.setMember_id(id);
                members.add(member2);
            }
            IMResult<OutputCreateGroupResult> result = GroupAdmin.createGroup(groupInfo.getOwner(), groupInfo, members, null, null);
            log.debug("IM--建群:" + result.toString());
            if (result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && result.getResult() != null) {
                return  result.getResult().getGroup_id();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "";
    }

    /**
     * 销毁群组
     *
     * @param userID  群主
     * @param groupId 群id
     * @return
     */
    public static void delGroup(String userID, String groupId) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = GroupAdmin.dismissGroup(userID, groupId, null, null);
            log.info("IM--解散群组:" + result.getMsg());
        } catch (Exception e) {
            log.error("IM--解散群组:{}", e.getMessage());
        }
    }

    /**
     * 修改群信息
     *
     * @param userID  群主
     * @param groupId 群id
     * @param type    修改资料类型：
     *                0	群名	string	群名称
     *                1	群头像	string	群头像链接地址
     *                2	群extra	string	群附加信息，系统不会发默认群通知，如果需要群通知，请自定义新的群通知，然后发送时带上对应消息payload
     *                3	群全局禁言	boolean	0 取消禁言；1全局禁言
     *                4	群加入方式	int	0 所有人都可以加入；1 仅群成员邀请加入；2仅群管理员或群主邀请加入。更复杂控制，请应用服务进行二次开发
     *                5	禁止私聊	boolean	0 允许私聊；1 禁止群成员私聊
     *                6	是否允许搜索群组	boolean	0 不允许；1 允许。实际上IM服务没有实现此功能，如果应用二开搜索功能可以使用此字段
     *                7	是否允许新加入成员查看历史消息	boolean	0 不允许；1 允许
     *                8	群组最大成员数	int
     * @param value   值（参考上面type对应的值类型）
     * @return
     */
    public static void editGroup(String userID, String groupId, int type, String value) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {

            IMResult<Void> result = GroupAdmin.modifyGroupInfo(userID, groupId, type, value, null, null);
            log.debug("IM--解散群组:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 转让群主
     *
     * @param userID  原群主
     * @param groupId 群id
     * @param uid2    新群主id
     * @return
     */
    public static void transferGroup(String userID, String groupId, String uid2) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {

            IMResult<Void> result = GroupAdmin.transferGroup(userID, groupId, uid2, null, null);
            log.debug("IM--转让群主:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 添加群成员
     *
     * @param userID  群主
     * @param groupId 群id
     * @param uid2    成员id
     * @return
     */
    public static void addGroupMember(String userID, String groupId, String uid2) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            PojoGroupMember m = new PojoGroupMember();
            m.setMember_id(uid2);
            //m.setAlias("hello user0");
            IMResult<Void> result = GroupAdmin.addGroupMembers(userID, groupId, Arrays.asList(m), null, null);
            log.debug("IM--添加群成员:" + result.getMsg());

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 移除群成员
     *
     * @param userID  群主
     * @param groupId 群id
     * @param ids     成员id
     * @return
     */
    public static void delGroupMember(String userID, String groupId, List<String> ids) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = GroupAdmin.kickoffGroupMembers(userID, groupId, ids, null, null);
            log.debug("IM--移除群成员:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 群成员退出
     *
     * @param userID  群主
     * @param groupId 群id
     * @return
     */
    public static void quitGroup(String userID, String groupId) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = GroupAdmin.quitGroup(userID, groupId, null, null);
            log.debug("IM--群成员退出:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 设置群管理
     *
     * @param userID  操作人
     * @param groupId 群id
     * @param ids     目标
     * @param flag    true设置  false取消
     * @return
     */
    public static void setManage(String userID, String groupId, List<String> ids, boolean flag) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = GroupAdmin.setGroupManager(userID, groupId, ids, flag, null, null);
            log.debug("IM--设置群管理:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 禁言
     *
     * @param userID  操作人
     * @param groupId 群id
     * @param ids     目标
     * @param flag    true设置  false取消
     * @return
     */
    public static void jinyan(String userID, String groupId, List<String> ids, boolean flag) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = GroupAdmin.muteGroupMemeber(userID, groupId, ids, flag, null, null);
            log.debug("IM--群成员禁言:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 发送单聊自定义消息
     *
     * @param userID 发送人
     * @param to     接收人
     * @param type   类型
     * @param info   内容
     * @return
     */
    public static void sendMsg(String userID, Integer type, String info, String to) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            Conversation conversation = new Conversation();
            conversation.setTarget(to);
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Private);

            MessagePayload payload = new MessagePayload();
            payload.setType(type);
            payload.setContent(info);
            payload.setSearchableContent(info);
            IMResult<SendMessageResult> result = MessageAdmin.sendMessage(userID, conversation, payload, null);
            log.info("IM--发消息:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 发送群自定义消息
     *
     * @param userID 发送人
     * @param to     接收群
     * @param type   类型     1001 修改公告  1004 修改发言间隔
     * @param info   内容
     * @return
     */
    public static void sendGroupMsg(String userID, Integer type, String info, String to) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            Conversation conversation = new Conversation();
            conversation.setTarget(to);
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Group);

            MessagePayload payload = new MessagePayload();
            payload.setType(type);
            payload.setContent(info);
            payload.setExtra(to);
            IMResult<SendMessageResult> result = MessageAdmin.sendMessage(userID, conversation, payload, null);
            log.info("IM--发消息:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 发送单人消息 - 文本
     *
     * @param userID 发送人
     * @param to     接收人
     * @param info   内容
     * @return
     */
    public static void sendTxtMsg(String userID, String info, String to) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            Conversation conversation = new Conversation();
            conversation.setTarget(to);
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Private);
            //测试发送文本消息
            TextMessageContent textMessageContent = new TextMessageContent(info);
            //消息转成Payload并发送
            MessagePayload payload = textMessageContent.encode();
            IMResult<SendMessageResult> result = MessageAdmin.sendMessage(userID, conversation, payload, null);
            log.debug("IM--发送单人消息 - 文本:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 发送群消息 - 文本
     *
     * @param userID 发送人
     * @param to     接收群
     * @param info   内容
     * @return
     */
    public static void sendGroupTxtMsg(String userID, String info, String to) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            Conversation conversation = new Conversation();
            conversation.setTarget(to);
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Group);
            //测试发送文本消息
            TextMessageContent textMessageContent = new TextMessageContent(info);
            //消息转成Payload并发送
            MessagePayload payload = textMessageContent.encode();
            IMResult<SendMessageResult> result = MessageAdmin.sendMessage(userID, conversation, payload, null);
            log.debug("IM--发送群消息 - 文本:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 发送群消息 - 图片
     *
     * @param userID 发送人
     * @param to     接收群
     * @param img    图片
     * @return
     */
    public static void sendGroupPicMsg(String userID, String img, String to) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            Conversation conversation = new Conversation();
            conversation.setTarget(to);
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Group);
            //测试发送图片消息
            ImageMessageContent imageMessageContent = new ImageMessageContent();
            //base64edData为图片的缩略图。缩略图的生成规则是，把图片压缩到120X120大小的方框内，45%的质量压缩为JPG格式，再把二进制做base64编码得到字符串。
            //String thumbnailBase64edData = "/9j/4AAQSkZJRgABAQAASABIAAD/4QCARXhpZgAATU0AKgAAAAgABQESAAMAAAABAAEAAAEaAAUAAAABAAAASgEbAAUAAAABAAAAUgEoAAMAAAABAAIAAIdpAAQAAAABAAAAWgAAAAAAAABIAAAAAQAAAEgAAAABAAKgAgAEAAAAAQAAAHigAwAEAAAAAQAAAFoAAAAA/+0AOFBob3Rvc2hvcCAzLjAAOEJJTQQEAAAAAAAAOEJJTQQlAAAAAAAQ1B2M2Y8AsgTpgAmY7PhCfv/AABEIAFoAeAMBIgACEQEDEQH/xAAfAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEBAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2wBDAAcHBwcHBwwHBwwRDAwMERcRERERFx4XFxcXFx4kHh4eHh4eJCQkJCQkJCQrKysrKysyMjIyMjg4ODg4ODg4ODj/2wBDAQkJCQ4NDhkNDRk7KCEoOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozv/3QAEAAj/2gAMAwEAAhEDEQA/APaXJHFIiMTk1UN0EYBuM1Qn8QW9tM0EincpUDGCDuOPz9q9B6HGa15f2mnW7T3L4VcAgDJ59hXnLeMYU1K6lChInTAbd9/bgA+xxnFcN4r8RXUlzLJGS0BJ75Iz2bHTGQOvFcBcC/EIfnBPfnj8M1xznNv3eh006EppuKbtv5Hdvrs9vMs6KrYbKHOcAd8HgcelUzqlvIrvaB1kI4IPAPr07e1cBI928aKoY+oPXJPYd6tQ3D4lliHEZB2j0Oc4HcDvzXM0x2OtTWL+C38gzrIEbIKkknd1/D1qOe9d5knjkUM/8KcA9MkkVmwyXMjIiq0QXjO3+HqDk4GKoxTRvJ5NuCvJPzZ60khHR3FyyL84Ksx3FcYB/E1VeeJyN3yHGEzznPvWfdzmVCZo2ZsDbjJwe/1FZcPmXUirGCSvPHt9aajcpJvRHQtqd3a3Xkwncy/Kq5yDwfTrWfE8qXQilAUMpKsMEdP5djVZoYkzcSswlyMdAB7EYzWpbvaGNVbDSeWVZBwG+YtnkfT8quMW9EdEMJVm2lEtLEsEbLMzAHD5XJ49OeM+9Qx3cMFwqrGSoI+UH0z361l3V3cW8+wABZBkKeMA/WrEN3Ldh0gQhQPnYAYGPeoafUwaa0Ztf2hbeaPNUovLDyxlsnnk+vepP7Vs/wC/dfmP/iayBfSLK0ICyK4xjswNLtH/AD5r/n8aQj//0MhfFmvRTGSeTfESd0bYbg9s47VgnUme7a/kIaQ5+Y8feGO2M47Vjf6NNzE5jlPOATkfWoXgs4Iw967Su3ccfpXfJpo+z+q4e13SjZ9en+f4fMnuxErKr7iGHy4OFJxyc0tqgj6Ekd17VGk9nFASuXVjwCc/zppj0xQD5RVj/dJx/OslCKd00ONGjSd4cq+b/Prckms4ZkC7CFzwQeKhOm20GDEjsfXPenokGNolIB4K54/A4zVpBCytFltuMU1CLVrGscJRcb+zX4f195XiaI27RgtEoypAPOM9MnpVeM2lirMImO48ZIJHHTNSpbEPsll+Tk4LgZqG8sozhQ/l9gG5H5io5ZRV4o4J0ORe0pU1db3t+Ww19ZeNiV71Yt9T+1ht4SMKOST1zWeq2dupimTzDkZJ5HPpjGKvSw2caNGrRqrcjHHTpmnCU+sjTCTxC1c1pui20jPHutSJB3XA6/jVKTeylmCsxOMKDn8RUls0U6hreUxue3UGnz/bIwZGI8vGX9eKqUOeF2a4uEa9Lne3lr/wSQ2yRYjmQOV4O7BK57c1Ygt1tojDbq5DHJxk4PTtWT9oEkIWRtueg/8AsupoAuLVjJBKdvBbPPT2qeeL2RMuWMVONNSt100/X+uhpu/kqSZs7BglTnb36VW/tJP+ftv++azobpLgNLPIEMnynaB/KnfZ7P8A57t/3xRGSer/AK/EwhV54qWj9bf5n//R81axtSgjt3PmpyzA9c/Sq32a1ClLmUyYHG7ggfXvWlc6TpdzF5mn+fDMvDLtOB9ST/KsyHTJYDJvJlc7SCP4evXrx0zXTOKpPkSTflr+B9Iq9KhpGF7+d/wCOz2KRbmN4z/eHzE/XpS/2SqbZS7xlhymAxGfQg9PwqxpltYIry3d2YpQxMYAyOO+MdzVxpIYUFxcfPEASHQfMx7cZ/A1rTpc8HJq/wA9v1N4PDVqfM47bK/+WqKcVvbBRBcyCQdNpGxhjpUYew09jC6GTP8AE3zHjtmoWitNTj8mFtkiZID8ED69arR6XNiQTAEZ/h5NJxlFJxXz3NFWlOalRhd9916alz7Rp1xiN1A569MVE2mxXMRFrL15IasFAQz7QWAyoB4NWbM3MNyse7y3bsew9DXOqnM7TRxLGQry5K9O99LrQltY3sZzJcBUIONhzkj/AGcVbmntZHfy4DJ0zyMCtdrYylZZSX25x+NZ8ltbo+1m+UMSoA28n1IrWcHBWjsd31OtQiqdJJxv1tf1108tikbqZYAtqG2g8/L0/Gr1i2oSBmYlUzghhz64xRJOzDy4MJs54PAFNfUGyI5Q6NkkMemMVnezvcq6oyUpTdlpbp+ZLcW9tcnYYAW9QSKrQXF3atsmh2Rjjb2+uBVo3wkjw7nevHPWmSJNdw+T5m09QT1//VWz97WO5vJRk/aU/i8ra+pVu7WOVRdFGVWxt4/DNUfsy+r/AJCrIsL1oiqygtGBmNjx1/XNR/YNS/55Q/kKxlGTd0jxq/PUlzey/D/gn//S88sNclsj9kuQJywIU7iv51jXV/fSyyyxkgnoAMenX6c061RGu0mkbIC7sepHY1s6PDo5yb9s7+4yQCevAPNaRxFWclBztY64V6tXlhz2scqI7hkVmzmQkAgZHNKUu9OnUSYZ1yMZyOR/L3ro7q7ijlaGyV1RR0PTIJHFZ4njkvGnvMyBCAVHAIGe/XP49KyV4T0fzMVeE7X+ZltqtxLObkKufu4Udj6d62LYXtzulY+WTzg8Gpr+70UIjaZA0DAjd6cjn8qrzX9vEomjbcfvFSOfpXZopNzlc9/LqijzOrUul2f4lC4aSJzDMQgBz5mDyc+1SKIVeRiRcbssc/wmq2oXr6g6pCm0McFR79OuafFCtjI5KEqw2/NjjPsP51zzmlLTUxniF7VziuaK0T7fp95p3D3ctqJo/l2AYC9PX6fSsR7mK8j8olhJ1w3qK6C2sfOh3W7Ocg9MkKR6k8dPeq7W0VoyytCd46spyGHfNVCEWvd2R6MsNNxSg7R2fX+mY9taNG5aXDKwx9OnNdINs8JiuNvuO/4dQKgmksmAVImZpFHK8EZz2H0qIR3CLksdvXHce9XC0dYu6DDYanBy9m+ZPz/MtS3NskezyVQjAD425/Ko54HnGBOEZOduOR+NUtRlYLHCz7vMcAkjn8KmTULdIvK8hQVBJfOMjPAI7k1VSveT5rbf0tP1Mq2MhSqOjLb7vyJl8mbbBMG7KWX72TVn+yLH1uv8/wDAqzrLUIr35bgqhUgA8j8eOa1dlr/z8r+b/wCFTeNk/wCvyZ0wjSrRVRJO/ff9T//T8esX8mRH3ImCRx2IH5c+lW7q4Et/GGjAdXIO0cHt+ZOaoixaGF2Ygk7QrA9CT2Her0bXQgjYzfOxZQ7YIyOgrF1Wk430Y/aSUHFPRkf2oRXIS443AsDjnrnjFUDKyTOQcru/zn3q9PNdQxRB0xJtyz4554x+VQmM3aRRwKm6RwnXHJOM9M0RSS5mb0sLKpFtbpXt3RmMJriQx243FjwijmuuufBGp2FkdRupo9iIHdM4IGOe3PpXT6T4MutEv01AulyACJUxg4PdTz+PTitbxVYX+q2P2OxUNk/MoPbtk/WvDrZsnWhCjJcvVnbTwDhTlOotVsjyUXenKMRwjJq5HfWd3GI/KQsDyc447Vm3ej3enwTSXRSOSBgpiY4c7u4HcCse2/djeTzmvoYV+b3ovQuObVIe7OKt2tY9S0/xWdBtJLGZRIm8lCMHBPBBPpXOz34NzIWJdJc7eyjvj0rj5nmGVmPOcD8PSrtusrTLbqco/wB4kY7Z/SsKWHhTqyq0lZy/EmOZ1HWvS27GvHqUVsxVY8noSpyCKmF8rMrygqr7sY9uO9VW0o2qlySVdcg9OP8ACqxtJFZY/vjt7Cumc5RWp318VjKEFJrRboZeXcM0wQj5QnHsTWvoGm2V7b3U+ond5ATCAkN8x+9x1FMk0KCUC5dyOOSMYOOw96ptZxWMhubaVjxgg4zzWNam6kG4y+5nC8NWbeKqJSXWzX9aHc6f4G0iSFdRt7wvglkVwMD2YHritT/hH0/572f/AH6X/GuDnW4uIGM0xBYY2jjPHXAGMVi/2Wf+ex/KuGOX4uV3Gtp6I7pYbl+CkmvU/9Tze11H7JJ9khjAjkI+V0ByBjHHen6guizKpH7pmb+A4UH3Hb/61T+I4400awlRQHYnLAcnhepp3jK3t4I7byI1TkfdAH8q8yUbyjJO1xuRhNdGGJrYBX77/vfgPY1Whv4LNylpAFfPzTMNzZ9s8D8qpZOY/wDfSk1XjUZwOBvrrkvae7LY1q4qpUspPbY7i08cX1vqEb3W2aKNcOq8ZBHX3NaV547lEsdzp0DRRcgmQcN9MV5Y/UfSvSL1V/4RaE46JFj9K8jE4LDQnBuG+h2YbE1ZQm3LZXOY1vVk1OV55QJZXAwemMCuVKlMsa0JBgZHvUOAQQR/DmvZo01CCjHY8+Tcm5PqVchssRk8c0PNIJBLGfp6VpWgH2u046yL/MV1Xje3t4Vt5Io1RnJ3FQATwOtVe0khxhpzIhOmanHpSaj8ssbRCQgcttYc5B/u/wD16htPD99dpHeJLHEkynapJz7A+meua9BsgPIs17GJQR7FaoQACGADoEGP++RXl1cwnyyVtjWripygoSd0ctaaBqws2v3ysq7tsHO7aDg/j7d66Oy0G0uNJY3yfvlJDKRtZeOB71068zMTz8g/9Bp12ALqBRwDt4rlqYqdRcl7ddPyLp4qUKfK9VfY8VsxqCRiYRPNbglQDkg9emDnjrxVz7U//Pg35S//ABVes4CzOq8AR9B9ajrtWN1a5Tpoykly32P/2Q==";
            imageMessageContent.setThumbnailBytes(Base64.getDecoder().decode(compressAndConvertToBase64(img, 120, 120)));
            //图片地址，发送前需要先上传到对象存储服务。
            imageMessageContent.setRemoteMediaUrl(img);
            //消息转成Payload并发送
            MessagePayload payload = imageMessageContent.encode();
            IMResult<SendMessageResult> result = MessageAdmin.sendMessage(userID, conversation, payload, null);
            log.error("IM--发送群消息 - 图片:" + result.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("IM--发送群消息 - 图片异常" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String imageUrl = "https://p26-dreamina-sign.byteimg.com/tos-cn-i-tb4s082cfz/a66bfc5342d54102a2f1d7baecd3ab57~tplv-tb4s082cfz-aigc_resize:0:0.jpg?lk3s=43402efa&x-expires=1749600000&x-signature=JPszC5RlFTwSLelqPa9Ve45h8V0=&format=.jpg"; // 替换为你的图片URL
        try {
            String base64Image = compressAndConvertToBase64(imageUrl, 120, 120);
            System.out.println("Base64 编码的图片数据:");
            System.out.println(base64Image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String compressAndConvertToBase64(String imageUrl, int width, int height) throws IOException {
        // 1. 从URL读取图片
        URL url = new URL(imageUrl);
        BufferedImage originalImage = ImageIO.read(url);

        // 2. 创建缩略图
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        // 3. 将图片转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos); // 可以根据需要更改格式，如"png"
        byte[] imageBytes = baos.toByteArray();

        // 4. 将字节数组转换为Base64字符串
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 发送群消息 - 视频
     *
     * @param userID 发送人
     * @param to     接收群
     * @param video  视频
     * @return
     */
    public static void sendGroupVideoMsg(String userID, String video, String to, Integer miao) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            Conversation conversation = new Conversation();
            conversation.setTarget(to);
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Group);
            VideoMessageContent videoMessageContent = new VideoMessageContent();
            //base64edData为视频的首帧的缩略图。缩略图的生成规则是，把图片压缩到120X120大小的方框内，45%的质量压缩为JPG格式，再把二进制做base64编码得到字符串。
//            thumbnailBase64edData = "/9j/4AAQSkZJRgABAQAASABIAAD/4QCMRXhpZgAATU0AKgAAAAgABQESAAMAAAABAAEAAAEaAAUAAAABAAAASgEbAAUAAAABAAAAUgEoAAMAAAABAAIAAIdpAAQAAAABAAAAWgAAAAAAAABIAAAAAQAAAEgAAAABAAOgAQADAAAAAQABAACgAgAEAAAAAQAAAESgAwAEAAAAAQAAAHgAAAAA/8AAEQgAeABEAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/bAEMABwcHBwcHDAcHDBIMDAwSGBISEhIYHhgYGBgYHiQeHh4eHh4kJCQkJCQkJCwsLCwsLDMzMzMzOTk5OTk5OTk5Of/bAEMBCQkJDw4PGQ4OGTwpISk8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PDw8PP/dAAQABf/aAAwDAQACEQMRAD8Ab/wjfiDRJLuGK4g/0qFH8tS4LGPIwpXHIH8684vLq8ijktHBjcqVbqCcg/0r0PUo/NSy03cvmyKQ7uoXGRlRuyeB04pdW0HT4oWZ2NyjIkjbUwdpOfkbgZ7cc8GnuTY89sbu4l0xbdU3IVXocHKnGfrUV5aG6tnv0ZVmj5kTpuA7n3qnbyT+R5S/NHC+Adoz7e/StnUAzRRXEMYQOCCBn5vf0/KptqIyY3M0aEjBIAG0c/U4q7CDav58TKwKkYbjPqCK63R9HhuLhVtVMe+BXD8hA4z7Y5xitOPwp9p0qS8mBjlwCgYjB/2s8mnyhY8z+1AnZImAx6DjI9K257a50y1hitym53EiPGwLfMMYyPSruteGZdLtRI+DKMZAxgA/StDR7lYLuMX0ZEKYxsUFs9sfWhIDotEfUpLeR78M7mTgsMErtXFbOJP7h/IV0sMqXBkk8lfvY+Zueg681PiP/njH/wB9/wD16odj/9CK3jmaRL2Z40KyFRAqZViSOOhIzjPI4rZ1HTjdXsgMEayrH5zIpZ1LnjaDwAGznjkE1y8c1/FeGaNdsRbarKTgAnIXPr06Y9+9dde6xNb+HZjBGq3kTkkELkKMkNxnjjsaEScRoulylb+Zk3iGQAx5IyrnY+AOThuDg9q6u78K20a7NMdZpgwURHIQBiPmAPH1OcVT03R73SraLWplzDB5ZcHDCRJGy5b6cHFdxqGkTwpPqNhPsaTB3OSQFXsB04GcfWrA57SrSSNLq22iGaEArsZgGB+9kr1x6V29noVu0DsG2pcKCFUDC5Azg47motEhtElaZMMq5Eci8Ixb7+OfUd66Tzk42/yP6UAeeX+mWWmSSQy7bkzRkv52VRVQZGDzycetebrd3WmWyyLJsdsbR/Ft6gj2r2e9hvZ3kkuU3QK42oeSR7Y9feuQv9B0XT3dE3PcStuXJGEz228cfyoYEujWVxf28l40WDK+7o3PyjnrWv8A2NN/zz/8db/Gm6BfXt3YkyuqmNynT0A9a2/Muf8Anqv/AHyKQz//0b1zYsl5JHb3ZlwjSorqeCueOflyTz71gXh1y+Kx3Kr5lzgMMjPlgAnIHIGB1xirmk+KoLDZPbEtKqhJAUAyB2B/mT1xmuy8OWg1yW81yVfszTARIq8FQDk5xjqcfhimhG9o1rc3WlfYNTwrJ8pAxyuOPbGKtjw9AFMKTzLbnP7oMNo5zxkEgD0HFXbKxjtxyNzL/F+A6fiK0iaoDBtbafSwIConhGBG4AUr6hgB+oH1rZjJcbzx7U7NMOVJYdD1FADbhwifdLHPAHc1w96VtL6S7vjE7LHvKgY2nsMn/Gu9ODyaz73T7a+ieK5Xcrdun8qAM3QX1a4sjcC4OJHLYVeBkDgcVtbdW/5+G/75H+FZOjXMltaG1t7MskLlAQfT681q/b7v/nyb9P8AGgD/0syDwu0sctxbkCOMDiXClj3U84Iz3zycjtXp3hZpE0eK0mV0O3zAyAlSM8DPr6iuP0DSri9Z5bqEwqu1/MLEKCxyMDk5GSRk/X1r1jTraa0tzFNJ5nJIPPA7DnmmIuIW3MW74Ip5NNzTSaYCk01j8poNJ1FMB+ahmRpYyisUJ7in0ZoETQuqJtbk/hUvmp/nFRRRRMpLAZz6VJ5MP90flSGf/9P0Xw417JLdS3SL5YYCKQDaWHOcj8sGuqzXH6RqWi2FjHaw3OQvPzbifXnrituLWNOmTzEuEx05OD+RpiNTNJmsW513TbU7XlDH/Y+b+VTQatp9xtEU6kt0BOD+RoC5pE0ZrPudSsrZS00oGOCByfyFFtqNndpuhcH2PB/I0wuX80maz5NUsIpPKkmUN6VaeaKNd7uoHqTQIvw/c/GpqzbS/s5YtyyjGTVn7Va/89V/OgZ//9TDjmwfmrVtrK6vF3wKCAcZLAfzNUI0kZtwAH4f/Wrb09HiiIA6tVNmaRQkL2zNDIcFTyB0oW5IOetW7m3kM5l6Emnx2lxIevWgmxAbot1qY3LBVb1qZtNuFGdvT3qhcX1tazJY3ThWbnOen1pNjSbJTcsetL9oY9ya1JLG5jQFuFUZzU39lTyASDB3c565qtBWNLQZs2bnn/WH+Qrb8361U0WxmS1ZT/fP8hWx9jmqR2P/1eui023AG1MVejsoQuAtWVBHBxxUoyBTJKj2cRbO2rMVrGDnFSZGOetSqcc0CK13GI7eSRVB2qW59RzXzDLqiXV3DLdqeHzKVPLZbPHpgcV9QX2Xs5xnrG38q+Qm/wBcF9TVRQmz6luNY0v+wzq6fvLYrwO/pj61V8Iaomr6WzKhRInKKCckL1Az7dK52+Wy0nRE8O2+XR03sScnLHP4Vq+AYYYNMmEXTzOn4ClYo9J06JfJbj+L+grQ8pfSqVj/AKo/739BVykI/9b0IPx6GnhmHNQHrU/8NMgeGNSBgO+ahFC0ATz4aF19VI/SvkC5Hl3Deoavr6T7jfQ/yr5Dv/8Aj6f/AHquImfR15JDP4WhvZdit5Me5247Acmuf8Da3DJfXGmIQwI3Aj1GAcVb1T/knv8A2xj/AKVwnw4/5GFv91v6UlsUz6W05/3Lf739BV/dWZpv+pb/AHv6CtCkI//Z";
            videoMessageContent.setThumbnailBytes(Base64.getDecoder().decode(compressAndConvertToBase64(video + "?vframe/jpg/offset/0/w/600/h/800", 120, 120)));
            //视频时长，单位秒
            videoMessageContent.setDuration(miao);
            //视频地址，发送前需要先上传到对象存储服务。
            videoMessageContent.setRemoteMediaUrl(video);
            //消息转成Payload并发送
            MessagePayload payload = videoMessageContent.encode();
            IMResult<SendMessageResult> result = MessageAdmin.sendMessage(userID, conversation, payload, null);
            log.debug("IM--发送群消息 - 视频:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 发送群消息 - 文件
     *
     * @param userID 发送人
     * @param to     接收群
     * @param file   文件
     * @return
     */
    public static void sendGroupFileMsg(String userID, String file, String to, String name, String size) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            Conversation conversation = new Conversation();
            conversation.setTarget(to);
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Group);
            FileMessageContent fileMessageContent = new FileMessageContent();
            //设置文件名
            fileMessageContent.setName(name);
//            //设置文件大小
            fileMessageContent.setSize(Integer.valueOf(size) * 1000);
            //设置文件链接
            fileMessageContent.setRemoteMediaUrl(file);

            //消息转成Payload并发送
            MessagePayload payload = fileMessageContent.encode();
            IMResult<SendMessageResult> result = MessageAdmin.sendMessage(userID, conversation, payload, null);
            log.debug("IM--发送群消息 - 文件:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 发送群消息 - 语音
     *
     * @param userID 发送人
     * @param to     接收群
     * @param sound  语音
     * @return
     */
    public static void sendGroupSoundMsg(String userID, String sound, String to, Integer miao) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            Conversation conversation = new Conversation();
            conversation.setTarget(to);
            conversation.setType(ProtoConstants.ConversationType.ConversationType_Group);
            //测试发送语音消息
            SoundMessageContent soundMessageContent = new SoundMessageContent();
            //语音文件时长，单位秒
            soundMessageContent.setDuration(miao);
            //语音文件格式为amr或者mp3，需要先上传到对象存储服务。
            soundMessageContent.setRemoteMediaUrl(sound);
            //消息转成Payload并发送
            MessagePayload payload = soundMessageContent.encode();
            IMResult<SendMessageResult> result = MessageAdmin.sendMessage(userID, conversation, payload, null);
            log.debug("IM--发送群消息 - 文件:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 更新消息
     *
     * @param messageUid 消息id
     * @param payload    消息负载
     * @param type       红包 2000  转账  2001
     */
    public static void updateMsg(String uid, Long messageUid, String payload, String type) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            MessagePayload payload1 = new MessagePayload();
            payload1.setPushContent(payload);
            payload1.setPushData(payload);
            payload1.setContent(payload);
            payload1.setType(Integer.valueOf(type));
            IMResult<Void> result = MessageAdmin.updateMessageContent(uid, messageUid, payload1, true);

            log.debug("IM--更新消息:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 更新消息2
     *
     * @param messageUid 消息id
     * @param context    消息内容
     */
    public static void updateMsg2(String uid, Long messageUid, String context) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            MessagePayload payload1 = new MessagePayload();
//            payload1.setPushContent(payload);
//            payload1.setPushData(payload);
            payload1.setSearchableContent(context);
            payload1.setContent(context);
            payload1.setType(1);
            IMResult<Void> result = MessageAdmin.updateMessageContent(uid, messageUid, payload1, true);

            log.debug("IM--更新消息:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 获取单条消息
     *
     * @param messageUid 消息id
     */
    public static OutputMessageData getOneMsg(Long messageUid) {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        OutputMessageData data = new OutputMessageData();
        try {
            IMResult<OutputMessageData> result = MessageAdmin.getMessage(messageUid);
            if (result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && result.getResult() != null) {
                data = result.getResult();
            }
            log.debug("IM--获取单条消息:" + result.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return data;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
    }


    public static InputOutputUserInfo getUserInfo(String userId) {
        try {
            IMResult<InputOutputUserInfo> result = UserAdmin.getUserByUserId(userId);
            if (result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                log.info("IM--获取用户信息:{}", result.getMsg());
                return result.getResult();
            } else {
                log.error("获取用户信息失败,用户id:{}", userId);
            }
        } catch (Exception e) {
            log.error("通过用户id获取野火信息异常,userId:{}", userId);
        }
        return null;
    }

    /**
     * 查询全局敏感词
     */
    public static List<String> getSensitiveWords() {
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<InputOutputSensitiveWords> result = SensitiveAdmin.getSensitives();
            if (result != null
                    && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS
                    && result.getResult() != null
                    && result.getResult().getWords() != null) {
                return result.getResult().getWords();
            }
            log.warn("IM--查询敏感词失败: {}", JSON.toJSONString(result));
        } catch (Exception e) {
            log.error("IM--查询敏感词异常", e);
        }
        return Collections.emptyList();
    }

    /**
     * 添加全局敏感词
     */
    public static boolean addSensitiveWords(List<String> words) {
        if (words == null || words.isEmpty()) {
            return true;
        }
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = SensitiveAdmin.addSensitives(words);
            boolean success = result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS;
            if (!success) {
                log.warn("IM--添加敏感词失败: {}", JSON.toJSONString(result));
            }
            return success;
        } catch (Exception e) {
            log.error("IM--添加敏感词异常", e);
            return false;
        }
    }

    /**
     * 删除全局敏感词
     */
    public static boolean removeSensitiveWords(List<String> words) {
        if (words == null || words.isEmpty()) {
            return true;
        }
        AdminConfig.initAdmin(imAdminHost, imAdminPassword);
        try {
            IMResult<Void> result = SensitiveAdmin.removeSensitives(words);
            boolean success = result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS;
            if (!success) {
                log.warn("IM--删除敏感词失败: {}", JSON.toJSONString(result));
            }
            return success;
        } catch (Exception e) {
            log.error("IM--删除敏感词异常", e);
            return false;
        }
    }
}
