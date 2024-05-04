package com.example.spring.student;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudentDataAccessService implements StudentDAO {

    private final StudentRepository studentRepository;

    public StudentDataAccessService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Optional<Student> findStudentById(Integer studentId) {
        return studentRepository.findById(studentId);
    }

    @Override
    public boolean existsStudentByEmail(String email) {
        return studentRepository.existsStudentByEmail(email);
    }

    @Override
    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    @Override
    public void deleteStudentById(Integer studentId) {
        studentRepository.deleteById(studentId);
    }

    @Override
    public void updateStudent(Student student) {
        studentRepository.save(student);
    }
}
