package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class AdminController {
    @GetMapping("admin")
    public String login() {
        return "admin/admin-list";
    }
}
