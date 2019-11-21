package models;

import java.util.UUID;

public class Client {
    private UUID id = UUID.randomUUID();
    private Region region;
    private String name;

    public Client(Region region, String name) {
        this.region = region;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                '}';
    }
}
