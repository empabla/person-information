package pl.kurs.personinformation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDto extends PersonDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate employmentStartDate;

    private String currentPosition;

    private Double currentSalary;

}
