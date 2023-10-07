package housemate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

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
        )
)
public class HouseMateApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(HouseMateApplication.class, args);
    }

}
