package ru.kata.spring.boot_security.demo.service;

import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements InterfaceUserService {
    @PersistenceContext
    private EntityManager em;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //Временный метод для автоматического создания пользователей
    public boolean utilSaveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public boolean saveUser(User user) {

        Optional<User> userPass = null;

        if (user.getId() == null) {
            User lastAddedUser = userRepository.findTopByOrderByIdDesc();
            Long lastAddedUserId = lastAddedUser.getId();
            user.setId(lastAddedUserId + 1L);
        } else {
            userPass = userRepository.findById(user.getId());
        }

        // проверка обязательной роли
        if (user.getRoles().isEmpty()) {
            user.setRoles(findUserById(user.getId()).getRoles());
        } else {
            user.setRoles(user.getRoles());
        }

        // проверка на наличие пользователя в бд
        if (userRepository.findByUserName(user.getFirstName()) != null && user.getId() == null) {
            return false;
        }

        // проверка на наличие пароля при создание нового пользователя
        if (user.getId() == null && user.getPassword().isEmpty()) {
            return false;
        }

        // проверка на наличие имени при создании и редактирование
        if (user.getUsername().isEmpty()) {
            return false;
        }

        // проверка, если не вводили новый пароль, то оставляем старый
        if (user.getPassword().isEmpty()) {
            user.setPassword(userPass.get().getPassword());
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }

        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean deleteUser(Principal principal, Long userId) {

        //проверка на удаление самих себя
        User admin = findByUsername(principal.getName());
        if (userId == admin.getId()) {
            return false;
        }

        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }
}
