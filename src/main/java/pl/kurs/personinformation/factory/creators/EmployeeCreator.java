package pl.kurs.personinformation.factory.creators;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.commands.CreateEmployeeCommand;
import pl.kurs.personinformation.commands.CreatePersonCommand;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.services.DictionaryValueService;

@Service
@RequiredArgsConstructor
public class EmployeeCreator implements PersonCreator {

    private final DictionaryValueService dictionaryValueService;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Person createPerson(CreatePersonCommand createPersonCommand) {
        CreateEmployeeCommand employeeCommand = modelMapper.map(createPersonCommand, CreateEmployeeCommand.class);
        return new Employee(
                dictionaryValueService.getByNameFromDictionary(this.getType(), "types"),
                employeeCommand.getFirstName(),
                employeeCommand.getLastName(),
                employeeCommand.getPesel(),
                employeeCommand.getHeight(),
                employeeCommand.getWeight(),
                employeeCommand.getEmail(),
                employeeCommand.getEmploymentStartDate(),
                dictionaryValueService.getByNameFromDictionary(
                        employeeCommand.getCurrentPosition().toLowerCase(), "positions"
                ),
                employeeCommand.getCurrentSalary()
        );
    }

}
