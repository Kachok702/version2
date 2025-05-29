package org.example.controllers;

import org.example.model.Person;
import org.example.services.PeopleService;
import org.example.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@Controller
@RequestMapping("/people")
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
        return "people/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.findOne(id));
        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
         return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "people/new";
        }
       peopleService.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id){
        model.addAttribute("person", peopleService.findOne(id));
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person updatedPerson, BindingResult bindingResult, @PathVariable("id") int id){
       if (bindingResult.hasErrors()){
           return "people/edit";
       }
       peopleService.update(id, updatedPerson);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
      peopleService.delete(id);
        return "redirect:/people";
    }
}
