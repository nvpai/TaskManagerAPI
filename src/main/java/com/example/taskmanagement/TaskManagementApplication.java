package com.example.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Task Management API.
 * This is a simple RESTful API built with Spring Boot to demonstrate
 * clean architecture and test-driven development (TDD) practices.
 */
@SpringBootApplication
public class TaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
    }
}