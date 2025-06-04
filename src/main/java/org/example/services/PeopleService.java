package org.example.services;

import org.example.model.Person;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface PeopleService extends UserDetailsService {
    List<Person> findAll();
    Person findOne(int id);
    void save(Person person);
    void update(int id, Person updatedPerson);
    void delete(int id);
}
