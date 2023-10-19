package pl.kurs.personinformation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.commands.CreateEmployeePositionCommand;
import pl.kurs.personinformation.commands.UpdateEmployeePositionEndDateCommand;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.EmployeePosition;
import pl.kurs.personinformation.repositories.DictionaryRepository;
import pl.kurs.personinformation.repositories.DictionaryValueRepository;
import pl.kurs.personinformation.repositories.EmployeePositionRepository;
import pl.kurs.personinformation.repositories.PersonRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PersonInformationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeePositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictionaryValueRepository dictionaryValueRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmployeePositionRepository employeePositionRepository;

    @BeforeEach
    public void setUp() {
        employeePositionRepository.deleteAllInBatch();
        personRepository.deleteAllInBatch();
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

    @Test
    public void shouldReturnOkStatusWhenAddCorrectNewPositionToEmployee() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue currentPosition = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue newPosition
                = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("director", positions)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition,
                        40000.00));
        Long employeeId = employee.getId();
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPosition.getName(), LocalDate.of(2021, 2, 1), 50000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "personSimpleDto":{"firstName":"John","lastName":"Doe"},
                            "position":"director",
                            "startDate":"2021-02-01",
                            "endDate":null,
                            "salary":50000.0
                        }
                                    """));
    }

    @Test
    public void shouldReturnBadRequestWhenAddNewPositionToEmployeeWithPositionWithoutEndDate() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue currentPosition = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue newPosition
                = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("director", positions)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition,
                        40000.00)
        );
        Long employeeId = employee.getId();
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employee, newPosition, LocalDate.of(2021, 2, 1),
                        null, 50000.00)
        );
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPosition.getName(), LocalDate.of(2021, 2, 1), 60000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Not all existing employee's positions have " +
                        "an end date. To add new position all previous must be over.")));
    }

    @Test
    public void shouldReturnBadRequestWhenAddNewPositionWithStartDateBeforeEmploymentStartDate() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue currentPosition = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue newPosition
                = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("director", positions)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition,
                        40000.00)
        );
        Long employeeId = employee.getId();
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPosition.getName(), LocalDate.of(2020, 12, 1), 50000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Start date of the new position cannot be " +
                        "before employee's employment start date: 2021-01-01")));
    }

    @Test
    public void shouldReturnBadRequestWhenAddNewPositionIfNewPositionOverlapsExistingOne() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue currentPosition = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue newPosition
                = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("director", positions)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition,
                        40000.00)
        );
        Long employeeId = employee.getId();
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employee, newPosition, LocalDate.of(2021, 2, 1),
                        LocalDate.of(2022, 2, 1), 50000.00)
        );
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPosition.getName(), LocalDate.of(2021, 12, 1), 50000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("New position overlaps with an existing one.")));
    }

    @Test
    public void shouldReturnNotFoundWhenAddPositionThatNotExist() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue currentPosition = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue newPosition = new DictionaryValue("director", positions);
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition,
                        40000.00)
        );
        Long employeeId = employee.getId();
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPosition.getName(), LocalDate.of(2021, 12, 1), 50000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Value 'director' not found.")));
    }

    @Test
    public void shouldReturnOkStatusWhenAddCorrectEndDateAfterStartDateForCurrentPosition() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue currentPosition = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue newPosition = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("director", positions)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1),
                        currentPosition, 40000.00));
        Long employeeId = employee.getId();
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employee, newPosition, LocalDate.of(2021, 2, 1),
                        null, 50000.00)
        );
        Long positionId = employeePosition.getId();
        UpdateEmployeePositionEndDateCommand updatePositionCommand = new UpdateEmployeePositionEndDateCommand(
                LocalDate.of(2022, 12, 1)
        );
        String jsonForTest = objectMapper.writeValueAsString(updatePositionCommand);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/employees/" + employeeId + "/positions/" + positionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "position":"director",
                            "startDate":"2021-02-01",
                            "endDate":"2022-12-01",
                            "salary":50000.0
                        }
                                    """));
    }

    @Test
    public void shouldReturnBadRequestWhenAddEmptyEndDateForPositionWhenUpdate() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue currentPosition = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue newPosition = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("director", positions)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1),
                        currentPosition, 40000.00)
        );
        Long employeeId = employee.getId();
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employee, newPosition, LocalDate.of(2021, 2, 1),
                        null, 50000.00)
        );
        Long positionId = employeePosition.getId();
        UpdateEmployeePositionEndDateCommand updatePositionCommand = new UpdateEmployeePositionEndDateCommand(null);
        String jsonForTest = objectMapper.writeValueAsString(updatePositionCommand);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/employees/" + employeeId + "/positions/" + positionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("field: endDate / rejectedValue: 'null' / message: Cannot be null")));
    }

    @Test
    public void shouldReturnListOfPositionsWhenGetAllEmployeePositionsByEmployeeId() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue managerDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue directorDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("director", positions)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe",
                        "12345678911", 180, 70, "john.doe@test.com",
                        LocalDate.of(2015, 1, 1), managerDV, 40000.00)
        );
        Long employeeId = employee.getId();
        EmployeePosition employeePosition1 = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employee, managerDV, LocalDate.of(2021, 2, 1),
                        LocalDate.of(2023, 9, 15), 50000.00)
        );
        EmployeePosition employeePosition2 = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employee, directorDV, LocalDate.of(2023, 9, 16),
                        null, 70000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/employees/" + employeeId + "/positions"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].position").value("manager"))
                .andExpect(jsonPath("$[0].startDate").value("2021-02-01"))
                .andExpect(jsonPath("$[0].endDate").value("2023-09-15"))
                .andExpect(jsonPath("$[0].salary").value("50000.0"))
                .andExpect(jsonPath("$[1].position").value("director"))
                .andExpect(jsonPath("$[1].startDate").value("2023-09-16"))
                .andExpect(jsonPath("$[1].endDate").isEmpty())
                .andExpect(jsonPath("$[1].salary").value("70000.0"));
    }

    @Test
    public void shouldReturnBadRequestWhenPositionNotBelongsToEmployeeWithProvidedId() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue managerDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        Employee employee1 = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe",
                        "12345678911", 180, 70, "john.doe@test.com",
                        LocalDate.of(2021, 1, 1), managerDV, 40000.00)
        );
        Long employee1Id = employee1.getId();
        Employee employee2 = personRepository.saveAndFlush(
                new Employee(employeeDV, "Tom", "Black",
                        "12345678912", 180, 70, "tom.black@test.com",
                        LocalDate.of(2021, 1, 1), managerDV, 45000.00)
        );
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employee2, managerDV, LocalDate.of(2022, 2, 1),
                        null, 70000.00)
        );
        Long employeePositionId = employeePosition.getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/employees/" + employee1Id + "/positions/" + employeePositionId));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Position with id " + employeePositionId
                                + " does not belong to the employee with id " + employee1Id)));
    }

    @Test
    public void shouldDeleteEmployeePositionById() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue managerDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe",
                        "12345678911", 180, 70, "john.doe@test.com",
                        LocalDate.of(2021, 1, 1), managerDV, 40000.00)
        );
        Long employeeId = employee.getId();
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employee, managerDV, LocalDate.of(2022, 2, 1),
                        null, 70000.00)
        );
        Long employeePositionId = employeePosition.getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/employees/" + employeeId + "/positions/" + employeePositionId));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("Position with id " + employeePositionId
                                + " deleted from employee with id " + employeeId));
    }

    @Test
    public void shouldReturnNotFoundStatusForNotExistingIdWhenGetAllByEmployeeId() throws Exception {
        //given
        Long employeeId = 10L;
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/employees/" + employeeId + "/positions"));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Employee with id " + employeeId + " not found.")));
    }

    @AfterEach
    public void tearDown() {
        employeePositionRepository.deleteAllInBatch();
        personRepository.deleteAllInBatch();
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

}