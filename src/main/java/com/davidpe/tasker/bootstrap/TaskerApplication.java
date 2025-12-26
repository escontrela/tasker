package com.davidpe.tasker.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.davidpe.tasker")
public class TaskerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskerApplication.class, args);
    }
}
