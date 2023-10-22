package pl.kurs.personinformation.factory.specifications;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeSpecification implements SearchSpecification<Employee> {

    private final GeneralPersonSpecification generalSpecification;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Specification<Person> createSpecification(Map<String, String> parameters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            parameters.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    switch (key) {
                        case "currentPosition" -> generalSpecification.addDictionaryValueCriteria(predicates,
                                criteriaBuilder, root, key, value);
                        case "currentSalary" -> generalSpecification.addRangeCriteria(predicates, criteriaBuilder, root,
                                key, value, Double::parseDouble);
                        case "employmentStartDate" -> generalSpecification.addRangeCriteria(predicates, criteriaBuilder,
                                root, key, value, LocalDate::parse);
                    }
                }
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
