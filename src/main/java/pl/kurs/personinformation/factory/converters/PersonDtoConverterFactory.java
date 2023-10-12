package pl.kurs.personinformation.factory.converters;

import lombok.Getter;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.dto.PersonDto;
import pl.kurs.personinformation.models.Person;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Getter
public class PersonDtoConverterFactory {

    private final Map<String, PersonDtoConverter> converters;

    public PersonDtoConverterFactory(Set<PersonDtoConverter> converters) {
        this.converters = converters.stream()
                .collect(Collectors.toMap(PersonDtoConverter::getType, Function.identity()));
    }

    public PersonDto convert(Person person) {
        return converters.get(person.getType().getName().toLowerCase())
                .convert(person);
    }

}
