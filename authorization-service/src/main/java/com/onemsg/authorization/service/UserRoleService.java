package com.onemsg.authorization.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.onemsg.authorization.repository.RoleRepository;
import com.onemsg.authorization.repository.UserRoleRepository;

/**
 * 管理用户和角色关系的服务
 * 
 * @since 2023-04
 */
public class UserRoleService {
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * 获取所有用户角色关系
     */
    public void getAllUserRoles() {
        // 查询 users_roles

        // 通过 role_id 查询所有的 role 信息
    }
}
