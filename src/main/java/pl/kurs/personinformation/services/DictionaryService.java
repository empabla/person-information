package pl.kurs.personinformation.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.exceptions.WrongEntityException;
import pl.kurs.personinformation.exceptions.WrongIdException;
import pl.kurs.personinformation.exceptions.DictionaryNotFoundException;
import pl.kurs.personinformation.models.Dictionary;
import pl.kurs.personinformation.repositories.DictionaryRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    public Dictionary add(Dictionary dictionary) {
        return dictionaryRepository.save(
                Optional.ofNullable(dictionary)
                        .filter(x -> Objects.isNull(x.getId()))
                        .orElseThrow(() -> new WrongEntityException("Wrong entity for persist."))
        );
    }

    public Dictionary getById(Long id) {
        return dictionaryRepository.findById(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new WrongIdException("Wrong id."))
        ).orElseThrow(() -> new DictionaryNotFoundException("Dictionary with id " + id + " not found."));
    }

    public Dictionary getByName(String name) {
        return dictionaryRepository.findByName(
                Optional.ofNullable(name)
                        .orElseThrow(() -> new DictionaryNotFoundException
                                ("Dictionary '" + name + "' not found.")));
    }

}
