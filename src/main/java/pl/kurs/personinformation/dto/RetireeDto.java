package pl.kurs.personinformation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RetireeDto extends PersonDto {

    private Double pension;

    private Integer yearsOfWork;

}
