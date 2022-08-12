package com.onemsg.protobuf.manager.web;

import java.util.Objects;

import com.onemsg.protobuf.manager.user.SimpleUserStore;

import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

public class SimpleUserAuthHandler implements UserAuthHandler {

    public static final String AUTH_HEADER_NAME = "SERVICE-AUTH-USERNAME";

    private final SimpleUserStore simpleUserStore;

    public SimpleUserAuthHandler(SimpleUserStore simpleUserStore) {
        this.simpleUserStore = Objects.requireNonNull(simpleUserStore);
    }

    @Override
    public void handle(RoutingContext ctx) {
        String name = ctx.request().getHeader(AUTH_HEADER_NAME);
        if (name == null) {
            ctx.response().setStatusCode(401).end();
        } else {
            var opional = simpleUserStore.findByName(name);
            if (opional.isEmpty()) {
                ctx.response().setStatusCode(403).end();
            } else {
                ctx.setUser(User.create(opional.get().toJson()));
                ctx.next();
            }
        }

    }

}
