package pl.kurs.personinformation.repositories.security;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.personinformation.models.security.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.roles WHERE u.username = ?1")
    Optional<AppUser> findByUsernameWithRoles(String username);

}
