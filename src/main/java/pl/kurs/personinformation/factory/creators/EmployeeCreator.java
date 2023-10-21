package pl.kurs.personinformation.factory.creators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.services.DictionaryValueService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeCreator implements PersonCreator {

    private final DictionaryValueService dictionaryValueService;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Person createPerson(Map<String, Object> parameters) {
        return new Employee(
                dictionaryValueService.getByName(this.getType()),
                getStringParameter("firstName", parameters),
                getStringParameter("lastName", parameters),
                getStringParameter("pesel", parameters),
                getIntegerParameter("height", parameters),
                getIntegerParameter("weight", parameters),
                getStringParameter("email", parameters),
                getLocalDateParameter("employmentStartDate", parameters),
                dictionaryValueService.getByName(getStringParameter("currentPosition", parameters)),
                getDoubleParameter("salary", parameters)
        );
    }

}
