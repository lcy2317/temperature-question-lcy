import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public class GsonUtil {

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private GsonUtil() {
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T toObject(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    public static <T> T toObject(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static <T> T toObject(String json, TypeToken<T> token) {
        return gson.fromJson(json, token.getType());
    }

    public static <T> List<T> toList(String json) {
        return toObject(json, new TypeToken<List<T>>() {
        });
    }

    public static <T> Map<String, T> toMap(String json) {
        return toObject(json, new TypeToken<Map<String, T>>() {
        });
    }
}
