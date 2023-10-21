package pl.kurs.personinformation.commands;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.personinformation.validators.LettersOnly;

@Getter
@Setter
@NoArgsConstructor
public class CreateStudentCommand extends CreatePersonCommand {

    @LettersOnly(message = "Cannot be null; should exist in 'university names' dictionary")
    private String universityName;

    @Positive(message = "Cannot be null; must be positive")
    @Max(value = 6, message = "Should not be greater than 6")
    private Integer enrollmentYear;

    @LettersOnly(message = "Cannot be null; should exist in 'fields of study' dictionary")
    private String fieldOfStudy;

    @PositiveOrZero(message = "Cannot be null; must be positive")
    private Double scholarship;

}
