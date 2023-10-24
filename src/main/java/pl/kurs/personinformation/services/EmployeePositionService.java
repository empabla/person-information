package pl.kurs.personinformation.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.personinformation.commands.CreateEmployeePositionCommand;
import pl.kurs.personinformation.commands.UpdateEmployeePositionEndDateCommand;
import pl.kurs.personinformation.exceptions.*;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.EmployeePosition;
import pl.kurs.personinformation.repositories.EmployeePositionRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeePositionService {

    private final EmployeePositionRepository employeePositionRepository;

    private final EmployeeService employeeService;

    private final DictionaryValueService dictionaryValueService;

    private final ModelMapper modelMapper;

    @Transactional
    public EmployeePosition addPositionToEmployee(Long employeeId, CreateEmployeePositionCommand command) {
        Employee employee = employeeService.getById(employeeId);
        DictionaryValue newPosition = dictionaryValueService.getByNameFromDictionary(
                command.getPosition(), "positions"
        );
        List<EmployeePosition> existingPositions = employeePositionRepository.findByEmployee(employeeId);
        boolean allPositionsHaveEndDate = existingPositions.stream()
                .allMatch(existingPosition -> existingPosition.getEndDate() != null);
        if (!allPositionsHaveEndDate)
            throw new WrongEmploymentDateException(
                    "Not all existing employee's positions have an end date. " +
                            "To add new position all previous must be over."
            );
        boolean isStartDateValid = command.getStartDate().isAfter(employee.getEmploymentStartDate());
        if (!isStartDateValid)
            throw new WrongEmploymentDateException(
                    "Start date of the new position cannot be before employee's " + "employment start date: "
                            + employee.getEmploymentStartDate()
            );
        boolean isOverlap = existingPositions.stream()
                .anyMatch(existingPosition -> !command.getStartDate().isAfter(existingPosition.getEndDate()));
        if (isOverlap)
            throw new WrongEmploymentDateException("New position overlaps with an existing one.");
        EmployeePosition employeePosition = modelMapper.map(command, EmployeePosition.class);
        employeePosition.setEmployee(employee);
        employeePosition.setPosition(newPosition);
        employee.setCurrentPosition(newPosition);
        employee.setCurrentSalary(command.getSalary());
        return add(employeePosition);
    }

    @Transactional
    public EmployeePosition updateEndDateForCurrentPosition(
            Long employeeId, Long positionId, UpdateEmployeePositionEndDateCommand command) {
        EmployeePosition currentPosition = getEmployeePositionById(employeeId, positionId);
        currentPosition.setEndDate(command.getEndDate());
        return employeePositionRepository.save(currentPosition);
    }

    @Transactional(readOnly = true)
    public List<EmployeePosition> getEmployeePositions(Long employeeId) {
        employeeService.getById(employeeId);
        return employeePositionRepository.findByEmployee(employeeId);
    }

    @Transactional(readOnly = true)
    public EmployeePosition getEmployeePositionById(Long employeeId, Long positionId) {
        EmployeePosition currentPosition = getById(positionId);
        if (!currentPosition.getEmployee().getId().equals(employeeId)) {
            throw new PositionNotBelongToEmployeeException(
                    "Position with id " + positionId + " does not belong " + "to the employee with id " + employeeId
            );
        }
        return currentPosition;
    }

    public void deleteById(Long employeeId, Long positionId) {
        EmployeePosition positionToDelete = getById(positionId);
        if (!positionToDelete.getEmployee().getId().equals(employeeId)) {
            throw new PositionNotBelongToEmployeeException(
                    "Position with id " + positionId + " does not belong " + "to the employee with id " + employeeId
            );
        }
        employeePositionRepository.delete(positionToDelete);
    }

    public EmployeePosition getById(Long id) {
        return employeePositionRepository.findById(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new WrongIdException("Wrong id."))
        ).orElseThrow(() -> new DictionaryValueNotFoundException("Employee position with id " + id + " not found."));
    }

    public EmployeePosition add(EmployeePosition employeePosition) {
        return employeePositionRepository.save(
                Optional.ofNullable(employeePosition)
                        .filter(x -> Objects.isNull(x.getId()))
                        .orElseThrow(() -> new WrongEntityException("Wrong entity for persist."))
        );
    }

}
