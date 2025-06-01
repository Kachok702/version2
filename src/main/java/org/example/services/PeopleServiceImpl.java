package org.example.services;

import org.example.model.Person;
import org.example.model.Role;
import org.example.repositories.PeopleRepository;
import org.example.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PeopleServiceImpl implements PeopleService {
    private final PeopleRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleServiceImpl(PeopleRepository peopleRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);

    }

    @Transactional
    public void save(Person person) {

        person.setPassword(passwordEncoder.encode(person.getPassword()));

        if (person.getRoles() == null || person.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER").orElse(null);
            if (userRole == null) {
                userRole = new Role("ROLE_USER");
                roleRepository.save(userRole);
            }
            person.setRoles(Set.of(userRole));
        }
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {

        Person personToUpdated = peopleRepository.findById(id).orElseThrow();

        personToUpdated.setName(updatedPerson.getName());
        personToUpdated.setAge(updatedPerson.getAge());
        personToUpdated.setUsername(updatedPerson.getUsername());

        if (!updatedPerson.getPassword().isEmpty()) {
            personToUpdated.setPassword(passwordEncoder.encode(updatedPerson.getPassword()));
        } else {
            personToUpdated.setPassword(personToUpdated.getPassword());
        }

        personToUpdated.setRoles(updatedPerson.getRoles());
        peopleRepository.save(personToUpdated);
    }

    @Transactional
    public void delete(int id) {
        if (peopleRepository.findById(id).isPresent()) {
            peopleRepository.deleteById(id);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByUsername(username);

        if (!person.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }

        return person.get();
    }

    @PostConstruct
    public void initRoles() {
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");
        createAdminIfNotExists();
    }

    private void createRoleIfNotFound(String name) {
        if (!roleRepository.existsByName(name)) {
            Role role = new Role(name);
            roleRepository.save(role);
        }
    }


    private void createAdminIfNotExists() {
        String adminUsername = "admin";
        if (peopleRepository.findByUsername(adminUsername).isEmpty()) {
            Person admin = new Person();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setName("Admin");
            admin.setAge(25);

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
            admin.setRoles(Set.of(adminRole));

            peopleRepository.save(admin);
        }
    }
}