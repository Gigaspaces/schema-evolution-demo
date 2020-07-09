package com.gigaspaces.schema_evolution.adapters;

import com.gigaspaces.datasource.SpaceTypeSchemaAdapter;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.schema_evolution.util.DemoUtils;

public class PersonSchemaAdapter implements SpaceTypeSchemaAdapter {

    public PersonSchemaAdapter() {
    }

    public SpaceDocument adaptEntry(SpaceDocument spaceDocument) {
        return spaceDocument;
    }

    public SpaceTypeDescriptor adaptTypeDescriptor(SpaceTypeDescriptor spaceTypeDescriptor) {
        return spaceTypeDescriptor;
    }

    public String getTypeName() {
        return DemoUtils.PERSON_DOCUMENT;
    }
}
