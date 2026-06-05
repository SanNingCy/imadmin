package cn.wildfirechat.sdk.utilities;

import cn.wildfirechat.pojos.mesh.MeshRestResult;
import cn.wildfirechat.sdk.model.IMResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ikidou.reflect.TypeBuilder;

public class JsonUtils {
    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    public static <T> IMResult<T> fromJsonObject(String content, Class<T> clazz) {
        TypeBuilder builder = TypeBuilder.newInstance(IMResult.class);
        if (!clazz.equals(Void.class)) {
            builder.addTypeParam(clazz);
        }
        return gson.fromJson(content, builder.build());
    }

    public static <T> MeshRestResult<T> fromJsonObject2(String content, Class<T> clazz) {
        TypeBuilder builder = TypeBuilder.newInstance(MeshRestResult.class);
        if (!clazz.equals(Void.class)) {
            builder.addTypeParam(clazz);
        }
        return gson.fromJson(content, builder.build());
    }

    public static <T, K> T fromJsonObject3(String content, Class<T> cls, Class<K> clazz) {
        TypeBuilder builder = TypeBuilder.newInstance(cls);
        if (!clazz.equals(Void.class)) {
            builder.addTypeParam(clazz);
        }
        return gson.fromJson(content, builder.build());
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
