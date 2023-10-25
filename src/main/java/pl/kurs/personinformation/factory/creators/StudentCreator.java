package pl.kurs.personinformation.factory.creators;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.commands.CreatePersonCommand;
import pl.kurs.personinformation.commands.CreateStudentCommand;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Student;
import pl.kurs.personinformation.services.DictionaryValueService;

@Service
@RequiredArgsConstructor
public class StudentCreator implements PersonCreator {

    private final DictionaryValueService dictionaryValueService;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public Person createPerson(CreatePersonCommand createPersonCommand) {
        CreateStudentCommand studentCommand = modelMapper.map(createPersonCommand, CreateStudentCommand.class);
        return new Student(
                dictionaryValueService.getByNameFromDictionary(this.getType(), "types"),
                studentCommand.getFirstName(),
                studentCommand.getLastName(),
                studentCommand.getPesel(),
                studentCommand.getHeight(),
                studentCommand.getWeight(),
                studentCommand.getEmail(),
                dictionaryValueService.getByNameFromDictionary(
                        studentCommand.getUniversityName().toLowerCase(), "university names"
                ),
                studentCommand.getEnrollmentYear(),
                dictionaryValueService.getByNameFromDictionary(
                        studentCommand.getFieldOfStudy().toLowerCase(), "fields of study"
                ),
                studentCommand.getScholarship()
        );
    }

}
