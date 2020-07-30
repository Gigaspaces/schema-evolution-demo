package com.gigaspaces.schema_evolution.adapters;

import com.gigaspaces.datasource.SpaceTypeSchemaAdapter;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;

import java.util.Date;

import static com.gigaspaces.schema_evolution.util.DemoUtils.PERSON_DOCUMENT;
import static com.gigaspaces.schema_evolution.util.DemoUtils.createRandomString;

public class PersonPojoSchemaAdapter implements SpaceTypeSchemaAdapter {

    public PersonPojoSchemaAdapter() {
    }

    @Override
    public SpaceDocument adaptEntry(SpaceDocument spaceDocument) {
        spaceDocument.setProperty("newField", createRandomString(8));
        spaceDocument.setProperty("calculatedField", spaceDocument.<String>getProperty("typeChangeField") + " " + spaceDocument.getProperty("newField"));
        spaceDocument.setProperty("typeChangeField", spaceDocument.<String>getProperty("typeChangeField").length());
        return spaceDocument;
    }

    @Override
    public SpaceTypeDescriptor adaptTypeDescriptor(SpaceTypeDescriptor spaceTypeDescriptor) {
        return new SpaceTypeDescriptorBuilder(spaceTypeDescriptor.getTypeName())
                .idProperty("id", true)
                .routingProperty("routing")
                .addPropertyIndex("created", SpaceIndexType.EQUAL)
                .addFixedProperty("id", String.class)
                .addFixedProperty("routing", Integer.class)
                .addFixedProperty("created", Date.class)
                .addFixedProperty("typeChangeField", Integer.class)
                .addFixedProperty("newField", String.class)
                .addFixedProperty("calculatedField", String.class)
                .create();
    }

    @Override
    public String getTypeName() {
        return "com.gigaspaces.schema_evolution.model.Person";
    }
}
