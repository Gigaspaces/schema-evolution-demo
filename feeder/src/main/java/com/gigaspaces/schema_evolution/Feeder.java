package com.gigaspaces.schema_evolution;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.schema_evolution.model.Person;
import com.gigaspaces.schema_evolution.util.FeederUtils;
import org.openspaces.core.GigaSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gigaspaces.schema_evolution.util.DemoUtils.PERSON_DOCUMENT;
import static com.gigaspaces.schema_evolution.util.FeederUtils.*;


@Component
public class Feeder {
    private final static Logger logger = LoggerFactory.getLogger(Feeder.class);

    @Resource
    private GigaSpace gigaSpace;
    private int batchSize = BATCH_SIZE;
    private EntryType entryType;
    private FeederMode feederMode;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> future;
    private AtomicInteger batchCounter = new AtomicInteger(0);

    @PostConstruct
    public void runFeed() throws Exception {
        logger.info("Initialized: connected to space {}", gigaSpace.getSpaceName());
//        gigaSpace.getTypeManager().registerTypeDescriptor(getV1PersonTypeDescriptor());
        future = executorService.scheduleAtFixedRate(new WriteTask(), 5000, 5000, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy(){
        future.cancel(false);
        future = null;
        executorService.shutdown();
    }

    private SpaceDocument[] createPersonDocumentArray(){
        SpaceDocument[] users = new SpaceDocument[batchSize];
        for (int counter = 0; counter < batchSize; counter++) {
            users[counter] = createV1PersonDocument(batchCounter.get() * batchSize + counter);
        }
        return users;
    }

    private Person[] createPersonArray(){
        Person[] people = new Person[batchSize];
        for (int counter = 0; counter < batchSize; counter++) {
            people[counter] = FeederUtils.createV1PersonPojo(batchCounter.get() * batchSize + counter);
        }
        return people;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }

    public void setFeederMode(String feederMode){
        this.feederMode = FeederMode.valueOf(feederMode);
    }

    public enum EntryType {
        POJO,
        DOCUMENT
    }

    private class WriteTask implements Runnable{
        @Override
        public void run() {
//            SpaceDocument[] persons = createPersonDocumentArray();
            Person[] persons = createPersonArray();
            switch (feederMode) {
                case write:
                    gigaSpace.writeMultiple(persons);
                    logger.info("Feeder wrote " + batchSize + " "  + PERSON_DOCUMENT + "s.");
                    break;
                case update:
                    gigaSpace.writeMultiple(persons);
                    logger.info("Feeder wrote " + batchSize + " "  + PERSON_DOCUMENT + "s.");
//                    Arrays.stream(persons).forEach(person -> person.setProperty("removedField", person.getProperty("removedField") + "_MODIFIED"));
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    persons = Arrays.stream(gigaSpace.readMultiple(new Person())).map(person -> person.setTypeChangeField(person.getTypeChangeField().toUpperCase())).toArray(Person[]::new);
                    gigaSpace.writeMultiple(persons);
                    logger.info("Feeder updated " + batchSize + " "  + PERSON_DOCUMENT + "s.");
                    break;
                case take:
                    gigaSpace.writeMultiple(persons);
                    logger.info("Feeder wrote " + batchSize + " "  + PERSON_DOCUMENT + "s.");
                    logger.info("Feeder removed " + gigaSpace.takeMultiple(null, 50).length + " "  + PERSON_DOCUMENT + "s from space.");
                    break;
            }
            batchCounter.incrementAndGet();
        }
    }
}
