package com.onemsg.protobuf.manager.application;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

import com.onemsg.protobuf.manager.application.model.ApplicationCreation;
import com.onemsg.protobuf.manager.application.model.ApplicationEntity;
import com.onemsg.protobuf.manager.application.model.ApplicationNameVo;
import com.onemsg.protobuf.manager.application.model.GroupEntity;
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

    private final AtomicReference<Map<Integer, ApplicationEntity>> applicationStore = new AtomicReference<>();

    @Scheduled(fixedDelay=5, timeUnit = TimeUnit.SECONDS)
    public void refreshStore() {
        List<ApplicationEntity> applications = applicationRepository.findAll();
        Map<Integer, ApplicationEntity> applicationMap = applications.stream()
                .collect(Collectors.toMap(application -> application.id, Function.identity()));
        applicationStore.set(applicationMap);
    }

    private void asyncRefreshStore() {
        executor.submit(this::refreshStore);
    }
    
    public List<ApplicationEntity> getAll() {
        return applicationStore.get().values().stream()
                .sorted(Comparator.comparing(ApplicationEntity::getId).reversed())
                .toList();
    }

    public List<ApplicationNameVo> getNameListByGroupId(int groupId) {
        return applicationStore.get().values().stream()
                .filter(app -> app.groupId == groupId)
                .map(ApplicationNameVo::create)
                .sorted(Comparator.comparing(ApplicationNameVo::name))
                .toList();
    }

    @Transactional
    public int create(ApplicationCreation creation) throws NotExistedException {
        Optional<GroupEntity> group = groupService.getById(creation.groupId);
        if (group.isEmpty()) {
            throw new NotExistedException("group", creation.groupId);
        }
        creation.groupName = group.get().name();
        try {
            int id = applicationRepository.save(creation);
            asyncRefreshStore();
            return id;
        } catch (DuplicateKeyException e) {
            throw new DataModelResponseException(400, 400, String.format("Application name [%s] 已存在", creation.getFullName()));
        }
    }

    @Transactional
    public void updateIntro(int id, String zhName, String intro) throws NotExistedException {
        requireExistsById(id);
        applicationRepository.update(id, zhName, intro);
        asyncRefreshStore();
    }

    @Transactional
    public void remove(int id) {
        applicationRepository.delete(id);
        asyncRefreshStore();
    }

    @Nullable
    public ApplicationEntity getById(int id) {
        return applicationRepository.findById(id).orElse(null);
    }

    private void requireExistsById(int id) throws NotExistedException{
        Optional<ApplicationEntity> optional = applicationRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotExistedException("Application", id);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refreshStore();
        log.info("Load application store ok");
    }
}
