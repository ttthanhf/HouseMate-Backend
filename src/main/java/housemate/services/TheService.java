package housemate.services;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import housemate.entities.Service;
import housemate.entities.enums.SaleStatus;
import housemate.repositories.ServiceRepository;

@org.springframework.stereotype.Service
public class TheService implements IService {

	@Autowired
	ServiceRepository serviceRepo ;

	private static final Logger logger = LoggerFactory.getLogger(TheService.class);

	@Override
	public List<Service> viewAll() {

		return serviceRepo.findAll();
	}

	@Override
	public List<Service> searchByName(String keyword) {

		return serviceRepo.findByTitleNameContaining(keyword);
	}

	@Override
	public List<Service> sortByOneField(String fieldName, String orderRequire) {

		// orderRequire: A-Z = asc, Z-A = desc
		return serviceRepo.sortByOneField(fieldName.trim().toLowerCase(), orderRequire.trim().toLowerCase());
	}

	@Override
	public List<Service> filterBySaleStatus(SaleStatus saleStatus) {

		return serviceRepo.findBySaleStatus(saleStatus.name());
	}

	@Override
	public Service viewOne(int serviceId) {

		return serviceRepo.findById(serviceId).orElse(null);
	}

	@Override
	public Service createNew(Service service) {
		try {
			if (duplicateTitleName(service.getTitleName())) {
				logger.info("Duplicated Service Title Name - Pick Another Name");
				throw new Exception("Duplicated Title Name");
			}
			LocalDateTime currentDateTime = LocalDateTime.now();
			service.setCreatorId(0001);
			service.setCreatedAt(currentDateTime);
			service = serviceRepo.save(service);
			return serviceRepo.findById(service.getId()).orElse(null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Service updateInfo(int serviceId, Service newServiceInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Service updateSaleStatus(int serviceId, SaleStatus saleStatus) {
		Service service = serviceRepo.findById(serviceId).orElse(null);
		if(service != null) {
			service.setSaleStatus(saleStatus);
			service = serviceRepo.save(service);
			return service;
		}
		return service;
	}

	@Override
	public void removeOne(int serviceId) {
		try {
			if (serviceRepo.findById(serviceId) == null) {
				logger.warn("The Service Not Exists To Remove !");
				throw new Exception("The service with the ID: " + serviceId + "Not Exists !");
			}
			serviceRepo.deleteById(serviceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean duplicateTitleName(String titleName) {
		if (serviceRepo.findByTitleNameIgnoreCase(titleName.trim()) != null)
			return true;
		return false;
	}

}
