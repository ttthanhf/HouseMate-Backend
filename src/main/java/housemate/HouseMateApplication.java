package housemate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author ThanhF
 */
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "HouseMate API",
                version = "1.0.0",
                description = "An API for the software system provides single service and package service for student apartments."
        ),
        servers = {
            @Server(url = "http://localhost:8080"),
            @Server(url = "https://housemateb.thanhf.dev")
        }
)
public class HouseMateApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(HouseMateApplication.class, args);
    }

    // Uncomment this code if datetime bug occur
//    @Bean
//    public Formatter<LocalDate> localDateFormatter() {
//        return new Formatter<>() {
//            @Override
//            public LocalDate parse(String text, java.util.Locale locale) {
//                // In front-end: months from 1 - 12. In back-end months from 0 - 11
//                return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy")).minusMonths(1);
//            }
//
//            @Override
//            public String print(LocalDate object, java.util.Locale locale) {
//                return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(object);
//            }
//        };
//    }

}
