package pl.kurs.personinformation.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DictionaryValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dictionary_value")
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

    public DictionaryValue(String name, Dictionary dictionary) {
        this.name = name;
        this.dictionary = dictionary;
    }

    public DictionaryValue(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
