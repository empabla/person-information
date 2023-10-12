package pl.kurs.personinformation.validators.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.validators.LettersOnly;

public class LettersOnlyValidator implements ConstraintValidator<LettersOnly, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && !s.isEmpty() && s.matches("^[a-zA-Z ]*$");
    }

}
