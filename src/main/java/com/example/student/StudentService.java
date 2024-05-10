package com.example.student;

import com.example.exceptions.EmailDuplicateException;
import com.example.exceptions.StudentException;
import com.example.exceptions.StudentNotFoundException;
import com.example.exceptions.ValidationException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    //GET
    public List<Student> findAllStudents() {
        return studentDAO.findAllStudents();
    }

    public Student findStudentById(Integer id) throws StudentNotFoundException {
        return studentDAO.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student with id " + id + " does not exist"));
    }

    //POST
    public void addNewStudent(StudentRequest studentRequest)
            throws StudentException {
        //check fields
        checkStudentFields(studentRequest);
        //check if email already exists
        existsStudentByEmail(studentRequest.email());
        //save student
        Student newStudent = new Student
                (studentRequest.name(),
                        studentRequest.email(),
                        studentRequest.birth()
                );

        studentDAO.saveStudent(newStudent);
    }

    //DELETE
    public void deleteStudentById(Integer studentId) throws StudentNotFoundException {
        findStudentById(studentId);
        studentDAO.deleteStudentById(studentId);
    }

    //PUT
    @Transactional
    public void updateStudentById(Integer studentId, StudentRequest studentRequest)
            throws StudentException {
        Student student = findStudentById(studentId);

        String nameRequest = studentRequest.name();
        String emailRequest = studentRequest.email();
        LocalDate birth = studentRequest.birth();

        if (emailRequest != null && !emailRequest.isBlank() && !emailRequest.equalsIgnoreCase(student.getEmail())) {
            existsStudentByEmail(emailRequest);
            student.setEmail(emailRequest);
        }

        if (nameRequest != null && !nameRequest.isBlank())
            student.setName(nameRequest);

        if (birth != null && !birth.toString().isBlank()) {
            student.setBirth(birth);
        }

        studentDAO.updateStudent(student);
    }

    private void checkStudentFields(StudentRequest studentRequest) throws StudentException {
        if (studentRequest == null)
            throw new StudentException("student can not be null");

        String nameRequest = studentRequest.name();
        String emailRequest = studentRequest.email();
        LocalDate birthRequest = studentRequest.birth();

        if (nameRequest == null || nameRequest.isBlank() ||
                emailRequest == null || emailRequest.isBlank() ||
                birthRequest == null || birthRequest.toString().isBlank()
        )
            throw new ValidationException("missing field/s");
    }

    private boolean existsStudentByEmail(String email) throws EmailDuplicateException {
        if (studentDAO.existsStudentByEmail(email))
            throw new EmailDuplicateException("email already taken");
        return false;
    }
}
