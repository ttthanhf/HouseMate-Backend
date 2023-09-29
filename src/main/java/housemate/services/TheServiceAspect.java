package housemate.services;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import housemate.entities.Service;
import housemate.repositories.ServiceRepository;

@Aspect
@Component
public class TheServiceAspect {
	
	private Logger LOG = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	//@Pointcut("within(housemate.services.TheService)") 
	@Pointcut("within(housemate.repositories.ServiceRepository)")
	public void updateRating() {
	}
	
	@Autowired
	ServiceRepository serviceRepo;
	
	@Before("updateRating()")
	public void before(JoinPoint jp) {
		LOG.info("Update the avg_rating every time calling query ---- " + jp.getSignature().getName());
		serviceRepo.updateAvgRating();
		serviceRepo.updatetheNumberOfSold();
		
	    }
		
	


	


}
