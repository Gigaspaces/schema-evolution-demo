package com.gigaspaces.schema_evolution.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import java.util.Date;

@SpaceClass
public class Person {
    private String id;
    private Integer routing;
    private Date created;
    private Integer typeChangeField;
    private String newField;
    private String calculatedField;

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

    public Integer getTypeChangeField() {
        return typeChangeField;
    }

    public Person setTypeChangeField(Integer typeChangeField) {
        this.typeChangeField = typeChangeField;
        return this;
    }

    public String getNewField() {
        return newField;
    }

    public Person setNewField(String newField) {
        this.newField = newField;
        return this;
    }

    public String getCalculatedField() {
        return calculatedField;
    }

    public Person setCalculatedField(String calculatedField) {
        this.calculatedField = calculatedField;
        return this;
    }
}
