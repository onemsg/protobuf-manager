package com.onemsg.protobuf.manager.application;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onemsg.protobuf.manager.application.model.Application;
import com.onemsg.protobuf.manager.application.model.Group;
import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.exception.NotExistedException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ApplicationService implements InitializingBean {

    private static final AtomicReference<Map<Integer, Application.Entity>> applicationStore = new AtomicReference<>();
    private static final AtomicReference<Map<Integer, Group.Entity>> groupStore = new AtomicReference<>();

    private final ApplicationRepository applicationRepository;

    private final ThreadPoolTaskExecutor executor;

    @Scheduled(fixedDelay=30, timeUnit = TimeUnit.SECONDS)
    public void refreshStore() {
        List<Group.Entity> groups = applicationRepository.findAllGroups();
        List<Application.Entity> applications = applicationRepository.findAll();
        
        Map<Integer, Group.Entity> groupMap = groups.stream()
            .collect(Collectors.toMap(group -> group.id, Function.identity()));

        Map<Integer, Application.Entity> applicationMap = applications.stream()
                .collect(Collectors.toMap(application -> application.id, Function.identity()));

        groupStore.set(groupMap);
        applicationStore.set(applicationMap);
    }

    @Async
    public void asyncRefreshStore() {
        executor.submit(this::refreshStore);
    }

    /**
     * 
     * @param model
     * @return
     * @throws NotExistedException
     */
    @Transactional
    public int create(Application.Creation model) throws NotExistedException {
        existGroupById(model.groupId());
        try {
            int id = applicationRepository.save(model.name(), model.intro(), model.groupId(), model.creator());
            asyncRefreshStore();
            return id;
        } catch (DuplicateKeyException e) {
            throw new DataModelResponseException(400, 400, String.format("group/name [%s] 已存在", model.name()));
        }
    }
    
    public List<Group.NameVo> getAllGroupNames() {
        return groupStore.get().values().stream()
            .map(Group.NameVo::create)
            .sorted(Comparator.comparing(Group.NameVo::name))
            .toList();
    }

    public List<Application.NameVo> getNamesByGroupId(int groupId) throws NotExistedException {
        return applicationStore.get().values().stream()
            .filter(app -> app.groupId == groupId)
            .map(Application.NameVo::create)
            .sorted(Comparator.comparing(Application.NameVo::name))
            .toList();
    }

    
    /**
     * 
     * @param id
     * @param intro
     * @throws NotExistedException 如果 application id 不存在
     */
    @Transactional
    public void updateIntro(int id, String intro) throws NotExistedException {
        existById(id);
        applicationRepository.updateIntroById(id, intro);
        asyncRefreshStore();
    }

    @Transactional
    public void remove(int id) {
        applicationRepository.delete(id);
        asyncRefreshStore();
    }

    @Nullable
    public Application.Model getById(int id) {
        Application.Entity application = applicationStore.get().get(id);
        if (application == null) {
            return null;
        }
        Application.Model model = Application.Model.create(application);
        Group.Entity group = groupStore.get().get(application.groupId);
        if (group == null) {
            return null;
        }
        model.groupName = group.name;
        return model;
    }

    public void existGroupById(int groupId) throws NotExistedException {
        boolean existed = applicationRepository.existGroupById(groupId);
        if (!existed) {
            throw new NotExistedException("GROUP", groupId);
        }
    }

    public void existById(int id) throws NotExistedException{
        boolean existed = applicationRepository.existById(id);
        if (!existed) {
            throw new NotExistedException("APPLICATION", id);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refreshStore();
        log.info("Load store ok");
    }
}
