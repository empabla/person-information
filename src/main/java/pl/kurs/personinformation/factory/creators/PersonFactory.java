package pl.kurs.personinformation.factory.creators;

import lombok.Getter;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.commands.CreatePersonFromCsvCommand;
import pl.kurs.personinformation.models.Person;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Getter
public class PersonFactory {

    private final Map<String, PersonCreator> creators;

    public PersonFactory(Set<PersonCreator> creators) {
        this.creators = creators.stream()
                .collect(Collectors.toMap(PersonCreator::getType, Function.identity()));
    }

    public Person create(CreatePersonFromCsvCommand command) {
        return creators.get(command.getPersonType().toLowerCase())
                .createPerson(command.getParameters());
    }

}
