package pl.kurs.personinformation.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.kurs.personinformation.validators.impl.PeselValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = PeselValidator.class)
@Target({FIELD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pesel {

    String message() default "Pesel cannot be null; should contain 11 digits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
