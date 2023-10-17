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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import housemate.constants.Role;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.ServiceCategory;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SortRequired;
import housemate.constants.Enum.UnitOfMeasure;
import housemate.constants.Enum.UsageDurationUnit;
import housemate.entities.PackageServiceItem;
import housemate.entities.Period;
import housemate.entities.Service;
import housemate.entities.ServiceType;
import housemate.models.ServiceNewDTO;
import housemate.models.ServiceViewDTO;
import housemate.repositories.CommentRepository;
import housemate.repositories.FeedbackRepository;
import housemate.repositories.PackageServiceItemRepository;
import housemate.repositories.PeriodRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.ServiceTypeRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import housemate.models.ServiceViewDTO.ServicePrice;

/**
 *
 * @author Anh
 */

@Component
public class TheService {

	@Autowired
	ServiceRepository serviceRepo;

	@Autowired
	ServiceTypeRepository serviceTypeRepo;

	@Autowired
	PackageServiceItemRepository packageServiceItemRepo;

	@Autowired
	CommentRepository commentRepo;

	@Autowired
	FeedbackRepository feedbackRepo;

	@Autowired
	PeriodRepository periodRepo;
	
	@Autowired
    AuthorizationUtil authorizationUtil;

	ModelMapper mapper = new ModelMapper();

