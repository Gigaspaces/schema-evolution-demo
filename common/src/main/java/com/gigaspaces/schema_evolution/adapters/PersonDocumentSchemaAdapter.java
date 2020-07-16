package com.gigaspaces.schema_evolution.adapters;

import com.gigaspaces.datasource.SpaceTypeSchemaAdapter;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;

import static com.gigaspaces.schema_evolution.util.DemoUtils.PERSON_DOCUMENT;
import static com.gigaspaces.schema_evolution.util.DemoUtils.createRandomString;

public class PersonDocumentSchemaAdapter implements SpaceTypeSchemaAdapter {

    public PersonDocumentSchemaAdapter() {
    }

    @Override
    public SpaceDocument adaptEntry(SpaceDocument spaceDocument) {
        spaceDocument.setProperty("newField", createRandomString(8));
        spaceDocument.setProperty("calculatedField", spaceDocument.getProperty("removedField").toString() + " " + spaceDocument.getProperty("newField"));
        spaceDocument.setProperty("typeChangeField", spaceDocument.getProperty("typeChangeField").toString().length());
        spaceDocument.setProperty("fixedPropertyField", spaceDocument.getProperty("fixedPropertyField").toString().length());
        spaceDocument.removeProperty("removedField");
        return spaceDocument;
    }

    @Override
    public SpaceTypeDescriptor adaptTypeDescriptor(SpaceTypeDescriptor spaceTypeDescriptor) {
        return new SpaceTypeDescriptorBuilder(PERSON_DOCUMENT)
                .idProperty("id", false)
                .routingProperty("routing")
                .addPropertyIndex("created", SpaceIndexType.EQUAL)
                .addFixedProperty("fixedPropertyField", Integer.class)
                .create();
    }

    @Override
    public String getTypeName() {
        return PERSON_DOCUMENT;
    }
}
