package pl.kurs.personinformation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.repositories.DictionaryRepository;
import pl.kurs.personinformation.repositories.DictionaryValueRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PersonInformationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DictionaryValueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictionaryValueRepository dictionaryValueRepository;

    @BeforeEach
    public void setUp() {
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

    @Test
    @WithMockUser
    public void shouldReturnOkStatusWhenUploadFromCorrectCsvFile() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(new Dictionary("types"));
        String fileContent = "name,dictionary_id\nemployee," + types.getId();
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-dictionaryValues.csv", "text/csv", fileContent.getBytes()
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/dictionaryvalues/upload").file(file));
        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(("Data from file imported successfully.")));
    }

    @Test
    @WithMockUser
    public void shouldReturnBadRequestStatusWhenDictionaryNotExist() throws Exception {
        //given
        String fileContent = "name,dictionary_id\nemployee,1";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-dictionaryValues.csv", "text/csv", fileContent.getBytes()
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/dictionaryvalues/upload").file(file));
        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Error during data import. Dictionary with id 1 not found.")));
    }

    @Test
    @WithMockUser
    public void shouldReturnBadRequestStatusForEmptyFile() throws Exception {
        //given
        String fileContent = "";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-dictionaryValues.csv", "text/csv", fileContent.getBytes()
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/dictionaryvalues/upload").file(file));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Error during data import. File is empty or does not exist.")));
    }

    @Test
    @WithMockUser
    public void shouldReturnOkStatusAndAddNewPersonTypeToTypesDictionary() throws Exception {
        //given
        Dictionary typesDictionary = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        Long typesDictionaryId = typesDictionary.getId();
        String newPersonType = "volunteer";
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaryvalues/person-type/" + newPersonType))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        DictionaryValue newType = objectMapper.readValue(responseContent, DictionaryValue.class);
        Long addedNewTypeId = newType.getId();
        //then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/dictionaryvalues/" + addedNewTypeId))
                .andExpect(jsonPath("$.id").value(addedNewTypeId))
                .andExpect(jsonPath("$.name").value(newPersonType))
                .andExpect(content().json("{\"id\":" + addedNewTypeId + ",\"name\":\"volunteer\"," +
                        "\"dictionary\":{\"id\":" + typesDictionaryId + ",\"name\":\"types\"}}"));
    }

    @Test
    @WithMockUser
    public void shouldReturnBadRequestStatusWhenDictionaryValueAlreadyExistsInTypesDictionary() throws Exception {
        //given
        Dictionary typesDictionary = dictionaryRepository.saveAndFlush(
                new Dictionary("types")
        );
        dictionaryValueRepository.saveAndFlush(
                new DictionaryValue("employee", typesDictionary)
        );
        String newPersonType = "employee";
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaryvalues/person-type/" + newPersonType));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("DictionaryValue 'employee' already exists.")));
    }

    @Test
    @WithMockUser
    public void shouldReturnBadRequestForDictionaryValueWithNumbers() throws Exception {
        //given
        String newPersonType = "student2";
        //when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaryvalues/person-type/" + newPersonType));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("property: createNewPersonType.typeName / invalid value: 'student2' " +
                                "/ message: Field cannot be null; can contain only letters")));
    }

    @AfterEach
    public void tearDown() {
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

}