package pl.kurs.personinformation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.repositories.DictionaryRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PersonInformationApplication.class)
@AutoConfigureMockMvc
class DictionaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @BeforeEach
    public void setUp() {
        dictionaryRepository.deleteAllInBatch();
    }

    @Test
    public void shouldReturnOkStatusWhenUploadFromCorrectCsvFile() throws Exception {
        //given
        String fileContent = "name\ntypes";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-dictionaries.csv", "text/csv", fileContent.getBytes()
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/dictionaries/upload").file(file));
        //then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(("Data from file imported successfully.")));
    }

    @Test
    public void shouldReturnBadRequestStatusForEmptyFile() throws Exception {
        //given
        String fileContent = "";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-dictionaries.csv", "text/csv", fileContent.getBytes()
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/dictionaries/upload").file(file));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Error during data import. File is empty or does not exist.")));
    }

    @AfterEach
    public void tearDown() {
        dictionaryRepository.deleteAllInBatch();
    }

}