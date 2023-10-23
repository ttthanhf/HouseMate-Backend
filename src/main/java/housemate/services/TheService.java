package housemate.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import housemate.constants.Role;
import housemate.constants.Enum.ImageType;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.ServiceCategory;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SortRequired;
import housemate.constants.Enum.UnitOfMeasure;
import housemate.constants.Enum.TimeUnit;
import housemate.entities.Image;
import housemate.entities.PackageServiceItem;
import housemate.entities.Period;
import housemate.entities.PeriodPriceConfig;
import housemate.entities.Service;
import housemate.entities.ServiceType;
import housemate.models.ServiceNewDTO;
import housemate.models.ServiceViewDTO;
import housemate.repositories.CommentRepository;
import housemate.repositories.FeedbackRepository;
import housemate.repositories.ImageRepository;
import housemate.repositories.PackageServiceItemRepository;
import housemate.repositories.PeriodPriceConfigRepository;
import housemate.repositories.PeriodRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.ServiceTypeRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import housemate.models.ServiceViewDTO.ServicePrice;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Pattern;

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
	ImageRepository imgRepo;
	
	@Autowired
	PeriodPriceConfigRepository perPriceConfRepo;
	
	@Autowired
    AuthorizationUtil authorizationUtil;

	ModelMapper mapper = new ModelMapper();
	
	private final ZoneId dateTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");

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
	
	public ResponseEntity<?> searchFilterAllKind (
			String keyword,
			Optional<ServiceCategory> category,
			Optional<SaleStatus> saleStatus,
			Optional<Integer> rating,
			Optional<ServiceField> sortBy,
			Optional<SortRequired> orderBy,
			Optional<Integer> page,
			Optional<Integer> size) {

		String keywordValue = keyword == null ? null : removeDiacriticalMarks(keyword.trim().replaceAll("\\s+", " "));
		Boolean categoryValue = category.isEmpty() ? null : (category.get().equals(ServiceCategory.PACKAGE) == true ? true : false);
		SaleStatus statusValue = saleStatus.orElse(null);
		int ratingValue = rating.orElse(0);
		ServiceField fieldname = sortBy.orElse(ServiceField.PRICE);
		SortRequired requireOrder = orderBy.orElse(SortRequired.ASC);
		int pageNo = page.orElse(0);
		int pageSize = size.orElse(9);

		if (pageNo < 0 || pageSize < 1)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Page number starts with 1. Page size must not be less than 1");
		
		// setting sort
		Sort sort;
		sort = Sort.by(Sort.Direction.ASC, fieldname.getFieldName());
		if (requireOrder.equals(SortRequired.DESC))
			sort = Sort.by(Sort.Direction.DESC, fieldname.getFieldName());

		Pageable sortedPage = pageNo == 0 ? PageRequest.of(0, pageSize, sort)
				                          : PageRequest.of(pageNo - 1, pageSize, sort);

		Page<Service> serviceList = serviceRepo.searchFilterAllKind(statusValue, keywordValue, ratingValue, categoryValue, sortedPage);
		int maxPages = (int) Math.ceil((double) serviceList.getTotalPages());
		
		if (serviceList.isEmpty() || serviceList == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found !");
		
		serviceList.forEach(s -> s.setMainImg(imgRepo.findFirtsByEntityIdAndImageType(s.getServiceId(), ImageType.SERVICE)));

		return ResponseEntity.ok(serviceList);
	}

	public ResponseEntity<?> searchFilterAllKindAvailable (
			String keyword,
			Optional<ServiceCategory> category,
			Optional<SaleStatus> saleStatus,
			Optional<Integer> rating,
			Optional<ServiceField> sortBy,
			Optional<SortRequired> orderBy,
			Optional<Integer> page,
			Optional<Integer> size) {

		String keywordValue = keyword == null ? null : removeDiacriticalMarks(keyword.trim().replaceAll("\\s+", " "));
		Boolean categoryValue = category.isEmpty() ? null : (category.get().equals(ServiceCategory.PACKAGE) == true ? true : false);
		SaleStatus statusValue = saleStatus.orElse(null);
		int ratingValue = rating.orElse(0);
		ServiceField fieldname = sortBy.orElse(ServiceField.PRICE);
		SortRequired requireOrder = orderBy.orElse(SortRequired.ASC);
		int pageNo = page.orElse(0);
		int pageSize = size.orElse(9);

		if (pageNo < 0 || pageSize < 1)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Page number starts with 1. Page size must not be less than 1");
		
		// setting sort
		Sort sort;
		sort = Sort.by(Sort.Direction.ASC, fieldname.getFieldName());
		if (requireOrder.equals(SortRequired.DESC))
			sort = Sort.by(Sort.Direction.DESC, fieldname.getFieldName());

		Pageable sortedPage = pageNo == 0 ? PageRequest.of(0, pageSize, sort)
				                          : PageRequest.of(pageNo - 1, pageSize, sort);

		Page<Service> serviceList = serviceRepo.searchFilterAllAvailable(statusValue, keywordValue, ratingValue, categoryValue, sortedPage);
		int maxPages = (int) Math.ceil((double) serviceList.getTotalPages());
		
		if (serviceList.isEmpty() || serviceList == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found !");
		
		serviceList.forEach(s -> s.setMainImg(imgRepo.findFirtsByEntityIdAndImageType(s.getServiceId(), ImageType.SERVICE)));

		return ResponseEntity.ok(serviceList);
	}

	public ResponseEntity<?> getOne(int serviceId) {

		ServiceViewDTO serviceDtoForDetail = new ServiceViewDTO();

		Service service = serviceRepo.findById(serviceId).orElse(null);

		if (service == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found this service !");

		service.setNumberOfReview(feedbackRepo.findAllByServiceId(serviceId).size());
		service.setNumberOfComment(commentRepo.getAllCommentAndReplyByServiceId(serviceId));
		serviceDtoForDetail.setService(service);

		if (!service.isPackage()) { // this is a service
			List<ServiceType> typeList = serviceTypeRepo.findAllByServiceId(service.getServiceId()).orElse(null);
			if (typeList != null)
				serviceDtoForDetail.setTypeList(typeList);
		} else if (service.isPackage()) { // this is a package
			List<PackageServiceItem> packageServiceChildList = packageServiceItemRepo
					.findAllByPackageServiceId(service.getServiceId()).orElse(null);
			if (packageServiceChildList != null) {
				for (PackageServiceItem packageServiceItem : packageServiceChildList) {
					Service serviceChild = serviceRepo.findByServiceId(packageServiceItem.getSingleServiceId()).orElse(null);
					packageServiceItem.setDescription(serviceChild.getTitleName() + " : " + serviceChild.getDescription());
				}
				serviceDtoForDetail.setPackageServiceItemList(packageServiceChildList);
			}
		}

		// set combo price for each service
		List<ServicePrice> priceList = new ArrayList<>();
		ServicePrice servicePrice = new ServicePrice();
		List<Period> periodServiceList = periodRepo.findAllByServiceId(serviceId);
		periodServiceList.forEach(s -> priceList.add(mapper.map(s, ServicePrice.class)));
		serviceDtoForDetail.setPriceList(priceList);

		List<Image> imgList = imgRepo.findAllByEntityIdAndImageType(serviceId, ImageType.SERVICE);
		serviceDtoForDetail.setImages(imgList);

		return ResponseEntity.ok().body(serviceDtoForDetail);
	}

	@Transactional
	public ResponseEntity<?> createNew(HttpServletRequest request, ServiceNewDTO serviceDTO) {

		// check the role admin is allowed
		if (!authorizationUtil.getRoleFromAuthorizationHeader(request).equals(Role.ADMIN.toString()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");

		Service savedService = null;

		try {
			// Check all before saving object service
			// Check duplicate title name
			String formatedTitleName = serviceDTO.getTitleName().trim().replaceAll("\\s+", " ");
			serviceDTO.setTitleName(formatedTitleName);
			if (serviceRepo.findByTitleNameIgnoreCase(removeDiacriticalMarks(formatedTitleName)) != null)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The title name has existed before !");

			//check valid between original price and final price of service 
			if (serviceDTO.getFinalPrice() > serviceDTO.getOriginalPrice())
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Set the Final Price from 0 to upper, smaller than or equal Original Price ");

			// Set auto sale status
			if (serviceDTO.getSaleStatus().equals(SaleStatus.DISCONTINUED))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Create new service the sale status must be Onsale or Available");
			else if ((serviceDTO.getOriginalPrice() - serviceDTO.getFinalPrice()) > 0)
				serviceDTO.setSaleStatus(SaleStatus.ONSALE);
			else
				serviceDTO.setSaleStatus(SaleStatus.AVAILABLE);

			// check single service constraints
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
						if (!uniqueNames.add(removeDiacriticalMarks(typeName.toLowerCase().trim().replaceAll("\\s+", " "))))
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
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
								"Single service child id " + singleServiceId + " does not existing in provided list !");
					if (serviceDTO.getServiceChildList().get(singleServiceId) <= 0)
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("The quantity of single child service must greater than 0 !");
				}
			}

			// check service price cycle list constraints
			List<Integer> cycleList = List.of(3, 6, 9, 12);
			Map<Integer, Integer> cylcePriceListOfNewServ = serviceDTO.getPeriodPriceServiceList();
			if (cylcePriceListOfNewServ.size() != 4 || !cycleList.containsAll(cylcePriceListOfNewServ.keySet()))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Have to set price foreach 4 cycles : 3, 6, 9 ,12 of this service");
			Function<Integer, PeriodPriceConfig> findByConfigValueAndConfigName = key -> perPriceConfRepo
					.findByConfigValueAndConfigName(LocalDateTime.now(dateTimeZone), key, TimeUnit.MONTH);
			for (Entry<Integer, Integer> period : cylcePriceListOfNewServ.entrySet()) {
				PeriodPriceConfig periPriceConfig = findByConfigValueAndConfigName.apply(period.getKey());
				if (periPriceConfig != null) {
					float min = periPriceConfig.getMin();
					float max = periPriceConfig.getMax();
					float periodPrice = period.getValue();
					float propor = periodPrice / serviceDTO.getOriginalPrice();
					boolean validSetting = min <= propor && propor <= max;
					if (!validSetting) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("Period price for cycle " + period.getKey() + " out of range proportion [" + min + "-" + max + "] against the original price " + serviceDTO.getOriginalPrice());
					}
				}
			}

			// ==after check all then map to DTO & save SavedService into DB to get newservice Id==
			savedService = serviceRepo.save(mapper.map(serviceDTO, Service.class));
			
			if (savedService == null)
				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Saved Failed !");

			// save typeNameList for single services after saving Service object success
			if (serviceDTO.getTypeNameList() != null && !serviceDTO.getIsPackage() && savedService != null) {
				int savedServiceId = savedService.getServiceId();
				for (String element : serviceDTO.getTypeNameList()) {
					ServiceType type = new ServiceType();
					type.setServiceId(savedServiceId);
					type.setTypeName(element.trim().replaceAll("\\s+", " "));
					serviceTypeRepo.save(type);
				}
			}

			// save child service for package after saving Service Object success
			if (serviceDTO.getServiceChildList() != null && serviceDTO.getIsPackage() && savedService != null) {
				int savedServiceId = savedService.getServiceId();
				Map<Integer, Integer> childServiceSet = serviceDTO.getServiceChildList();
				int sumSingleServiceOriginalPrice = 0;
				for (Integer singleServiceId : childServiceSet.keySet()) {
					PackageServiceItem item = new PackageServiceItem(); // save package service item
					item.setPackageServiceId(savedServiceId);
					item.setSingleServiceId(singleServiceId);
					item.setQuantity(childServiceSet.get(singleServiceId));
					sumSingleServiceOriginalPrice += (serviceRepo.findByServiceId(singleServiceId).orElse(null)
							                      .getOriginalPrice() * item.getQuantity());
					packageServiceItemRepo.save(item);
				}

				// check original price of package
				if (serviceDTO.getOriginalPrice() != sumSingleServiceOriginalPrice) {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("The original price of package must be the sum of all single service child list !"
							    + "\nThe original price of package should be " + sumSingleServiceOriginalPrice);
				}
				
				//Auto saving sum originalPrice
				savedService.setOriginalPrice(sumSingleServiceOriginalPrice);
				serviceRepo.save(savedService);
			}
			
			// save the cycle price list
			for (Integer cycleVaule : cylcePriceListOfNewServ.keySet()) {
				Period newServicePeriod = Period.builder()
						.serviceId(savedService.getServiceId())
						.periodValue(cycleVaule)
						.periodName(TimeUnit.MONTH.name())
						.finalPrice(cylcePriceListOfNewServ.get(cycleVaule))
						.originalPrice(savedService.getOriginalPrice()*cycleVaule)
						.build();
				periodRepo.save(newServicePeriod);
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
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");

		Service updatedService = null;

		try {
			Service oldService = serviceRepo.findById(serviceId).orElse(null);
			if (oldService == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The service does not exists !");

			// Check Valid Name For Update
			String formatedTitleName = serviceDTO.getTitleName().trim().replaceAll("\\s+", " ");
			serviceDTO.setTitleName(formatedTitleName);
			if (!formatedTitleName.equalsIgnoreCase(oldService.getTitleName()))
				if (serviceRepo.findByTitleNameIgnoreCase(removeDiacriticalMarks(serviceDTO.getTitleName().trim())) != null)
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The title name has existed before !");
			
			//check valid between original price and final price of service 
			if (serviceDTO.getFinalPrice() > serviceDTO.getOriginalPrice())
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Set the Final Price from 0 to upper and smaller than or equal Original Price ");

			// update status //TODO: Check based on figma
			if (serviceDTO.getSaleStatus().equals(SaleStatus.DISCONTINUED))
				oldService.setSaleStatus(SaleStatus.DISCONTINUED);
			else if ((serviceDTO.getOriginalPrice() - serviceDTO.getFinalPrice()) > 0)
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
				if (serviceDTO.getTypeNameList().isEmpty())
					serviceTypeRepo.updateServiceIdForRemove(serviceId);
				// check type name of each single service is unique ignore case after request
				if (!serviceDTO.getTypeNameList().isEmpty()) {
					Set<String> typeNameList = serviceDTO.getTypeNameList();
					Set<String> uniqueNames = new HashSet<>();
					// check any type name have equal ignore case
					for (String typeName : typeNameList)
						if (!uniqueNames.add(removeDiacriticalMarks(typeName.toLowerCase().trim().replaceAll("\\s+", " "))))
							return ResponseEntity.status(HttpStatus.BAD_REQUEST)
									.body("Duplicated the type name of this service !");
					// Reset Type Name List and Update
					serviceTypeRepo.updateServiceIdForRemove(serviceId);
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
			oldService.setFinalPrice(serviceDTO.getFinalPrice());
			oldService.setGroupType(serviceDTO.getGroupType());

			// TODO: UPDATE IMAGES
			updatedService = serviceRepo.save(oldService);
			
			//Update the price cycle list of this service
			// check service price cycle list constraints
			List<Integer> cycleList = List.of(3, 6, 9, 12);
			Map<Integer, Integer> cylcePriceListOfNewServ = serviceDTO.getPeriodPriceServiceList();
			if (cylcePriceListOfNewServ.size() != 4 || !cycleList.containsAll(cylcePriceListOfNewServ.keySet()))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Have to set price foreach 4 cycles : 3, 6, 9 ,12 of this service");
			Function<Integer, PeriodPriceConfig> findByConfigValueAndConfigName = key -> perPriceConfRepo
					.findByConfigValueAndConfigName(LocalDateTime.now(dateTimeZone), key, TimeUnit.MONTH);
			for (Entry<Integer, Integer> period : cylcePriceListOfNewServ.entrySet()) {
				PeriodPriceConfig periPriceConfig = findByConfigValueAndConfigName.apply(period.getKey());
				if (periPriceConfig != null) {
					float min = periPriceConfig.getMin();
					float max = periPriceConfig.getMax();
					float periodPrice = period.getValue();
					float propor = periodPrice / serviceDTO.getOriginalPrice();
					boolean validSetting = min <= propor && propor <= max;
					if (!validSetting) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("Period price for cycle " + period.getKey() + " out of range proportion [" + min + "-" + max + "] against the original price " + serviceDTO.getOriginalPrice());
					}
				}
			}
			for (Integer cycleVaule : cylcePriceListOfNewServ.keySet()) {
				periodRepo.save(Period.builder()
						.periodId(periodRepo.findByServiceIdAndPeriodValue(oldService.getServiceId(),cycleVaule).getPeriodId())
						.serviceId(oldService.getServiceId())
						.periodValue(cycleVaule)
						.periodName(TimeUnit.MONTH.name())
						.finalPrice(cylcePriceListOfNewServ.get(cycleVaule))
						.originalPrice(updatedService.getOriginalPrice()*cycleVaule)
						.build());
			}

		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Update Failed ! ");
		}

		return this.getOne(updatedService.getServiceId());
	}
	
	  private static String removeDiacriticalMarks(String str) {
	        str = Normalizer.normalize(str, Normalizer.Form.NFD);
	        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	        return pattern.matcher(str).replaceAll("");
	    }

	// TODO: DELETE SERVICE LATER
	  

}
