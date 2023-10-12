package pl.kurs.personinformation.factory.creators;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.services.DictionaryValueService;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class EmployeeCreator implements PersonCreator {

    private final DictionaryValueService dictionaryValueService;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Person createPerson(String[] parameters) {
        return new Employee(
                dictionaryValueService.getByName(parameters[0].trim()),
                parameters[1].trim(),
                parameters[2].trim(),
                parameters[3].trim(),
                Integer.parseInt(parameters[4].trim()),
                Integer.parseInt(parameters[5].trim()),
                parameters[6].trim(),
                LocalDate.parse(parameters[7].trim()),
                dictionaryValueService.getByName(parameters[8].trim()),
                Double.parseDouble(parameters[9].trim())
        );
    }

}

