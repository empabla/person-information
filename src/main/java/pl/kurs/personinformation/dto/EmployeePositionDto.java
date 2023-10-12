package pl.kurs.personinformation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class EmployeePositionDto {

    private String position;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double salary;

}
