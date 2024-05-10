package com.example.student;

import java.time.LocalDate;

public record StudentRequest(
        String name,
        String email,
        LocalDate birth
) {
}
