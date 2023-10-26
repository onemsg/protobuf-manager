package com.onemsg.protobuf.manager.application;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onemsg.protobuf.manager.application.event.RefreshGroupStoreEvent;
import com.onemsg.protobuf.manager.application.model.Group;
import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.exception.NotExistedException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class GroupService implements InitializingBean, ApplicationEventPublisherAware {

    private final GroupRepository groupRepository;
    private final ThreadPoolTaskExecutor executor;

    private final AtomicReference<Map<Integer, Group.Entity>> groupStore = new AtomicReference<>(
            Collections.emptyMap());

    @EventListener(RefreshGroupStoreEvent.class)
    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    public void refreshStore() {
        List<Group.Entity> groups = groupRepository.findAll();
        Map<Integer, Group.Entity> groupMap = groups.stream()
                .collect(Collectors.toMap(group -> group.id, Function.identity()));
        groupStore.set(groupMap);
    }

    // private void asyncRefreshStore() {
    //     executor.submit(this::refreshStore);
    // }

    public Collection<Group.Entity> getAll() {
        return groupStore.get().values();
    }

    public List<Group.NameVo> getAllNames() {
        return groupStore.get().values().stream().map(Group.NameVo::create)
                .sorted(Comparator.comparing(Group.NameVo::name)).toList();
    }

    @Nullable
    public Group.Entity getById(int id) {
        return groupStore.get().get(id);
    }

    @Transactional
    public int create(String name, String intro, String creator) throws DataModelResponseException {
        try {
            int id = groupRepository.save(name, intro, creator);
            publishRefreshEvent();
            return id;
        } catch (DuplicateKeyException e) {
            throw new DataModelResponseException(400, 400, String.format("Group name [%s] 已存在", name));
        }
    }

    @Transactional
    public void updateIntro(int id, String intro) throws NotExistedException {
        existById(id);
        groupRepository.updateIntroById(id, intro);
        publishRefreshEvent();
    }

    public void existById(int id) throws NotExistedException {
        boolean existed = groupRepository.exist(id);
        if (!existed) {
            throw new NotExistedException("Group", id);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refreshStore();
        log.info("Load group store ok");
    }

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        publisher = applicationEventPublisher;
    }

    public void publishRefreshEvent() {
        publisher.publishEvent(new RefreshGroupStoreEvent(this));
    }
}
