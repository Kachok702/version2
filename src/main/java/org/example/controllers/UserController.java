package org.example.controllers;

import org.example.model.Person;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public String userHome(Authentication authentication, Model model) {
        Person person = (Person) authentication.getPrincipal();
        model.addAttribute("person", person);
        return "people/admin/show"; // используем ту же view
    }
}
