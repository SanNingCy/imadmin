package com.seekweb4.chat.modules.notification.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.seekweb4.chat.common.utils.StringUtils;

import java.io.IOException;

/**
 * 将库中的 JSON 数组字符串直接以 JSON 数组写出，便于前端使用。
 */
public class TargetUserIdsJsonSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isBlank(value)) {
            gen.writeNull();
            return;
        }
        gen.writeRawValue(value.trim());
    }
}
