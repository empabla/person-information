package pl.kurs.personinformation.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.dto.EmployeePositionFullDto;
import pl.kurs.personinformation.dto.PersonSimpleDto;
import pl.kurs.personinformation.models.EmployeePosition;
import pl.kurs.personinformation.models.Person;

@Service
public class EmployeePositionToEmployeePositionFullDtoConverter implements
        Converter<EmployeePosition, EmployeePositionFullDto> {

    @Override
    public EmployeePositionFullDto convert(MappingContext<EmployeePosition, EmployeePositionFullDto> mappingContext) {
        EmployeePosition source = mappingContext.getSource();
        Person employee = source.getEmployee();
        PersonSimpleDto personSimpleDto = PersonSimpleDto.builder()
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .build();
        return EmployeePositionFullDto.builder()
                .personSimpleDto(personSimpleDto)
                .position(source.getPosition().getName())
                .startDate(source.getStartDate())
                .endDate(source.getEndDate())
                .salary(source.getSalary())
                .build();
    }

}
