package top.foler.easybot_forge.bridge.message;

public class AtSegment extends Segment {
    public AtSegment(String qq) {
        super(SegmentType.AT);
        this.put("qq", qq);
    }

    public String getQQ() {
        return this.get("qq");
    }
}