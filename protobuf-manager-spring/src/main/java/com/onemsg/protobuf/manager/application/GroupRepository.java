package com.onemsg.protobuf.manager.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.onemsg.protobuf.manager.application.model.GroupEntity;

@Repository
public class GroupRepository {
    
    private final SimpleJdbcInsert insertGroup;
    private final JdbcClient jdbcClient;

    public GroupRepository(JdbcTemplate jdbcTemplate, JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        insertGroup = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("`group`")
            .usingColumns("name", "creator")
            .usingGeneratedKeyColumns("id");
    }

    public List<GroupEntity> findAll() {
        String sql = "SELECT id, name, creator, created_time FROM `group`";
        return jdbcClient.sql(sql).query(GROUP_ENTITY_ROWMAPPER).list();
    }

    public Optional<GroupEntity> findById(int id) {
        String sql = "SELECT id, name, creator, created_time FROM `group` WHERE id = ?";
        return jdbcClient.sql(sql)
            .param(id)
            .query(GROUP_ENTITY_ROWMAPPER)
            .optional();
    }

    public int save(String name, String creator) throws DuplicateKeyException{
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("creator", creator);
        return insertGroup.executeAndReturnKey(parameters).intValue();
    }

    public int delete(int id) {
        String sql = "DELETE FROM `group` WHERE id = ?";
        return jdbcClient.sql(sql)
            .param(id)
            .update();
    }

    private static final RowMapper<GroupEntity> GROUP_ENTITY_ROWMAPPER = (rs, rowNum) -> {
        return new GroupEntity(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("creator"), 
            rs.getTimestamp("created_time").toLocalDateTime());
    };
}
