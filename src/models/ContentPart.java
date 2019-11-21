package models;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ContentPart {
    private UUID id = UUID.randomUUID();
    private Content content;
    private int sequenceNumber;
    private double size;
    private double length;
    private float cacheHitRatio;
    private ArrayList<CacheNode> distributedCacheNodes;
    private ArrayList<ClientContentPartRequest> requests;
    public static final float DEFAULT_CONTENT_PART_SIZE_IN_GB = 2 / 1024f;

    public ContentPart(Content content, int sequenceNumber) {
        this.content = content;
        this.sequenceNumber = sequenceNumber;
    }

    public UUID getId() {
        return id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public float getCacheHitRatio() {
        return cacheHitRatio;
    }

    public void setCacheHitRatio(float cacheHitRatio) {
        this.cacheHitRatio = cacheHitRatio;
    }

    public ArrayList<CacheNode> getDistributedCacheNodes() {
        return distributedCacheNodes;
    }

    public void setDistributedCacheNodes(ArrayList<CacheNode> distributedCacheNodes) {
        this.distributedCacheNodes = distributedCacheNodes;
    }

    public ArrayList<ClientContentPartRequest> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<ClientContentPartRequest> requests) {
        this.requests = requests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ContentPart that = (ContentPart) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "ContentPart{" +
                "content=" + content +
                ", sequenceNumber=" + sequenceNumber +
                '}';
    }
}
