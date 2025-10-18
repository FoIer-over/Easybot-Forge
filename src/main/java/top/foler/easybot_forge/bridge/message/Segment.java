package top.foler.easybot_forge.bridge.message;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public abstract class Segment {
    protected final SegmentType type;
    protected final Map<String, String> data;

    public Segment(SegmentType type) {
        this.type = type;
        this.data = new HashMap<>();
    }
    
    public SegmentType getType() {
        return type;
    }
    
    public Map<String, String> getData() {
        return data;
    }

    public void put(String key, String value) {
        this.data.put(key, value);
    }

    public String get(String key) {
        return this.data.get(key);
    }

    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.type.name().toLowerCase());
        JsonObject data = new JsonObject();
        this.data.forEach(data::addProperty);
        json.add("data", data);
        return json;
    }

    public static Class<? extends Segment> getSegmentClass(SegmentType type) {
        switch (type) {
            case TEXT:
                return TextSegment.class;
            case IMAGE:
                return ImageSegment.class;
            case FACE:
                return FaceSegment.class;
            case AT:
                return AtSegment.class;
            case FILE:
                return FileSegment.class;
            case REPLY:
                return ReplySegment.class;
            default:
                return UnknownSegment.class;
        }
    }
}