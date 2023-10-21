package pl.kurs.personinformation.factory.creators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Retiree;
import pl.kurs.personinformation.services.DictionaryValueService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RetireeCreator implements PersonCreator {

    private final DictionaryValueService dictionaryValueService;

    @Override
    public String getType() {
        return "retiree";
    }

    @Override
    public Person createPerson(Map<String, Object> parameters) {
        return new Retiree(
                dictionaryValueService.getByName(this.getType()),
                getStringParameter("firstName", parameters),
                getStringParameter("lastName", parameters),
                getStringParameter("pesel", parameters),
                getIntegerParameter("height", parameters),
                getIntegerParameter("weight", parameters),
                getStringParameter("email", parameters),
                getDoubleParameter("pension", parameters),
                getIntegerParameter("yearsOfWork", parameters)
        );
    }

}
