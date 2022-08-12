package com.onemsg.protobuf.manager.application;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class ApplicationDetailRepository {
    
    private static final ConcurrentNavigableMap<Long, ApplicationDetail> store = new ConcurrentSkipListMap<>();
    private static final AtomicLong NEXT_ID = new AtomicLong(1);

    public void save(ApplicationDetail applicationDetail) {
        applicationDetail.id = NEXT_ID.getAndIncrement();
        store.put(applicationDetail.id, applicationDetail);
    }

    public void update(ApplicationDetail applicationDetail) {
        if (store.containsKey(applicationDetail.id)) {
            store.put(applicationDetail.id, applicationDetail);
        }
    }

    public void remove(long id) {
        store.remove(id);
    }

    public Optional<ApplicationDetail> find(long id) {
        return Optional.ofNullable(store.get(id));
    }

    public int count() {
        return store.size();
    }

    public List<ApplicationDetail> find(int skip, int limit) {
        return store.values().stream().skip(skip).limit(limit).toList();
    }


}
