package pl.kurs.personinformation.services.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kurs.personinformation.models.security.AppRole;
import pl.kurs.personinformation.models.security.AppUser;
import pl.kurs.personinformation.repositories.security.AppUserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        createUserIfNotExists("admin", "ROLE_ADMIN", "admin");
        createUserIfNotExists("importer", "ROLE_IMPORTER", "importer");
        createUserIfNotExists("employee", "ROLE_EMPLOYEE", "employee");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private void createUserIfNotExists(String username, String roleName, String password) {
        if (appUserRepository.findByUsernameWithRoles(username).isEmpty()) {
            AppRole role = new AppRole(roleName);
            AppUser user = new AppUser(username, passwordEncoder.encode(password), Set.of(role));
            appUserRepository.save(user);
        }
    }

}
