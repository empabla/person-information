package pl.kurs.personinformation.factory.creators;

import pl.kurs.personinformation.commands.CreatePersonCommand;
import pl.kurs.personinformation.models.Person;

public interface PersonCreator {

    String getType();

    Person createPerson(CreatePersonCommand createCommand);

}
