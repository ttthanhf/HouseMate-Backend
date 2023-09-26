package housemate.backend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import housemate.repositories.ServiceRepository;
import housemate.services.TheService;

@SpringBootTest(classes = HousemateApplicationTests.class)
class HousemateApplicationTests {

	@Autowired
	private ServiceRepository serviceRepo;
	
	@Autowired
	private TheService serviceDao;
	
	@Test
	void contextLoads() {
		assertThat(serviceRepo).isNotNull();
		assertThat(serviceDao).isNotNull();
		
	}

}
