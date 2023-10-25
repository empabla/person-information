package pl.kurs.personinformation.factory.updaters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.commands.UpdateEmployeeCommand;
import pl.kurs.personinformation.commands.UpdatePersonCommand;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.repositories.EmployeeRepository;
import pl.kurs.personinformation.repositories.RetireeRepository;
import pl.kurs.personinformation.repositories.StudentRepository;
import pl.kurs.personinformation.services.DictionaryValueService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = PersonInformationApplication.class)
@ActiveProfiles("test")
class PersonUpdaterFactoryTest {

    @Mock
    private DictionaryValueService dictionaryValueService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RetireeRepository retireeRepository;

    private PersonUpdaterFactory updaterFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        updaterFactory = new PersonUpdaterFactory(Set.of(
                new EmployeeUpdater(dictionaryValueService, employeeRepository, new ModelMapper()),
                new StudentUpdater(dictionaryValueService, studentRepository, new ModelMapper()),
                new RetireeUpdater(retireeRepository, new ModelMapper())
        ));
    }

    @Test
    public void shouldUpdateEmployeeDataUsingPersonUpdaterFactory() {
        //given
        UpdatePersonCommand updateCommand = new UpdateEmployeeCommand(
                1L, "Employee", "John", "Doe", "12345678911", 180, 70,
                "johndoe@test.com", 0L, LocalDate.of(2021, 1, 1),
                "director", 100000.00
        );
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(new Employee()));
        Mockito.doReturn(new DictionaryValue("director"))
                .when(dictionaryValueService).getByNameFromDictionary("director", "positions");
        //when
        Employee employee = (Employee) updaterFactory.update(updateCommand);
        //then
        assertNotNull(employee);
        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals("12345678911", employee.getPesel());
        assertEquals(180, employee.getHeight());
        assertEquals(70, employee.getWeight());
        assertEquals("johndoe@test.com", employee.getEmail());
        assertEquals(LocalDate.of(2021, 1, 1), employee.getEmploymentStartDate());
        assertEquals("director", employee.getCurrentPosition().getName());
        assertEquals(100000.00, employee.getCurrentSalary());
    }

}