package pl.kurs.personinformation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.exceptions.DictionaryAlreadyExists;
import pl.kurs.personinformation.exceptions.DictionaryNotFoundException;
import pl.kurs.personinformation.exceptions.WrongEntityException;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.repositories.DictionaryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    public Dictionary add(Dictionary dictionary) {
        if (dictionary == null || dictionary.getId() != null)
            throw new WrongEntityException("Wrong entity for persist.");
        if (existsByName(dictionary.getName()))
            throw new DictionaryAlreadyExists("Dictionary '" + dictionary + "' already exists.");
        dictionary.setName(dictionary.getName().toLowerCase());
        return dictionaryRepository.save(dictionary);
    }

    @Cacheable("dictionaries")
    public Dictionary getByName(String name) {
        validateByName(name);
        return dictionaryRepository.findByName(name.toLowerCase());
    }

    public void validateByName(String name) {
        if (!existsByName(name)) {
            throw new DictionaryNotFoundException("Dictionary '" + name + "' not found.");
        }
    }

    public boolean existsByName(String name) {
        return dictionaryRepository.existsByName(
                Optional.ofNullable(name.toLowerCase())
                        .orElseThrow(() -> new WrongEntityException("Dictionary name cannot be null."))
        );
    }

}
