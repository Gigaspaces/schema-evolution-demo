package com.gigaspaces.schema_evolution.model;

import com.gigaspaces.annotation.pojo.*;

import java.util.Date;

@SpaceClass
public class Person {
    private Integer id;
    private Integer routing;
    private Date created;
    private String removedField;
    private String typeChangeField;

    public Person() {
    }
    @SpaceId
    public Integer getId() {
        return id;
    }

    public Person setId(Integer id) {
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

    public String getRemovedField() {
        return removedField;
    }

    public Person setRemovedField(String removedField) {
        this.removedField = removedField;
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
