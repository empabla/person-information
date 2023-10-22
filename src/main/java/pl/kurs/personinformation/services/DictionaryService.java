package pl.kurs.personinformation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.exceptions.*;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.repositories.DictionaryRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    public boolean existsByName(String name) {
        return dictionaryRepository.existsByName(name.toLowerCase());
    }

    public Dictionary add(Dictionary dictionary) {
        if (existsByName(dictionary.getName())) {
            throw new DictionaryAlreadyExists("Dictionary '" + dictionary + "' already exists.");
        }
        dictionary.setName(dictionary.getName().toLowerCase());
        return dictionaryRepository.save(
                Optional.ofNullable(dictionary)
                        .filter(x -> Objects.isNull(x.getId()))
                        .orElseThrow(() -> new WrongEntityException("Wrong entity for persist."))
        );
    }

    public Dictionary getByName(String name) {
        validateDictionary(name);
        return dictionaryRepository.findByName(
                Optional.ofNullable(name)
                        .orElseThrow(() -> new DictionaryNotFoundException
                                ("Invalid value - provided name is empty or null.")));
    }

    public void validateDictionary(String name) {
        if (!dictionaryRepository.existsByName(name.toLowerCase())) {
            throw new DictionaryValueNotFoundException("Dictionary '" + name + "' not found.");
        }
    }

}
