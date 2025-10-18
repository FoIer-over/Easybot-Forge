package top.foler.easybot_forge.bridge.message;

public class FileSegment extends Segment {
    public FileSegment(String id, String name, String size) {
        super(SegmentType.FILE);
        this.put("id", id);
        this.put("name", name);
        this.put("size", size);
    }

    public String getId() {
        return this.get("id");
    }

    public String getName() {
        return this.get("name");
    }

    public String getSize() {
        return this.get("size");
    }
}