package pl.kurs.personinformation.factory.updaters;

import lombok.Getter;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.commands.UpdatePersonCommand;
import pl.kurs.personinformation.models.Person;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Getter
public class PersonUpdaterFactory {

    private final Map<String, PersonUpdater> updaters;

    public PersonUpdaterFactory(Set<PersonUpdater> updaters) {
        this.updaters = updaters.stream()
                .collect(Collectors.toMap(PersonUpdater::getType, Function.identity()));
    }

    public Person update(UpdatePersonCommand command) {
        return updaters.get(command.getType().toLowerCase())
                .updatePerson(command);
    }

}