	public ResponseEntity<?> getAllAvailable() {
		List<Service> serviceList = serviceRepo.findAllAvailable();
		if (serviceList.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empty Services Now !");
		return ResponseEntity.ok(serviceList);
	}

	public ResponseEntity<?> getAllKind(HttpServletRequest request) {
		if (!authorizationUtil.getRoleFromAuthorizationHeader(request).equals(Role.ADMIN.toString()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");
		List<Service> serviceList = serviceRepo.findAll();
		if (serviceList == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty List !");
		return ResponseEntity.ok().body(serviceList);
	}

	public ResponseEntity<?> getAllSingleService() {
		List<Service> serviceList = serviceRepo.findAllByIsPackageFalse();
		if (serviceList == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty List !");
		return ResponseEntity.ok().body(serviceList);
	}

	public ResponseEntity<?> getTopsale() {
		List<Service> serviceList = serviceRepo.findTopSale();
		if (serviceList == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty List !");
		return ResponseEntity.ok().body(serviceList);
	}

	public ResponseEntity<?> searchFilterAllKind(
			String keywordValue,
			Optional<ServiceCategory> category,
			Optional<SaleStatus> saleStatus,
			Optional<Integer> rating,
			Optional<ServiceField> sortBy,
			Optional<SortRequired> orderBy) {

		List<Service> serviceList;

		ServiceCategory cateogryValue = category.orElse(ServiceCategory.GENERAL);
		SaleStatus statusValue = saleStatus.orElse(SaleStatus.AVAILABLE);
		Integer ratingValue = rating.orElse(0);
		ServiceField fieldname = sortBy.orElse(ServiceField.PRICE);
		SortRequired requireOrder = orderBy.orElse(SortRequired.ASC);

		// sort by field
		Sort sort;
		if (requireOrder.equals(SortRequired.ASC))
			sort = Sort.by(Sort.Direction.ASC, fieldname.getFieldName());
		else
			sort = Sort.by(Sort.Direction.DESC, fieldname.getFieldName());

		serviceList = serviceRepo.searchFilterAllKind(statusValue, keywordValue, ratingValue, sort);

		// For sort by price field only
		if (fieldname.equals(fieldname.PRICE)) {
			Comparator<Service> theComparator = Comparator
					.comparingDouble(service -> service.getOriginalPrice() - service.getSalePrice());
			if (requireOrder.equals(SortRequired.DESC))
				theComparator = theComparator.reversed();

			Collections.sort(serviceList, theComparator);
		}

		// Update the list by category
		List<Service> updateListByCategory = new ArrayList<>();
		if (cateogryValue.equals(ServiceCategory.PACKAGES)) {
			for (Service service : serviceList)
				if (service.isPackage())
					updateListByCategory.add(service);
			serviceList = updateListByCategory;
		}
		if (cateogryValue.equals(ServiceCategory.SINGLES)) {
			for (Service service : serviceList)
				if (!service.isPackage())
					updateListByCategory.add(service);
			serviceList = updateListByCategory;
		}

		if (serviceList.isEmpty() || serviceList == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found !");

		return ResponseEntity.ok(serviceList);
	}

	public ResponseEntity<?> getOne(int serviceId) {

		ServiceViewDTO serviceDtoForDetail = new ServiceViewDTO();

		Service service = serviceRepo.findById(serviceId).orElse(null);

		if (service == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found this service !");

		service.setNumberOfReview(feedbackRepo.findAllByServiceId(serviceId).size());
		service.setNumberOfComment(commentRepo.getAllCommentByServiceId(serviceId).size());
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

		// set combo price for each service
		List<ServicePrice> priceList = new ArrayList<>();
		ServicePrice servicePrice = new ServicePrice();
		List<Period> periodService = periodRepo.findAll();
		periodService.forEach(s -> priceList.add(
				servicePrice.setPriceForComboMonth(service, s.getPeriodId(), UsageDurationUnit.MONTH, s.getPercent())));
		serviceDtoForDetail.setPriceList(priceList);

		// TODO: Update imgList later
		List<String> imgList = new ArrayList<>();
		imgList.add("https://t.ly/itj2o");
		imgList.add("https://t.ly/sRTe7");
		imgList.add("https://t.ly/9xEHO");
		imgList.add("bit.ly/48Ua85g");
		imgList.add("bit.ly/45vftNa");
		imgList.add("bit.ly/3tsNi4d");
		serviceDtoForDetail.setImages(imgList);

		return ResponseEntity.ok().body(serviceDtoForDetail);
	}

	@Transactional
	public ResponseEntity<?> createNew(HttpServletRequest request, ServiceNewDTO serviceDTO) {
		
		// check the role admin is allowed
		if (!authorizationUtil.getRoleFromAuthorizationHeader(request).equals(Role.ADMIN.toString()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");

		Service savedService = null;
		
		try {
			// Check all before saving object service
			// Check duplicate title name
			if (serviceRepo.findByTitleNameIgnoreCase(serviceDTO.getTitleName().trim()) != null)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The title name has existed before !");

			// Set auto sale status
			if (serviceDTO.getSaleStatus().equals(SaleStatus.DISCONTINUED))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Create new service the sale status must be Onsale or Available");
			else if (serviceDTO.getSalePrice() > 0)
				serviceDTO.setSaleStatus(SaleStatus.ONSALE);
			else
				serviceDTO.setSaleStatus(SaleStatus.AVAILABLE);

			// check single service contraints
			if (!serviceDTO.getIsPackage()) {
				if (serviceDTO.getServiceChildList() != null)
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("The single service not allow to set service child list !");
				if (serviceDTO.getUnitOfMeasure().equals(UnitOfMeasure.COMBO))
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("The unit of measure of single service should not be COMBO !");
				if (serviceDTO.getTypeNameList() != null) {
					Set<String> typeNameList = serviceDTO.getTypeNameList();
					Set<String> uniqueNames = new HashSet<>();
					// check any type name have equal ignore case
					for (String typeName : typeNameList)
						if (!uniqueNames.add(typeName.toLowerCase().trim()))
							return ResponseEntity.status(HttpStatus.BAD_REQUEST)
									.body("Duplicated the type name in this service !");
				}
			}

			// check package constraints
			if (serviceDTO.getIsPackage()) {
				if (serviceDTO.getTypeNameList() != null)
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("The package not allow to set type name list !");
				if (serviceDTO.getServiceChildList() == null || serviceDTO.getServiceChildList().size() < 2)
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("The package contains at least 2 single services !");
				if (!serviceDTO.getUnitOfMeasure().equals(UnitOfMeasure.COMBO))
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("The unit of measure of package must be COMBO !");
				for (Integer singleServiceId : serviceDTO.getServiceChildList().keySet()) {
					if (serviceRepo.findByServiceId(singleServiceId).isEmpty())
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Single service child id "
								+ singleServiceId + " does not existing in provided list !");
					if (serviceDTO.getServiceChildList().get(singleServiceId) <= 0)
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("The quantity of single child service must greater than 0 !");
				}
			}

			// TODO CHECK IMAGES CONSTRAINTS HERE IF HAVE

			// after check all then map to DTO & save SavedService into DB to get new
			// service Id;
			savedService = serviceRepo.save(mapper.map(serviceDTO, Service.class));

			if (savedService == null)
				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Saved Failed !");

			// save typeNameList for single services after saving Service object success
			if (serviceDTO.getTypeNameList() != null && !serviceDTO.getIsPackage() && savedService != null) {
				int savedServiceId = savedService.getServiceId();
				for (String element : serviceDTO.getTypeNameList()) {
					ServiceType type = new ServiceType();
					type.setServiceId(savedServiceId);
					type.setTypeName(element.trim());
					serviceTypeRepo.save(type);
				}
			}

			// save child service for package after saving Service Object success
			if (serviceDTO.getServiceChildList() != null && serviceDTO.getIsPackage() && savedService != null) {
				int savedServiceId = savedService.getServiceId();
				Map<Integer, Integer> childServiceSet = serviceDTO.getServiceChildList();
				int sumSingleServiceSalePrice = 0;
				for (Integer singleServiceId : childServiceSet.keySet()) {
					PackageServiceItem item = new PackageServiceItem(); // save package service item
					item.setPackageServiceId(savedServiceId);
					item.setSingleServiceId(singleServiceId);
					item.setQuantity(childServiceSet.get(singleServiceId));
					sumSingleServiceSalePrice += (serviceRepo.findByServiceId(singleServiceId).orElse(null)
							.getOriginalPrice() * item.getQuantity());
					packageServiceItemRepo.save(item);
				}

				// check original price of package
				if (serviceDTO.getOriginalPrice() != sumSingleServiceSalePrice) {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
							"The original price of package must be the sum of all single service child list ! "
									+ "\nThe original price of package should be " + sumSingleServiceSalePrice);
				}
				savedService.setOriginalPrice(sumSingleServiceSalePrice);
				serviceRepo.save(savedService);

				// TODO SAVE IMAGES

			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Saved Failed !");
		}

		return getOne(savedService.getServiceId());
	}

	@Transactional
	public ResponseEntity<?> updateInfo(HttpServletRequest request, int serviceId, ServiceNewDTO serviceDTO) {

		// check the role admin is allowed
		if (!authorizationUtil.getRoleFromAuthorizationHeader(request).equals(Role.ADMIN.toString()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");

		Service savedService = null;

		try {
			Service oldService = serviceRepo.findById(serviceId).orElse(null);
			if (oldService == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The service does not exists !");

			// Update name
			if (!serviceDTO.getTitleName().equalsIgnoreCase(oldService.getTitleName()))
				if (serviceRepo.findByTitleNameIgnoreCase(serviceDTO.getTitleName().trim()) != null)
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The title name has existed before !");

			// update status //TODO: Check based on figma
			if (serviceDTO.getSaleStatus().equals(SaleStatus.DISCONTINUED))
				oldService.setSaleStatus(SaleStatus.DISCONTINUED);
			else if (serviceDTO.getSalePrice() > 0)
				oldService.setSaleStatus(SaleStatus.ONSALE);
			else
				oldService.setSaleStatus(SaleStatus.AVAILABLE);

			//check not allow to update the unit of measure
			if (!serviceDTO.getUnitOfMeasure().equals(oldService.getUnitOfMeasure()))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Not allow to update the unit of measure. Please correct the unit of measure to " + oldService.getUnitOfMeasure());
			
			// check is package
			if (oldService.isPackage() && serviceDTO.getIsPackage().equals(false))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("This sevice id is the package service. Not allow to change the status of is package !"
								+ " \nSet this isPackage to be true please");
			else if (!oldService.isPackage() && serviceDTO.getIsPackage().equals(true))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("This sevice id is the single service. Not allow to change the status of is package !"
								+ " \nSet this isPackage to be false please");

			// update typeNameList for single services
			if (!oldService.isPackage()) {
				if (serviceDTO.getServiceChildList() != null)
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("This sevice id is the single service. Not allow to set service child list !");
				if (serviceDTO.getTypeNameList() == null)
					serviceTypeRepo.deleteAllByServiceId(serviceId);
				// check type name of each single service is unique ignore case after request
				if (serviceDTO.getTypeNameList() != null) {
					Set<String> typeNameList = serviceDTO.getTypeNameList();
					Set<String> uniqueNames = new HashSet<>();
					// check any type name have equal ignore case
					for (String typeName : typeNameList)
						if (!uniqueNames.add(typeName.toLowerCase().trim()))
							return ResponseEntity.status(HttpStatus.BAD_REQUEST)
									.body("Duplicated the type name of this service !");
					// Reset Type Name List and Update
					serviceTypeRepo.deleteAllByServiceId(serviceId);
					for (String element : serviceDTO.getTypeNameList()) {
						ServiceType type = new ServiceType();
						type.setServiceId(serviceId);
						type.setTypeName(element);
						serviceTypeRepo.save(type);
					}
				}
			}

			// check single service id existed in db
			if (oldService.isPackage()) {
				if (serviceDTO.getTypeNameList() != null)
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("The package not allow to set type name list !");
				if (serviceDTO.getServiceChildList() == null || serviceDTO.getServiceChildList().size() < 2)
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("The package contains at least 2 single services !");
				Map<Integer, Integer> childServiceSet = serviceDTO.getServiceChildList();
				for (Integer singleServiceId : childServiceSet.keySet()) {
					if (packageServiceItemRepo.findByPackageServiceIdAndSingleServiceId(serviceId, singleServiceId)
							.isEmpty())
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The single service id "
								+ singleServiceId + " is not allow in here !"
								+ " \nNot allow to change the existing single service item list in this package !");
					if (serviceDTO.getServiceChildList().get(singleServiceId) <= 0)
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("The new quantity of single child service must greater than 0 !");
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

			// TODO: UPDATE IMAGES
			savedService = serviceRepo.save(oldService);

		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Update Failed ! ");
		}

		return this.getOne(savedService.getServiceId());
	}

	// TODO: DELETE SERVICE LATER

}
