package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.security.Principal;
import java.util.List;

public interface InterfaceUserService extends UserDetailsService {
    User findUserById(Long userId);
    List<User> allUsers();
    boolean saveUser(User user);
    boolean deleteUser(Principal principal, Long userId);
    List<Role> getRoles();
    User findByUsername(String username);
}
