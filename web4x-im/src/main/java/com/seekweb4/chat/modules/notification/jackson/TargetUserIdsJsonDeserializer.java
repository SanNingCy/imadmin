package com.seekweb4.chat.modules.notification.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.seekweb4.chat.common.utils.StringUtils;

import java.io.IOException;

/**
 * 公告目标用户，落库统一为 JSON 数组字符串。
 * <ul>
 *   <li>JSON 数组：["u1","u2"]</li>
 *   <li>单个用户 ID 字符串：7b15ad87...（自动转为 ["7b15ad87..."]）</li>
 *   <li>已是数组形式的字符串：["a","b"]（与数组入参等价）</li>
 * </ul>
 * null、空数组、空白串 视为未指定（全体用户），存 null。
 */
public class TargetUserIdsJsonDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_NULL) {
            return null;
        }
        if (t == JsonToken.START_ARRAY) {
            ObjectCodec codec = p.getCodec();
            JsonNode node = codec.readTree(p);
            if (node == null || !node.isArray() || node.isEmpty()) {
                return null;
            }
            return node.toString();
        }
        if (t == JsonToken.VALUE_STRING) {
            String s = p.getValueAsString();
            if (StringUtils.isBlank(s)) {
                return null;
            }
            s = s.trim();
            if ("[]".equals(s) || "null".equalsIgnoreCase(s)) {
                return null;
            }
            ObjectCodec codec = p.getCodec();
            ObjectMapper mapper = codec instanceof ObjectMapper
                    ? (ObjectMapper) codec
                    : new ObjectMapper();
            if (s.startsWith("[") && s.endsWith("]")) {
                try {
                    JsonNode node = mapper.readTree(s);
                    if (node != null && node.isArray()) {
                        if (node.isEmpty()) {
                            return null;
                        }
                        return node.toString();
                    }
                } catch (Exception ignored) {
                    // 非合法 JSON 数组字符串时按「单个 ID」处理
                }
            }
            ArrayNode one = mapper.createArrayNode();
            one.add(s);
            return one.toString();
        }
        throw new com.fasterxml.jackson.databind.JsonMappingException(p, "targetUserIds 仅支持 JSON 数组、字符串或 null");
    }
}
