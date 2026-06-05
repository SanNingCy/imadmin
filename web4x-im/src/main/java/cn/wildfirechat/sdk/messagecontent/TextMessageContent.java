package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.sdk.model.QuoteInfo;
import org.apache.http.util.TextUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TextMessageContent extends MessageContent {
    private String text;
    private QuoteInfo quoteInfo;

    //必须有个空的构造函数
    public TextMessageContent() {
    }

    public TextMessageContent(String text) {
        this.text = text;
    }

    public TextMessageContent text(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuoteInfo getQuoteInfo() {
        return quoteInfo;
    }

    public void setQuoteInfo(QuoteInfo quoteInfo) {
        this.quoteInfo = quoteInfo;
    }

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Text;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = encodeBase();
        payload.setSearchableContent(text);
        if(quoteInfo != null) {
            payload.setBase64edData(Base64.getEncoder().encodeToString(quoteInfo.encode().toJSONString().getBytes(StandardCharsets.UTF_8)));
        }
        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        text = payload.getSearchableContent();
        if(!TextUtils.isEmpty(payload.getBase64edData())) {
            String jsonStr = new String(Base64.getDecoder().decode(payload.getBase64edData()), StandardCharsets.UTF_8);
            try {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonStr);
                quoteInfo = new QuoteInfo();
                quoteInfo.decode(jsonObject);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
