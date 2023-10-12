package pl.kurs.personinformation.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.dto.EmployeePositionDto;
import pl.kurs.personinformation.models.EmployeePosition;

@Service
public class EmployeePositionToEmployeePositionDtoConverter implements Converter<EmployeePosition, EmployeePositionDto>  {

    @Override
    public EmployeePositionDto convert(MappingContext<EmployeePosition, EmployeePositionDto> mappingContext) {
        EmployeePosition source = mappingContext.getSource();
        return EmployeePositionDto.builder()
                .position(source.getPosition().getName())
                .startDate(source.getStartDate())
                .endDate(source.getEndDate())
                .salary(source.getSalary())
                .build();
    }

}
