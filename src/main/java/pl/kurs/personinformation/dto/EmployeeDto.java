package pl.kurs.personinformation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDto extends PersonDto {

    private LocalDate employmentStartDate;

    private String position;

    private Double salary;

}
