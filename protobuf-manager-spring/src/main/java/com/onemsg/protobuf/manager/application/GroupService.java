package com.onemsg.protobuf.manager.application;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onemsg.protobuf.manager.application.event.RefreshGroupStoreEvent;
import com.onemsg.protobuf.manager.application.model.GroupEntity;
import com.onemsg.protobuf.manager.application.model.GroupNameVo;
import com.onemsg.protobuf.manager.exception.DataModelResponseException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class GroupService implements InitializingBean {

    private final GroupRepository groupRepository;
    private final ApplicationEventPublisher publisher;

    private final AtomicReference<Map<Integer, GroupEntity>> groupStore = new AtomicReference<>(
            Collections.emptyMap());

    @EventListener(RefreshGroupStoreEvent.class)
    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void refreshStore() {
        List<GroupEntity> groups = groupRepository.findAll();
        Map<Integer, GroupEntity> groupMap = groups.stream()
                .collect(Collectors.toMap(GroupEntity::id, Function.identity()));
        groupStore.set(groupMap);
    }

    public Collection<GroupEntity> getAll() {
        if (groupStore.get() == null) {
            return Collections.emptyList();
        }
        return groupStore.get().values();
    }

    public List<GroupNameVo> getNameList() {
        if (groupStore.get() == null) {
            return Collections.emptyList();
        }
        return groupStore.get().values().stream()
            .map(GroupNameVo::create)
            .sorted(Comparator.comparing(GroupNameVo::name))
            .toList();
    }

    @Transactional
    public int create(String name, String creator) throws DataModelResponseException {
        try {
            int id = groupRepository.save(name, creator);
            publishRefreshEvent();
            return id;
        } catch (DuplicateKeyException e) {
            throw new DataModelResponseException(400, 400, String.format("GroupName [%s] 已存在", name));
        }
    }

    public boolean existsById(int groupId) {
        return groupRepository.findById(groupId).isPresent();
    }

    public Optional<GroupEntity> getById(int groupId) {
        return groupRepository.findById(groupId);
    }

    public int remove(int groupId) {
        return groupRepository.delete(groupId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refreshStore();
        log.info("Load group store ok");
    }

    public void publishRefreshEvent() {
        publisher.publishEvent(new RefreshGroupStoreEvent(this));
    }
}
