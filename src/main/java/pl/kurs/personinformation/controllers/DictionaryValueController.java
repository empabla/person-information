package pl.kurs.personinformation.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.kurs.personinformation.dto.DictionaryValueDto;
import pl.kurs.personinformation.dto.StatusDto;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.services.DataImportFromCsvService;
import pl.kurs.personinformation.services.DictionaryService;
import pl.kurs.personinformation.services.DictionaryValueService;
import pl.kurs.personinformation.validators.LettersOnly;

@RestController
@RequestMapping("/api/dictionaryvalues")
@RequiredArgsConstructor
@Validated
@Api(value = "Dictionary Value Controller")
public class DictionaryValueController {

    private final DictionaryValueService dictionaryValueService;
    private final DictionaryService dictionaryService;
    private final DataImportFromCsvService dataImportFromCsvService;
    private final ModelMapper modelMapper;

    @GetMapping("/{valueId}")
    @ApiOperation(value = "Get a single dictionary value by ID", response = DictionaryValueDto.class)
    public ResponseEntity<DictionaryValueDto> getDictionaryValueById(@PathVariable Long valueId) {
        DictionaryValue dictionaryValue = dictionaryValueService.getById(valueId);
        DictionaryValueDto dictionaryValueDto = modelMapper.map(dictionaryValue, DictionaryValueDto.class);
        return ResponseEntity.ok(dictionaryValueDto);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "Import dictionary values from a CSV file",
            notes = "Auxiliary endpoint used to prepare data",
            response = StatusDto.class)
    public ResponseEntity<StatusDto> addDictionaryValuesFromCsvFile(@RequestParam("file") MultipartFile file) {
        dataImportFromCsvService.importDictionaryValuesFromCsvFile(file);
        return ResponseEntity.ok(new StatusDto("Data from file imported successfully."));
    }

    @PostMapping("/person-type/{typeName}")
    @Transactional
    @ApiOperation(value = "Create a new person type", response = DictionaryValueDto.class)
    public ResponseEntity<DictionaryValueDto> createNewPersonType(@PathVariable @LettersOnly @Valid String typeName) {
        DictionaryValue dictionaryValueForSave = new DictionaryValue(typeName);
        dictionaryValueService.addToDictionary(dictionaryValueForSave, "types");
        DictionaryValueDto dictionaryValueDto = modelMapper.map(dictionaryValueForSave, DictionaryValueDto.class);
        return ResponseEntity.ok(dictionaryValueDto);
    }

    @PostMapping("/{dictionaryName}/{dictionaryValue}")
    @Transactional
    @ApiOperation(value = "Add a dictionary value to a specific dictionary", response = DictionaryValueDto.class)
    public ResponseEntity<DictionaryValueDto> addDictionaryValue(@PathVariable @LettersOnly @Valid String dictionaryName,
                                                                 @PathVariable @LettersOnly @Valid String dictionaryValue) {
        DictionaryValue dictionaryValueForSave = new DictionaryValue(
                dictionaryValue, dictionaryService.getByName(dictionaryName)
        );
        dictionaryValueService.addToDictionary(dictionaryValueForSave, dictionaryName);
        DictionaryValueDto dictionaryValueDto = modelMapper.map(dictionaryValueForSave, DictionaryValueDto.class);
        return ResponseEntity.ok(dictionaryValueDto);
    }

}
