package models;

import java.util.UUID;

public class Currency {
    private UUID id = UUID.randomUUID();
    private String name;
    private String acronym;

    public Currency(String name, String acronym) {
        this.name = name;
        this.acronym = acronym;
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

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }
}
