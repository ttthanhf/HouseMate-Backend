package housemate.aspects;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import housemate.repositories.ServiceRepository;

/**
*
* @author Anh
*/

@Aspect
@Component
public class TheServiceAspect {
	
	private Logger LOG = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Pointcut("within(housemate.services.TheService)")
	public void updateService() {
	}

	@Autowired
	ServiceRepository serviceRepo;
	
	@Before("updateService()")
	public void before(JoinPoint jp) {
		LOG.info("Update the avg_rating every time calling query ---- " + jp.getSignature().getName());
		serviceRepo.updateAvgRating();
		serviceRepo.updatePriceAfterSale();
	    }
}
