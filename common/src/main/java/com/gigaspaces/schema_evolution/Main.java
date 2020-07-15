package com.gigaspaces.schema_evolution;

import com.gigaspaces.async.AsyncFuture;
import com.gigaspaces.datasource.SpaceDataSourceLoadRequest;
import com.gigaspaces.datasource.SpaceDataSourceLoadResult;
import com.gigaspaces.persistency.MongoSpaceDataSourceFactory;
import com.gigaspaces.schema_evolution.adapters.PersonDocumentSchemaAdapter;
import com.gigaspaces.schema_evolution.adapters.PersonPojoSchemaAdapter;
import org.apache.commons.io.FilenameUtils;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;
import org.openspaces.core.GigaSpace;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    private final static long TIMEOUT = 60;
    private final static String SEPARTAOR = File.separator;
    private final static String DEPLOY_DIR = System.getProperty("java.io.tmpdir") + SEPARTAOR + "schema-evolution-demo";
    private final static String LOOKUP_GROUP = "schema-demo";
    private enum Action {undeployAll, deployV1, deployV2, undeployV1Mirror, deployV1TemporaryMirror, deployV1FinalMirror, deployFeeder, loadDB}

    private static Admin admin;
    private static GridServiceManager gsm;
    private static ProcessingUnit v1PU, v2PU, v1MirrorPU, v2MirrorPU;
    private static GigaSpace v2Gigaspace;

    public static void main(String[] args) {
        Action action = Action.valueOf(args[0]);
        String feederMode = args.length >= 2 ? args[1] : "write";
        admin = new AdminFactory().addGroup(LOOKUP_GROUP).create();
        gsm = admin.getGridServiceManagers().waitForAtLeastOne(TIMEOUT, TimeUnit.SECONDS);
        try {
            switch (action) {
                case undeployAll:
                    undeployAllServices();
                    break;
                case deployV1:
                    deployV1Service();
                    break;
                case deployV2:
                    deployV2Service();
                    break;
                case undeployV1Mirror:
                    undeployV1Mirror();
                    break;
                case deployV1TemporaryMirror:
                    undeployV1Mirror();
                    deployMirror("v1-temporary-mirror");
                    break;
                case deployV1FinalMirror:
                    undeployV1Mirror();
                    deployMirror("v1-final-mirror");
                    break;
                case deployFeeder:
                    deployFeeder(feederMode);
                    break;
                case loadDB:
                    try {
                        loadV1DbToV2();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
            admin.close();
            System.exit(1);
        }
        admin.close();
    }

    private static void undeployAllServices() {
        undeployPu("feeder");
        undeployPu("v1-service");
        undeployPu("v1-mirror");
        undeployPu("v2-service");
        undeployPu("v2-mirror");
    }

    private static void deployMirror(String jarName){
        deployPu(jarName,"v1-mirror", null);
    }

    private static void deployV1Service(){
        v1PU = deployPu("v1-service");
        v1MirrorPU = deployPu("v1-mirror");
        if(!waitForPuInstances(v1PU, 4))
            throw new RuntimeException("Failed to deploy v1 pu");
        if(!waitForPuInstances(v1MirrorPU, 1))
            throw new RuntimeException("Failed to deploy v1 mirror");
    }

    private static void deployV2Service(){
        v2PU = deployPu("v2-service");
        v2MirrorPU = deployPu("v2-mirror");
        if(!waitForPuInstances(v2PU, 4))
            throw new RuntimeException("Failed to deploy v2 pu");
        if(!waitForPuInstances(v2MirrorPU, 1))
            throw new RuntimeException("Failed to deploy v2 mirror");
        try{
            v2Gigaspace = v2PU.getSpace().getGigaSpace();
        }catch (Exception e){
            throw new RuntimeException("Failed to get v2 gigaspace", e);
        }
    }

    private static void undeployV1Mirror(){
        undeployPu("v1-mirror");
    }

    private static void deployFeeder(String feedMode){
        Map<String, String> contextProperties = new HashMap<>();
        contextProperties.put("feedMode", feedMode);
        deployPu("feeder", contextProperties);
    }

    private static void loadV1DbToV2() throws ExecutionException, InterruptedException, TimeoutException {
        v2Gigaspace = admin.getProcessingUnits().waitFor("v2-service").waitForSpace().getGigaSpace();
        if(v2Gigaspace != null) {
            AsyncFuture<SpaceDataSourceLoadResult> future = v2Gigaspace.asyncLoad(createDataLoadRequest());
            future.get(TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public static SpaceDataSourceLoadRequest createDataLoadRequest(){
        MongoSpaceDataSourceFactory mongoSpaceDataSourceFactory = new MongoSpaceDataSourceFactory().host("127.0.1.1").port(27017).db("v1-db");
        SpaceDataSourceLoadRequest spaceDataSourceLoadRequest = new SpaceDataSourceLoadRequest(mongoSpaceDataSourceFactory);
        return spaceDataSourceLoadRequest.addTypeAdapter(new PersonDocumentSchemaAdapter()).addTypeAdapter(new PersonPojoSchemaAdapter());
    }

    private static void initAdmin(){

    }

    private static ProcessingUnit deployPu(String jarName){
        return deployPu(jarName, null);
    }

    private static ProcessingUnit deployPu(String jarName, Map<String,String> contextProperties){
        return deployPu(jarName, null, contextProperties);
    }

    private static ProcessingUnit deployPu(String jarName, String puName, Map<String,String> contextProperties){
        String jarPath = FilenameUtils.normalize( DEPLOY_DIR + SEPARTAOR + jarName + ".jar");
        File puArchive = new File(jarPath);
        ProcessingUnitDeployment deployment = new ProcessingUnitDeployment(puArchive);
        if(puName != null)
            deployment.name(puName);
        if(contextProperties != null){
            for(Map.Entry<String,String> entry: contextProperties.entrySet()){
                deployment.setContextProperty(entry.getKey(), entry.getValue());
            }
        }
        return gsm.deploy(deployment, TIMEOUT, TimeUnit.SECONDS);
    }

    private static void undeployPu(String puName){
        undeployPu(admin.getProcessingUnits().waitFor(puName, TIMEOUT, TimeUnit.SECONDS));
    }

    private static void undeployPu(ProcessingUnit pu){
        if(pu != null)
            pu.undeployAndWait(TIMEOUT, TimeUnit.SECONDS);
    }

    private static boolean waitForPuInstances(ProcessingUnit pu, int numberOfInstances){
//        System.out.println("number of pu " + pu.getName() + ": " + admin.getProcessingUnits().waitFor(pu.getName()).getInstances().length);
        if(pu != null)
            return pu.waitFor(numberOfInstances, TIMEOUT, TimeUnit.SECONDS);
        return false;
    }
}
