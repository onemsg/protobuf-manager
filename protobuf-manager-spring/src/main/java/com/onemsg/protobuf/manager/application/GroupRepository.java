package com.onemsg.protobuf.manager.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.onemsg.protobuf.manager.application.model.Group;

@Repository
public class GroupRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertGroup;

    public GroupRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        insertGroup = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("`group`")
            .usingColumns("name", "creator", "intro")
            .usingGeneratedKeyColumns("id");
    }

    public List<Group.Entity> findAll() {
        String sql = "SELECT id, name, intro, creator, created_time, updated_time FROM `group`";
        return jdbcTemplate.query(sql, GROUP_ROWMAPPER);
    }

    public int save(String name, String intro, String creator) throws DuplicateKeyException{
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("intro", intro);
        parameters.put("creator", creator);
        return insertGroup.executeAndReturnKey(parameters).intValue();
    }

    public void updateIntroById(int id, String intro) {
        String sql = "UPDATE `group` SET `intro` = ? WHERE `id` = ?";
        jdbcTemplate.update(sql, intro, id);
    }

    public boolean exist(int id) {
        String sql = "SELECT count(id) FROM `group` WHERE `id` = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id) > 0;
    }

    private static final RowMapper<Group.Entity> GROUP_ROWMAPPER = (rs, rowNum) -> {
        var entity = new Group.Entity();
        entity.id = rs.getInt("id");
        entity.name = rs.getString("name");
        entity.intro = rs.getString("intro");
        entity.creator = rs.getString("creator");
        entity.createdTime = rs.getTimestamp("created_time").toLocalDateTime();
        entity.updatedTime = rs.getTimestamp("updated_time").toLocalDateTime();
        return entity;
    };
}
