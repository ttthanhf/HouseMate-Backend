package housemate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

/**
 *
 * @author ThanhF
 */
@SpringBootApplication
@EnableSpringConfigured
public class HouseMateApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SpringApplication.run(HouseMateApplication.class, args);
    }

}
