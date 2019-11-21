package models;

import java.util.Date;
import java.util.UUID;

public class ContentPartRequests {
    private UUID id = UUID.randomUUID();
    private ContentPart contentPart;
    private CacheNode cacheNode;
    private Date requestHitDateTime;
    private Date mainServerRequestDateTime;
    private Date mainServerResponseHitDateTime;
    private Date responseDateTime;
    private int distanceToRequester;

    public ContentPartRequests(ContentPart contentPart, CacheNode cacheNode, Date requestHitDateTime, Date mainServerRequestDateTime, Date mainServerResponseHitDateTime, Date responseDateTime, int distanceToRequester) {
        this.contentPart = contentPart;
        this.cacheNode = cacheNode;
        this.requestHitDateTime = requestHitDateTime;
        this.mainServerRequestDateTime = mainServerRequestDateTime;
        this.mainServerResponseHitDateTime = mainServerResponseHitDateTime;
        this.responseDateTime = responseDateTime;
        this.distanceToRequester = distanceToRequester;
    }

    public UUID getId() {
        return id;
    }

    public ContentPart getContentPart() {
        return contentPart;
    }

    public void setContentPart(ContentPart contentPart) {
        this.contentPart = contentPart;
    }

    public CacheNode getCacheNode() {
        return cacheNode;
    }

    public void setCacheNode(CacheNode cacheNode) {
        this.cacheNode = cacheNode;
    }

    public Date getRequestHitDateTime() {
        return requestHitDateTime;
    }

    public void setRequestHitDateTime(Date requestHitDateTime) {
        this.requestHitDateTime = requestHitDateTime;
    }

    public Date getMainServerRequestDateTime() {
        return mainServerRequestDateTime;
    }

    public void setMainServerRequestDateTime(Date mainServerRequestDateTime) {
        this.mainServerRequestDateTime = mainServerRequestDateTime;
    }

    public Date getMainServerResponseHitDateTime() {
        return mainServerResponseHitDateTime;
    }

    public void setMainServerResponseHitDateTime(Date mainServerResponseHitDateTime) {
        this.mainServerResponseHitDateTime = mainServerResponseHitDateTime;
    }

    public Date getResponseDateTime() {
        return responseDateTime;
    }

    public void setResponseDateTime(Date responseDateTime) {
        this.responseDateTime = responseDateTime;
    }

    public int getDistanceToRequester() {
        return distanceToRequester;
    }

    public void setDistanceToRequester(int distanceToRequester) {
        this.distanceToRequester = distanceToRequester;
    }
}
