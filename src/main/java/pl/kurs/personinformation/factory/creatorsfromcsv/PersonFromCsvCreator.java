package pl.kurs.personinformation.factory.creatorsfromcsv;

import pl.kurs.personinformation.models.Person;

public interface PersonFromCsvCreator {

    String getType();

    Person createPerson(String[] parameters);

}
