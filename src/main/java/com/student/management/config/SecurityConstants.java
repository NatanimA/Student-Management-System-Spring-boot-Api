package com.student.management.config;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 900_000000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/students";

    public static final String UPLOAD_URL = "/api/upload";

    public static final String LOAD_IMAGE = "/api/image/{id}";

    public static final String DELETE_USER = "/api/students/{id}";

    public static final  String SECRET = "STUDENTMANAGEMENT";
}
