package pl.kurs.personinformation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.exceptions.*;
import pl.kurs.personinformation.models.DictionaryValue;
import pl.kurs.personinformation.repositories.DictionaryValueRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DictionaryValueService {

    private final DictionaryValueRepository dictionaryValueRepository;
    private final DictionaryService dictionaryService;

    public DictionaryValue addToDictionary(DictionaryValue dictionaryValue, String dictionaryName) {
        if (existsByNameInDictionary(dictionaryValue.getName(), dictionaryName) ||  dictionaryValue.getId() != null)
            throw new DictionaryValueAlreadyExistsException(
                    "DictionaryValue '" + dictionaryValue + "' already exists in '" + dictionaryName + "' dictionary."
            );
        dictionaryValue.setName(dictionaryValue.getName().toLowerCase());
        dictionaryValue.setDictionary(dictionaryService.getByName(dictionaryName));
        return dictionaryValueRepository.save(dictionaryValue);
    }

    @Cacheable("dictionaryValues")
    public DictionaryValue getByNameFromDictionary(String name, String dictionaryName) {
        validateByNameInDictionary(name, dictionaryName);
        return dictionaryValueRepository.findByNameAndDictionary(
                name.toLowerCase(), dictionaryService.getByName(dictionaryName)
        );
    }

    public void validateByNameInDictionary(String name, String dictionaryName) {
        if (!existsByNameInDictionary(name, dictionaryName))
            throw new DictionaryValueNotFoundException(
                    "Dictionary value '" + name + "' not found in the '" + dictionaryName + "' dictionary."
            );
    }

    public boolean existsByNameInDictionary(String name, String dictionaryName) {
        return dictionaryValueRepository.existsByNameAndDictionary(
                Optional.ofNullable(name.toLowerCase())
                        .orElseThrow(() -> new WrongEntityException("Dictionary value name cannot be null.")),
                dictionaryService.getByName(dictionaryName)
        );
    }

    public DictionaryValue getById(Long id) {
        return dictionaryValueRepository.findById(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new WrongIdException("Wrong id."))
        ).orElseThrow(() -> new DictionaryNotFoundException("Dictionary value with id " + id + " not found."));
    }

}
