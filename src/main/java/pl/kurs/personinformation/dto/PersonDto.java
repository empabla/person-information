package pl.kurs.personinformation.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        use = JsonTypeInfo.Id.NAME,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StudentDto.class, name = "student"),
        @JsonSubTypes.Type(value = EmployeeDto.class, name = "employee"),
        @JsonSubTypes.Type(value = RetireeDto.class, name = "retiree")
})
@Getter
@Setter
@NoArgsConstructor
public class PersonDto {

    private String type;

    private String firstName;

    private String lastName;

    private String email;

    private Long version;

}