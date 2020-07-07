package com.gigaspaces.schema_evolution.util;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;
import com.gigaspaces.schema_evolution.model.Person;

import java.util.Date;
import java.util.Random;

import static com.gigaspaces.schema_evolution.util.DemoUtils.PERSON_DOCUMENT;
import static com.gigaspaces.schema_evolution.util.DemoUtils.createRandomString;

public class FeederUtils {

    public final static int BATCH_SIZE = 100;
    private final static Random random = new Random();

    public static SpaceTypeDescriptor getV1PersonTypeDescriptor(){
        return new SpaceTypeDescriptorBuilder(PERSON_DOCUMENT)
                .idProperty("id", false)
                .routingProperty("routing")
                .addPropertyIndex("created", SpaceIndexType.EQUAL)
                .addFixedProperty("fixedPropertyField", String.class)
                .create();
    }

    public static SpaceDocument createV1PersonDocument(int id){
        SpaceDocument result = new SpaceDocument().setTypeName(PERSON_DOCUMENT);
        result.setProperty("id", id);
        result.setProperty("routing", getNextInt(100000));
        result.setProperty("created", new Date(System.currentTimeMillis()));
        result.setProperty("removedField", createRandomString(getNextInt(10)));
        result.setProperty("typeChangeField", createRandomString(getNextInt(10)));
        result.setProperty("fixedPropertyField", createRandomString(getNextInt(10)));
        return result;
    }

    public static Person createV1PersonPojo(int id){
        return new Person()
                .setRouting(getNextInt(100000))
                .setCreated(new Date(System.currentTimeMillis()))
                .setTypeChangeField(createRandomString(getNextInt(10)));
    }

    private static int getNextInt(int bound){
        return Math.abs(random.nextInt(bound)) + 1;
    }
}
