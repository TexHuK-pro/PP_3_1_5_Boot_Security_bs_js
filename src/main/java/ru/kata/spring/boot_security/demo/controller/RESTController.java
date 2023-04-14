package ru.kata.spring.boot_security.demo.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.InterfaceUserService;

import static org.springframework.http.HttpStatus.OK;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class RESTController {

    private final InterfaceUserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    @Autowired
    public RESTController(InterfaceUserService userService, UserRepository userRepository, ModelMapper modelMapper, RoleRepository roleRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
    }

    //вернем выбронного пользователя
    @GetMapping("/admin/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        return new ResponseEntity<>(userToDto(user), HttpStatus.OK);
    }

    //список пользователей
    @GetMapping("/admin")
    public ResponseEntity<List<UserDto>> showAllUsers() {
        List<User> users = userService.allUsers();
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users) {
            if (user != null) {
                UserDto userDto = userToDto(user);
                userDtos.add(userDto);
            }
        }

        if(userDtos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(userDtos);
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id, Principal principal) {
        userService.deleteUser(principal, id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @PostMapping("/admin/")
    public ResponseEntity<HttpStatus> addAndUpdateUser(@RequestBody UserDto userDto) {
        User user = dtoToUser(userDto);
        userService.saveUser(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //возвращаем себя
    @GetMapping("/user")
    public ResponseEntity<UserDto> getThisUser(Principal principal) {
        return new ResponseEntity<>(userToDto(userRepository.findByUserName(principal.getName())), HttpStatus.OK);
    }

    // вернем рорли
    @GetMapping("/roles")
    public ResponseEntity<Set<Role>> getRoles() {
        return new ResponseEntity<>(new HashSet<>(roleRepository.findAll()), OK);
    }
    private User dtoToUser(UserDto dto) {
        return modelMapper.map(dto, User.class);
    }
    private UserDto userToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }


}
