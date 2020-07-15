package com.gigaspaces.schema_evolution.adapters;

import com.gigaspaces.datasource.SpaceTypeSchemaAdapter;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;

import static com.gigaspaces.schema_evolution.util.DemoUtils.PERSON_DOCUMENT;
import static com.gigaspaces.schema_evolution.util.DemoUtils.createRandomString;

public class PersonSchemaAdapter implements SpaceTypeSchemaAdapter {

    public PersonSchemaAdapter() {
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
        SpaceTypeDescriptorBuilder builder = new SpaceTypeDescriptorBuilder(spaceTypeDescriptor.getTypeName());
        builder.idProperty(spaceTypeDescriptor.getIdPropertyName(), spaceTypeDescriptor.isAutoGenerateId());
        builder.routingProperty(spaceTypeDescriptor.getRoutingPropertyName());
        spaceTypeDescriptor.getIndexes().values().forEach(builder::addIndex);
        builder.addFixedProperty("fixedPropertyField", Integer.class);
        return spaceTypeDescriptor;
    }

    @Override
    public String getTypeName() {
        return PERSON_DOCUMENT;
    }
}
