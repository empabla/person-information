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
import pl.kurs.personinformation.factory.creatorsfromcsv.PersonFromCsvFactory;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.models.ImportStatus;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.repositories.PersonRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DataImportFromCsvService {

    private final PersonRepository personRepository;

    private final PersonFromCsvFactory personFromCsvFactory;

    private final DictionaryService dictionaryService;

    private final DictionaryValueService dictionaryValueService;

    private ImportStatus importStatus;

    private final ReentrantLock importLock = new ReentrantLock();

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Void> importPeopleFromCsvFile(MultipartFile file) {
        importStatus = new ImportStatus();
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (importLock.tryLock()) {
            try {
                if (file == null || file.isEmpty()) {
                    future.completeExceptionally(new DataImportFromFileException("File is empty or does not exist."));
                }
                importStatus.setInProgress(true);
                importStatus.setStartTime(LocalDateTime.now());
                Stream<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream())).lines();
                AtomicLong importedCount = new AtomicLong(0);
                try {
                    lines
                            .skip(1)
                            .map(line -> line.split(","))
                            .filter(parameters -> parameters.length > 0)
                            .forEach(parameters -> {
                                String personType = parameters[0].trim();
                                CreatePersonFromCsvCommand command = new CreatePersonFromCsvCommand(personType, parameters);
                                Person person = personFromCsvFactory.create(command);
                                personRepository.save(person);
                                importStatus.setProcessedRows(importedCount.incrementAndGet());
                            });
                } catch (DataIntegrityViolationException e) {
                    future.completeExceptionally(new DataImportFromFileException("Duplicate entry. " +
                            "Constraint violation: UC_PERSON_PESEL"));
                }
            } catch (Exception e) {
                future.completeExceptionally(new DataImportFromFileException("Error during data import. " +
                        "Invalid file content. Message: " + e.getMessage()));
            } finally {
                if (!future.isCompletedExceptionally())
                    importStatus.setCompleted(true);
                importStatus.setInProgress(false);
                importStatus.setEndTime(LocalDateTime.now());
                future.complete(null);
                importLock.unlock();
            }
        } else {
            future.completeExceptionally(new DataImportFromFileException("Another import is already in progress."));
        }
        return future;
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
                    .map(args -> new DictionaryValue(args[0], dictionaryService.getByName(args[1])))
                    .forEach(x -> dictionaryValueService.addToDictionary(x, x.getDictionary().getName()));
        } catch (Exception e) {
            throw new DataImportFromFileException("Error during data import. " + e.getMessage());
        }
    }

}
