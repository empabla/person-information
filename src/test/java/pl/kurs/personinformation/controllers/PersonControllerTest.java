package pl.kurs.personinformation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.commands.UpdateEmployeeCommand;
import pl.kurs.personinformation.commands.UpdatePersonCommand;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.models.Employee;
import pl.kurs.personinformation.models.Student;
import pl.kurs.personinformation.repositories.DictionaryRepository;
import pl.kurs.personinformation.repositories.DictionaryValueRepository;
import pl.kurs.personinformation.repositories.PersonRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = PersonInformationApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {

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

    @BeforeEach
    public void setUp() {
        personRepository.deleteAllInBatch();
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

    @Test
    public void shouldGetPersonByFirstName() throws Exception {
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
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00));
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?firstName=John"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    public void shouldGetAllPeopleByEmployeeType() throws Exception {
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
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00)
        );
        Employee employee2 = personRepository.saveAndFlush(
                new Employee(employeeDV, "Adam", "Wick", "12345678912", 170, 80,
                        "adam.wick@test.com", LocalDate.of(2021, 2, 2), managerDV,
                        50000.00));
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?type=employee"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "type": "employee",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-01-01",
                            "position": "manager",
                            "salary": 40000.00
                          },
                          {
                            "type": "employee",
                            "firstName": "Adam",
                            "lastName": "Wick",
                            "email": "adam.wick@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-02-02",
                            "position": "manager",
                            "salary": 50000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldGetSinglePersonByEmployeeTypeAndName() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        Dictionary fieldsOfStudy = dictionaryRepository.saveAndFlush(
                new Dictionary("fields of study")
        );
        Dictionary universityNames = dictionaryRepository.saveAndFlush(
                new Dictionary("university names")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue studentDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("student", types)
        );
        DictionaryValue managerDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue universityDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("test university", universityNames)
        );
        DictionaryValue fieldOfStudyDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("test field", fieldsOfStudy)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00)
        );
        Student student = personRepository.saveAndFlush(
                new Student(studentDV, "John", "Doe", "12345678912", 180, 70,
                        "john.doe@test.com", universityDV, 2, fieldOfStudyDV, 5000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?firstName=John&type=employee"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "type": "employee",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-01-01",
                            "position": "manager",
                            "salary": 40000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldGetSinglePersonByLastNameAndWeighRange() throws Exception {
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
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00)
        );
        Employee employee2 = personRepository.saveAndFlush(
                new Employee(employeeDV, "Tom", "Doe", "12345678912", 170, 60,
                        "tom.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        50000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?lastName=Doe&weight=from55,to65"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "type": "employee",
                            "firstName": "Tom",
                            "lastName": "Doe",
                            "email": "tom.doe@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-01-01",
                            "position": "manager",
                            "salary": 50000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldGetSinglePersonByStudentTypeAndLastNameAndSex() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Dictionary positions = dictionaryRepository.saveAndFlush(
                new Dictionary("positions")
        );
        Dictionary fieldsOfStudy = dictionaryRepository.saveAndFlush(
                new Dictionary("fields of study")
        );
        Dictionary universityNames = dictionaryRepository.saveAndFlush(
                new Dictionary("university names")
        );
        DictionaryValue employeeDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", types)
        );
        DictionaryValue studentDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("student", types)
        );
        DictionaryValue managerDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("manager", positions)
        );
        DictionaryValue universityDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("test university", universityNames)
        );
        DictionaryValue fieldOfStudyDV = dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("test field", fieldsOfStudy)
        );
        Employee employee = personRepository.saveAndFlush(
                new Employee(employeeDV, "Anna", "Doe", "12345678921", 165, 55,
                        "anna.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00)
        );
        Student student1 = personRepository.saveAndFlush(
                new Student(studentDV, "John", "Doe", "12345678912", 170, 60,
                        "john.doe@test.com", universityDV, 2, fieldOfStudyDV, 50000.00)
        );
        Student student2 = personRepository.saveAndFlush(
                new Student(studentDV, "Mia", "Doe", "1234567822", 160, 50,
                        "mia.doe@test.com", universityDV, 2, fieldOfStudyDV, 1000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?type=student&lastName=Doe&sex=w"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "type": "student",
                            "firstName": "Mia",
                            "lastName": "Doe",
                            "email": "mia.doe@test.com",
                            "version": 0,
                            "universityName": "test university",
                            "enrollmentYear": 2,
                            "fieldOfStudy": "test field",
                            "scholarship": 1000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldGetSinglePersonByPositionAndEmploymentStartDateRange() throws Exception {
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
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00)
        );
        Employee employee2 = personRepository.saveAndFlush(
                new Employee(employeeDV, "Tom", "Doe", "12345678912", 170, 60,
                        "tom.doe@test.com", LocalDate.of(2022, 1, 1), managerDV,
                        50000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?position=manager&employmentStartDate=from2021-01-01,to2021-12-31"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                         {
                            "type": "employee",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-01-01",
                            "position": "manager",
                            "salary": 40000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldUpdateEmployeeLastNameAndPositionAndSalary() throws Exception {
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
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00)
        );
        Long id = employee.getId();
        UpdatePersonCommand updateEmployeeCommandForTest = new UpdateEmployeeCommand(
                id, employeeDV.getName(), "Jonathan", "Doe", "12345678911", 180, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                directorDV.getName(), 60000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(updateEmployeeCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("employee")))
                .andExpect(jsonPath("$.firstName", is("Jonathan")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@test.com")))
                .andExpect(jsonPath("$.version", is(1)))
                .andExpect(jsonPath("$.employmentStartDate", is("2021-01-01")))
                .andExpect(jsonPath("$.position", is("director")))
                .andExpect(jsonPath("$.salary", is(60000.00)));
    }

    @Test
    public void shouldReturnConflictStatusForOptimisticLockingExceptionWhenUpdateOutOfDateVersion() throws Exception {
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
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00));
        Long id = employee.getId();
        UpdatePersonCommand updateEmployeeCommandFoTest1 = new UpdateEmployeeCommand(
                id, employeeDV.getName(), "Jonathan", "Doe", "12345678911", 180, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                managerDV.getName(), 50000.00
        );
        UpdatePersonCommand updateEmployeeCommandFoTest2 = new UpdateEmployeeCommand(
                id, employeeDV.getName(), "Jonathan", "Doe", "12345678911", 180, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                directorDV.getName(), 60000.00
        );
        //first update attempt
        String jsonForTest1 = objectMapper.writeValueAsString(updateEmployeeCommandFoTest1);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest1))
                .andExpect(status().isOk());
        //when
        //second update attempt
        String jsonForTest2 = objectMapper.writeValueAsString(updateEmployeeCommandFoTest2);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest2));
        //then
        resultActions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("CONFLICT"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Row was updated or deleted by another transaction (or unsaved-value mapping was " +
                                "incorrect). Current version of entity: 1")));
    }

    @Test
    public void shouldReturnNotFoundStatusForNotExistingIdWhenUpdate() throws Exception {
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
        UpdatePersonCommand updateEmployeeCommandForTest = new UpdateEmployeeCommand(
                10L, employeeDV.getName(), "John", "Doe", "12345678911", 180, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                managerDV.getName(), 50000.00
        );
        //when
        String jsonForTest = objectMapper.writeValueAsString(updateEmployeeCommandForTest);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Person with id " + updateEmployeeCommandForTest.getId() + " not found!")));
    }

    @Test
    public void shouldReturnConstraintViolationExceptionForTheSamePesel() throws Exception {
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
                new DictionaryValue("manager", positions
                ));
        Employee employee1 = personRepository.saveAndFlush(
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00)
        );
        Employee employee2 = personRepository.saveAndFlush(
                new Employee(employeeDV, "Tom", "Doe", "12345678912", 170, 60,
                        "tom.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        50000.00)
        );
        String pesel1 = employee1.getPesel();
        Long id2 = employee2.getId();
        UpdatePersonCommand updateEmployeeCommandForTest = new UpdateEmployeeCommand(
                id2, employeeDV.getName(), "Tom", "Doe", pesel1, 170, 60,
                "tom.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                managerDV.getName(), 50000.00);
        //when
        String jsonForTest = objectMapper.writeValueAsString(updateEmployeeCommandForTest);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("PESEL_NOT_UNIQUE"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Duplicated entry for 'pesel' field.")));
    }

    @Test
    public void shouldReturnBadRequestForNotValidPeselAndHeightWhenUpdate() throws Exception {
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
                new Employee(employeeDV, "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), managerDV,
                        40000.00)
        );
        Long employeeId = employee.getId();
        UpdatePersonCommand updateEmployeeCommandForTest = new UpdateEmployeeCommand(
                employeeId, employeeDV.getName(), "John", "Doe", "1234567911", 0, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                managerDV.getName(), 50000.00
        );
        //when
        String jsonForTest = objectMapper.writeValueAsString(updateEmployeeCommandForTest);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("field: pesel / rejectedValue: '1234567911' " +
                        "/ message: Pesel cannot be null; should contain 11 digits")))
                .andExpect(jsonPath("$.errorMessages", hasItem("field: height / rejectedValue: '0' " +
                        "/ message: Cannot be null; must be positive")));
    }

    @Test
    public void shouldReturnOkStatusWhenImportPeopleFromCorrectCsvFile() throws Exception {
        //given
        String fileContent = "type,first_name,last_name,pesel,height,weight,email,param1,param2,param3,param4" +
                "\nEmployee,John,Doe,12345678911,180,70,johndoe@test.com,2021-01-01,Manager,40000";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-peopleToImport.csv", "text/csv", fileContent.getBytes()
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/people/import").file(file));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(("Data import has started. " +
                        "Check status endpoint /api/people/import/status for progress.")));
    }

    @Test
    public void shouldReturnBadRequestStatusForEmptyFile() throws Exception {
        //given
        String fileContent = "";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-peopleToImport.csv", "text/csv", fileContent.getBytes()
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/people/import").file(file));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Error during data import. File is empty or does not exist.")));
    }

    @Test
    public void shouldReturnImportStatusNotStartedBeforeImportAndCompletedWhenImportingAFile() throws Exception {
        //given
        String fileContent = "type,first_name,last_name,pesel,height,weight,email,param1,param2,param3,param4" +
                "\nEmployee,John,Doe,12345678911,180,70,johndoe@test.com,2021-01-01,Manager,40000";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-peopleToImport.csv", "text/csv", fileContent.getBytes()
        );
        //then before import
        ResultActions resultActionsBefore = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people/import/status"));
        resultActionsBefore
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Import has not started yet."))
                .andExpect(jsonPath("$.startTime").isEmpty())
                .andExpect(jsonPath("$.endTime").isEmpty())
                .andExpect(jsonPath("$.processedRows").value(0));
        //when
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/people/import").file(file));
        //then after import
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people/import/status"));
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Import completed."))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isNotEmpty())
                .andExpect(jsonPath("$.processedRows").value(1));
    }

    @AfterEach
    public void tearDown() {
        personRepository.deleteAllInBatch();
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

    @Test
    public void shouldPaginate10ResultsIntoTwoPagesWith5Results() throws Exception {
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
        for (int i = 0; i < 10; i++) {
            Employee employee = new Employee(employeeDV, "Test" + i, "TestTest" + i,
                    "1234567891" + i, 170, 70, "test" + i + "@test.com",
                    LocalDate.of(2021, 1, 1), managerDV, 40000.00);
            personRepository.saveAndFlush(employee);
        }
        //when
        int pageNumber = 1;
        int pageSize = 5;
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?page=" + pageNumber + "&size=" + pageSize));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(pageSize)))
                .andExpect(jsonPath("$[0].firstName", is("Test5")));
    }

}