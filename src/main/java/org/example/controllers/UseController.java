package org.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class UseController {

    @GetMapping("/login")
    public String loginPage() {
        return "people/admin/login";
    }

    @GetMapping("/acces-denied")
    public String accessDenied() {
        return "acces-denied";
    }
}
