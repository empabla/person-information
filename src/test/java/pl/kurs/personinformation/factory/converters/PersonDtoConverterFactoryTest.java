package pl.kurs.personinformation.factory.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.dto.EmployeeDto;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.services.DictionaryValueService;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = PersonInformationApplication.class)
@ActiveProfiles("test")
class PersonDtoConverterFactoryTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DictionaryValueService dictionaryValueService;

    private PersonDtoConverterFactory dtoConverterFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dtoConverterFactory = new PersonDtoConverterFactory(Set.of(
                new EmployeeDtoConverter(modelMapper, dictionaryValueService),
                new StudentDtoConverter(modelMapper, dictionaryValueService),
                new RetireeDtoConverter(modelMapper)
        ));
    }

    @Test
    @WithMockUser
    public void shouldConvertEmployeeToEmployeeDtoUsingPersonDtoConverterFactory() {
        // given
        DictionaryValue employeeDV = new DictionaryValue("Employee");
        DictionaryValue managerDV = new DictionaryValue("Manager");
        Mockito.when(dictionaryValueService.getByName("Employee")).thenReturn(employeeDV);
        Mockito.when(dictionaryValueService.getByName("Manager")).thenReturn(managerDV);
        Person expectedEmployee = new Employee(
                employeeDV, "John", "Doe", "12345678911", 180, 70,
                "johndoe@test.com", LocalDate.of(2021, 1, 1), managerDV, 40000.00
        );
        Mockito.doReturn(expectedEmployee).when(modelMapper).map(Mockito.any(), Mockito.eq(Employee.class));
        // when
        EmployeeDto employeeDto = (EmployeeDto) dtoConverterFactory.convert(expectedEmployee);
        // then
        assertEquals("Employee", employeeDto.getType());
        assertEquals("John", employeeDto.getFirstName());
        assertEquals("Doe", employeeDto.getLastName());
        assertEquals("johndoe@test.com", employeeDto.getEmail());
        assertEquals(LocalDate.of(2021, 1, 1), employeeDto.getEmploymentStartDate());
        assertEquals("Manager", employeeDto.getCurrentPosition());
        assertEquals(40000.00, employeeDto.getCurrentSalary());
    }

}