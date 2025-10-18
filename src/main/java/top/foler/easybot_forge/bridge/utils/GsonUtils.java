package top.foler.easybot_forge.bridge.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import top.foler.easybot_forge.bridge.adapter.OpCodeAdapter;
import top.foler.easybot_forge.bridge.OpCode;

public class GsonUtils {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OpCode.class, new OpCodeAdapter())
            .create();

    public static Gson getGson() {
        return gson;
    }
}