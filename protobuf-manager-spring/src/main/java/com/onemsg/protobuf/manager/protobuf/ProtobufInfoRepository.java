package com.onemsg.protobuf.manager.protobuf;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeCreation;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeEntity;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeVersion;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoCreation;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ProtobufInfoRepository {
    
    private final JdbcClient jdbcClient;

    public ProtobufInfoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<ProtobufInfoEntity> search(String search, int skip, int limit) {
        String sql = """
            SELECT id, name, intro, application_name, group_name, protocol, current_version, creator, created_time, updated_time
            FROM `protobuf_info`
            WHERE group_name like ? OR application_name like ? OR name like ? OR creator like ?
            ORDER BY id DESC
            LIMIT ?,?
        """;
        String word = "%" + search + "%";
        return jdbcClient.sql(sql).params(word, word, word, word, skip, limit)
            .query(ProtobufInfoEntity.class)
            .list();
    }


    public int count(String search) {
        String sql = """
            SELECT count(id) FROM `protobuf_info`
            WHERE group_name like ? OR application_name like ? OR name like ?
        """;
        String word = "%" + search + "%";
        return jdbcClient.sql(sql)
            .params(word, word, word)
            .query(int.class)
            .single();
    }

    public List<ProtobufInfoEntity> search(int skip, int limit) {
        String sql = """
            SELECT id, name, intro, application_name, group_name, protocol, current_version, creator, created_time, updated_time
            FROM `protobuf_info`
            ORDER BY id DESC
            LIMIT ?,?
        """;
        
        return jdbcClient.sql(sql).params(skip, limit).query(ProtobufInfoEntity.class).list();
    }

    public int count() {
        String sql = "SELECT count(id) FROM `protobuf_info`";
        return jdbcClient.sql(sql).query(int.class).single();
    }

    public Optional<ProtobufInfoEntity> findById(int id) {
        String sql = """
            SELECT id, name, intro, application_name, group_name, protocol, current_version, creator, created_time, updated_time
            FROM `protobuf_info` WHERE id = ?
        """;
        return jdbcClient.sql(sql).param(id).query(ProtobufInfoEntity.class).optional();
    }

    public boolean existsById(int id) {
        String sql = "SELECT count(id) FROM `protobuf_info` WHERE id = ?";
        return jdbcClient.sql(sql).param(id).query(int.class).single() > 0;
    }

    public int updateIntroById(int id, String intro) {
        String sql = "UPDATE `protobuf_info` SET `intro` = ? WHERE `id` = ?";
        return jdbcClient.sql(sql).param(intro, id).update();
    }

    public int insert(ProtobufInfoCreation creation) {
        String sql = """
            INSERT INTO `protobuf_info`(name, intro, application_name, group_name, protocol, creator) 
            VALUES(:name, :intro, :applicationName, :groupName, :protocol, :creator);
        """;

        log.info("Insert {}", creation);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
            .paramSource(creation)
            .update(keyHolder, "id");

        return getKeyAsInt(keyHolder, "protobuf_info");
    }


    private static int getKeyAsInt(KeyHolder keyHolder, String tableName) {
        var key = keyHolder.getKey();
        if (key == null) {
            String msg = String.format("获取 Table[%s] 自增ID失败", tableName);
            throw new DataAccessException(msg) {};
        }
        return key.intValue();
    }

    public int delete(int id) {
        String sql = "DELETE FROM `protobuf_info` WHERE id = ?";
        return jdbcClient.sql(sql).param(id).update();
    }

    public int insertCode(ProtobufCodeCreation creation) {

        int nextVersion = jdbcClient.sql("SELECT _next_version FROM `protobuf_info` WHERE id = ? FOR UPDATE")
            .param(creation.protobufId)
            .query(int.class)
            .single();
    
        creation.version = nextVersion;

        String insertSql = """
                INSERT INTO `protobuf_code`(protobuf_id, protobuf_name, code, version, creator)
                VALUES(:protobufId, :protobufName, :code, :version, :creator );
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(insertSql).paramSource(creation).update(keyHolder, "id");
        
        String updateSql = "UPDATE `protobuf_info` SET current_version = ?, _next_version = _next_version + 1 WHERE id = ?";
        jdbcClient.sql(updateSql)
            .param(nextVersion)
            .param(creation.protobufId)
            .update();

        return getKeyAsInt(keyHolder, "protobuf_code");
    }

    public int updateCurrentVersion(int id, int version) {
        String updateSql = "UPDATE `protobuf_info` SET current_version = ? WHERE id = ?";
        return jdbcClient.sql(updateSql).param(version, id).update();
    }

    public Optional<ProtobufCodeEntity> findCodeById(int codeId) {
        String sql = """
           SELECT id, protobuf_id, protobuf_name, code, version, creator, created_time  
           FROM `protobuf_code` WHERE id = ?
        """;
        return jdbcClient.sql(sql).param(codeId).query(ProtobufCodeEntity.class).optional();
    }

    public Optional<ProtobufCodeEntity> findCodeByProtobufIdAndVersion(int protobufId, int version) {
        String sql = """
                   SELECT id, protobuf_id, protobuf_name, code, version, creator, created_time
                   FROM `protobuf_code` 
                   WHERE protobuf_id = ? AND version = ?
            """; 
        return jdbcClient.sql(sql).params(protobufId, version).query(ProtobufCodeEntity.class).optional();
    }

    public Optional<ProtobufCodeEntity> findCurrentCodeByProtobufId(int protobufId) {
        String sql = """
                SELECT id, protobuf_id, protobuf_name, code, version, creator, created_time
                FROM `protobuf_code` 
                WHERE protobuf_id = ?
                    AND version in (SELECT current_version FROM `protobuf_info` WHERE id = ?)
        """;
        return jdbcClient.sql(sql).params(protobufId, protobufId)
            .query(ProtobufCodeEntity.class).optional();
    }

    public boolean existsCodeByProtobufIdAndVersion(int protobufId, int version) {
        String sql = """
                SELECT count(id)
                FROM `protobuf_code`
                WHERE protobuf_id = ? AND version = ?
                """;
        return jdbcClient.sql(sql).params(protobufId, version).query(int.class).single() > 0;    
    }

    public List<ProtobufCodeVersion> findCodeVersionListByProtobufId(int protobufId) {
        String sql = """
            SELECT code.id, code.version, code.version = info.current_version as is_current, code.creator, code.created_time
            FROM `protobuf_code` as code
            LEFT JOIN `protobuf_info` as info ON code.protobuf_id = info.id
            WHERE code.protobuf_id = ?
        """;
        return jdbcClient.sql(sql).param(protobufId).query(ProtobufCodeVersion.class).list();
    }

}
