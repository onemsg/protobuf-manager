package com.onemsg.protobuf.manager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.onemsg.protobuf.manager.application.ApplicationDetailService;
import com.onemsg.protobuf.manager.proto.ProtoDetail;
import com.onemsg.protobuf.manager.proto.ProtoService;
import com.onemsg.protobuf.manager.user.SimpleUser;
import com.onemsg.protobuf.manager.user.SimpleUserStore;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;

public class Launcher {
    
    private static final Logger log = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {

        configApp();

        SimpleUserStore simpleUserStore = initSimpleUserStore();
        ApplicationDetailService applicationDetailService = initApplicationDetailService();
        ProtoService protoService = initProtoService();
        
        Vertx vertx = Vertx.vertx();
        
        vertx.deployVerticle(() -> {
            WebVerticle webVerticle = new WebVerticle();
            webVerticle.simpleUserStore = simpleUserStore;
            webVerticle.applicationDetailService = applicationDetailService;
            webVerticle.protoService = protoService;
            return webVerticle;
        }, new DeploymentOptions().setInstances(3))
            .onComplete(handleDeployComplete(WebVerticle.class))
            .onSuccess( id -> {
                log.info("ProtobufManager starting success");
            });
    }

    static void configApp() {
        JavaTimeModule module = new JavaTimeModule();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(f));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(f));
        DatabindCodec.mapper().registerModule(module);
        DatabindCodec.prettyMapper().registerModule(module);
        DatabindCodec.mapper().findAndRegisterModules();
        DatabindCodec.prettyMapper().findAndRegisterModules();
    }


    static SimpleUserStore initSimpleUserStore() throws IOException {
        String file = "/data/simpleUsers.json";
        byte[] bytes = Launcher.class.getResourceAsStream(file).readAllBytes();
        Object data = Json.decodeValue(Buffer.buffer(bytes));
        if (data instanceof JsonArray list) {
            SimpleUserStore store = new SimpleUserStore();
            for (int i = 0; i < list.size(); i++) {
                store.add(SimpleUser.fromJson(list.getJsonObject(i)));
            }
            log.info("Init SimpleUserStore bean, size {}", list.size());
            return store;
        } else {
            throw new IOException("File " + file + " is not json array");
        }
    }

    static ApplicationDetailService initApplicationDetailService() throws IOException {
        String file = "/data/applicationDetails.json";
        byte[] bytes = Launcher.class.getResourceAsStream(file).readAllBytes();
        Object data = Json.decodeValue(Buffer.buffer(bytes));
        if (data instanceof JsonArray list) {
            ApplicationDetailService service = new ApplicationDetailService();
            for (int i = 0; i < list.size(); i++) {
                JsonObject e = list.getJsonObject(i);
                service.create(e.getString("group"), e.getString("name"), e.getString("intro"), e.getString("creator"));
            }
            log.info("Init ApplicationDetailService bean, size {}", list.size());
            return service;
        } else {
            throw new IOException("File " + file + " is not json array");
        }
    }

    static ProtoService initProtoService() throws IOException {
        String file = "/data/protoDetails.json";
        byte[] bytes = Launcher.class.getResourceAsStream(file).readAllBytes();
        Object data = Json.decodeValue(Buffer.buffer(bytes));
        if (data instanceof JsonArray list) {
            ProtoService service = new ProtoService();
            for (int i = 0; i < list.size(); i++) {
                JsonObject e = list.getJsonObject(i);
                ProtoDetail protoDetail = new ProtoDetail();
                protoDetail.applicationId = e.getLong("applicationId");
                protoDetail.name = e.getString("name");
                protoDetail.intro = e.getString("intro");
                protoDetail.owner = e.getString("owner");
                protoDetail.lastModifier = e.getString("lastModifier");
                service.create(protoDetail);
            }
            log.info("Init ProtoService bean, size {}", list.size());
            return service;
        } else {
            throw new IOException("File " + file + " is not json array");
        }
    }

    static Handler<AsyncResult<String>> handleDeployComplete(Class<? extends Verticle> verticleClass){
        return ar -> {
            if (ar.failed()) {
                log.error("{} deploy failed", verticleClass.getSimpleName(), ar.cause());
                log.error("ProtobufManager start to stop!");
                System.exit(-1);
            } else {
                log.info("{} deploy succeed [ID={}]", verticleClass.getSimpleName(), ar.result());
            }
        };
    }
}
