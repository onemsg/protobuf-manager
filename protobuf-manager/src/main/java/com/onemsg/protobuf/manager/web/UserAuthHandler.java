package com.onemsg.protobuf.manager.web;

import com.onemsg.protobuf.manager.user.SimpleUserStore;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface UserAuthHandler extends Handler<RoutingContext> {
    
    public static UserAuthHandler create(SimpleUserStore simpleUserStore) {
        return new SimpleUserAuthHandler(simpleUserStore);
    }

}
