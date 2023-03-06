package com.student.management.controller;


import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import com.student.management.service.StudentService;
import com.student.management.util.Util;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/api")
public class StudentController {

    @Autowired
    private final StudentService studentService;

    @Autowired
    private final StudentRepository studentRepository;

    public StudentController(StudentService studentService, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @GetMapping(path ="/students")
    public ResponseEntity getAllStudents(){
        /**
         Api end point to retrieve all the users from repository
         */
        return studentService.getAllStudents();
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @GetMapping(path = "/students/{id}")
    public ResponseEntity getStudent(@PathVariable("id") Long id) {
        /**
         Api end point to retrieve a student by Id
          */
        return studentService.getStudent(id);
    }


    @PostMapping(path = "/students")
    public ResponseEntity saveStudent(@RequestBody Student student){
        /**
         Api end point to save a student
         */
        return studentService.saveStudent(student);
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {
        /**
         Api end point to upload student image to the database
         */

        if (file.isEmpty()) {
            // If request body is empty return bad request
            return ResponseEntity.badRequest().build();
        }

        try {
            // If file exists get the bytes
            byte[] bytes = file.getBytes();
            // Upload it to the folder specified in Utils
            Path path = Paths.get(Util.MediaDirectory() + "\\" + file.getOriginalFilename());
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Checks the format of the file uploaded path if its os is windows or linux based.
        if(System.getProperty("os.name").toLowerCase().contains("window")){
            return ResponseEntity.ok(Util.MediaDirectory() + "\\" + file.getOriginalFilename());
        }
        else {
            return ResponseEntity.ok(Util.MediaDirectory() + "/" + file.getOriginalFilename());
        }
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @DeleteMapping(path = "/students/{id}")
    public ResponseEntity deleteStudent(@PathVariable Long id){
        /**
         Api end point to delete a student by Id
         */
        return studentService.deleteStudent(id);
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @PutMapping(path = "/students/{id}")
    public ResponseEntity editStudent(@PathVariable Long id, @RequestBody Student student, Neo4jProperties.Authentication authentication){
        /**
         Api end point to edit student information.
          */
        return studentService.editStudent(id,student,authentication);
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @GetMapping(path = "/search")
    public ResponseEntity searchStudent(@RequestParam String name){
        /**
         Api end point to search for a student by name.
         */
        return studentService.searchStudent(name);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity getImageStream(@PathVariable("id") long id) throws IOException {
        /**
         Api end point to stream Image files from upload folder.
         */
        try {
            // Get the Image path from the student
            String path = studentRepository.findById(id).get().photo;
            // Put it in a map
            MultiValueMap<String, Object> files = new LinkedMultiValueMap<>();
            files.add("files", new FileSystemResource(path));

            // Get the file resources
            FileSystemResource resource = (FileSystemResource)files.get("files").get(0);
            byte[] imageBytes;

            // Try-with-resources statement to ensure the stream is closed
            try (InputStream inputStream = resource.getInputStream()) {
                imageBytes = IOUtils.toByteArray(inputStream);
            }

            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (FileNotFoundException e) {
            return ResponseEntity.internalServerError().body("Something went wrong please try again later");
        }
    }



}
