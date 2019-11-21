package models;

import java.util.ArrayList;
import java.util.UUID;

public class Region {
    private UUID id = UUID.randomUUID();
    private String name;
    private int timezoneOffset;
    private ArrayList<Client> clients = new ArrayList<>();
    private CacheNode cacheNode;

    public Region(String name, int timezoneOffset) {
        this.name = name;
        this.timezoneOffset = timezoneOffset;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public CacheNode getCacheNode() {
        return cacheNode;
    }

    public void setCacheNode(CacheNode cacheNode) {
        this.cacheNode = cacheNode;
    }

    @Override
    public String toString() {
        return "Region{" +
                "name='" + name + '\'' +
                ", timezoneOffset=" + timezoneOffset +
                '}';
    }
}
