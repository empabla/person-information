package pl.kurs.personinformation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.exceptions.*;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.repositories.DictionaryValueRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DictionaryValueService {

    private final DictionaryValueRepository dictionaryValueRepository;

    public boolean existsByName(String name) {
        return dictionaryValueRepository.existsByName(name.toLowerCase());
    }

    public DictionaryValue add(DictionaryValue dictionaryValue) {
        if (existsByName(dictionaryValue.getName())) {
            throw new DictionaryValueAlreadyExistsException("DictionaryValue '" + dictionaryValue + "' already exists.");
        }
        dictionaryValue.setName(dictionaryValue.getName().toLowerCase());
        return dictionaryValueRepository.save(
                Optional.ofNullable(dictionaryValue)
                        .filter(x -> Objects.isNull(x.getId()))
                        .orElseThrow(() -> new WrongEntityException("Wrong entity for persist."))
        );
    }

    public DictionaryValue getByName(String name) {
        validateDictionaryValue(name);
        return dictionaryValueRepository.findByName(
                Optional.ofNullable(name.toLowerCase())
                        .orElseThrow(() -> new DictionaryValueNotFoundException
                                ("Invalid value - provided name is empty or null.")));
    }

    public DictionaryValue getById(Long id) {
        return dictionaryValueRepository.findById(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new WrongIdException("Wrong id."))
        ).orElseThrow(() -> new DictionaryNotFoundException("Dictionary value with id " + id + " not found."));
    }

    public void validateDictionaryValue(String name) {
        if (!dictionaryValueRepository.existsByName(name.toLowerCase())) {
            throw new DictionaryValueNotFoundException("Dictionary value '" + name + "' not found.");
        }
    }

}
