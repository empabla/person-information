package pl.kurs.personinformation.commands;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.personinformation.validators.LettersOnly;

@Getter
@Setter
@AllArgsConstructor
public class CreatePersonFromCsvCommand {

    @LettersOnly(message = "Field cannot be null; can contain only letters, should exist in 'types' dictionary")
    private String personType;

    @Size(min = 7)
    private String[] parameters;

}
