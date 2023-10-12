package pl.kurs.personinformation.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.security.AppRole;
import pl.kurs.personinformation.models.security.AppUser;
import pl.kurs.personinformation.repositories.AppUserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        AppRole adminRole = new AppRole("ROLE_ADMIN");
        AppUser admin = new AppUser(
                "admin", passwordEncoder.encode("admin"), Set.of(adminRole)
        );
        appUserRepository.save(admin);
        AppRole importerRole = new AppRole("ROLE_IMPORTER");
        AppUser importer = new AppUser(
                "importer", passwordEncoder.encode("importer"), Set.of(importerRole)
        );
        appUserRepository.save(importer);
        AppRole employeeRole = new AppRole("ROLE_EMPLOYEE");
        AppUser employee = new AppUser(
                "employee", passwordEncoder.encode("employee"), Set.of(employeeRole)
        );
        appUserRepository.save(employee);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
