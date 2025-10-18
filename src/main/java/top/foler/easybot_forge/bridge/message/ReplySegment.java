package top.foler.easybot_forge.bridge.message;

public class ReplySegment extends Segment {
    public ReplySegment(String id) {
        super(SegmentType.REPLY);
        this.put("id", id);
    }

    public String getId() {
        return this.get("id");
    }
}