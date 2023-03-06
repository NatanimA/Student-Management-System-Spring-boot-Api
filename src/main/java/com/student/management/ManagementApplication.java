package com.student.management;

import com.student.management.util.Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

@SpringBootApplication
public class ManagementApplication {
	public static void main(String[] args) {
        // Make media directory in resources
        new File(Util.MediaDirectory()).mkdir();
        SpringApplication.run(ManagementApplication.class, args);
	}

    //Create a Bean to be used at run time for encryption and decryption
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
