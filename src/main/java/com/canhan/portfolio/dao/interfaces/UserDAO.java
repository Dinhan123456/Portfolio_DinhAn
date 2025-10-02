package com.canhan.portfolio.dao.interfaces;

import com.canhan.portfolio.entity.User;

public interface UserDAO {
    User findByUserName(String userName);

    void save(User user);
}
