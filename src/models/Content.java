package models;

import java.util.ArrayList;
import java.util.UUID;

public class Content {
    private UUID id = UUID.randomUUID();
    private String title;
    private double size;
    private double length;
    private PopularityLevel popularityLevel;
    private ArrayList<ContentPart> parts = new ArrayList<>();

    public Content(String title, PopularityLevel popularityLevel) {
        this.title = title;
        this.popularityLevel = popularityLevel;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public PopularityLevel getPopularityLevel() {
        return popularityLevel;
    }

    public void setPopularityLevel(PopularityLevel popularityLevel) {
        this.popularityLevel = popularityLevel;
    }

    public ArrayList<ContentPart> getParts() {
        return parts;
    }

    public void setParts(ArrayList<ContentPart> parts) {
        this.parts = parts;
    }

    @Override
    public String toString() {
        return "Content{" +
                "title='" + title + '\'' +
                ", popularityLevel=" + popularityLevel +
                '}';
    }
}
