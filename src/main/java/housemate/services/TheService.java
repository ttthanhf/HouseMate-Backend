package housemate.services;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.ServiceCategory;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SortRequired;
import housemate.constants.Enum.UnitOfMeasure;
import housemate.constants.Enum.UsageDurationUnit;
import housemate.entities.PackageServiceItem;
import housemate.entities.Service;
import housemate.entities.ServiceType;
import housemate.models.ServiceNewDTO;
import housemate.models.ServiceViewDTO;
import housemate.repositories.PackageServiceItemRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.ServiceTypeRepository;
import housemate.models.ServiceViewDTO.ServicePrice;
/**
*
* @author Anh
*/

@org.springframework.stereotype.Service
public class TheService  {

	@Autowired
	ServiceRepository serviceRepo;
	
	@Autowired
	ServiceTypeRepository serviceTypeRepo;
	
	@Autowired
	PackageServiceItemRepository packageServiceItemRepo;
	
	ModelMapper mapper = new ModelMapper();	
	
	public ResponseEntity<?> getAllAvailable() {
		List<Service> serviceList = serviceRepo.findAllAvailable();
		if (serviceList.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empty Services Now !");
		return ResponseEntity.ok(serviceList);
	}

	public ResponseEntity<?> searchFilterAllKind(
			String keywordValue ,
			Optional<ServiceCategory> category,
			Optional<Boolean> topsale,
			Optional<SaleStatus> saleStatus,
			Optional<Integer> rating,
			Optional<ServiceField> sortBy,
			Optional<SortRequired> orderBy
			) {
		
		List<Service> serviceList;
		
		ServiceCategory cateogryValue = category.orElse(ServiceCategory.GENERAL);
		SaleStatus statusValue = saleStatus.orElse(SaleStatus.AVAILABLE); 
		boolean isTopSale = topsale.orElse(false);
		Integer ratingValue = rating.orElse(0); 
		ServiceField fieldname = sortBy.orElse(ServiceField.PRICE);
		SortRequired requireOrder = orderBy.orElse(SortRequired.ASC);
		
		Sort sort;
		// sort by field
		if (requireOrder.equals(SortRequired.ASC))
			sort = Sort.by(Sort.Direction.ASC, fieldname.getFieldName());
		sort = Sort.by(Sort.Direction.DESC, fieldname.getFieldName());

		serviceList = isTopSale == true 
				? serviceRepo.searchFilterForTopSale(statusValue, ratingValue)
				: serviceRepo.searchFilterAllKind(statusValue, keywordValue, ratingValue, sort);
		
		// For sort by price field only
		if (fieldname.equals(fieldname.PRICE)) {
			Comparator<Service> theComparator = Comparator
					.comparingDouble(service -> (service.getOriginalPrice() - service.getSalePrice()));
			if (requireOrder.equals(SortRequired.DESC)) {
				theComparator = theComparator.reversed();
			}
			Collections.sort(serviceList, theComparator);
		}
		
		//Update the list by category
		List<Service> updateListByCategory = new ArrayList<>();
		if (cateogryValue.equals(ServiceCategory.PACKAGES)) {	
			for (Service service : serviceList) 
				if (service.isPackage()) 
					updateListByCategory.add(service);
			serviceList = updateListByCategory;
		} 
		if (cateogryValue.equals(ServiceCategory.SINGLES)){
			for (Service service : serviceList) 
				if (!service.isPackage())
					updateListByCategory.add(service);
			serviceList = updateListByCategory;
		}
		
		if (serviceList.isEmpty() || serviceList == null){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found !");
		}
		
		return ResponseEntity.ok(serviceList);
	}


	public ResponseEntity<?> getOne(int serviceId) {

		ServiceViewDTO serviceDtoForDetail = new ServiceViewDTO();
		
		Service service = serviceRepo.findById(serviceId).orElse(null);
		
		if (service == null) 
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found this service !");
			
		serviceDtoForDetail.setService(service);
			
		if (!service.isPackage()) { // this is a service
			List<ServiceType> typeList = serviceTypeRepo
					.findAllByServiceId(service.getServiceId()).orElse(null);
			if (typeList != null)
				serviceDtoForDetail.setTypeList(typeList);
		} else if (service.isPackage()) { // this is a package
			List<PackageServiceItem> packageServiceChildList = packageServiceItemRepo
					.findAllByPackageServiceId(service.getServiceId()).orElse(null);
			if (packageServiceChildList != null) {
				for (PackageServiceItem packageServiceItem : packageServiceChildList) {
					Service serviceChild = serviceRepo
							.findByServiceId(packageServiceItem.getSingleServiceId()).orElse(null);
					packageServiceItem
							.setDescription(serviceChild.getTitleName() + " : " + serviceChild.getDescription());
				}
				serviceDtoForDetail.setPackageServiceItemList(packageServiceChildList);
			}
		}
		
		//set combo price for each service
		List<ServicePrice> priceList = new ArrayList<>();
		ServicePrice servicePrice = new ServicePrice();
		priceList.add(servicePrice.setPriceForComboMonth(service, 3, UsageDurationUnit.MONTH, 0));
		priceList.add(servicePrice.setPriceForComboMonth(service, 6, UsageDurationUnit.MONTH, 10000));//extension fee 15000/month for 6 months
		priceList.add(servicePrice.setPriceForComboMonth(service, 12, UsageDurationUnit.MONTH, 20000));//extension fee 20000/month for 12 months
		serviceDtoForDetail.setPriceList(priceList);
			
		return ResponseEntity.ok().body(serviceDtoForDetail);
	}

	public ResponseEntity<?> createNew(ServiceNewDTO serviceDTO) {	
		
		Service savedService = null;
		try {
			// Check duplicate title name
			if (serviceRepo.findByTitleNameIgnoreCase(serviceDTO.getTitleName().trim()) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The title name has existed before !");
			}

			// Set auto sale status
			if (serviceDTO.getSalePrice() >= serviceDTO.getOriginalPrice())
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The sale price must be smaller than the original price");
			if (serviceDTO.getSalePrice() > 0)
				serviceDTO.setSaleStatus(SaleStatus.ONSALE);
			else
				serviceDTO.setSaleStatus(SaleStatus.AVAILABLE);

			// check if single service is not allow to
			if (!serviceDTO.isPackage() && serviceDTO.getServiceChildList() == null)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The single service not allow to set service child list !");

			//check single service id existed in db
			if (serviceDTO.isPackage()) {
				if (serviceDTO.getServiceChildList() != null && serviceDTO.getTypeNameList() == null) {
					if (serviceDTO.getServiceChildList().size() < 2)
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The package contains at least 2 single services !");
					if (!serviceDTO.getUnitOfMeasure().equals(UnitOfMeasure.COMBO))
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The unit of measure of package must be COMBO");
					for (Integer key : serviceDTO.getServiceChildList().keySet()) {
						if (serviceRepo.findByServiceId(key) == null)
							return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This service does not existing before");
					}
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The package not allow to set type name list !");
				}
			}
			
			//map to DTO & save into DB
			savedService = serviceRepo.save(mapper.map(serviceDTO, Service.class));
			if (savedService == null)
				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Saved Failed ! ");
			
			// save typeNameList for single services after saving Service object success
			if (serviceDTO.getTypeNameList() != null && !serviceDTO.isPackage() && savedService != null) {
				int savedServiceId = savedService.getServiceId();
				for (String element : serviceDTO.getTypeNameList()) {
					ServiceType type = new ServiceType();
					type.setServiceId(savedServiceId);
					type.setTypeName(element.trim());
					serviceTypeRepo.save(type);
				}
			}
			
			//save child service for package after saving Service Object success
			if(serviceDTO.getServiceChildList() != null && serviceDTO.isPackage() && savedService != null) {
				int savedServiceId = savedService.getServiceId();
				Map<Integer, Integer> childServiceSet = serviceDTO.getServiceChildList();
				for(Integer singleServiceId : childServiceSet.keySet()) {
					PackageServiceItem item = new PackageServiceItem();
					item.setPackageServiceId(savedServiceId);
					item.setSingleServiceId(singleServiceId);
					item.setQuantity(childServiceSet.get(singleServiceId));
					packageServiceItemRepo.save(item);
		        }
			}
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Saved Failed ! ");
		}
		
		return getOne(savedService.getServiceId());
	}

	public ResponseEntity<?> updateInfo(int serviceId, ServiceNewDTO serviceDTO) {

		Service savedService = null;

		try {
			Service oldService = serviceRepo.findById(serviceId).orElse(null);
			if (oldService == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The service does not exists");
			}

			// Update name
			if (!serviceDTO.getTitleName().equalsIgnoreCase(oldService.getTitleName())) {
				if (serviceRepo.findByTitleNameIgnoreCase(serviceDTO.getTitleName().trim()) != null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The title name has existed before !");
				}
			}

			// update status //TODO: Check based on figma
			if (serviceDTO.getSalePrice() >= serviceDTO.getOriginalPrice())
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The sale price must be smaller than the original price");
			if (serviceDTO.getSaleStatus().equals(SaleStatus.DISCONTINUED)) {
				oldService.setSaleStatus(SaleStatus.DISCONTINUED);
			} else if (serviceDTO.getSalePrice() > 0) {
				oldService.setSaleStatus(SaleStatus.ONSALE);
			} else
				oldService.setSaleStatus(SaleStatus.AVAILABLE);

			// check type name of each single service is unique ignore case after request
			// binding for Set
			// update typeNameList for single services
			if (!oldService.isPackage()) {
				if (serviceDTO.getTypeNameList() != null && serviceDTO.getServiceChildList() == null) {
					Set<String> typeNameList = serviceDTO.getTypeNameList();
					Set<String> uniqueNames = new HashSet<>();
					for (String typeName : typeNameList) {
						if (!uniqueNames.add(typeName.toLowerCase().trim()))
							return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated the type name of this service !");
					}
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This id is the single service. Not allow to set service child list !");
				}
				serviceTypeRepo.deleteAllByServiceId(serviceId);
				for (String element : serviceDTO.getTypeNameList()) {
					ServiceType type = new ServiceType();
					type.setServiceId(serviceId);
					type.setTypeName(element);
					serviceTypeRepo.save(type);
				}
			}

			// check single service id existed in db
			if (oldService.isPackage()) {
				if (serviceDTO.getServiceChildList() != null && serviceDTO.getTypeNameList() == null) {
					if (serviceDTO.getServiceChildList().size() < 2)
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The package contains at least 2 single services !");
					for (Integer singleServiceId : serviceDTO.getServiceChildList().keySet()) {
						if (packageServiceItemRepo.findByPackageServiceIdAndSingleServiceId(serviceId, singleServiceId) == null)
							return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not allow to change the existing single service item list in this package");
					}
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This id is the package. Not allow to set type name list !");
				}
				Map<Integer, Integer> childServiceSet = serviceDTO.getServiceChildList();
				for (Integer singleServiceId : childServiceSet.keySet()) {
					PackageServiceItem item = packageServiceItemRepo
							.findByPackageServiceIdAndSingleServiceId(serviceId, singleServiceId).orElse(null);
					item.setQuantity(childServiceSet.get(singleServiceId));
					packageServiceItemRepo.save(item);
				}
			}

			// check typename list and single service list ok then save all into db
			oldService.setTitleName(serviceDTO.getTitleName());
			oldService.setDescription(serviceDTO.getDescription());
			oldService.setOriginalPrice(serviceDTO.getOriginalPrice());
			oldService.setSalePrice(serviceDTO.getSalePrice());
			oldService.setGroupType(serviceDTO.getGroupType());
			savedService = serviceRepo.save(oldService);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Update Failed ! ");
		}

		return this.getOne(savedService.getServiceId());
	}
	
	public ResponseEntity<?> getAllKind() {
		List<Service> serviceList = serviceRepo.findAll();
		if (serviceList == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty List !");

		return ResponseEntity.ok().body(serviceList);
	}
	
}
