package pl.kurs.personinformation.factory.creatorsfromcsv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
class PersonFromCsvFactoryTest {

    @Mock
    private DictionaryValueService dictionaryValueService;

    private PersonFromCsvFactory personFromCsvFactory;

    @BeforeEach
    public void setUp() {
        dictionaryValueService = Mockito.mock(DictionaryValueService.class);
        personFromCsvFactory = new PersonFromCsvFactory(Set.of(
                new EmployeeFromCsvCreator(dictionaryValueService),
                new StudentFromCsvCreator(dictionaryValueService),
                new RetireeFromCsvCreator(dictionaryValueService)
        ));
    }

    @Test
    public void shouldCreateEmployeeFromProvidedDataUsingPersonFromCsvFactory() {
        //given
        String[] employeeData = {
                "employee", "Mia", "Smith", "78062890123", "167", "63", "miasmith@test.com", "2020-12-01",
                "manager", "95000"
        };
        Mockito.doReturn(new DictionaryValue("manager"))
                .when(dictionaryValueService).getByNameFromDictionary("manager", "positions");
        CreatePersonFromCsvCommand command = new CreatePersonFromCsvCommand("employee", employeeData);
        //when
        Employee employee = (Employee) personFromCsvFactory.create(command);
        //then
        assertNotNull(employee);
        assertEquals("Mia", employee.getFirstName());
        assertEquals("Smith", employee.getLastName());
        assertEquals("78062890123", employee.getPesel());
        assertEquals(167, employee.getHeight());
        assertEquals(63, employee.getWeight());
        assertEquals("miasmith@test.com", employee.getEmail());
        assertEquals(LocalDate.of(2020, 12, 01), employee.getEmploymentStartDate());
        assertEquals("manager", employee.getCurrentPosition().getName());
        assertEquals(95000.00, employee.getCurrentSalary());
    }

}