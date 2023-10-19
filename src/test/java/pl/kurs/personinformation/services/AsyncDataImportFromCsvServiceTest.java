package pl.kurs.personinformation.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.repositories.PersonRepository;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PersonInformationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AsyncDataImportFromCsvServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    public void setUp() {
        personRepository.deleteAllInBatch();
    }

    @Test
    public void shouldPerformAsyncImportAndAllowImportStatusCheckWhileImportIsInProgress() throws Exception {
        // given
        String fileContent = "type,first_name,last_name,pesel,height,weight,email,param1,param2,param3,param4" +
                "\nEmployee,John,Doe,12345678911,180,70,johndoe@test.com,2021-01-01,Manager,40000";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-peopleToImport.csv", "text/csv", fileContent.getBytes()
        );
        //when
        MvcResult asyncResult = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/people/import")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();
        //then - check import status while importing
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people/import/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Import is in progress."))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isEmpty())
                .andExpect(jsonPath("$.processedRows").value(0));
        //when -  manually perform an async dispatch
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Data import has started. " +
                        "Check status endpoint /api/people/import/status for progress."));
        //then - check import status after import
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people/import/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Import completed."))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isNotEmpty())
                .andExpect(jsonPath("$.processedRows").value(1));
    }

    @AfterEach
    public void tearDown() {
        personRepository.deleteAllInBatch();
    }

}