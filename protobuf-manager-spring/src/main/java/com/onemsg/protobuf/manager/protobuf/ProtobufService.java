package com.onemsg.protobuf.manager.protobuf;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.onemsg.protobuf.manager.application.ApplicationService;
import com.onemsg.protobuf.manager.application.model.Application;
import com.onemsg.protobuf.manager.exception.NotExistedException;
import com.onemsg.protobuf.manager.model.Pageable;
import com.onemsg.protobuf.manager.model.Totalable;
import com.onemsg.protobuf.manager.protobuf.model.Protobuf;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCode;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProtobufService {

    @Autowired
    private ProtobufRepository protobufRepository;

    @Autowired
    private ApplicationService applicationService;

    /**
     * 
     * @param search    应用名|protobuf名|author名
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Totalable<Protobuf.SearchVo> search(String search, int pageIndex, int pageSize) {
        int skip = Pageable.toSkip(pageIndex, pageSize);

        List<Protobuf.Entity> entities = null;
        int count = 0;
        if (StringUtils.hasText(search)) {
            entities = protobufRepository.search(search, skip, pageSize);
            count = protobufRepository.count(search);
        } else {
            entities = protobufRepository.search(skip, pageSize);
            count = protobufRepository.count();
        }
        List<Protobuf.SearchVo> searchVos = entities.stream()
                .map(this::toSearchVo)
                .toList();
        return Totalable.create(searchVos, count);
    }

    public Protobuf.DetailVo getDetailById(int id) throws NotExistedException {
        Protobuf.Entity entity = protobufRepository.findById(id);
        if (entity == null) {
            throw new NotExistedException("PROTOBUF", id);
        }
        return toDetailVo(entity);
    }

    @Transactional
    public int create(Protobuf.Creation creation, String creator) throws NotExistedException {
        applicationService.existById(creation.applicationId);
        return protobufRepository.insert(creation, creator);
    }

    @Transactional
    public void updateIntro(int id, String intro) throws NotExistedException {
        existById(id);
        protobufRepository.updateIntroById(id, intro);
    }

    @Transactional
    public void delete(int id) {
        protobufRepository.delete(id);
    }

    public void existById(int id) throws NotExistedException {
        if (!protobufRepository.existById(id)) {
            throw new NotExistedException("PROTOBUF", id);
        }
    }

    public int createProtobufCode(ProtobufCode.Creation creation, String creator) throws NotExistedException {
        existById(creation.protoId);
        return protobufRepository.insertCode(creation, creator);
    }

    public ProtobufCode.DetailVo getProtobufCodeById(int id) throws NotExistedException {
        ProtobufCode.DetailVo detailVo = protobufRepository.findCodeById(id);
        if (detailVo == null) {
            throw new NotExistedException("PROTOBUF_CODE", id);
        }
        return detailVo;
    }
    
    @Nullable
    public ProtobufCode.DetailVo getCurrentProtobufCodeByProtobufId(int protobufId) throws NotExistedException {
        existById(protobufId);
        return protobufRepository.findCurrentCodeByProtobufId(protobufId);
    }

    public List<ProtobufCode.ItemVo> getProtobufCodesByProtobufId(int protobufId) throws NotExistedException {
        existById(protobufId);
        var list = protobufRepository.findCodesByProtobufId(protobufId);
        var entity = protobufRepository.findById(protobufId);
        if (entity != null) {
            list.forEach(vo -> {
                if (vo.version == entity.currentVersion) {
                    vo.isCurrent = true;
                }
            });
        }
        return list;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateCurrentVersion(int id, int version) throws NotExistedException {
        existById(id);
        existCodeVersionByProtobufId(id, version);
        protobufRepository.updateCurrentVersion(id, version);
    }

    private void existCodeVersionByProtobufId(int id, int version) throws NotExistedException {
        if (!protobufRepository.existCodeVersionByProtobufId(id, version)) {
            throw new NotExistedException("PROTOBUF_CODE.VERSION", version);
        }
    }

    private Protobuf.SearchVo toSearchVo(Protobuf.Entity entity) {
        Protobuf.SearchVo searchVo = Protobuf.SearchVo.create(entity);
        Application.Model application = applicationService.getById(entity.applicationId);
        if (application == null) {
            return searchVo;
        }
        searchVo.group = application.groupName;
        searchVo.application = application.name;
        return searchVo;
    }

    private Protobuf.DetailVo toDetailVo(Protobuf.Entity entity) {
        Protobuf.DetailVo detailVo = Protobuf.DetailVo.create(entity);
        Application.Model application = applicationService.getById(entity.applicationId);
        if (application == null) {
            return detailVo;
        }
        detailVo.group = application.groupName;
        detailVo.application = application.name;
        return detailVo;
    }
}
