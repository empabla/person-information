package pl.kurs.personinformation.commands;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.personinformation.validators.LettersOnly;
import pl.kurs.personinformation.validators.Pesel;

@JsonTypeInfo(
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        use = JsonTypeInfo.Id.NAME,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpdateStudentCommand.class, name = "student"),
        @JsonSubTypes.Type(value = UpdateEmployeeCommand.class, name = "employee"),
        @JsonSubTypes.Type(value = UpdateRetireeCommand.class, name = "retiree")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePersonCommand {

    @Positive()
    private Long id;

    @LettersOnly(message = "Field cannot be null; can contain only letters; should exist in 'types' dictionary")
    private String type;

    @LettersOnly
    private String firstName;

    @LettersOnly
    private String lastName;

    @Pesel
    private String pesel;

    @Positive(message = "Cannot be null; must be positive")
    private Integer height;

    @Positive(message = "Cannot be null; must be positive")
    private Integer weight;

    @Email
    private String email;

    @PositiveOrZero(message = "Cannot be null; must be positive")
    private Long version;

}
