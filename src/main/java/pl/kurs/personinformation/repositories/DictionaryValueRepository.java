package pl.kurs.personinformation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.models.DictionaryValue;

import java.util.List;

public interface DictionaryValueRepository extends JpaRepository<DictionaryValue, Long> {

    DictionaryValue findByName(String name);

    boolean existsByName(String name);

    List<DictionaryValue> getAllByDictionary(Dictionary dictionary);

}