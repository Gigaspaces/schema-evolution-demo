package com.gigaspaces.schema_evolution.model;

import com.gigaspaces.annotation.pojo.*;

import java.util.Date;

@SpaceClass
public class Person {
    private String id;
    private Integer routing;
    private Date created;
    private String typeChangeField;

    public Person() {
    }
    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    public Person setId(String id) {
        this.id = id;
        return this;
    }
    @SpaceRouting
    public Integer getRouting() {
        return routing;
    }

    public Person setRouting(Integer routing) {
        this.routing = routing;
        return this;
    }
    @SpaceIndex
    public Date getCreated() {
        return created;
    }

    public Person setCreated(Date created) {
        this.created = created;
        return this;
    }

    public String getTypeChangeField() {
        return typeChangeField;
    }

    public Person setTypeChangeField(String typeChangeField) {
        this.typeChangeField = typeChangeField;
        return this;
    }
}
