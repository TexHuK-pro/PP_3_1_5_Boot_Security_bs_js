package ru.kata.spring.boot_security.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.List;

@Component
public class UserCreator {

    private RoleRepository roleRepository;

    @Autowired
    public UserCreator(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    public List<User> createUsersWithRoles() {
        User user = new User("user", "lastUserName", 22, "1", "user@user.ru", List.of(ROLE_USER));
        User admin = new User("admin", "lastAdminName", 27, "1", "admin@admin.ru", List.of(ROLE_ADMIN));
        return List.of(admin, user);
    }
}
