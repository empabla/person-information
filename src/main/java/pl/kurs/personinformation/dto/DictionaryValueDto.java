package pl.kurs.personinformation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryValueDto {

    private Long id;

    private String name;

    private DictionarySimpleDto dictionary;

}
