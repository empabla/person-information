package pl.kurs.personinformation.factory.creators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.commands.CreatePersonFromCsvCommand;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.services.DictionaryValueService;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = PersonInformationApplication.class)
class PersonFactoryTest {

    @Mock
    private DictionaryValueService dictionaryValueService;

    private PersonFactory personFactory;

    @BeforeEach
    public void setUp() {
        dictionaryValueService = Mockito.mock(DictionaryValueService.class);
        personFactory = new PersonFactory(Set.of(
                new EmployeeCreator(dictionaryValueService),
                new StudentCreator(dictionaryValueService),
                new RetireeCreator(dictionaryValueService)
        ));
    }

    @Test
    public void testCreateEmployee() {
        //given
        String[] employeeData = {
                "Employee", "Mia", "Smith", "78062890123", "167", "63", "miasmith@test.com", "2020-12-01",
                "Manager", "95000"
        };
        Mockito.doReturn(new DictionaryValue("Manager")).when(dictionaryValueService).getByName("Manager");
        CreatePersonFromCsvCommand command = new CreatePersonFromCsvCommand("employee", employeeData);
        //when
        Employee employee = (Employee) personFactory.create(command);
        //then
        assertNotNull(employee);
        assertEquals("Mia", employee.getFirstName());
        assertEquals("Smith", employee.getLastName());
        assertEquals("78062890123", employee.getPesel());
        assertEquals(167, employee.getHeight());
        assertEquals(63, employee.getWeight());
        assertEquals("miasmith@test.com", employee.getEmail());
        assertEquals(LocalDate.of(2020, 12, 01), employee.getEmploymentStartDate());
        assertEquals("Manager", employee.getCurrentPosition().getName());
        assertEquals(95000.00, employee.getCurrentSalary());
    }

}