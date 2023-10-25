package pl.kurs.personinformation.factory.creatorsfromcsv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Retiree;
import pl.kurs.personinformation.services.DictionaryValueService;

@Service
@RequiredArgsConstructor
public class RetireeFromCsvCreator implements PersonFromCsvCreator {

    private final DictionaryValueService dictionaryValueService;

    @Override
    public String getType() {
        return "retiree";
    }

    @Override
    public Person createPerson(String[] parameters) {
        return new Retiree(
                dictionaryValueService.getByNameFromDictionary(parameters[0].trim(), "types"),
                parameters[1].trim(),
                parameters[2].trim(),
                parameters[3].trim(),
                Integer.parseInt(parameters[4].trim()),
                Integer.parseInt(parameters[5].trim()),
                parameters[6].trim(),
                Double.parseDouble(parameters[7].trim()),
                Integer.parseInt(parameters[8].trim())
        );
    }

}
