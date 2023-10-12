package pl.kurs.personinformation.commands;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRetireeCommand extends UpdatePersonCommand {

    @PositiveOrZero(message = "Cannot be null; must be positive")
    private Double pension;

    @PositiveOrZero(message = "Cannot be null; must be positive")
    private Integer yearsOfWork;

}
