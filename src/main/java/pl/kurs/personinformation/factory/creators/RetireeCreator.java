package pl.kurs.personinformation.factory.creators;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.commands.CreatePersonCommand;
import pl.kurs.personinformation.commands.CreateRetireeCommand;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Retiree;
import pl.kurs.personinformation.services.DictionaryValueService;

@Service
@RequiredArgsConstructor
public class RetireeCreator implements PersonCreator {

    private final DictionaryValueService dictionaryValueService;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "retiree";
    }

    @Override
    public Person createPerson(CreatePersonCommand createPersonCommand) {
        CreateRetireeCommand retireeCommand = modelMapper.map(createPersonCommand, CreateRetireeCommand.class);
        return new Retiree(
                dictionaryValueService.getByName(this.getType()),
                retireeCommand.getFirstName(),
                retireeCommand.getLastName(),
                retireeCommand.getPesel(),
                retireeCommand.getHeight(),
                retireeCommand.getWeight(),
                retireeCommand.getEmail(),
                retireeCommand.getPension(),
                retireeCommand.getYearsOfWork()
        );
    }

}
