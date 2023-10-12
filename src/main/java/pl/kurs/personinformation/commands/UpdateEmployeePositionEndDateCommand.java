package pl.kurs.personinformation.commands;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeePositionEndDateCommand {

    @NotNull(message = "Cannot be null")
    private LocalDate endDate;

}
