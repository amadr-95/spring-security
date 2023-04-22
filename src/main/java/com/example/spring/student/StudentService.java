package com.example.spring.student;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    //GET
    public List<Student> getStudents(){
        return studentRepository.findAll();
    }

    //POST
    public void addNewStudent(Student student) {
        Optional<Student> optionalStudent = studentRepository.findStudentByEmail(student.getEmail());
        if(optionalStudent.isPresent())
            throw new IllegalArgumentException("email taken");
        studentRepository.save(student);
    }

    //DELETE
    public void deleteStudent(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if(student.isEmpty())
            throw new IllegalArgumentException("id "+ studentId +" does not exist");
        studentRepository.deleteById(studentId);

    }
    //PUT
    @Transactional
    public void updateStudent(Long studentId, String name, String email) {
        //Otra forma
        /*Student student = studentRepository.findStudentByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("id "+studentId+" does not exist"));*/
        if(!studentRepository.existsById(studentId))
            throw new IllegalArgumentException("id "+studentId+" does not exist");

        Student student = studentRepository.getReferenceById(studentId);
        if(name != null && !name.isEmpty() && !name.equalsIgnoreCase(student.getName()))
            student.setName(name);

        if(email != null && email.contains("@") && !email.equalsIgnoreCase(student.getEmail())){
            //comprobamos q ese mail no lo tenga otra persona
            if(studentRepository.findStudentByEmail(email).isPresent())
                throw new IllegalArgumentException("email "+ email + " already exists");
            student.setEmail(email);
        }
    }
}
