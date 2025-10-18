package top.foler.easybot_forge.bridge.message;

public class TextSegment extends Segment {
    public TextSegment(String text) {
        super(SegmentType.TEXT);
        this.put("text", text);
    }

    public String getText() {
        return this.get("text");
    }
}