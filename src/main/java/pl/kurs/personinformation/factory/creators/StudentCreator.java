package pl.kurs.personinformation.factory.creators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Student;
import pl.kurs.personinformation.services.DictionaryValueService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentCreator implements PersonCreator {

    private final DictionaryValueService dictionaryValueService;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public Person createPerson(Map<String, Object> parameters) {
        return new Student(
                dictionaryValueService.getByName(this.getType()),
                getStringParameter("firstName", parameters),
                getStringParameter("lastName", parameters),
                getStringParameter("pesel", parameters),
                getIntegerParameter("height", parameters),
                getIntegerParameter("weight", parameters),
                getStringParameter("email", parameters),
                dictionaryValueService.getByName(getStringParameter("universityName", parameters)),
                getIntegerParameter("enrollmentYear", parameters),
                dictionaryValueService.getByName(getStringParameter("fieldOfStudy", parameters)),
                getDoubleParameter("scholarship", parameters)
        );
    }

}
