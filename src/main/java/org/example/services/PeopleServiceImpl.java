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
import javax.persistence.EntityManager;
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
    public void init() {
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");
        createUsersIfNotExists("Marsel", 25, "MarselGod", "password", Set.of("ROLE_ADMIN", "ROLE_USER"));
        createUsersIfNotExists("Liza", 27, "LizaAdmin", "password", Set.of("ROLE_ADMIN"));
        createUsersIfNotExists("Timur", 44, "TimurUser", "password", Set.of("ROLE_USER"));
        createUsersIfNotExists("Ivan", 34, "IvanUser", "password", Set.of("ROLE_USER"));
    }

    private void createRoleIfNotFound(String name) {
        if (!roleRepository.existsByName(name)) {
            Role role = new Role(name);
            roleRepository.save(role);
        }
    }


    private void createUsersIfNotExists(String name, int age, String username, String password, Set<String> role) {
        if (peopleRepository.findByUsername(username).isEmpty()) {
            Person person = new Person();
            person.setName(name);
            person.setAge(age);
            person.setUsername(username);
            person.setPassword(passwordEncoder.encode(password));

            Set<Role> roles = new HashSet<>();
            for (String roleName : role) {
                Role role1 = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role1);
            }

            person.setRoles(roles);
            peopleRepository.save(person);
        }
    }
}