package pl.kurs.personinformation.factory.creators;

import pl.kurs.personinformation.models.Person;

import java.time.LocalDate;
import java.util.Map;

public interface PersonCreator {

    String getType();

    Person createPerson(Map<String, Object> parameters);

    default String getStringParameter(String name, Map<String, Object> parameters) {
        return (String) parameters.get(name);
    }

    default Integer getIntegerParameter(String name, Map<String, Object> parameters) {
        return (Integer) parameters.get(name);
    }

    default Double getDoubleParameter(String name, Map<String, Object> parameters) {
        return (Double) parameters.get(name);
    }

    default LocalDate getLocalDateParameter(String name, Map<String, Object> parameters) {
        return (LocalDate) parameters.get(name);
    }

}
