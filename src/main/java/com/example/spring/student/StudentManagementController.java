package com.example.spring.student;

import com.example.spring.exceptions.StudentException;
import com.example.spring.exceptions.StudentNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "management/api/v1/students")
public class StudentManagementController {

    private final StudentService studentService;

    public StudentManagementController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> findAllStudents() {
        return studentService.findAllStudents();
    }

    @GetMapping("{id}")
    public Student findStudentById(@PathVariable("id") Integer studentId) throws StudentNotFoundException {
        return studentService.findStudentById(studentId);
    }

    @PostMapping
    public void addNewStudent(@RequestBody StudentRequest student) throws StudentException {
        studentService.addNewStudent(student);
    }

    @DeleteMapping("{id}")
    public void deleteStudentById(@PathVariable("id") Integer studentId) throws StudentNotFoundException {
        studentService.deleteStudentById(studentId);
    }

    @PutMapping("{id}")
    public void updateStudent(@PathVariable("id") Integer studentId,
                              @RequestBody StudentRequest student) throws StudentException {
        studentService.updateStudentById(studentId, student);
    }
}

