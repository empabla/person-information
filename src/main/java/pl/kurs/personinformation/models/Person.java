package pl.kurs.personinformation.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "people", uniqueConstraints = {@UniqueConstraint(name = "UC_PERSON_PESEL", columnNames = "pesel")})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private DictionaryValue type;

    private String firstName;

    private String lastName;

    @Column
    private String pesel;

    private Integer height;

    private Integer weight;

    private String email;

    @Version
    private Long version;

    public Person(DictionaryValue type, String firstName, String lastName, String pesel, Integer height,
                  Integer weight, String email) {
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.height = height;
        this.weight = weight;
        this.email = email;
    }

}
