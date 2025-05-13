package org.example.dao;

import org.example.model.Person;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class PersonDAO {

    private final JdbcTemplate jdbcTemplate;

    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> index() {
//        List<Person> people = new ArrayList<>();
//
//        try {
//            Statement statement = jdbcTemplate.createStatement();
//            String SQL = "select * from person";
//            ResultSet resultSet = statement.executeQuery(SQL);
//
//            while (resultSet.next()) {
//                Person person = new Person();
//
//                person.setName(resultSet.getString("name"));
//                person.setAge(resultSet.getInt("age"));
//
//                people.add(person);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return people;

        return jdbcTemplate.query("select * from person", new BeanPropertyRowMapper<>(Person.class));
    }

    public Person show(int id) {
//        Person person = null;
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("select * from Person where id=?");
//            preparedStatement.setInt(1, id);
//
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            resultSet.next();
//            person = new Person();
//            person.setId(resultSet.getInt("id"));
//            person.setName(resultSet.getString("name"));
//            person.setId(resultSet.getInt("age"));
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return person;
        return jdbcTemplate.query("select * from person where id=?", new Object[]{id}, new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny().orElse(null);
    }

    public void save(Person person) {
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Person VALUES (?, ?)");
//            preparedStatement.setString(1, person.getName());
//            preparedStatement.setInt(2, person.getAge());
//
//            preparedStatement.executeUpdate();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

        jdbcTemplate.update("INSERT INTO person (name, age) VALUES (?, ?)", person.getName(), person.getAge());
    }


    public void update(int id, Person updatedPerson) {
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE person set name=?, age=? where id=?");
//            preparedStatement.setString(1, updatedPerson.getName());
//            preparedStatement.setInt(2, updatedPerson.getAge());
//            preparedStatement.setInt(3, id);
//
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        jdbcTemplate.update("UPDATE person set name=?, age=? where id=?", updatedPerson.getName(), updatedPerson.getAge(), id);
    }

    public void delete(int id) {
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM person where id=?");
//            preparedStatement.setInt(1, id);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        jdbcTemplate.update("DELETE FROM person where id=?", id);
    }
}
