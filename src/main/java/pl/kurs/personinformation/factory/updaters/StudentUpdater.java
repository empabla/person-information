package pl.kurs.personinformation.factory.updaters;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.commands.UpdatePersonCommand;
import pl.kurs.personinformation.commands.UpdateStudentCommand;
import pl.kurs.personinformation.exceptions.WrongTypeException;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Student;
import pl.kurs.personinformation.repositories.PersonRepository;
import pl.kurs.personinformation.services.DictionaryValueService;

@Service
@RequiredArgsConstructor
public class StudentUpdater implements PersonUpdater {

    private final DictionaryValueService dictionaryValueService;

    private final PersonRepository personRepository;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public Person updatePerson(UpdatePersonCommand updatePersonCommand) {
        try {
            UpdateStudentCommand studentCommand = modelMapper.map(updatePersonCommand, UpdateStudentCommand.class);
            Student studentForUpdate = (Student) personRepository.findById(studentCommand.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No entity found"));
            studentForUpdate.setFirstName(studentCommand.getFirstName());
            studentForUpdate.setLastName(studentCommand.getLastName());
            studentForUpdate.setPesel(studentCommand.getPesel());
            studentForUpdate.setHeight(studentCommand.getHeight());
            studentForUpdate.setWeight(studentCommand.getWeight());
            studentForUpdate.setEmail(studentCommand.getEmail());
            studentForUpdate.setVersion(studentCommand.getVersion());
            studentForUpdate.setUniversityName(dictionaryValueService
                    .getByNameFromDictionary(studentCommand.getUniversityName(), "university names"));
            studentForUpdate.setEnrollmentYear(studentCommand.getEnrollmentYear());
            studentForUpdate.setFieldOfStudy(dictionaryValueService
                    .getByNameFromDictionary(studentCommand.getFieldOfStudy(), "fields of study"));
            studentForUpdate.setScholarship(studentCommand.getScholarship());
            return studentForUpdate;
        } catch (ClassCastException e) {
            throw new WrongTypeException("The type in the request body does not match the entity type");
        }
    }

}