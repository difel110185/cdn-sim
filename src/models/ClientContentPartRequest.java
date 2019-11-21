package models;

import java.util.Date;
import java.util.UUID;

public class ClientContentPartRequest {
    private UUID id = UUID.randomUUID();
    private ContentPart contentPart;
    private Date requestDateTime;
    private Date responseHitDateTime;
    private CacheNode responderCacheNodeId;
    private Client client;
    private int day;
    private int hour;

    public ClientContentPartRequest(ContentPart contentPart, Client client, int day, int hour) {
        this.contentPart = contentPart;
        this.client = client;
        this.day = day;
        this.hour = hour;
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

    public Date getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(Date requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public Date getResponseHitDateTime() {
        return responseHitDateTime;
    }

    public void setResponseHitDateTime(Date responseHitDateTime) {
        this.responseHitDateTime = responseHitDateTime;
    }

    public CacheNode getResponderCacheNodeId() {
        return responderCacheNodeId;
    }

    public void setResponderCacheNodeId(CacheNode responderCacheNodeId) {
        this.responderCacheNodeId = responderCacheNodeId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "ClientContentPartRequest{" +
                "contentPart=" + contentPart +
                ", client=" + client +
                ", day=" + day +
                ", hour=" + hour +
                '}';
    }
}
