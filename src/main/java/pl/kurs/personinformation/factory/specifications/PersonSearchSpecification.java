package pl.kurs.personinformation.factory.specifications;

import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Person;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Getter
public class PersonSearchSpecification {

    private final SearchSpecification<Person> generalSpecification;
    private final Map<String, SearchSpecification<? extends Person>> typeSpecifications;

    public PersonSearchSpecification(SearchSpecification<Person> generalSpecification,
                                     Set<SearchSpecification<? extends Person>> typeSpecifications) {
        this.generalSpecification = generalSpecification;
        this.typeSpecifications = typeSpecifications.stream()
                .collect(Collectors.toMap(SearchSpecification::getType, Function.identity()));
    }

    public Specification<Person> filterByCriteria(Map<String, String> parameters) {
        String type = parameters.get("type");
        Specification<Person> generalSpec = generalSpecification.createSpecification(parameters);
        if (type != null) {
            SearchSpecification<? extends Person> typeSpec = typeSpecifications.get(type);
            if (typeSpec != null) {
                Specification<Person> typeSpecification = typeSpec.createSpecification(parameters);
                return generalSpec.and(typeSpecification);
            }
        }
        return generalSpec;
    }

}