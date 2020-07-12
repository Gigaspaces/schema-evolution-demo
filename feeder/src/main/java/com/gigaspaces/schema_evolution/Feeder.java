package com.gigaspaces.schema_evolution;

import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.document.SpaceDocument;
import org.openspaces.core.GigaSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.gigaspaces.schema_evolution.util.DemoUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import java.util.concurrent.*;

import static com.gigaspaces.schema_evolution.util.DemoUtils.BATCH_SIZE;
import static com.gigaspaces.schema_evolution.util.DemoUtils.PERSON_DOCUMENT;


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

    @PostConstruct
    public void runFeed() throws Exception {
        logger.info("Initialized: connected to space {}", gigaSpace.getSpaceName());
        gigaSpace.getTypeManager().registerTypeDescriptor(DemoUtils.getPersonTypeDescriptor());
        switch (feederMode) {
            case write:
                future = executorService.scheduleAtFixedRate(new WriteTask(), 5000, 5000, TimeUnit.MILLISECONDS);
        }
    }

    @PreDestroy
    public void destroy(){
        future.cancel(false);
        future = null;
        executorService.shutdown();
    }

    private void write() {
        while (true) {
            try {
                gigaSpace.writeMultiple(createUserDocumentArray(), WriteModifiers.MEMORY_ONLY_SEARCH);
                logger.info("Feeder wrote " + batchSize + " "  + PERSON_DOCUMENT + "s.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.info("Stopped space feeder.");
                break;
            }
        }
    }

    private SpaceDocument[] createUserDocumentArray(){
        SpaceDocument[] users = new SpaceDocument[batchSize];
        for (int counter = 0; counter < batchSize; counter++) {
            users[counter] = DemoUtils.createPersonDocument();
        }
        return users;
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
            gigaSpace.writeMultiple(createUserDocumentArray());
            logger.info("Feeder wrote " + batchSize + " "  + PERSON_DOCUMENT + "s.");
        }
    }
}
