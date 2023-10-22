package pl.kurs.personinformation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PersonInformationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonInformationApplication.class, args);
    }

}
