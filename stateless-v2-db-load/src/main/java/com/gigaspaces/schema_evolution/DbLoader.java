package com.gigaspaces.schema_evolution;

import com.gigaspaces.async.AsyncFuture;
import com.gigaspaces.datasource.SpaceDataSourceLoadRequest;
import com.gigaspaces.datasource.SpaceDataSourceLoadResult;
import com.gigaspaces.datasource.SpaceTypeSchemaAdapter;
import com.gigaspaces.persistency.MongoSpaceDataSourceFactory;
import org.openspaces.core.GigaSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Component
public class DbLoader {
    private final static long TIMEOUT = 60;
    private final static Logger logger = LoggerFactory.getLogger(DbLoader.class);
    @Resource
    private GigaSpace v2GigaSpace;
    private Collection<SpaceTypeSchemaAdapter> schemaAdapters;
    private String mongoDbIp;
    private int mongoDbPort;
    private String dbName;

    @PostConstruct
    public void loadV1Db() throws Exception {
        logger.info("Initialized: connected to space {}", v2GigaSpace.getSpaceName());
        MongoSpaceDataSourceFactory mongoSpaceDataSourceFactory =
                new MongoSpaceDataSourceFactory().host(mongoDbIp).port(mongoDbPort).db(dbName);
        SpaceDataSourceLoadRequest spaceDataSourceLoadRequest =
                new SpaceDataSourceLoadRequest(mongoSpaceDataSourceFactory, schemaAdapters);
        SpaceDataSourceLoadResult result = v2GigaSpace.asyncLoad(spaceDataSourceLoadRequest).get(TIMEOUT, TimeUnit.SECONDS);
        if(result != null)
            logger.info("Completed loading and adapting {} to space {}" ,dbName, v2GigaSpace.getSpaceName());
    }

    public Collection<SpaceTypeSchemaAdapter> getSchemaAdapters() {
        return schemaAdapters;
    }

    public DbLoader setSchemaAdapters(Collection<SpaceTypeSchemaAdapter> schemaAdapters) {
        this.schemaAdapters = schemaAdapters;
        return this;
    }

    public String getMongoDbIp() {
        return mongoDbIp;
    }

    public DbLoader setMongoDbIp(String mongoDbIp) {
        this.mongoDbIp = mongoDbIp;
        return this;
    }

    public int getMongoDbPort() {
        return mongoDbPort;
    }

    public DbLoader setMongoDbPort(int mongoDbPort) {
        this.mongoDbPort = mongoDbPort;
        return this;
    }

    public String getDbName() {
        return dbName;
    }

    public DbLoader setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }
}
