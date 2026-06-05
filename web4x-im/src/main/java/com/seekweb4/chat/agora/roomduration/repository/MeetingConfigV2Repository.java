package com.seekweb4.chat.agora.roomduration.repository;

import com.seekweb4.chat.agora.roomduration.entity.MeetingConfigV2Entity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MeetingConfigV2Repository extends MongoRepository<MeetingConfigV2Entity, String> {
}


