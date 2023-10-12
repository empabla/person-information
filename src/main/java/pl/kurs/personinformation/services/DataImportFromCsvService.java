package pl.kurs.personinformation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.kurs.personinformation.commands.CreatePersonFromCsvCommand;
import pl.kurs.personinformation.exceptions.DataImportFromFileException;
import pl.kurs.personinformation.exceptions.InvalidFileException;
import pl.kurs.personinformation.factory.creators.PersonFactory;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.models.ImportStatus;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.repositories.PersonRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DataImportFromCsvService {

    private final PersonRepository personRepository;

    private final PersonFactory personFactory;

    private final DictionaryService dictionaryService;

    private final DictionaryValueService dictionaryValueService;

    private final ImportStatus importStatus = new ImportStatus();

    private final ReentrantLock importLock = new ReentrantLock();

    @Async("threadPoolTaskExecutor")
    @Transactional
    public void importPeopleFromCsvFile(MultipartFile file) {
        if (importLock.tryLock()) {
            try {
                if (file == null || file.isEmpty()) {
                    throw new InvalidFileException("File is empty or does not exist.");
                }
                importStatus.setInProgress(true);
                importStatus.setStartTime(LocalDateTime.now());
                Stream<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream())).lines();
                AtomicLong importedCount = new AtomicLong(0);
                lines
                        .skip(1)
                        .map(line -> line.split(","))
                        .filter(parameters -> parameters.length > 0)
                        .forEach(parameters -> {
                            try {
                                String personType = parameters[0].trim();
                                CreatePersonFromCsvCommand command = new CreatePersonFromCsvCommand(personType, parameters);
                                Person person = personFactory.create(command);
                                personRepository.save(person);
                                importStatus.setProcessedRows(importedCount.incrementAndGet());
                            } catch (DataIntegrityViolationException e) {
                                throw new DataImportFromFileException("Error while processing line: "
                                        + Arrays.toString(parameters) + ". Message: Constraint violation: UC_PERSON_PESEL");
                            } catch (Exception e) {
                                throw new DataImportFromFileException("Error while processing line: "
                                        + Arrays.toString(parameters) + ". Message: " + e.getMessage());
                            }
                        });
                importStatus.setEndTime(LocalDateTime.now());
                importStatus.setCompleted(true);
            } catch (Exception e) {
                throw new DataImportFromFileException("Error during data import. " + e.getMessage());
            } finally {
                importLock.unlock();
                importStatus.setInProgress(false);
            }
        } else {
            throw new DataImportFromFileException("Another import is already in progress.");
        }
    }

    public ImportStatus getImportStatus() {
        return importStatus;
    }

    @Transactional
    public void importDictionaryFromCsvFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty())
                throw new InvalidFileException("File is empty or does not exist.");
            Stream<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream())).lines();
            lines
                    .skip(1)
                    .map(line -> line.split(","))
                    .map(args -> new Dictionary(args[0]))
                    .forEach(dictionaryService::add);
        } catch (Exception e) {
            throw new DataImportFromFileException("Error during data import. " + e.getMessage());
        }
    }

    @Transactional
    public void importDictionaryValuesFromCsvFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty())
                throw new InvalidFileException("File is empty or does not exist.");
            Stream<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream())).lines();
            lines
                    .skip(1)
                    .map(line -> line.split(","))
                    .map(args -> new DictionaryValue(args[0], dictionaryService.getById(Long.parseLong(args[1]))))
                    .forEach(dictionaryValueService::add);
        } catch (Exception e) {
            throw new DataImportFromFileException("Error during data import. " + e.getMessage());
        }
    }

}
