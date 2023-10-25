package pl.kurs.personinformation.factory.creatorsfromcsv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Student;
import pl.kurs.personinformation.services.DictionaryValueService;

@Service
@RequiredArgsConstructor
public class StudentFromCsvCreator implements PersonFromCsvCreator {

    private final DictionaryValueService dictionaryValueService;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public Person createPerson(String[] parameters) {
        return new Student(
                dictionaryValueService.getByNameFromDictionary(parameters[0].trim(), "types"),
                parameters[1].trim(),
                parameters[2].trim(),
                parameters[3].trim(),
                Integer.parseInt(parameters[4].trim()),
                Integer.parseInt(parameters[5].trim()),
                parameters[6].trim(),
                dictionaryValueService.getByNameFromDictionary(parameters[7].trim(), "university names"),
                Integer.parseInt(parameters[8].trim()),
                dictionaryValueService.getByNameFromDictionary(parameters[9].trim(), "fields of study"),
                Double.parseDouble(parameters[10].trim())
        );
    }

}

