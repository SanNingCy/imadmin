/*
 * Copyright (c) 2022 WildFireChat. All rights reserved.
 */

package cn.wildfirechat.sdk.messagecontent;

import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.proto.ProtoConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class ArticlesMessageContent extends MessageContent {
    public Article topArticle;
    public ArrayList<Article> subArticles;

    @Override
    public int getContentType() {
        return ProtoConstants.ContentType.Articles;
    }

    @Override
    public int getPersistFlag() {
        return ProtoConstants.PersistFlag.Persist_And_Count;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encodeBase();
        payload.setSearchableContent(topArticle.title);
        JSONObject object = new JSONObject();

        object.put("top", topArticle.toJson());
        if (subArticles != null) {
            JSONArray jsonArray = new JSONArray();
            object.put("subArticles", jsonArray);
            for (Article article : subArticles) {
                jsonArray.add(article.toJson());
            }
        }
        payload.setBase64edData(Base64.getEncoder().encodeToString(object.toString().getBytes()));


        return payload;
    }

    @Override
    public void decode(MessagePayload payload) {
        try {
            JSONObject object = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(payload.getBase64edData())));
            JSONObject topObj = (JSONObject) object.get("top");
            this.topArticle = Article.fromJson(topObj);
            JSONArray jsonArray = (JSONArray) object.get("subArticles");
            if (jsonArray != null && jsonArray.size() > 0) {
                this.subArticles = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    subArticles.add(Article.fromJson((JSONObject) jsonArray.get(i)));
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public List<LinkMessageContent> toLinkMessageContent() {
        List<LinkMessageContent> contents = new ArrayList<>();
        contents.add(this.topArticle.toLinkMessageContent());
        if (this.subArticles != null) {
            for (Article article : subArticles) {
                contents.add(article.toLinkMessageContent());
            }
        }
        return contents;
    }

    public static class Article {
        public String articleId;
        public String cover;
        public String title;
        public String digest;
        public String url;
        boolean readReport;

        JSONObject toJson() {
            JSONObject obj = new JSONObject();
            obj.put("id", articleId);
            obj.put("cover", cover);
            obj.put("title", title);
            obj.put("digest", digest);
            obj.put("url", url);
            obj.put("rr", readReport);


            return obj;
        }

        static Article fromJson(JSONObject obj) {
            Article article = new Article();
            article.articleId = (String) obj.get("id");
            article.cover = (String) obj.get("cover");
            article.title = (String) obj.get("title");
            article.digest = (String) obj.get("digest");
            article.url = (String) obj.get("url");
            article.readReport = (boolean) obj.get("rr");
            return article;
        }

        public LinkMessageContent toLinkMessageContent() {
            LinkMessageContent content = new LinkMessageContent(this.title, this.url);
            content.setContentDigest(this.digest);
            content.setThumbnailUrl(this.cover);
            return content;
        }

        public Article() {
        }
    }
}
