package pl.kurs.personinformation.factory.creatorsfromcsv;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.services.DictionaryValueService;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class EmployeeFromCsvCreator implements PersonFromCsvCreator {

    private final DictionaryValueService dictionaryValueService;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Person createPerson(String[] parameters) {
        return new Employee(
                dictionaryValueService.getByNameFromDictionary(parameters[0].trim(), "types"),
                parameters[1].trim(),
                parameters[2].trim(),
                parameters[3].trim(),
                Integer.parseInt(parameters[4].trim()),
                Integer.parseInt(parameters[5].trim()),
                parameters[6].trim(),
                LocalDate.parse(parameters[7].trim()),
                dictionaryValueService.getByNameFromDictionary(parameters[8].trim(), "positions"),
                Double.parseDouble(parameters[9].trim())
        );
    }

}

