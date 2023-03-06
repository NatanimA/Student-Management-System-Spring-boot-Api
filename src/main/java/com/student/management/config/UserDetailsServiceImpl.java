package com.student.management.config;

import com.student.management.model.Student;
import com.student.management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        /**
         This will be executed at runtime to check if a Student is who they say they are.
         */
        Optional<Student> user =
                studentRepository.findByEmail(email);
        User.UserBuilder builder = null;
        if (user.isPresent()) {
            Student currentUser = user.get();
            builder =
                    org.springframework.security.core.userdetails.
                            User.withUsername(email);
            builder.password(currentUser.password);
            builder.roles("user");
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
        return builder.build();
    }
}
