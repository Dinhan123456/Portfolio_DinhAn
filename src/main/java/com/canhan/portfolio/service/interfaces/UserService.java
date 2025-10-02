package com.canhan.portfolio.service.interfaces;

import com.canhan.portfolio.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByUserName(String userName);
}
