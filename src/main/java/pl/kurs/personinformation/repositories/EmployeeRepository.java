package pl.kurs.personinformation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.personinformation.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
