package pl.kurs.personinformation.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.kurs.personinformation.commands.UpdatePersonCommand;
import pl.kurs.personinformation.dto.ImportStatusDto;
import pl.kurs.personinformation.dto.PersonDto;
import pl.kurs.personinformation.dto.StatusDto;
import pl.kurs.personinformation.exceptions.DataImportFromFileException;
import pl.kurs.personinformation.factory.converters.PersonDtoConverterFactory;
import pl.kurs.personinformation.models.ImportStatus;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.services.DataImportFromCsvService;
import pl.kurs.personinformation.services.PersonService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
@Validated
@Api(value = "Person Controller")
public class PersonController {

    private final PersonService personService;
    private final DataImportFromCsvService dataImportFromCsvService;
    private final PersonDtoConverterFactory personDtoConverterFactory;
    private final ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(value = "Get a list of people based on parameters",
            notes = "This endpoint allows you to get results based on parameters given in the URL after the '?'." +
                    "Join conditions with the '&'." +
                    "- for literal parameters, enter: parameter=value," +
                    "- for numerical parameters, specify the range: parameter=fromX,toY, where X and Y " +
                    "are the limits of the closed range;" +
                    "- for gender, specify sex=m for a man and sex=w for a woman." +
                    "Provide pageable if required",
            response = PersonDto.class, responseContainer = "List")
    public ResponseEntity<List<PersonDto>> getPeople(@RequestParam Map<String, String> parameters,
                                                     @PageableDefault Pageable pageable) {
        Page<Person> people = personService.getPeople(parameters, pageable);
        List<PersonDto> personDtoList = people.stream()
                .map(personDtoConverterFactory::convert)
                .collect(Collectors.toList());
        return ResponseEntity.ok(personDtoList);
    }

    @PutMapping
    @ApiOperation(value = "Update person data", response = PersonDto.class)
    public ResponseEntity<PersonDto> updatePerson(@RequestBody @Valid UpdatePersonCommand updatePersonCommand) {
        Person personForUpdate = personService.edit(updatePersonCommand);
        PersonDto personDto = personDtoConverterFactory.convert(personForUpdate);
        return ResponseEntity.ok(personDto);
    }

    @PostMapping("/import")
    @ApiOperation(value = "Import data from a CSV file asynchronously",
            notes = "This endpoint allows you to import data from a CSV file in an asynchronous manner. " +
                    "It processes the CSV file and saves the data to the database.", response = StatusDto.class)
    public CompletableFuture<ResponseEntity<StatusDto>> importPeople(@RequestParam("file") MultipartFile file) {
        return dataImportFromCsvService.importPeopleFromCsvFile(file)
                .thenApply(result -> ResponseEntity.ok(new StatusDto("Data import has started. Check status endpoint " +
                        "/api/people/import/status for progress.")));
    }

    @GetMapping("/import/status")
    @ApiOperation(value = "Get data import status", response = ImportStatusDto.class)
    public ResponseEntity<ImportStatusDto> getImportStatus() {
        ImportStatus importStatus = dataImportFromCsvService.getImportStatus();
        ImportStatusDto importStatusDto = modelMapper.map(importStatus, ImportStatusDto.class);
        return ResponseEntity.ok(importStatusDto);
    }

}