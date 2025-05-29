package org.example.services;

import org.example.model.Person;
import org.example.model.Role;
import org.example.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService implements UserDetailsService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
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
        Person personFromDataBase = peopleRepository.findByUsername(person.getUsername());

        if (personFromDataBase == null) {
            person.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
            person.setPassword(person.getPassword());
            peopleRepository.save(person);
        }
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        if(peopleRepository.findById(id).isPresent()){
        peopleRepository.deleteById(id);}
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = peopleRepository.findByUsername(username);

        if (person == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return person;
    }


}


