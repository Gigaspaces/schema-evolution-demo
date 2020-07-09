package com.gigaspaces.schema_evolution;

import com.gigaspaces.schema_evolution.util.DemoUtils;
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

public class Main {
    private final static String SEPARTAOR = File.separator;
    private final static String DEPLOY_DIR = System.getProperty("java.io.tmpdir") + SEPARTAOR + "schema-evolution-demo";
    private final static String LOOKUP_GROUP = "schema-demo";
    private enum Action {deployAll, undeployAll, deployV1, deployV2, undeployV1Mirror, deployV1NewMirror, deployFeeder, loadDB}

    private static Admin admin;
    private static ProcessingUnit v1PU, v2PU, v1MirrorPU, v2MirrorPU, feeder;
    private static GigaSpace v1Gigaspace, v2Gigaspace;

    public static void main(String[] args) {
        Action action = Action.valueOf(args[0]);
        FeederMode feederMode = args.length >= 2 ? FeederMode.valueOf(args[1]) : FeederMode.write;
        switch (action) {
            case deployAll:
                deployV1Service();
                deployV2Service();
                break;
            case undeployAll:
                undeploy();
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
            case deployV1NewMirror:
                deployV1NewMirror();
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
                }
                break;
        }
        admin.close();
    }

    private static void undeploy() {
        initAdmin();
        admin.getProcessingUnits().waitFor("v1-service").undeployAndWait();
        admin.getProcessingUnits().waitFor("v2-service").undeployAndWait();
        admin.getProcessingUnits().waitFor("v1-mirror").undeployAndWait();
        admin.getProcessingUnits().waitFor("v2-mirror").undeployAndWait();
        admin.getProcessingUnits().waitFor("feeder").undeployAndWait();
    }

    private static void deployV1Service(){
        initAdmin();
        v1PU = deployPu("v1-service");
        v1MirrorPU = deployPu("v1-mirror");
        v1PU.waitFor(4);
        v1MirrorPU.waitFor(1);
        v1Gigaspace = v1PU.getSpace().getGigaSpace();
    }

    private static void deployV2Service(){
        v2PU = deployPu("v2-service");
        v2MirrorPU = deployPu("v2-mirror");
        v2PU.waitFor(4);
        v2MirrorPU.waitFor(1);
        v2Gigaspace = v2PU.getSpace().getGigaSpace();
    }

    private static void undeployV1Mirror(){
        if(v1MirrorPU != null)
            v1MirrorPU.undeployAndWait();
    }

    private static void deployV1NewMirror(){
        v1MirrorPU = deployPu("v1-new-mirror");
        v1MirrorPU.waitFor(1);
    }

    private static void deployFeeder(FeederMode feedMode){
        Map<String, String> contextProperties = new HashMap<>();
        contextProperties.put("feedMode", feedMode.toString());
        feeder = deployPu("feeder", contextProperties);
    }

    private static void undeployFeeder(){
        if(feeder != null)
            feeder.undeployAndWait();
    }

    private static void loadV1DbToV2() throws ExecutionException, InterruptedException {
        v2Gigaspace.asyncLoad(DemoUtils.createDataLoadRequest()).get();
    }

    private static void initAdmin(){
        if(admin == null){
            admin = new AdminFactory().addGroup(LOOKUP_GROUP).create();
            admin.getGridServiceManagers().waitForAtLeastOne(60, TimeUnit.SECONDS);
        }
    }

    private static ProcessingUnit deployPu(String puName){
        return deployPu(puName, null);
    }

    private static ProcessingUnit deployPu(String puName, Map<String,String> contextProperties){
        initAdmin();
        String jarPath = FilenameUtils.normalize( DEPLOY_DIR + SEPARTAOR + puName + ".jar");
        File puArchive = new File(jarPath);
        ProcessingUnitDeployment deployment = new ProcessingUnitDeployment(puArchive);
        if(contextProperties != null){
            for(Map.Entry<String,String> entry: contextProperties.entrySet()){
                deployment.setContextProperty(entry.getKey(), entry.getValue());
            }
        }
        return admin.getGridServiceManagers().deploy(deployment);
    }
}
