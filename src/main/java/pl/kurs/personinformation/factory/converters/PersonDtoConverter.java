package pl.kurs.personinformation.factory.converters;

import pl.kurs.personinformation.dto.PersonDto;
import pl.kurs.personinformation.models.Person;

public interface PersonDtoConverter {

    String getType();

    PersonDto convert(Person person);

}
