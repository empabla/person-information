package pl.kurs.personinformation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.personinformation.models.Dictionary;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    Dictionary findByName(String name);

    boolean existsByName(String name);

}
