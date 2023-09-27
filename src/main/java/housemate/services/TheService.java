package housemate.services;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import housemate.entities.Service;
import housemate.entities.enums.SaleStatus;
import housemate.repositories.ServiceRepository;
import housemate.services.interfaces.IService;
import jakarta.xml.bind.PropertyException;

@org.springframework.stereotype.Service
public class TheService implements IService {

	@Autowired
	ServiceRepository serviceRepo;

	private static final Logger logger = LoggerFactory.getLogger(TheService.class);

	@Override
	public List<Service> getAll() {

		return serviceRepo.findAll();
	}

	@Override
	public List<Service> searchByName(String keyword) {

		return serviceRepo.findByTitleNameContaining(keyword);
	}

	@Override
	public List<Service> sortByOneField(String fieldName, String orderRequire) {

		// orderRequire: A-Z = asc, Z-A = desc
		
		List<Service> service;
		try {
			Sort nameSort = Sort.by(fieldName.trim());
			if(orderRequire.equalsIgnoreCase("asc")) service = serviceRepo.findAll(nameSort.ascending());
			else service = serviceRepo.findAll(nameSort.descending());
		}catch (Exception e) {
			service = null;
			e.printStackTrace();
		}
			
			return service;

	}

	@Override
	public List<Service> filterBySaleStatus(SaleStatus saleStatus) {

		return serviceRepo.findBySaleStatus(saleStatus);
	}

	@Override
	public Service getOne(int serviceId) {

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
			service.setSaleStatus(SaleStatus.NOT_AVAILABLE);
			service = serviceRepo.save(service);
			return serviceRepo.findById(service.getId()).orElse(null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Service updateInfo(int serviceId, Service newServiceInfo) {
		Service service = serviceRepo.findById(serviceId).orElse(null);
		try {

			if (service == null) {
				logger.warn("The Service Not Exists To Update !");
				throw new Exception("The service with the ID: " + serviceId + "Not Exist !");

			}
			service.setTitleName(newServiceInfo.getTitleName().trim());
			service.setDescription(newServiceInfo.getTitleName().trim());
			service.setSalePrice(newServiceInfo.getSalePrice());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return service;
	}

	@Override
	public Service updateSaleStatus(int serviceId, SaleStatus saleStatus) {
		Service service = serviceRepo.findById(serviceId).orElse(null);
		if (service != null) {
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
				logger.warn("The Service Not Exist To Remove !");
				throw new Exception("The service with the ID: " + serviceId + "Not Exist !");
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
