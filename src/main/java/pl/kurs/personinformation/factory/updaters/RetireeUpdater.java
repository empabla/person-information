package pl.kurs.personinformation.factory.updaters;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.commands.UpdatePersonCommand;
import pl.kurs.personinformation.commands.UpdateRetireeCommand;
import pl.kurs.personinformation.exceptions.WrongTypeException;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Retiree;
import pl.kurs.personinformation.repositories.PersonRepository;

@Service
@RequiredArgsConstructor
public class RetireeUpdater implements PersonUpdater {

    private final PersonRepository personRepository;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "retiree";
    }

    @Override
    public Person updatePerson(UpdatePersonCommand updatePersonCommand) {
        try {
            UpdateRetireeCommand retireeCommand = modelMapper.map(updatePersonCommand, UpdateRetireeCommand.class);
            Retiree retireeForUpdate = (Retiree) personRepository.findById(retireeCommand.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No entity found"));
            retireeForUpdate.setFirstName(retireeCommand.getFirstName());
            retireeForUpdate.setLastName(retireeCommand.getLastName());
            retireeForUpdate.setPesel(retireeCommand.getPesel());
            retireeForUpdate.setHeight(retireeCommand.getHeight());
            retireeForUpdate.setWeight(retireeCommand.getWeight());
            retireeForUpdate.setEmail(retireeCommand.getEmail());
            retireeForUpdate.setVersion(retireeCommand.getVersion());
            retireeForUpdate.setYearsOfWork(retireeCommand.getYearsOfWork());
            retireeForUpdate.setPension(retireeCommand.getPension());
            return retireeForUpdate;
        } catch (ClassCastException e) {
            throw new WrongTypeException("The type in the request body does not match the entity type");
        }
    }

}
