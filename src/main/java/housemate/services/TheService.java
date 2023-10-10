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
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.ServiceCategory;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SortRequired;
import housemate.constants.Enum.UsageDurationUnit;
import housemate.entities.PackageServiceItem;
import housemate.entities.Service;
import housemate.entities.ServiceType;
import housemate.exceptions.ApiException;
import housemate.models.ServiceNewDTO;
import housemate.models.ServiceViewDTO;
import housemate.repositories.PackageServiceItemRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.ServiceTypeRepository;
import housemate.services.interfaces.IService;
import housemate.models.ServiceViewDTO.ServicePrice;
/**
*
* @author Anh
*/

@org.springframework.stereotype.Service
public class TheService implements IService {

	@Autowired
	ServiceRepository serviceRepo;
	
	@Autowired
	ServiceTypeRepository serviceTypeRepo;
	
	@Autowired
	PackageServiceItemRepository packageServiceItemRepo;
	
	ModelMapper mapper = new ModelMapper();	
	
	@Override
	public List<Service> getAllAvailable() {
		List<Service> serviceList = serviceRepo.findAllAvailable();
		if(serviceList.isEmpty())
			throw new ApiException(HttpStatus.NOT_FOUND, "Empty Services Now !");
		return serviceList;
	}

	@Override
	public List<Service> searchFilterAllKind(
			String keywordValue ,
			Optional<ServiceCategory> category,
			Optional<SaleStatus> saleStatus,
			Optional<Integer> rating,
			Optional<ServiceField> sortBy,
			Optional<SortRequired> orderBy) {
		
		List<Service> serviceList;
		
		if(keywordValue != null) keywordValue = keywordValue.trim(); 
		else keywordValue = "";
		ServiceCategory cateogryValue = category.orElse(ServiceCategory.general);
		SaleStatus statusValue = saleStatus.orElse(SaleStatus.AVAILABLE); 
		Integer ratingValue = rating.orElse(0); 
		ServiceField fieldname = sortBy.orElse(ServiceField.PRICE);
		SortRequired requireOrder = orderBy.orElse(SortRequired.ASC);
		
		
		Sort sort;

		if (fieldname.equals(fieldname.PRICE)){
			sort = Sort.unsorted();
			serviceList = serviceRepo.searchFilterAllKind(statusValue, keywordValue, ratingValue, sort);
			Comparator<Service> theComparator = Comparator
					.comparingDouble(service -> (service.getOriginalPrice() - service.getSalePrice()));
			if (requireOrder.equals(SortRequired.DESC)) {
				theComparator = theComparator.reversed();
			}
			
			Collections.sort(serviceList, theComparator);
			
		}else {
			if (requireOrder.equals(SortRequired.ASC))
				sort = Sort.by(Sort.Direction.ASC, fieldname.getFieldName());
			sort = Sort.by(Sort.Direction.DESC, fieldname.getFieldName());
			serviceList = serviceRepo.searchFilterAllKind(statusValue, keywordValue, ratingValue, sort);
		}
		
		List<Service> updateListByCategory = new ArrayList<>();
		if (cateogryValue.equals(ServiceCategory.packages)) {	
			for (Service service : serviceList) 
				if (service.isPackage()) 
					updateListByCategory.add(service);
			serviceList = updateListByCategory;
		}if (cateogryValue.equals(ServiceCategory.singles)) {
			for (Service service : serviceList) 
				if (!service.isPackage())
					updateListByCategory.add(service);
			serviceList = updateListByCategory;
		}
		
		if (serviceList.isEmpty() || serviceList == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "Not found !");
		}
		return serviceList;
	}


	@Override
	public ServiceViewDTO getOne(int serviceId) {
		
		ServiceViewDTO serviceDtoForDetail = new ServiceViewDTO();
		
		Service service = serviceRepo.findById(serviceId).orElse(null);
		
		if(service == null) 
				throw new ApiException(HttpStatus.NOT_FOUND, "Not found this service !");
			
		serviceDtoForDetail.setService(service);
			
			if(!service.isPackage()){ //this is a service
				List<ServiceType> typeList = serviceTypeRepo.findAllByServiceId(service.getServiceId()).orElse(null);
				if(typeList != null)
					serviceDtoForDetail.setTypeList(typeList);
					
			}else if(service.isPackage()){ //this is a package
				List<PackageServiceItem> packageServiceChildList = 
						packageServiceItemRepo.findAllByPackageServiceId(service.getServiceId()).orElse(null);
				if(packageServiceChildList != null) {
					for (PackageServiceItem packageServiceItem : packageServiceChildList) {
						Service serviceChild = serviceRepo.findByServiceId(packageServiceItem.getSingleServiceId()).orElse(null);
						packageServiceItem.setDescription( serviceChild.getTitleName() +  " : " + serviceChild.getDescription());
						serviceDtoForDetail.setPackageServiceItemList(packageServiceChildList);
					}
				}
			}
			 //set combo price for each service
			List<ServicePrice> priceList = new ArrayList<>();	
			ServicePrice servicePrice = new ServicePrice();
			priceList.add(servicePrice.setPriceForComboMonth(service, 3, UsageDurationUnit.MONTH,0));
			priceList.add(servicePrice.setPriceForComboMonth(service, 6, UsageDurationUnit.MONTH,10000)); //extension fee 15000/month for 6 months
			priceList.add(servicePrice.setPriceForComboMonth(service, 12, UsageDurationUnit.MONTH,20000)); //extension fee 20000/month for 12 months
			serviceDtoForDetail.setPriceList(priceList);
			
		return serviceDtoForDetail;
	}

	@Override
	public ServiceViewDTO createNew(ServiceNewDTO serviceDTO) {	
		
		ServiceViewDTO savedServiceDTO = null;
			//Check duplicate title name
			if(serviceRepo.findByTitleNameIgnoreCase(serviceDTO.getTitleName().trim()) != null) {
				
				throw new DataAccessResourceFailureException("The title name has existed before !");
				
			}
			//Set auto sale status	
			if(serviceDTO.getSalePrice() >= serviceDTO.getOriginalPrice())
				throw new IllegalArgumentException("The sale price must be smaller than the original price");
			if (serviceDTO.getSalePrice() > 0)
				serviceDTO.setSaleStatus(SaleStatus.ONSALE);
			else
				serviceDTO.setSaleStatus(SaleStatus.AVAILABLE);
			
			
			//check type name of each single service is unique ignore case after request binding for Set
			if(!serviceDTO.isPackage()){
				if(serviceDTO.getTypeNameList() != null && serviceDTO.getServiceChildList() == null){
				Set<String> typeNameList = serviceDTO.getTypeNameList();
				Set<String> uniqueNames = new HashSet<>();
				for(String typeName : typeNameList) {
					if(!uniqueNames.add(typeName.toLowerCase().trim()))
						throw new ApiException(HttpStatus.BAD_REQUEST, "Not found !");
				}
			}
			else {
				throw new ApiException(HttpStatus.BAD_REQUEST, "The single service not allow to set service child list !");
			}
		}
			
			//check single service id existed in db
			if (serviceDTO.isPackage()) {
				if (serviceDTO.getServiceChildList() != null && serviceDTO.getTypeNameList() == null) {
					if(serviceDTO.getServiceChildList().size() < 2)
						throw new IllegalArgumentException("The package contains at least 2 single services !");
					for (Integer key : serviceDTO.getServiceChildList().keySet()) {
						if (serviceRepo.findByServiceId(key) == null)
							throw new ApiException(HttpStatus.BAD_REQUEST, "This service does not existing before");
					}
				} else {
					throw new ApiException(HttpStatus.BAD_REQUEST, "The package not allow to set type name list !");
				}
			}
			
			//map to DTO & save into DB
			Service savedService = serviceRepo.save(mapper.map(serviceDTO, Service.class));
			
			//save typeNameList for single services
			if(serviceDTO.getTypeNameList() != null && !serviceDTO.isPackage() && savedService != null) {
					int savedServiceId = savedService.getServiceId();
					for (String element : serviceDTO.getTypeNameList()) {
						ServiceType type = new ServiceType();
						type.setServiceId(savedServiceId);
						type.setTypeName(element.trim());
						serviceTypeRepo.save(type);
			        }	
			}
			
			//save child service for package
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
			savedServiceDTO = this.getOne(savedService.getServiceId());
	
			if(savedServiceDTO == null ) 
				throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Saved Failed !");
			
		return savedServiceDTO;
	}

	@Override
	public ServiceViewDTO updateInfo(int serviceId, ServiceNewDTO serviceDTO) {
		
		ServiceViewDTO savedServiceDTONewInfo = null;
		 Service oldService = serviceRepo.findById(serviceId).orElse(null);
		 if(oldService == null) {
				throw new ApiException(HttpStatus.NOT_FOUND, "The service with id : " + serviceId + "does not exists");			
			}
		//Update name
		 if(!serviceDTO.getTitleName().equalsIgnoreCase(oldService.getTitleName())) {
			 if(serviceRepo.findByTitleNameIgnoreCase(serviceDTO.getTitleName().trim()) != null) {
					throw new ApiException(HttpStatus.BAD_REQUEST, "The title name has existed before !");			
				}
		 }
		
		//update status 	
		if(serviceDTO.getSalePrice() >= serviceDTO.getOriginalPrice())
			throw new ApiException(HttpStatus.BAD_REQUEST, "The sale price must be smaller than the original price");
		
			if(!serviceDTO.getSaleStatus().equals(SaleStatus.DISCONTINUED)) {
				oldService.setSaleStatus(SaleStatus.DISCONTINUED);
				
			}else if(serviceDTO.getSalePrice() > 0 ){
				oldService.setSaleStatus(SaleStatus.ONSALE);
				
			}else
				oldService.setSaleStatus(SaleStatus.AVAILABLE);
		
			
		
		//check type name of each single service is unique ignore case after request binding for Set
		//update typeNameList for single services
		if(!oldService.isPackage()){
			if(serviceDTO.getTypeNameList() != null && serviceDTO.getServiceChildList() == null){
			Set<String> typeNameList = serviceDTO.getTypeNameList();
			Set<String> uniqueNames = new HashSet<>();
			for(String typeName : typeNameList) {
				if(!uniqueNames.add(typeName.toLowerCase().trim()))
					throw new ApiException(HttpStatus.BAD_REQUEST, "Duplicated the type name of this service !");
			}
		}
		else {
			throw new ApiException(HttpStatus.BAD_REQUEST, "This id is the single service. Not allow to set service child list !");
		}
			serviceTypeRepo.deleteAllByServiceId(serviceId);
			for(String element : serviceDTO.getTypeNameList()) {
				ServiceType type = new ServiceType();
				type.setServiceId(serviceId);
				type.setTypeName(element);
				serviceTypeRepo.save(type);
	        }
	}
		
		//check single service id existed in db
		if (oldService.isPackage()) {
			if (serviceDTO.getServiceChildList() != null && serviceDTO.getTypeNameList() == null) {
				if(serviceDTO.getServiceChildList().size() < 2)
					throw new ApiException(HttpStatus.BAD_REQUEST, "The package contains at least 2 single services !");
				for (Integer singleServiceId : serviceDTO.getServiceChildList().keySet()) {
					if(packageServiceItemRepo.findByPackageServiceIdAndSingleServiceId(serviceId,singleServiceId) == null);
						throw new ApiException(HttpStatus.BAD_REQUEST, "Not allow to change the existing single service item list in this package");
				}
			} else {
				throw new IllegalArgumentException("This id is the package. Not allow to set type name list !");
			}
			Map<Integer, Integer> childServiceSet = serviceDTO.getServiceChildList();
			for(Integer singleServiceId : childServiceSet.keySet()) {
				PackageServiceItem item = 
						packageServiceItemRepo.findByPackageServiceIdAndSingleServiceId(serviceId, singleServiceId).orElse(null);
				item.setQuantity(childServiceSet.get(singleServiceId));
				packageServiceItemRepo.save(item);
	        }
		}
		
		//check typename list and single service list ok then save all into db
		oldService.setTitleName(serviceDTO.getTitleName());
		oldService.setDescription(serviceDTO.getDescription());
		oldService.setOriginalPrice(serviceDTO.getOriginalPrice());
		oldService.setSalePrice(serviceDTO.getSalePrice());
		oldService.setImageUrl(serviceDTO.getImageUrl());	
		Service savedService = serviceRepo.save(oldService);
		
		savedServiceDTONewInfo = this.getOne(savedService.getServiceId());

	return savedServiceDTONewInfo;
	}
	
	




	






}
