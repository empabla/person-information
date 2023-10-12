package pl.kurs.personinformation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentDto extends PersonDto {

    private String universityName;

    private Integer enrollmentYear;

    private String fieldOfStudy;

    private Double scholarship;

}
