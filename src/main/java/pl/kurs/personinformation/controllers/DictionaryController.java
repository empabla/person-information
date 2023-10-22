package pl.kurs.personinformation.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.kurs.personinformation.dto.StatusDto;
import pl.kurs.personinformation.services.DataImportFromCsvService;

@RestController
@RequestMapping("/api/dictionaries")
@RequiredArgsConstructor
@Api(value = "Dictionary Controller")
public class DictionaryController {

    private final DataImportFromCsvService dataImportFromCsvService;

    @PostMapping("/upload")
    @ApiOperation(value = "Import dictionaries from a CSV file",
            notes = "Auxiliary endpoint used to prepare data",
            response = StatusDto.class)
    public ResponseEntity<StatusDto> addDictionaryFromCsvFile(@RequestParam("file") MultipartFile file) {
        dataImportFromCsvService.importDictionaryFromCsvFile(file);
        return ResponseEntity.ok(new StatusDto("Data from file imported successfully."));
    }

}
