package com.student.management.repository;

import com.student.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {

    // Query a student to find by email
    Optional<Student> findByEmail(String email);

    // Query List of students to find by name
    List<Student> findByNameIsContaining(String name);

    // Query a student to find by student Id
    Student findStudentById(long id);
}
