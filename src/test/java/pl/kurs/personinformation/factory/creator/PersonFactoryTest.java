package pl.kurs.personinformation.factory.creator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.commands.CreateEmployeeCommand;
import pl.kurs.personinformation.commands.CreatePersonCommand;
import pl.kurs.personinformation.factory.creators.EmployeeCreator;
import pl.kurs.personinformation.factory.creators.PersonFactory;
import pl.kurs.personinformation.factory.creators.RetireeCreator;
import pl.kurs.personinformation.factory.creators.StudentCreator;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.services.DictionaryValueService;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = PersonInformationApplication.class)
@ActiveProfiles("test")
public class PersonFactoryTest {

    @Mock
    private DictionaryValueService dictionaryValueService;

    private PersonFactory personFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        personFactory = new PersonFactory(Set.of(
                new EmployeeCreator(dictionaryValueService, new ModelMapper()),
                new StudentCreator(dictionaryValueService, new ModelMapper()),
                new RetireeCreator(dictionaryValueService, new ModelMapper())
        ));
    }

    @Test
    public void shouldCreateNewEmployeeUsingPersonFactory() {
        //given
        CreatePersonCommand createCommand = new CreateEmployeeCommand(
                "employee", "John", "Doe", "12345678911", 180, 70,
                "johndoe@test.com", LocalDate.of(2021, 1, 1),
                "director", 100000.00
        );
        Mockito.doReturn(new DictionaryValue("director"))
                .when(dictionaryValueService).getByNameFromDictionary("director", "positions");
        //when
        Employee employee = (Employee) personFactory.create(createCommand);
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
