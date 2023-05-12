package com.onemsg.protobuf.manager.protobuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.onemsg.protobuf.manager.protobuf.model.Protobuf;
import com.onemsg.protobuf.manager.protobuf.model.Protobuf.Creation;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCode;

@Repository
public class ProtobufRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertProtobuf;
    private final SimpleJdbcInsert insertProtobufCode;

    public ProtobufRepository(DataSource dataSource) {

        jdbcTemplate = new JdbcTemplate(dataSource);

        insertProtobuf = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("protobuf")
            .usingColumns( "application_id", "name", "intro", "protocol", "current_version", "creator")
            .usingGeneratedKeyColumns("id");

        insertProtobufCode = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("protobuf_code")
            .usingColumns("protobuf_id", "code", "version","creator")
            .usingGeneratedKeyColumns("id");
    }

    public List<Protobuf.Entity> search(String search, int skip, int limit) {
        String sql = """
            SELECT p.id as id, p.name as name, p.application_id as application_id, p.intro as intro, 
                p.protocol, p.current_version, p.creator, p.created_time, p.updated_time
            FROM `protobuf` as p
            LEFT JOIN `application` as a ON p.application_id = a.id
            WHERE a.name like ? or p.name like ? or a.creator like ?
            ORDER BY id DESC
            LIMIT ?,?
        """;
        String word = "%" + search + "%";
        return jdbcTemplate.query(sql, P_E_RM, word, word, word, skip, limit);
    }


    public int count(String search) {
        String sql = """
            SELECT count(p.id) 
            FROM `protobuf` as p
            LEFT JOIN `application` as a  ON p.application_id = a.id
            WHERE a.name like ? or p.name like ? or a.creator like ?
        """;
        String word = "%" + search + "%";
        return jdbcTemplate.queryForObject(sql, Integer.class, word, word, word);
    }

    public List<Protobuf.Entity> search(int skip, int limit) {
        String sql = """
            SELECT id, name, application_id, intro, protocol, current_version, creator, created_time, updated_time
            FROM `protobuf`
            ORDER BY id DESC
            LIMIT ?,?
        """;
        
        return jdbcTemplate.query(sql, P_E_RM, skip, limit);
    }

    public int count() {
        String sql = "SELECT count(id) FROM `protobuf`";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count == null ? 0 : count.intValue();
    }


    @Nullable
    public Protobuf.Entity findById(int id) {
        String sql = """
            SELECT id, name, application_id, intro, protocol, current_version, creator, created_time, updated_time
            FROM `protobuf` WHERE id = ?
        """;
        return DataAccessUtils.singleResult(jdbcTemplate.query(sql, P_E_RM, id));
    }

    public boolean existById(int id) {
        String sql = "SELECT count(id) FROM `protobuf` WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != 0 && count > 0;
    }

    public int updateIntroById(int id, String intro) {
        String sql = "UPDATE `protobuf` SET `intro` = ? WHERE `id` = ?";
        return jdbcTemplate.update(sql, intro, id);
    }

    public int insert(Creation creation, String creator) {
        Map<String, Object> params = new HashMap<>();
        params.put("application_id", creation.applicationId);
        params.put("name", creation.name);
        params.put("intro", creation.intro);
        params.put("protocol", creation.protocol);
        params.put("creator", creator);
        params.put("current_version", 0);
        return insertProtobuf.executeAndReturnKey(params).intValue();
    }

    public int delete(int id) {
        String sql = "DELETE FROM `protobuf` WHERE id = ?";
        int count = jdbcTemplate.update(sql, id);
        String sql2 = "DELETE FROM `protobuf_code` WHERE protobuf_id = ?";
        jdbcTemplate.update(sql2, id);
        return count;
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int insertCode(ProtobufCode.Creation creation, String creator) {

        String sql = "SELECT version FROM `protobuf_code` WHERE protobuf_id = ?";
        List<Integer> versions = jdbcTemplate.queryForList(sql, Integer.class, creation.protoId);
        int version = versions.stream().mapToInt(Integer::intValue).max().orElse(100) + 1;

        Map<String, Object> params = new HashMap<>();
        params.put("protobuf_id", creation.protoId);
        params.put("code", creation.code);
        params.put("version", version);
        params.put("creator", creator);
        int id = insertProtobufCode.executeAndReturnKey(params).intValue();

        String updateSql = "UPDATE `protobuf` SET current_version = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, version, creation.protoId);
        return id;
    }

    public int updateCurrentVersion(int id, int version) {
        String updateSql = "UPDATE `protobuf` SET current_version = ? WHERE id = ?";
        return jdbcTemplate.update(updateSql, version, id);
    }

    @Nullable
    public ProtobufCode.DetailVo findCodeById(int codeId) {
        String sql = """
           SELECT id, protobuf_id, code, version, creator, created_time  
           FROM `protobuf_code` WHERE id = ?
        """;
        return DataAccessUtils.singleResult(jdbcTemplate.query(sql, PC_D_RM, codeId));
    }

    @Nullable
    public ProtobufCode.DetailVo findCurrentCodeByProtobufId(int protobufId) {
        String sql = """
                SELECT id, protobuf_id, code, version, creator, created_time
                FROM `protobuf_code` 
                WHERE protobuf_id = ?
                    AND version in (SELECT current_version FROM `protobuf` WHERE id = ?)
        """;
        return DataAccessUtils.singleResult(jdbcTemplate.query(sql, PC_D_RM, protobufId, protobufId));
    }


    public List<ProtobufCode.ItemVo> findCodesByProtobufId(int protobufId) {
        String sql = """
            SELECT pc.id, pc.protobuf_id, pc.version, pc.version = p.current_version as is_current, pc.creator, pc.created_time
            FROM `protobuf_code` as pc
            LEFT JOIN `protobuf` as p on p.id = pc.protobuf_id
            WHERE pc.protobuf_id = ?
        """;
        return jdbcTemplate.query(sql, PC_IV_RM, protobufId);
    }

    public boolean existCodeVersionByProtobufId(int protobufId, int version) {
        String sql = "SELECT count(id) FROM `protobuf_code` WHERE protobuf_id = ? AND version = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, protobufId, version);
        return count != null && count.intValue() > 0;
    }

    private static final RowMapper<Protobuf.Entity> P_E_RM = (rs, rowNum) -> {

        var entity = new Protobuf.Entity();
        entity.id = rs.getInt("id");
        entity.name = rs.getString("name");
        entity.applicationId = rs.getInt("application_id");
        entity.intro = rs.getString("intro");
        entity.name = rs.getString("name");
        entity.protocol = rs.getString("protocol");
        entity.currentVersion = rs.getInt("current_version");
        entity.creator = rs.getString("creator");
        entity.createdTime = rs.getTimestamp("created_time").toLocalDateTime();
        entity.updatedTime = rs.getTimestamp("updated_time").toLocalDateTime();
        return entity;
    };

    private static final RowMapper<ProtobufCode.ItemVo> PC_IV_RM = (rs, rowNum) -> {
        var itemVo = new ProtobufCode.ItemVo();
        itemVo.id = rs.getInt("id");
        itemVo.protoId = rs.getInt("protobuf_id");
        itemVo.version = rs.getInt("version");
        itemVo.isCurrent = rs.getBoolean("is_current");
        itemVo.creator = rs.getString("creator");
        itemVo.createdTime = rs.getTimestamp("created_time").toLocalDateTime();
        return itemVo;
    };

    private static final RowMapper<ProtobufCode.DetailVo> PC_D_RM = (rs, rowNum) -> {
        var detailVo = new ProtobufCode.DetailVo();
        detailVo.id = rs.getInt("id");
        detailVo.protoId = rs.getInt("protobuf_id");
        detailVo.code = rs.getString("code");
        detailVo.version = rs.getInt("version");
        detailVo.creator = rs.getString("creator");
        detailVo.createdTime = rs.getTimestamp("created_time").toLocalDateTime();
        return detailVo;
    };
}
