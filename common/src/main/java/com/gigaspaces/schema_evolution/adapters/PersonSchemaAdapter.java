package com.gigaspaces.schema_evolution.adapters;

import com.gigaspaces.datasource.SpaceTypeSchemaAdapter;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.schema_evolution.util.DemoUtils;

import static com.gigaspaces.schema_evolution.util.DemoUtils.createRandomString;

public class PersonSchemaAdapter implements SpaceTypeSchemaAdapter {

    public PersonSchemaAdapter() {
    }

    public SpaceDocument adaptEntry(SpaceDocument spaceDocument) {
        spaceDocument.setProperty("newField", createRandomString(8));
        spaceDocument.setProperty("calculatedField", spaceDocument.getProperty("removedField").toString() + " " + spaceDocument.getProperty("newField"));
        spaceDocument.setProperty("typeChangeField", spaceDocument.getProperty("typeChangeField").toString().length());
        spaceDocument.removeProperty("removedField");
        return spaceDocument;
    }

    public SpaceTypeDescriptor adaptTypeDescriptor(SpaceTypeDescriptor spaceTypeDescriptor) {
        return spaceTypeDescriptor;
    }

    public String getTypeName() {
        return DemoUtils.PERSON_DOCUMENT;
    }
}
