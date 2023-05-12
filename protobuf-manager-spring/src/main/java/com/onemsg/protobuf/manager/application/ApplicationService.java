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
    
    private final ApplicationRepository applicationRepository;

    private final GroupService groupService;
    
    private final ThreadPoolTaskExecutor executor;

    private final AtomicReference<Map<Integer, Application.Entity>> applicationStore = new AtomicReference<>();

    @Scheduled(fixedDelay=30, timeUnit = TimeUnit.SECONDS)
    public void refreshStore() {
        List<Application.Entity> applications = applicationRepository.findAll();
        Map<Integer, Application.Entity> applicationMap = applications.stream()
                .collect(Collectors.toMap(application -> application.id, Function.identity()));
        applicationStore.set(applicationMap);
    }

    private void asyncRefreshStore() {
        executor.submit(this::refreshStore);
    }
    
    public List<Application.Entity> getAll() {
        return applicationStore.get().values().stream()
                .sorted(Comparator.comparing(Application.Entity::getId).reversed())
                .toList();
    }

    public List<Application.NameVo> getNamesByGroupId(int groupId) {
        return applicationStore.get().values().stream()
                .filter(app -> app.groupId == groupId)
                .map(Application.NameVo::create)
                .sorted(Comparator.comparing(Application.NameVo::name))
                .toList();
    }

    /**
     * 
     * @param model
     * @return
     * @throws NotExistedException
     */
    @Transactional
    public int create(Application.Creation model, String creator) throws NotExistedException {
        groupService.existById(model.groupId());
        try {
            int id = applicationRepository.save(model.name(), model.intro(), model.groupId(), creator);
            asyncRefreshStore();
            return id;
        } catch (DuplicateKeyException e) {
            throw new DataModelResponseException(400, 400, String.format("Application name [%s] 已存在", model.name()));
        }
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
        Group.Entity group = groupService.getById(application.groupId);
        if (group == null) {
            return null;
        }
        model.groupName = group.name;
        return model;
    }

    public void existById(int id) throws NotExistedException{
        boolean existed = applicationRepository.existById(id);
        if (!existed) {
            throw new NotExistedException("Application", id);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refreshStore();
        log.info("Load application store ok");
    }
}
