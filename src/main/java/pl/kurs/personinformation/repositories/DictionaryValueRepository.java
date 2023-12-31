package pl.kurs.personinformation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.models.DictionaryValue;

public interface DictionaryValueRepository extends JpaRepository<DictionaryValue, Long> {

    DictionaryValue findByName(String name);

    boolean existsByName(String name);

    DictionaryValue findByNameAndDictionary(String name, Dictionary dictionary);

    boolean existsByNameAndDictionary(String name, Dictionary dictionary);

}