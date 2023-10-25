package pl.kurs.personinformation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.personinformation.models.Retiree;

public interface RetireeRepository extends JpaRepository<Retiree, Long> {

}
