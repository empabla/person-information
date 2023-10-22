package pl.kurs.personinformation.services;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.personinformation.PersonInformationApplication;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.repositories.DictionaryRepository;
import pl.kurs.personinformation.repositories.DictionaryValueRepository;
import pl.kurs.personinformation.repositories.PersonRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PersonInformationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConcurrentDataImportFromCsvServiceTest {

    @Autowired
    private MockMvc mockMvc;

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
    @WithMockUser(username = "importer", roles = "IMPORTER")
    public void shouldPreventAnotherImportWhileFirstOneIsInProgress() throws Exception {
        //given
        Dictionary types = dictionaryRepository.saveAndFlush(new Dictionary("types"));
        Dictionary positions = dictionaryRepository.saveAndFlush(new Dictionary("positions"));
        dictionaryValueRepository.saveAndFlush(new DictionaryValue("employee", types));
        dictionaryValueRepository.saveAndFlush(new DictionaryValue("manager", positions));
        String fileContent = "type,first_name,last_name,pesel,height,weight,email,param1,param2,param3,param4" +
                "\nemployee,John,Doe,12345678911,180,70,johndoe@test.com,2021-01-01,manager,40000";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-peopleToImport.csv", "text/csv", fileContent.getBytes()
        );
        //when - perform first import
        MvcResult importResult1 = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/people/import")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();
        //when - trying to start another import
        MvcResult importResult2 = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/people/import")
                .file(file))
                .andReturn();
        //then - another import status check
        mockMvc.perform(asyncDispatch(importResult2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Another import is already in progress.")));
        //then - manually perform first import
        mockMvc.perform(asyncDispatch(importResult1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Data import has started. " +
                        "Check status endpoint /api/people/import/status for progress."));
    }

    @AfterEach
    public void tearDown() {
        personRepository.deleteAllInBatch();
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

}