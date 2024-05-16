package com.spring.student;

import java.util.List;
import java.util.Optional;

public interface StudentDAO {
    List<Student> findAllStudents();
    Optional<Student> findStudentById(Integer studentId);
    boolean existsStudentByEmail(String email);
    void saveStudent(Student student);
    void deleteStudentById(Integer studentId);
    void updateStudent(Student student);
}
