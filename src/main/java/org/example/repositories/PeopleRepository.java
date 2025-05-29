package org.example.repositories;

import org.example.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
Person findByUsername(String  username);
}
