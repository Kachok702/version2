package org.example.services;

import org.example.model.Person;
import org.example.model.Role;
import org.example.repositories.PeopleRepository;
import org.example.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class PeopleService implements UserDetailsService {
    private final PeopleRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
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

        Role userRole = roleRepository.findByName("ROLE_USER").orElse(null);
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        person.setRoles(Collections.singleton(userRole));

        peopleRepository.save(person);

    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        if (peopleRepository.findById(id).isPresent()) {
            peopleRepository.deleteById(id);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Optional <Person> person = peopleRepository.findByUsername(username);

        if (person == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return person.get();
    }


}


