package com.seekweb4.chat.agora.roomduration.service;

import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingCreateReq;

import java.math.BigDecimal;
import java.util.Map;

public interface IMeetingService {
     Map<String, Object> createMeeting(MeetingCreateReq req);
     Map<String, BigDecimal> calculateTokens(String ownerId, String meetingTime, String meetingMaxUsers) throws Exception;
     Map<String, BigDecimal> deductTokens(String ownerId, String meetingTime, String meetingMaxUsers, String title, String info) throws Exception;
     Map<String, Object> getGroupCurrentMeeting(String groupId);
     boolean addGroupTime(String groupId, String ownerId, String meetingTime, String roomId) throws Exception;
     Map<String, Object> getGroupActiveMeetings(String groupId) throws Exception;
     Map<String, Object> getGroupActiveMeetingsByStatus(String groupId) throws Exception;
     boolean updateMeetingSettings(com.seekweb4.chat.agora.roomduration.entity.dto.MeetingSettingsUpdateReq req) throws Exception;
     Map<String, Object> getMeetingByRoomId(String roomId) throws Exception;
}


