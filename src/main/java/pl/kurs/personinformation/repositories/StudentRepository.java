package pl.kurs.personinformation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.personinformation.models.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
