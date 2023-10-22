package pl.kurs.personinformation.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.kurs.personinformation.commands.CreatePersonCommand;
import pl.kurs.personinformation.commands.UpdatePersonCommand;
import pl.kurs.personinformation.exceptions.*;
import pl.kurs.personinformation.factory.creators.PersonFactory;
import pl.kurs.personinformation.factory.updaters.PersonUpdaterFactory;
import pl.kurs.personinformation.models.EmployeePosition;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.repositories.PersonRepository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    private final PersonUpdaterFactory personUpdaterFactory;

    private final PersonSearchSpecificationService personSearchSpecificationService;

    private final PersonFactory personFactory;

    @Transactional(readOnly = true)
    public Page<Person> getPeople(Map<String, String> params, Pageable pageable) {
        Specification<Person> specification = personSearchSpecificationService.filterByCriteria(params);
        return personRepository.findAll(specification, pageable);
    }

    public Person edit(UpdatePersonCommand command) {
        Person personForUpdate = personRepository.findById(
                Optional.ofNullable(command.getId())
                        .orElseThrow(() -> new WrongIdException("Wrong id!")))
                .orElseThrow(() -> new EntityNotFoundException("Person with id " + command.getId() + " not found!"));
        try {
            Person personForSave = personUpdaterFactory.update(command);
            return personRepository.saveAndFlush(personForSave);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new UpdateOptimisticLockingException("Row was updated or deleted by another transaction " +
                    "(or unsaved-value mapping was incorrect). Current version of entity: " + personForUpdate.getVersion());
        }
    }

    public Person add(CreatePersonCommand createPersonCommand) {
        Person personForSave = personFactory.create(createPersonCommand);
        return personRepository.save(
                Optional.ofNullable(personForSave)
                        .filter(x -> Objects.isNull(x.getId()))
                        .orElseThrow(() -> new WrongEntityException("Wrong entity for persist."))
        );
    }

    public Person getById(Long id) {
        return personRepository.findById(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new WrongIdException("Wrong id."))
        ).orElseThrow(() -> new PersonNotFoundException("Person with id " + id + " not found."));
    }

    public void deleteById(Long id) {
        Person personToDelete = getById(id);
        personRepository.delete(personToDelete);
    }

}
