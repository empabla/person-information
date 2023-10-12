package pl.kurs.personinformation.validators.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.validators.Pesel;

import java.util.Optional;

public class PeselValidator implements ConstraintValidator<Pesel, String> {

    @Override
    public boolean isValid(String pesel, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Optional.ofNullable(pesel)
                    .filter(x -> x.matches("\\d{11}"))
                    .orElseThrow(() -> new RuntimeException("Pesel validation failed."));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

}
