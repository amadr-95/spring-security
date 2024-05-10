package com.example.student;

import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class StudentConfig {

    private final Faker faker;
    private final int NUMBER_OF_STUDENTS = 30;

    public StudentConfig() {
        this.faker = new Faker();
    }

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository) {
        return args -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            List<Student> students = new ArrayList<>();
            for (int i = 0; i < NUMBER_OF_STUDENTS; i++) {
                Student student = new Student(
                        faker.name().firstName(),
                        faker.internet().emailAddress(),
                        LocalDate.parse(dateFormat.format(faker.date().birthday()))
                );
                students.add(student);
            }
            repository.saveAll(students);
        };
    }
}
