package com.onemsg.authorization.repository;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.onemsg.authorization.model.UserRole;

@Repository
public class UserRoleRepository {
    
    private JdbcTemplate jdbcTemplate;

    public UserRoleRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<UserRole.Entity> queryAll() {
        String sql = "SELECT id, user, role_id FROM users_roles";        
        return jdbcTemplate.query(sql, URE_RM);
    }



    private static final RowMapper<UserRole.Entity> URE_RM = (row, rowNum) -> {
        return new UserRole.Entity(
            row.getInt("id"), 
            row.getString("user"), 
            row.getInt("role_id"));
        
    };

}
