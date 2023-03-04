package com.student.management.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import com.student.management.service.StudentService;
import org.apache.commons.io.IOUtils;
import org.slf4j.ILoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static com.student.management.config.SecurityConstants.EXPIRATION_TIME;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/api")
public class StudentController {

    @Autowired
    private final StudentService studentService;

    @Autowired
    private final StudentRepository studentRepository;

    private AuthenticationManager authenticationManager;

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${file.upload.folder}")
    private String uploadFolder;

    public StudentController(StudentService studentService, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
    }

//    @RequestMapping(value="/service/login", method=RequestMethod.POST)
//    public ResponseEntity<?> getToken(@RequestBody Student credentials) {
//        // Generate token and send it in the response
//        //Authorization
//        // header
//        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(credentials.email, credentials.password);
//        Authentication auth = authenticationManager.authenticate(creds);
//        String jwts = JWT.create()
//                .withSubject(((Student) auth.getPrincipal()).email)
//                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .sign(Algorithm.HMAC512(SECRET.getBytes()));
//
//        return  ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwts).
//                header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization").build();
//
//    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @GetMapping(path ="/students")
    public ResponseEntity getAllStudents(){
        return studentService.getAllStudents();
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @GetMapping(path = "/students/{id}")
    public ResponseEntity getStudent(@PathVariable("id") Long id) {
        return studentService.getStudent(id);
    }


    @PostMapping(path = "/students")
    public ResponseEntity saveStudent(@RequestBody Student student){
        return studentService.saveStudent(student);
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadFolder + file.getOriginalFilename());
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(uploadFolder + file.getOriginalFilename());
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @PostMapping(path = "/login")
    public ResponseEntity login(@RequestBody Student student){
        return studentService.login(student);
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @DeleteMapping(path = "/students/{id}")
    public ResponseEntity deleteStudent(@PathVariable Long id){
        return studentService.deleteStudent(id);
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @PutMapping(path = "/students/{id}")
    public ResponseEntity editStudent(@PathVariable Long id,@RequestBody Student student){
        return studentService.editStudent(id,student);
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @GetMapping(path = "/search")
    public ResponseEntity searchStudent(@RequestParam String name){
        return studentService.searchStudent(name);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity getImageStream(@PathVariable("id") long id) throws IOException {
        try {
            String path = studentRepository.findById(id).get().photo;
            FileInputStream fileInputStream = new FileInputStream(path);
            MultiValueMap<String, Object> files = new LinkedMultiValueMap<>();
            files.add("files", new FileSystemResource(path));
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
