package models;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class PopularityLevel {
    private UUID id = UUID.randomUUID();
    private String name;
    private ArrayList<Content> contents;

    public PopularityLevel(String name) {
        this.name = name;
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

    public ArrayList<Content> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Content> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "PopularityLevel{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        PopularityLevel popularityLevel = (PopularityLevel) o;
        return getId().equals(popularityLevel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
