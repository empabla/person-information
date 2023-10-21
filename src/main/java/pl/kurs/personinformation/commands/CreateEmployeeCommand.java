package pl.kurs.personinformation.commands;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.personinformation.validators.LettersOnly;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateEmployeeCommand extends CreatePersonCommand {

    @NotNull(message = "Cannot be null")
    private LocalDate employmentStartDate;

    @LettersOnly(message = "Cannot be null; should exist in 'positions' dictionary")
    private String position;

    @PositiveOrZero(message = "Cannot be null; must be positive")
    private Double salary;

}
