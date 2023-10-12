package pl.kurs.personinformation.factory.creators;

import pl.kurs.personinformation.models.Person;

public interface PersonCreator {

    String getType();

    Person createPerson(String[] parameters);

}
