package pl.kurs.personinformation.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CreateEmployeePositionCommand {

    @NotBlank(message = "Cannot be null/empty")
    private String position;

    @NotNull(message = "Cannot be null")
    private LocalDate startDate;

    @PositiveOrZero(message = "Cannot be null; must be positive")
    private Double salary;

}
