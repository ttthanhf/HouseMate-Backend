package housemate.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import housemate.entities.Service;
import housemate.repositories.ServiceRepository;
import housemate.services.TheService;

@SpringBootTest
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
	
	//@Test
	public void testOrderByDynamicFieldname() {
		List<Service> list =  serviceDao.sortByOneField("salePrice", "DESC");
		
		assertThat(list).isNotEmpty();
		
		for (Service service : list) {
			System.out.println(service.toString() + "/n");
		}
	}
	
	@Test
	public void testUpdateRating() {
		serviceRepo.updateAvgRating();
		List<Service> list =  serviceDao.getAll();

		assertThat(list).isNotEmpty();
		
		for (Service service : list) {
			System.out.println(service.toString() + "/n");
		}
	}

}
