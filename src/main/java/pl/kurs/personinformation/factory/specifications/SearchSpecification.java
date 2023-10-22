package pl.kurs.personinformation.factory.specifications;

import org.springframework.data.jpa.domain.Specification;
import pl.kurs.personinformation.models.Person;

import java.util.Map;

public interface SearchSpecification<T extends Person> {

    String getType();

    Specification<Person> createSpecification(Map<String, String> parameters);

}
