package models;

import java.util.UUID;

public class CacheNodeCost {
    private UUID id = UUID.randomUUID();
    private CacheNode cacheNode;
    private CacheNodeCost lastCacheNodeCostId;
    private double value;
    private Currency currency;

    public CacheNodeCost(CacheNode cacheNode, CacheNodeCost lastCacheNodeCostId, double value, Currency currency) {
        this.cacheNode = cacheNode;
        this.lastCacheNodeCostId = lastCacheNodeCostId;
        this.value = value;
        this.currency = currency;
    }

    public UUID getId() {
        return id;
    }

    public CacheNode getCacheNode() {
        return cacheNode;
    }

    public void setCacheNode(CacheNode cacheNode) {
        this.cacheNode = cacheNode;
    }

    public CacheNodeCost getLastCacheNodeCostId() {
        return lastCacheNodeCostId;
    }

    public void setLastCacheNodeCostId(CacheNodeCost lastCacheNodeCostId) {
        this.lastCacheNodeCostId = lastCacheNodeCostId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
