package com.onemsg.protobuf.manager;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.RequestOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientMain {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        var client = vertx
                .createHttpClient(new HttpClientOptions().setProtocolVersion(HttpVersion.HTTP_2).setMaxPoolSize(5)
                    .setHttp2MaxPoolSize(7)
                );

        var reqOptions = new RequestOptions()
                .setAbsoluteURI("http://localhost:5000/sleep?time=2000")
                .setMethod(HttpMethod.GET);

        client.connectionHandler(conn -> {
            log.info("Build conn {} {}" , conn.localAddress(), conn.sslSession());
            conn.closeHandler(v -> {
                log.info("{} close", conn.localAddress());
            });
        });
        
        for (int i = 0; i < 1000; i++) {    
            final boolean lasted = i == 99;
            client.request(reqOptions)
                    .onSuccess(req -> req.send()
                            .onSuccess(res -> {
                                log.info("{} {} {}", res.version(), res.statusCode(), res.request().streamId());
                                if (lasted) {
                                    vertx.setTimer(1000, id -> {
                                                                     vertx.close()
                                        .onSuccess((v) -> {
                                            log.info("Vertx close");
                                        });
                                    });

       
                                }
                            }))
                    .onFailure(System.out::println);
        }
    }
}
