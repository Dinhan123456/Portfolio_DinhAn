package com.canhan.portfolio.config;

import com.canhan.portfolio.dao.interfaces.RoleDAO;
import com.canhan.portfolio.dao.interfaces.UserDAO;
import com.canhan.portfolio.entity.Role;
import com.canhan.portfolio.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserDAO userDao;

    @Autowired
    private RoleDAO roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Tạo role ADMIN nếu chưa tồn tại
        Role adminRole = roleDao.findRoleByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setId(1);
            adminRole.setName("ROLE_ADMIN");
            roleDao.save(adminRole);
        }

        // Tạo user admin mặc định nếu chưa tồn tại
        User existingAdmin = userDao.findByUserName("admin");
        if (existingAdmin == null) {
            User adminUser = new User();
            adminUser.setId(1);
            adminUser.setUserName("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEnabled(true);
            adminUser.setRoles(Arrays.asList(adminRole));

            userDao.save(adminUser);

            System.out.println("===========================================");
            System.out.println("✅ Tài khoản admin đã được tạo thành công!");
            System.out.println("📧 Username: admin");
            System.out.println("🔑 Password: admin123");
            System.out.println("===========================================");
        } else {
            System.out.println("ℹ️  Tài khoản admin đã tồn tại: " + existingAdmin.getUserName());
        }
    }
}
