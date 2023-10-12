package pl.kurs.personinformation.services;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Person;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;

@Service
public class PersonSearchSpecificationService {

    public Specification<Person> filterByCriteria(Map<String, String> parameters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            parameters.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    switch (key) {
                        case "type", "universityName", "fieldOfStudy", "currentPosition" ->
                                addDictionaryValueCriteria(predicates, criteriaBuilder, root, key, value);
                        case "firstName", "lastName", "email", "pesel" ->
                                addStringCriteria(predicates, criteriaBuilder, root, key, value);
                        case "weight", "height", "enrollmentYear", "yearsOfWork" ->
                                addRangeCriteria(predicates, criteriaBuilder, root, key, value, Integer::parseInt);
                        case "scholarship", "currentSalary", "pension" ->
                                addRangeCriteria(predicates, criteriaBuilder, root, key, value, Double::parseDouble);
                        case "employmentStartDate" ->
                                addRangeCriteria(predicates, criteriaBuilder, root, key, value, LocalDate::parse);
                        case "sex" -> addSexCriteria(predicates, criteriaBuilder, root, value);
                    }
                }
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addDictionaryValueCriteria(List<Predicate> predicates, CriteriaBuilder builder, Root<Person> root,
                                            String key, String value) {
        predicates.add(builder.equal(builder.lower(root.get(key).get("name")), value.toLowerCase()));
    }

    private void addStringCriteria(List<Predicate> predicates, CriteriaBuilder builder, Root<Person> root,
                                   String key, String value) {
        predicates.add(builder.equal(builder.lower(root.get(key)), value.toLowerCase()));
    }

    private <T extends Comparable<? super T>> void addRangeCriteria(List<Predicate> predicates, CriteriaBuilder builder,
                                                                    Root<Person> root, String key, String value,
                                                                    Function<String, T> parser) {
        String[] parts = value.split(",to");
        if (parts.length == 2) {
            try {
                T lowerBound = parser.apply(parts[0].substring(4));
                T upperBound = parser.apply(parts[1]);
                predicates.add(builder.between(root.get(key), lowerBound, upperBound));
            } catch (NumberFormatException | DateTimeParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void addSexCriteria(List<Predicate> predicates, CriteriaBuilder builder, Root<Person> root,
                                String value) {
        Expression<String> peselExpression = root.get("pesel");
        Expression<Character> sexExpression = builder
                .function("SUBSTRING", Character.class, peselExpression, builder.literal(10), builder.literal(1));
        Map<String, List<Character>> sexMap = new HashMap<>();
        sexMap.put("m", Arrays.asList('1', '3', '5', '7', '9'));
        sexMap.put("w", Arrays.asList('0', '2', '4', '6', '8'));
        List<Character> validSexChars = sexMap.get(value.toLowerCase());
        if (validSexChars != null)
            predicates.add(sexExpression.in(validSexChars));
    }

}