package pl.kurs.personinformation.factory.converters;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.dto.EmployeeDto;
import pl.kurs.personinformation.dto.PersonDto;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.repositories.PersonRepository;
import pl.kurs.personinformation.services.DictionaryValueService;

@Service
@RequiredArgsConstructor
public class EmployeeDtoConverter implements PersonDtoConverter {

    private final ModelMapper modelMapper;

    private final DictionaryValueService dictionaryValueService;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public PersonDto convert(Person person) {
        EmployeeDto employeeDto = new EmployeeDto();
        Employee employee = modelMapper.map(person, Employee.class);
        employeeDto.setType(employee.getType().getName());
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setVersion(employee.getVersion());
        employeeDto.setEmploymentStartDate(employee.getEmploymentStartDate());
        employeeDto.setPosition(dictionaryValueService.getByName(employee.getCurrentPosition().getName()).toString());
        employeeDto.setPosition(employee.getCurrentPosition().getName());
        employeeDto.setSalary(employee.getCurrentSalary());
        return employeeDto;
    }

}
