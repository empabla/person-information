package pl.kurs.personinformation.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class EmployeePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private DictionaryValue position;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double salary;

    public EmployeePosition(Employee employee, DictionaryValue position, LocalDate startDate,
                            LocalDate endDate, Double salary) {
        this.employee = employee;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.setEmployee(employee);
        this.setPosition(position);
    }

}
