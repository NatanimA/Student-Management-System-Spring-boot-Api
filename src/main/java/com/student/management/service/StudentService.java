package com.student.management.service;

import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private final StudentRepository studentRepository;

    //bcrypt instance to encrypt our user passwords
    BCryptPasswordEncoder bCryptPasswordEncoder =
            new BCryptPasswordEncoder(10,new SecureRandom());

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public ResponseEntity getAllStudents() {
        /**
         This service will try to find all the existing users and return it in a body
         */
        try{
            // fetch all current users from repository
            List<Student> studentList = studentRepository.findAll();
            // return it in a response body
            return  ResponseEntity.status(200).body(studentList);

        }catch (Exception e){
            // if error happens return with internal server error
            return ResponseEntity.status(500).body(null);
        }

    }

    public ResponseEntity getStudent(Long id) {
        /**
         This service will get the id passed in the parameters and use this id to find the requested user.
         */
        try{
            // search for the user with the given Id
            Optional<Student> studentOptional = studentRepository.findById(id);
            // If user is present returns the user else return with bad request
            if(studentOptional.isPresent()){
                Student student = studentRepository.findStudentById(id);
                return ResponseEntity.ok().body(student);
            }
            else {
                return ResponseEntity.badRequest().body("User could not be found.");
            }
        }catch (Exception e){
            // if something happens, it will return with an internal server error
            return ResponseEntity.internalServerError().body("Something went wrong please try again later.");
        }
    }

    public ResponseEntity saveStudent(Student student) {
        /**
         This service will get the student in a response body and try to save it if the email address is valid.
         */
        try {
            Optional<Student> studentOptional = studentRepository.findByEmail(student.email.toLowerCase());
            // If a student does not exist, It will save the new user else, it will return with bad request
            if (studentOptional.isPresent()){
                return ResponseEntity.ok().body("Email is already taken.");
            }

            // It will hash the student password with bcrypt and encode it.
            String hashedPassword = bCryptPasswordEncoder.encode(student.password);

            //It will save the User will all the given credentials.
            Student newStudent = Student.builder()
                        .bio(student.bio)
                        .email(student.email.toLowerCase())
                        .name(student.name)
                        .photo(student.photo)
                        .password(hashedPassword)
                        .build();

            studentRepository.save(newStudent);
            return ResponseEntity.status(201).body(newStudent);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Something went wrong please try again later.");
        }
    }

    public ResponseEntity deleteStudent(Long id) {
        /**
         This service tries to find the user with the given Id and if the user is found it will remove the user,
         else it will return with bad request.
         */
        try{
            Optional<Student> studentOptional = studentRepository.findById(id);
            if(!studentOptional.isPresent()){
                return ResponseEntity.badRequest().body("User is not found.");
            }
            studentRepository.deleteById(id);
            return ResponseEntity.status(204).body("User is removed");
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Something went wrong please try again later.");
        }
    }

    @Transactional
    public ResponseEntity editStudent(Long id, Student student, Neo4jProperties.Authentication authentication) {
        /**
         This Service tries to find with the given Id and tries to update the user with the passed student properties.
         */
        try{
            Optional<Student> studentOptional = studentRepository.findById(id);
            if (student.name != ""){
                studentOptional.get().name = student.name;
            }
            if (student.bio != ""){
                studentOptional.get().bio = student.bio;
            }
            if (student.photo != ""){
                studentOptional.get().photo = student.photo;
            }
            return ResponseEntity.ok().body("Student updated successfully");
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Something went wrong please try again later.");
        }
    }

    public ResponseEntity searchStudent(String name) {
        /**
         This service will take the passed name and search for it in the database and returns a list of students that matches the request.
         */
        try{
            List<Student> studentList = studentRepository.findByNameIsContaining(name);
            return ResponseEntity.ok().body(studentList);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Something went wrong please try again later.");
        }
    }
}
