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

import com.onemsg.protobuf.manager.application.model.Application;
import com.onemsg.protobuf.manager.application.model.Group;


@Repository
public class ApplicationRepository {
    
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertApplication;

    public ApplicationRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        insertApplication = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("application")
            .usingColumns("name", "intro", "group_id", "creator")
            .usingGeneratedKeyColumns("id");
    }


    public List<Application.Entity> findAll() {
        String sql = """
            SELECT id, name, intro, group_id, creator, created_time, updated_time
            FROM `application`
            """;
        return jdbcTemplate.query(sql, A_E_RM);
    }


    public List<Group.Entity> findAllGroups() {
        String sql = """
            SELECT id, name, intro, created_time, updated_time
            FROM `group`
            """;
        return jdbcTemplate.query(sql, G_E_RM);

    }

    public boolean existById(int id){
        String sql = "SELECT count(id) FROM `application` WHERE `id` = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id) > 0;
    }

    /**
     * 
     * @param name
     * @param intro
     * @param groupId
     * @param creator
     * @return
     * @exception DuplicateKeyException 如果 name+groupId 重复
     */
    public int save(String name, String intro, int groupId, String creator) throws DuplicateKeyException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("intro", intro);
        parameters.put("group_id", groupId);
        parameters.put("creator", creator);
        Number id = insertApplication.executeAndReturnKey(parameters);
        return id.intValue();
    }

    public int delete(int id){
        String sql = "DELETE FROM `application` WHERE `id` = ?";
        int count = jdbcTemplate.update(sql, id);

        String sql3 = "DELETE FROM `protobuf_code` WHERE protobuf_id in "
                + "(SELECT id FROM `protobuf` WHERE application_id = ?) ";
        jdbcTemplate.update(sql3, id);

        String sql2 = "DELETE FROM `protobuf` WHERE application_id = ?";
        jdbcTemplate.update(sql2, id);
        return count;
    }

    public void updateIntroById(int id, String intro){
        String sql = "UPDATE `application` SET `intro` = ? WHERE `id` = ?";
        jdbcTemplate.update(sql, intro, id);
    }

    public boolean existGroupById(int groupId){
        String sql = "SELECT count(id) FROM `group` WHERE `id` = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, groupId) > 0;
    }

    private static final RowMapper<Application.Entity> A_E_RM =  (rs, rowNum) -> {

        var entity = new Application.Entity();
        entity.id = rs.getInt("id");
        entity.name = rs.getString("name");
        entity.intro = rs.getString("intro");
        entity.groupId = rs.getInt("group_id");
        entity.creator = rs.getString("creator");
        entity.createdTime = rs.getTimestamp("created_time").toLocalDateTime();
        entity.updatedTime = rs.getTimestamp("updated_time").toLocalDateTime();
        return entity;
    };

    private static final RowMapper<Group.Entity> G_E_RM = (rs, rowNum) -> {

        var entity = new Group.Entity();
        entity.id = rs.getInt("id");
        entity.name = rs.getString("name");
        entity.intro = rs.getString("intro");
        entity.createdTime = rs.getTimestamp("created_time").toLocalDateTime();
        entity.updatedTime = rs.getTimestamp("updated_time").toLocalDateTime();
        return entity;
    };

}
