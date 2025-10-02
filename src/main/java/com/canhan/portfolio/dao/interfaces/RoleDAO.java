package com.canhan.portfolio.dao.interfaces;

import com.canhan.portfolio.entity.Role;

public interface RoleDAO {
    public Role findRoleByName(String roleName);

    public void save(Role role);
}
