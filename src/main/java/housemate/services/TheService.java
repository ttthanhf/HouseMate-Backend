package housemate.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.javapoet.TypeName;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import housemate.configs.ModelMapper;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.ServiceCategory;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SortRequired;
import housemate.constants.Enum.UsageDurationUnit;
import housemate.entities.PackageServiceItem;
import housemate.entities.Service;
import housemate.entities.ServiceType;
import housemate.models.ServiceNewDTO;
import housemate.models.ServiceViewDTO;
import housemate.repositories.PackageServiceItemRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.ServiceTypeRepository;
import housemate.services.interfaces.IService;
import housemate.models.ServiceViewDTO.PackagePrice;
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
	
	private static final Logger logger = LoggerFactory.getLogger(TheService.class);

	@Override
	public List<Service> getAllAvailable() {
		List<Service> serviceDtoList = serviceRepo.findAllAvailable();
		return serviceDtoList;
	}

	@Override
	public List<Service> fitlerAndSortForAllKind(ServiceCategory category, SaleStatus saleStatus, int ratingUpperFrom,
			ServiceField fieldname, SortRequired requireOrder) {

		List<Service> serviceDtoList;
		Sort sort;

		if (fieldname.equals(fieldname.PRICE)) {
			sort = Sort.unsorted();
			serviceDtoList = serviceRepo.filterAllKind(saleStatus, ratingUpperFrom, sort);
			Comparator<Service> theComparator = Comparator
					.comparingDouble(service -> (service.getOriginalPrice() - service.getSalePrice()));
			if (requireOrder.equals(SortRequired.DESC)) {
				theComparator = theComparator.reversed();
			}
			Collections.sort(serviceDtoList, theComparator);
		} else {
			if (requireOrder.equals(SortRequired.ASC))
				sort = Sort.by(Sort.Direction.ASC, fieldname.getFieldName());
			sort = Sort.by(Sort.Direction.DESC, fieldname.getFieldName());
			serviceDtoList = serviceRepo.filterAllKind(saleStatus, ratingUpperFrom, sort);
		}
		
		List<Service> updateList = new ArrayList<>();
		if (category.equals(ServiceCategory.packages)) {	
			for (Service service : serviceDtoList) 
				if (service.isPackage()) 
					updateList.add(service);
			serviceDtoList = updateList;
		} if (category.equals(ServiceCategory.singles)) {
			for (Service service : serviceDtoList) 
				if (!service.isPackage())
					updateList.add(service);
			serviceDtoList = updateList;
		}
		return serviceDtoList;
	}


	@Override
	public List<Service> searchForAllKind(String keyword, ServiceCategory category, SaleStatus saleStatus,
			int ratingUpperFrom, ServiceField fieldname, SortRequired requireOrder) {
		List<Service> serviceDtoList;
		Sort sort;

		if (fieldname.equals(fieldname.PRICE)) {
			sort = Sort.unsorted();
			serviceDtoList = serviceRepo.searchAllKind( saleStatus, keyword, ratingUpperFrom, sort);
			Comparator<Service> theComparator = Comparator
					.comparingDouble(service -> (service.getOriginalPrice() - service.getSalePrice()));
			if (requireOrder.equals(SortRequired.DESC)) {
				theComparator = theComparator.reversed();
			}
			Collections.sort(serviceDtoList, theComparator);
		} else {
			if (requireOrder.equals(SortRequired.ASC))
				sort = Sort.by(Sort.Direction.ASC, fieldname.getFieldName());
			sort = Sort.by(Sort.Direction.DESC, fieldname.getFieldName());
			serviceDtoList = serviceRepo.searchAllKind(saleStatus, keyword, ratingUpperFrom, sort);
		}
		
		List<Service> updateList = new ArrayList<>();
		if (category.equals(ServiceCategory.packages)) {	
			for (Service service : serviceDtoList) 
				if (service.isPackage()) 
					updateList.add(service);
			serviceDtoList = updateList;
		} if (category.equals(ServiceCategory.singles)) {
			for (Service service : serviceDtoList) 
				if (!service.isPackage())
					updateList.add(service);
			serviceDtoList = updateList;
		}
		return serviceDtoList;
	}





	@Override
	public ServiceViewDTO getOne(int serviceId) {
		ServiceViewDTO serviceDtoForDetail = new ServiceViewDTO();
		Service service = serviceRepo.findById(serviceId).orElse(null);
		if(service != null) {
			serviceDtoForDetail.setService(service);
			if(!service.isPackage()) //this is a service
			{
				List<ServiceType> typeList = serviceTypeRepo.findAllByserviceId(service.getServiceId()).orElse(null);
				if(typeList != null)
					serviceDtoForDetail.setTypeList(typeList);
					
			}else if(service.isPackage()){ //this is a package
				List<PackageServiceItem> packageServiceChildList = packageServiceItemRepo.findAllByPackageServiceId(service.getServiceId()).orElse(null);
				if(packageServiceChildList != null) {
					serviceDtoForDetail.setPackageServiceItemList(packageServiceChildList);
				}
				 //set combo price for each package service
					List<PackagePrice> priceList = new ArrayList<>();	
					PackagePrice packagePrice = new PackagePrice();
					priceList.add(packagePrice.setPackagePrice(service, 3, UsageDurationUnit.MONTH));
					priceList.add(packagePrice.setPackagePrice(service, 6, UsageDurationUnit.MONTH));
					priceList.add(packagePrice.setPackagePrice(service, 12, UsageDurationUnit.MONTH));
					serviceDtoForDetail.setPriceList(priceList);
			}
		}
		return serviceDtoForDetail;
	}

	@Override
	public ServiceViewDTO createNew(ServiceNewDTO serviceDTO) {	
		//set sale status base on sale price and original price
		if(serviceDTO.getSalePrice() > serviceDTO.getOriginalPrice());
		if (serviceDTO.getSalePrice() > 0)
			serviceDTO.setSaleStatus(SaleStatus.ONSALE);
		else
			serviceDTO.setSaleStatus(SaleStatus.AVAILABLE);
		
		//map to DTO
		Service newService = mapper.map(serviceDTO, Service.class);
		//save into DB
		Service savedService = serviceRepo.save(newService);
		
		//save typeNameList for single services
		if(serviceDTO.getTypeNameList() != null && !serviceDTO.isPackage() && savedService != null) {
				int savedServiceId = savedService.getServiceId();
				for(String element : serviceDTO.getTypeNameList()) {
					ServiceType type = new ServiceType();
					type.setServiceId(savedServiceId);
					type.setTypeName(element);
					System.out.println("ID: " + type.getServiceId() + "Name: " + type.getTypeName());
					serviceTypeRepo.save(type);
		        }	
		}
		
		//save childService for package
		if(serviceDTO.getServiceChildList() != null && serviceDTO.isPackage() && savedService != null) {
			int savedServiceId = savedService.getServiceId();
			System.out.println("ID: ======== " + savedServiceId);
			Map<Integer, Integer> childServiceSet = serviceDTO.getServiceChildList();
			Set<Integer> keySet = childServiceSet.keySet();
			for(Integer singleServiceId : keySet) {
				PackageServiceItem item = new PackageServiceItem();
				item.setPackageServiceId(savedServiceId);
				item.setService(serviceRepo.findByServiceId(singleServiceId).orElse(null));
				item.setQuantity(childServiceSet.get(singleServiceId));
				System.out.println("ID: " + singleServiceId + " Name: " + childServiceSet.get(singleServiceId));
				packageServiceItemRepo.save(item);
	        }
			
		}
		
		return this.getOne(savedService.getServiceId());
	}

	public boolean duplicateTitleName(String titleName) {
		if (serviceRepo.findByTitleNameIgnoreCase(titleName.trim()) != null)
			return true;
		return false;
	}
	






}
