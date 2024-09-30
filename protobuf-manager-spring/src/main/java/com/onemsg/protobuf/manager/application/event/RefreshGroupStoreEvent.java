package com.onemsg.protobuf.manager.application.event;

import org.springframework.context.ApplicationEvent;

public class RefreshGroupStoreEvent extends ApplicationEvent{

    public RefreshGroupStoreEvent(Object source) {
        super(source);
    }
}
