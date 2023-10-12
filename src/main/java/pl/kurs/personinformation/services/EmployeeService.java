package pl.kurs.personinformation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.exceptions.DictionaryValueNotFoundException;
import pl.kurs.personinformation.exceptions.PersonNotFoundException;
import pl.kurs.personinformation.exceptions.WrongEntityException;
import pl.kurs.personinformation.exceptions.WrongIdException;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.repositories.EmployeeRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee getById(Long id) {
        return employeeRepository.findById(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new WrongIdException("Wrong id."))
        ).orElseThrow(() -> new PersonNotFoundException("Employee with id " + id + " not found."));
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(
                Optional.ofNullable(employee)
                        .filter(x -> Objects.isNull(x.getId()))
                        .orElseThrow(() -> new WrongEntityException("Wrong entity for persist."))
        );
    }

}
