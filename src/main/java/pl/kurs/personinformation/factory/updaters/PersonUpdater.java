package pl.kurs.personinformation.factory.updaters;

import pl.kurs.personinformation.commands.UpdatePersonCommand;
import pl.kurs.personinformation.models.Person;

public interface PersonUpdater {

    String getType();

    Person updatePerson(UpdatePersonCommand updateCommand);

}
