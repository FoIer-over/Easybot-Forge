package top.foler.easybot_forge.bridge.message;

public class ImageSegment extends Segment {
    public ImageSegment(String url) {
        super(SegmentType.IMAGE);
        this.put("url", url);
    }

    public String getUrl() {
        return this.get("url");
    }
}