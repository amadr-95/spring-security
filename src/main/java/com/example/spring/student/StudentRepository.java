package com.example.spring.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    //SELECT * FROM student WHERE email = ?
    @Query("SELECT s FROM Student s WHERE s.email = ?1")
    Optional<Student> findStudentByEmail(String email);
    //El propio metodo con el nombre adecuado deberia "hacer su magia".
    //El equivalente seria la query de arriba (esta escrita en jpql, no sql)
    //Se puede dejar, siendo asi mas especifico

}
