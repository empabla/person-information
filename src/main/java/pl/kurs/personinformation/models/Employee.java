package pl.kurs.personinformation.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
public class Employee extends Person {

    private LocalDate employmentStartDate;

    @ManyToOne
    @JoinColumn(name = "current_position_id")
    private DictionaryValue currentPosition;

    private Double currentSalary;

    public Employee(DictionaryValue type, String firstName, String lastName, String pesel, Integer height,
                    Integer weight, String email, LocalDate employmentStartDate, DictionaryValue currentPosition,
                    Double currentSalary) {
        super(type, firstName, lastName, pesel, height, weight, email);
        this.employmentStartDate = employmentStartDate;
        this.currentPosition = currentPosition;
        this.currentSalary = currentSalary;
    }

}
