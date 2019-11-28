package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CacheNode {
    private UUID id = UUID.randomUUID();
    private String location;
    private String ip;
    private String domainName;
    private int distanceToMainServer;
    private double capacity;
    private Region region;
    private int detaultTtl;
    private ArrayList<ClientContentPartRequest> requests = new ArrayList<>();
    private ArrayList<ContentPart> contentParts = new ArrayList<>();
    private ArrayList<CacheNodeCost> costs = new ArrayList<>();
    private ArrayList<ContentPart>[] ttlTable;
    private HashMap<String, Float> costToClientTable = new HashMap<>();
    private float costFromServer = 0.020f;
    private float requestCost = 0.0075f;
    private long requestCount = 0;
    private long requestsToServerCount = 0;
    private double storageHostingCost = 0;
    private static final double STORAGE_PRICE_PER_GB_PER_HOUR = (0.022 / 30) / 24;
    private static final double STORAGE_PRICE_PER_1000_REQUEST = 0.005;

    public CacheNode(Region region, int defaultTtl) {
        this.region = region;
        this.detaultTtl = defaultTtl;

        this.ttlTable = new ArrayList[defaultTtl];
        for (int i = 0; i < defaultTtl; i++)
            this.ttlTable[i] = new ArrayList<>();

        this.costToClientTable.put("First 10 TB", 0.085f);
        this.costToClientTable.put("Next 40 TB", 0.080f);
        this.costToClientTable.put("Next 100 TB", 0.060f);
        this.costToClientTable.put("Next 350 TB", 0.040f);
        this.costToClientTable.put("Next 500 TB", 0.030f);
        this.costToClientTable.put("Above 1000 TB", 0.025f);
    }

    public UUID getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public int getDistanceToMainServer() {
        return distanceToMainServer;
    }

    public void setDistanceToMainServer(int distanceToMainServer) {
        this.distanceToMainServer = distanceToMainServer;
    }

    public ArrayList<ClientContentPartRequest> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<ClientContentPartRequest> requests) {
        this.requests = requests;
    }

    public ArrayList<ContentPart> getContentParts() {
        return contentParts;
    }

    public void setContentParts(ArrayList<ContentPart> contentParts) {
        this.contentParts = contentParts;
    }

    public ArrayList<CacheNodeCost> getCosts() {
        return costs;
    }

    public void setCosts(ArrayList<CacheNodeCost> costs) {
        this.costs = costs;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public int getDetaultTtl() {
        return detaultTtl;
    }

    public void setDetaultTtl(int detaultTtl) {
        this.detaultTtl = detaultTtl;
    }

    public ArrayList[] getTtlTable() {
        return ttlTable;
    }

    public void setTtlTable(ArrayList[] ttlTable) {
        this.ttlTable = ttlTable;
    }

    public void cacheContentPart (ContentPart contentPart) {
        this.contentParts.add(contentPart);

        this.requestsToServerCount++;

        renewTtl(contentPart);
    }

    public void renewTtl (ContentPart contentPart) {
        if (ttlTable[detaultTtl-1] == null)
            ttlTable[detaultTtl-1] = new ArrayList<>();

        ttlTable[detaultTtl-1].add(contentPart);
    }

    public void decreaseCachedContentPartsTtl() {
        if (ttlTable[0] != null)
            for (ContentPart contentPart : ttlTable[0])
                contentParts.remove(contentPart);

        for (int i = 0; i < detaultTtl-1; i++)
            ttlTable[i] = ttlTable[i+1];

        ttlTable[detaultTtl-1] = new ArrayList<>();

        updateStorageHostingCost();
    }

    private void updateStorageHostingCost() {
        for (ArrayList<ContentPart> cachedParts : ttlTable)
            storageHostingCost += cachedParts.size() * ContentPart.DEFAULT_CONTENT_PART_SIZE_IN_GB * STORAGE_PRICE_PER_GB_PER_HOUR;
    }

    public void increaseRequestCount() {
        this.requestCount++;
    }

    public double calculateCost() {
        double transferToClientsCost = calculateTransferToCLientsCost();
        double transferFromServerCost = calculateTransferFromServerCost();
        double requestTotalCost = calculateRequestTotalCost();
        double storageRequestTotalCost = STORAGE_PRICE_PER_1000_REQUEST * requestCount / 1000;

        return requestTotalCost + transferToClientsCost + transferFromServerCost + storageRequestTotalCost + storageHostingCost;
    }

    private float calculateRequestTotalCost() {
        return (requestCount / 10000f) * requestCost;
    }

    private double calculateTransferToCLientsCost() {
        double cost = 0;

        double totalSizeTransferredInTb = requestCount * ContentPart.DEFAULT_CONTENT_PART_SIZE_IN_GB / 1024d;

        cost += costToClientTable.get("First 10 TB") * Math.max(Math.min(totalSizeTransferredInTb, 10), 0);
        cost += costToClientTable.get("Next 40 TB") * Math.max(Math.min(totalSizeTransferredInTb - 10, 40), 0);
        cost += costToClientTable.get("Next 100 TB") * Math.max(Math.min(totalSizeTransferredInTb - (40 + 10), 100), 0);
        cost += costToClientTable.get("Next 350 TB") * Math.max(Math.min(totalSizeTransferredInTb - (100 + 40 + 10), 350), 0);
        cost += costToClientTable.get("Next 500 TB") * Math.max(Math.min(totalSizeTransferredInTb - (350 + 100 + 40 + 10), 500), 0);
        cost += costToClientTable.get("Above 1000 TB") * Math.max(totalSizeTransferredInTb - (500 + 350 + 100 + 40 + 10), 0);

        return cost;
    }

    private double calculateTransferFromServerCost() {
        return costFromServer * requestsToServerCount * ContentPart.DEFAULT_CONTENT_PART_SIZE_IN_GB;
    }

    @Override
    public String toString() {
        return "CacheNode{" +
                "region=" + region +
                '}';
    }
}
