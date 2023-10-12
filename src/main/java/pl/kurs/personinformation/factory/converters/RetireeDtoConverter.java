package pl.kurs.personinformation.factory.converters;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.dto.PersonDto;
import pl.kurs.personinformation.dto.RetireeDto;
import pl.kurs.personinformation.models.Person;
import pl.kurs.personinformation.models.Retiree;

@Service
@RequiredArgsConstructor
public class RetireeDtoConverter implements PersonDtoConverter {

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "retiree";
    }

    @Override
    public PersonDto convert(Person person) {
        RetireeDto retireeDto = new RetireeDto();
        Retiree retiree = modelMapper.map(person, Retiree.class);
        retireeDto.setType(retiree.getType().getName());
        retireeDto.setFirstName(retiree.getFirstName());
        retireeDto.setLastName(retiree.getLastName());
        retireeDto.setEmail(retiree.getEmail());
        retireeDto.setVersion(retiree.getVersion());
        retireeDto.setPension(retiree.getPension());
        retireeDto.setYearsOfWork(retiree.getYearsOfWork());
        return retireeDto;
    }
}
