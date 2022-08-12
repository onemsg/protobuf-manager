package com.onemsg.protobuf.manager.proto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import com.onemsg.protobuf.manager.exception.StatusResponseException;
import com.onemsg.protobuf.manager.model.Totalable;

public class ProtoService {

    private final SortedMap<Long, ProtoDetail> store = new ConcurrentSkipListMap<>();

    // protoDetailId -> [protoFile...]
    private final Map<Long, SortedSet<ProtoFile>> fileStore = new ConcurrentHashMap<>();

    private final AtomicLong nextProtoId = new AtomicLong(1);

    public ProtoDetail findById(long id) {
        return store.get(id);
    }

    public Totalable<ProtoDetail> find(Predicate<ProtoDetail> predicate) {
        var list = store.values().stream().filter(predicate).toList();
        return Totalable.of(list, list.size());
    }

    public Totalable<ProtoDetail> find(Predicate<ProtoDetail> predicate, int start, int limit) {
        var list = store.values().stream().filter(predicate).skip(start).limit(limit).toList();
        long total = store.values().stream().filter(predicate).count();
        return Totalable.of(list, total);
    }

    public Totalable<ProtoDetail> find(int start, int limit) {
        var list = store.values().stream().skip(start).limit(limit).toList();
        return Totalable.of(list, count());
    }

    public int count() {
        return store.size();
    }

    public ProtoDetail create(ProtoDetail protoDetail) {
        long id = nextProtoId.getAndIncrement();
        protoDetail.id = id;
        protoDetail.created = LocalDateTime.now();
        protoDetail.modified = protoDetail.created;
        store.put(id, protoDetail);
        fileStore.put(id, new TreeSet<>(ProtoFile.comparator));
        return protoDetail;
    }

    public void updateCurrentProtoFileVersion(long protoDetailId, int version) throws StatusResponseException {
        ProtoDetail protoDetail = requireExist(protoDetailId);
        if (fileStore.getOrDefault(protoDetailId, Collections.emptySortedSet()).stream()
                .noneMatch(file -> file.version() == version) ) {
            throw StatusResponseException.build(400, "version [%s] 无效", version);
        }
        protoDetail.currentVersion = version;
        protoDetail.modified = LocalDateTime.now();
    }

    public void delete(long id) {
        store.remove(id);
        fileStore.remove(id);
    }

    public ProtoFile findCurrentProtoFile(long protoId) throws StatusResponseException {
        ProtoDetail protoDetail = requireExist(protoId);
        return fileStore.getOrDefault(protoId, Collections.emptySortedSet()).stream()
            .filter(file -> file.version() == protoDetail.currentVersion )
            .findFirst()
            .orElse(null);
    }

    public ProtoFile findProtoFile(long protoId, int version) throws StatusResponseException {
        requireExist(protoId);
        return fileStore.getOrDefault(protoId, Collections.emptySortedSet()).stream()
                .filter(file -> file.version() == version)
                .findFirst()
                .orElse(null);
    }

    public List<ProtoFile> findProtoFiles(long protoId) throws StatusResponseException {
        requireExist(protoId);
        return fileStore.getOrDefault(protoId, Collections.emptySortedSet()).stream().toList();
    }

    public ProtoFile createProtoFile(long protoId, String text, String creator) throws StatusResponseException {
        ProtoDetail protoDetail = requireExist(protoId);
        synchronized (protoDetail) {
            SortedSet<ProtoFile> protoFiles = fileStore.computeIfAbsent(protoId,
                    key -> new TreeSet<>(ProtoFile.comparator));
            int version = protoFiles.isEmpty() ? 101 : protoFiles.last().version() + 1;
            ProtoFile protoFile = new ProtoFile(protoId, version,text, creator,LocalDateTime.now());
            protoFiles.add(protoFile);
            protoDetail.currentVersion = version;
            protoDetail.modified = LocalDateTime.now();
            return protoFile;
        }
    }

    public ProtoDetail requireExist(long id) throws StatusResponseException {
        var protoDetail = store.get(id);
        if (protoDetail == null) {
            throw new StatusResponseException(404, String.format("Protobuf [%s] not found", id));
        }
        return protoDetail;
    }
}
