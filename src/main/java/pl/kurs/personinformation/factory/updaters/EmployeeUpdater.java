package pl.kurs.personinformation.factory.updaters;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.commands.UpdateEmployeeCommand;
import pl.kurs.personinformation.commands.UpdatePersonCommand;
import pl.kurs.personinformation.exceptions.WrongTypeException;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.repositories.PersonRepository;
import pl.kurs.personinformation.services.DictionaryValueService;

@Service
@RequiredArgsConstructor
public class EmployeeUpdater implements PersonUpdater {

    private final DictionaryValueService dictionaryValueService;

    private final PersonRepository personRepository;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Person updatePerson(UpdatePersonCommand updatePersonCommand) {
        try {
            UpdateEmployeeCommand employeeCommand = modelMapper.map(updatePersonCommand, UpdateEmployeeCommand.class);
            Employee employeeForUpdate = (Employee) personRepository.findById(employeeCommand.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No entity found"));
            employeeForUpdate.setFirstName(employeeCommand.getFirstName());
            employeeForUpdate.setLastName(employeeCommand.getLastName());
            employeeForUpdate.setPesel(employeeCommand.getPesel());
            employeeForUpdate.setHeight(employeeCommand.getHeight());
            employeeForUpdate.setWeight(employeeCommand.getWeight());
            employeeForUpdate.setEmail(employeeCommand.getEmail());
            employeeForUpdate.setVersion(employeeCommand.getVersion());
            employeeForUpdate.setEmploymentStartDate(employeeCommand.getEmploymentStartDate());
            dictionaryValueService.validateDictionaryValue(employeeCommand.getPosition());
            employeeForUpdate.setCurrentPosition(dictionaryValueService.getByName(employeeCommand.getPosition()));
            employeeForUpdate.setCurrentSalary(employeeCommand.getSalary());
            return employeeForUpdate;
        } catch (ClassCastException e) {
            throw new WrongTypeException("The type in the request body does not match the entity type");
        }
    }

}
