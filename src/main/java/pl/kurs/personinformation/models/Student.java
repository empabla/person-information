package pl.kurs.personinformation.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
public class Student extends Person {

    @ManyToOne
    @JoinColumn(name = "university_name_id")
    private DictionaryValue universityName;

    private Integer enrollmentYear;

    @ManyToOne
    @JoinColumn(name = "field_of_study_id")
    private DictionaryValue fieldOfStudy;

    private Double scholarship;

    public Student(DictionaryValue type, String firstName, String lastName, String pesel, Integer height,
                   Integer weight, String email, DictionaryValue universityName, Integer enrollmentYear,
                   DictionaryValue fieldOfStudy, Double scholarship) {
        super(type, firstName, lastName, pesel, height, weight, email);
        this.universityName = universityName;
        this.enrollmentYear = enrollmentYear;
        this.fieldOfStudy = fieldOfStudy;
        this.scholarship = scholarship;
    }

}
