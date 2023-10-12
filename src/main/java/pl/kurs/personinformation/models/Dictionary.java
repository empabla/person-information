package pl.kurs.personinformation.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dictionary")
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dictionary")
    private Set<DictionaryValue> dictionaryValues = new HashSet<>();

    public Dictionary(String name) {
        this.name = name;
    }

}
