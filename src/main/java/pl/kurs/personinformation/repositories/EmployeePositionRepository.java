package pl.kurs.personinformation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.EmployeePosition;

import java.util.List;

public interface EmployeePositionRepository extends JpaRepository<EmployeePosition, Long> {

    @Query("SELECT ep FROM EmployeePosition ep LEFT JOIN FETCH ep.employee e LEFT JOIN FETCH ep.position p " +
            "WHERE e.id = :employeeId")
    List<EmployeePosition> findByEmployee(long employeeId);

}
