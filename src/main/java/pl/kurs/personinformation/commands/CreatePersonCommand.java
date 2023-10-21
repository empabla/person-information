package pl.kurs.personinformation.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.personinformation.validators.LettersOnly;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class CreatePersonCommand {

    @LettersOnly(message = "Field cannot be null; can contain only letters, should exist in 'types' dictionary")
    private String type;

    private Map<String, Object> parameters;

}
