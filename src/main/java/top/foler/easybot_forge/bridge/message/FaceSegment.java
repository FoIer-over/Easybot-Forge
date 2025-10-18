package top.foler.easybot_forge.bridge.message;

public class FaceSegment extends Segment {
    public FaceSegment(String id) {
        super(SegmentType.FACE);
        this.put("id", id);
    }

    public String getId() {
        return this.get("id");
    }
}