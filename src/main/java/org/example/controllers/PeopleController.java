package org.example.controllers;

import org.example.model.Person;
import org.example.model.Role;
import org.example.services.PeopleService;
import org.example.services.RoleService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/people/admin")
public class PeopleController {

    private final PeopleService peopleService;
    private final RoleService roleService;

    public PeopleController(PeopleService peopleService, RoleService roleService) {
        this.peopleService = peopleService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());
        return "people/admin/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, Authentication authentication) {

        Person currentPerson = (Person) authentication.getPrincipal();
        Person person = peopleService.findOne(id);

        boolean isAdmin = false;
        for (GrantedAuthority authority : currentPerson.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                isAdmin = true;
                break;
            }
        }

        if (!isAdmin && currentPerson.getId() != person.getId()) {
            return "redirect:/access-denied";
        }

        model.addAttribute("person", person);
        return "people/admin/show";
    }

    @GetMapping("/new")
    public String newPerson(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "people/admin/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult, @RequestParam("roles") Set<Long> roleId) {
        if (bindingResult.hasErrors()) {
            return "people/admin/new";
        }

        Set<Role> roles = new HashSet<>();
        for (Long roleIds : roleId) {
            roles.add(roleService.getRoleById(roleIds));
        }
        person.setRoles(roles);

        peopleService.save(person);
        return "redirect:/people/admin";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", peopleService.findOne(id));
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "people/admin/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person updatedPerson,
                         BindingResult bindingResult,
                         @PathVariable("id") int id,
                         @RequestParam(value = "roles", required = false) Set<Long> roleIds) {
        if (bindingResult.hasErrors()) {
            return "people/admin/edit";
        }

        Set<Role> roles = new HashSet<>();
        if (roleIds != null ) {
                     for (Long roledId : roleIds) {
                roles.add(roleService.getRoleById(roledId));
            }
        } else {
            return "people/admin/edit";
        }
        updatedPerson.setRoles(roles);

        peopleService.update(id, updatedPerson);
        return "redirect:/people/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/people/admin";
    }
}
