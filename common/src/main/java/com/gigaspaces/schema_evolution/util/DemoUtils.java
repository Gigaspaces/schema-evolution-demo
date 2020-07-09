package com.gigaspaces.schema_evolution.util;

import com.gigaspaces.datasource.SpaceDataSourceLoadRequest;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;
import com.gigaspaces.metadata.index.SpaceIndexType;

import java.util.Date;
import java.util.Random;

public class DemoUtils {
    public final static String PERSON_DOCUMENT = "PersonDocument";
    public final static int BATCH_SIZE = 100;
    private final static Random random = new Random();

    public static SpaceTypeDescriptor getPersonTypeDescriptor(){
        return new SpaceTypeDescriptorBuilder(PERSON_DOCUMENT)
                .idProperty("userId", false)
                .routingProperty("routing")
                .addPropertyIndex("created", SpaceIndexType.EQUAL)
                .create();
    }

    public static SpaceTypeDescriptor getOrderTypeDescriptor(){
        return null;
    }

    public static SpaceDocument createPersonDocument(){
        SpaceDocument result = new SpaceDocument().setTypeName(PERSON_DOCUMENT);
        int id = random.nextInt();
        result.setProperty("userId", id);
        result.setProperty("routing", id);
        result.setProperty("created", new Date(System.currentTimeMillis()));
        return result;
    }

    public static SpaceDocument createOrderDocument(){
        return null;
    }

    public static SpaceDataSourceLoadRequest createDataLoadRequest(){
        return null;
    }


}
