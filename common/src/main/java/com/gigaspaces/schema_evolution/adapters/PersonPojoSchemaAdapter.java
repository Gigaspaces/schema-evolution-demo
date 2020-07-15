package com.gigaspaces.schema_evolution.adapters;

import com.gigaspaces.datasource.SpaceTypeSchemaAdapter;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;

import static com.gigaspaces.schema_evolution.util.DemoUtils.PERSON_DOCUMENT;
import static com.gigaspaces.schema_evolution.util.DemoUtils.createRandomString;

public class PersonPojoSchemaAdapter implements SpaceTypeSchemaAdapter {

    public PersonPojoSchemaAdapter() {
    }

    @Override
    public SpaceDocument adaptEntry(SpaceDocument spaceDocument) {
        spaceDocument.setProperty("newField", createRandomString(8));
        spaceDocument.setProperty("calculatedField", spaceDocument.getProperty("typeChangeField").toString() + " " + spaceDocument.getProperty("newField"));
        spaceDocument.setProperty("typeChangeField", spaceDocument.getProperty("typeChangeField").toString().length());
        return spaceDocument;
    }

    @Override
    public SpaceTypeDescriptor adaptTypeDescriptor(SpaceTypeDescriptor spaceTypeDescriptor) {
        try {
            Class clazz = Class.forName(spaceTypeDescriptor.getTypeName());
            return new SpaceTypeDescriptorBuilder(clazz, null).create();
        } catch (ClassNotFoundException e) {
            return spaceTypeDescriptor;
        }
    }

    @Override
    public String getTypeName() {
        return "com.gigaspaces.schema_evolution.model.Person";
    }
}
