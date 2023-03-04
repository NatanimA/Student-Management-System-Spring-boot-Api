package com.student.management.service;

import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private final StudentRepository studentRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder =
            new BCryptPasswordEncoder(10,new SecureRandom());

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public ResponseEntity getAllStudents() {
        try{
            List<Student> studentList = studentRepository.findAll();
            return  ResponseEntity.status(200).body(studentList);

        }catch (Exception e){
            return ResponseEntity.status(500).body(null);
        }

    }

    public ResponseEntity getStudent(Long id) {
        try{
            Optional<Student> studentOptional = studentRepository.findById(id);
            if(studentOptional.isPresent()){
                Student student = studentRepository.findStudentById(id);
                return ResponseEntity.ok().body(student);
            }
            else {
                return ResponseEntity.badRequest().body("User could not be found.");
            }
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Something went wrong please try again later.");
        }
    }

    public ResponseEntity saveStudent(Student student) {
        try {
            Optional<Student> studentOptional = studentRepository.findByEmail(student.email.toLowerCase());
            if (studentOptional.isPresent()){
                return ResponseEntity.badRequest().body("Email is already taken.");
            }

            String hashedPassword = bCryptPasswordEncoder.encode(student.password);

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

    public ResponseEntity login(Student student) {
        Optional<Student> studentOptional = studentRepository.findByEmail(student.email.toLowerCase());
        HashMap<String,Object> response = new HashMap<String, Object>();
        if (!studentOptional.isPresent()){
            response.put("status",400);
            response.put("message","Email or password is not correct");
            return ResponseEntity.ok().body(response);
        }
        Boolean matcher = bCryptPasswordEncoder.matches(student.password,studentOptional.get().password);
        if (!matcher){
            response.put("status",400);
            response.put("message","Email or password is not correct");
            return ResponseEntity.ok().body(response);
        }
        response.put("status",200);
        response.put("message","Login success");
        response.put("data",studentOptional.get());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity deleteStudent(Long id) {
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
    public ResponseEntity editStudent(Long id, Student student) {
        try{
            Optional<Student> studentOptional = studentRepository.findById(id);
            if (student.email != ""){
                studentOptional.get().email = student.email.toLowerCase();
            }
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
        try{
            List<Student> studentList = studentRepository.findByNameIsContaining(name);
            return ResponseEntity.ok().body(studentList);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Something went wrong please try again later.");
        }
    }
}
