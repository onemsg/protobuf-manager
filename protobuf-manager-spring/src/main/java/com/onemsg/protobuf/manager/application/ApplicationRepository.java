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

import com.onemsg.protobuf.manager.application.model.ApplicationEntity;
import com.onemsg.protobuf.manager.application.model.ApplicationCreation;


@Repository
public class ApplicationRepository {
    
    private SimpleJdbcInsert insertApplication;

    private final JdbcClient jdbcClient;

    public ApplicationRepository(JdbcTemplate jdbcTemplate, JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        insertApplication = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("application")
            .usingColumns("name", "zhName", "intro", "group_id", "group_name", "creator")
            .usingGeneratedKeyColumns("id");
    }

    public List<ApplicationEntity> findAllByGroupName(String groupName) {
        String sql = """
            SELECT id, name, zhName, intro, group_name, creator, created_time, updated_time
            FROM `application`
            WHERE group_name = ?;
            """;
        return jdbcClient.sql(sql)
            .param(groupName)
            .query(APPLICATION_ENTITY_ROWMAPPER)
            .list();
    }


    public List<ApplicationEntity> findAll() {
        String sql = "SELECT id, name, creator, created_time FROM `application`";
        return jdbcClient.sql(sql)
            .query(APPLICATION_ENTITY_ROWMAPPER)
            .list();
    }

    public Optional<ApplicationEntity> findById(int id) {
        String sql = "SELECT id, name, creator, created_time FROM `application` WHERE id = ?";
        return jdbcClient.sql(sql)
            .param(id)
            .query(APPLICATION_ENTITY_ROWMAPPER)
            .optional();
    }

    /**
     * @return 新保存对象 ID
     * @exception DuplicateKeyException 如果 name+groupName 重复
     */
    public int save(ApplicationCreation application) throws DuplicateKeyException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", application.name);
        parameters.put("zhName", application.zhName);
        parameters.put("intro", application.intro);
        parameters.put("group_id", application.groupId);
        parameters.put("group_name", application.groupName);
        parameters.put("creator", application.creator);
        Number id = insertApplication.executeAndReturnKey(parameters);
        return id.intValue();
    }

    public int update(int id, String zhName, String intro) {
        String sql = """
                UPDATE `application` 
                SET zhName = IFNULL(:zhName, zhName), intro = IFNULL(:intro, intro)
                WHERE id = :id;
                """;
        return jdbcClient.sql(sql)
            .param("zhName", zhName)
            .param("intro", intro)
            .param("id", id)
            .update();
    }

    public int delete(int id) {
        String sql = "DELETE FROM `application` WHERE `id` = ?";
        return jdbcClient.sql(sql).param(id).update();
    }

    private static final RowMapper<ApplicationEntity> APPLICATION_ENTITY_ROWMAPPER =  (rs, rowNum) -> {
        var entity = new ApplicationEntity();
        entity.id = rs.getInt("id");
        entity.name = rs.getString("name");
        entity.zhName = rs.getString("zh_name");
        entity.intro = rs.getString("intro");
        entity.groupId = rs.getInt("group_id");
        entity.groupName = rs.getString("group_name");
        entity.creator = rs.getString("creator");
        entity.createdTime = rs.getTimestamp("created_time").toLocalDateTime();
        entity.updatedTime = rs.getTimestamp("updated_time").toLocalDateTime();
        return entity;
    };

}
