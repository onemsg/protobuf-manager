package com.onemsg.protobuf.manager.protobuf;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.onemsg.protobuf.manager.exception.NotExistedException;
import com.onemsg.protobuf.manager.gen.GrpcClientMavenGenerator;
import com.onemsg.protobuf.manager.gen.LocalProtocRunner;
import com.onemsg.protobuf.manager.model.Pageable;
import com.onemsg.protobuf.manager.model.Totalable;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeCreation;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeEntity;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeVersion;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoCreation;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoEntity;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProtobufInfoService {

    private final ProtobufInfoRepository protobufInfoRepository;

    /**
     * 
     * @param search  应用名|protobuf名|author名
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Totalable<ProtobufInfoEntity> search(String search, int pageIndex, int pageSize) {
        int skip = Pageable.toSkip(pageIndex, pageSize);

        List<ProtobufInfoEntity> entities = null;
        int count = 0;
        if (StringUtils.hasText(search)) {
            entities = protobufInfoRepository.search(search, skip, pageSize);
            count = protobufInfoRepository.count(search);
        } else {
            entities = protobufInfoRepository.search(skip, pageSize);
            count = protobufInfoRepository.count();
        }
        return Totalable.create(entities, count);
    }

    public ProtobufInfoEntity getInfoById(int id) throws NotExistedException {
        return protobufInfoRepository.findById(id)
            .orElseThrow(() -> new NotExistedException("Protobuf", id));
    }

    @Transactional
    public int create(ProtobufInfoCreation creation) {
        return protobufInfoRepository.insert(creation);
    }

    @Transactional
    public int updateIntro(int id, String intro) throws NotExistedException {
        requireExistsById(id);
        return protobufInfoRepository.updateIntroById(id, intro);
    }

    @Transactional
    public int delete(int id) {
        return protobufInfoRepository.delete(id);
    }

    @Transactional
    public int createProtobufCode(ProtobufCodeCreation creation) throws NotExistedException {
        requireExistsById(creation.protobufId); 
        var optional = protobufInfoRepository.findById(creation.protobufId);
        if (optional.isEmpty()) {
            throw new NotExistedException("Protobuf", creation.protobufId);
        }
        creation.protobufName = optional.get().name;
        return protobufInfoRepository.insertCode(creation);
    }

    public ProtobufCodeEntity getProtobufCodeById(int id) throws NotExistedException {
        return protobufInfoRepository.findCodeById(id)
            .orElseThrow(() -> new NotExistedException("PROTOBUF_CODE", id));
    }

    public ProtobufCodeEntity getProtobufCodeByProtobufIdAndVersion(int id, int version) throws NotExistedException {
        return protobufInfoRepository.findCodeByProtobufIdAndVersion(id, version)
            .orElseThrow(() -> new NotExistedException("PROTOBUF_CODE", id));
    }

    
    @Nullable
    public ProtobufCodeEntity getCurrentProtobufCodeByProtobufId(int protobufId) throws NotExistedException {
        requireExistsById(protobufId);
        return protobufInfoRepository.findCurrentCodeByProtobufId(protobufId).orElse(null);
    }

    public List<ProtobufCodeVersion> getProtobufCodeVersionsByProtobufId(int protobufId) throws NotExistedException {
        requireExistsById(protobufId);
        return protobufInfoRepository.findCodeVersionListByProtobufId(protobufId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateCurrentVersion(int protobufId, int version) throws NotExistedException {
        requireExistsById(protobufId);
        requireExistCodeVersionByProtobufId(protobufId, version);
        protobufInfoRepository.updateCurrentVersion(protobufId, version);
    }

    private void requireExistsById(int id) throws NotExistedException {
        if (!protobufInfoRepository.existsById(id)) {
            throw new NotExistedException("Protobuf", id);
        }
    }

    public void requireExistCodeVersionByProtobufId(int id, int version) throws NotExistedException {
        if (!protobufInfoRepository.existsCodeByProtobufIdAndVersion(id, version)) {
            throw new NotExistedException("PROTOBUF_CODE.VERSION", version);
        }
    }

}
