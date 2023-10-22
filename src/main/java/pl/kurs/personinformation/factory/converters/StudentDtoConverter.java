package pl.kurs.personinformation.factory.converters;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.dto.PersonDto;
import pl.kurs.personinformation.dto.StudentDto;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Student;
import pl.kurs.personinformation.services.DictionaryValueService;

@Service
@RequiredArgsConstructor
public class StudentDtoConverter implements PersonDtoConverter {

    private final ModelMapper modelMapper;

    private final DictionaryValueService dictionaryValueService;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public PersonDto convert(Person person) {
        StudentDto studentDto = new StudentDto();
        Student student = modelMapper.map(person, Student.class);
        studentDto.setId(student.getId());
        studentDto.setType(student.getType().getName());
        studentDto.setFirstName(student.getFirstName());
        studentDto.setLastName(student.getLastName());
        studentDto.setEmail(student.getEmail());
        studentDto.setVersion(student.getVersion());
        studentDto.setUniversityName(dictionaryValueService.getByName(student.getUniversityName().getName()).toString());
        studentDto.setEnrollmentYear(student.getEnrollmentYear());
        studentDto.setFieldOfStudy(dictionaryValueService.getByName(student.getFieldOfStudy().getName()).toString());
        studentDto.setScholarship(student.getScholarship());
        return studentDto;
    }

}
