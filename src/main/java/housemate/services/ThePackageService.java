package housemate.services;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import housemate.entities.PackageService;
import housemate.entities.PackageService.Summary;
import housemate.entities.PackageServiceItem;
import housemate.repositories.PackageServiceItemRepository;
import housemate.repositories.PackageServiceRepository;
import housemate.services.interfaces.IPackageService;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.SortRequired;

@Service
public class ThePackageService implements IPackageService{

	@Autowired
	PackageServiceRepository pakcageServiceRepo;
	
	@Autowired
	PackageServiceItemRepository packageServiceIemRepo;
	
	@Autowired
	TheService serviceDao; 
	
	
	private static final Logger logger = LoggerFactory.getLogger(TheService.class);
	
	@Override
	public List<PackageService.Summary> getAll() {
		
		return pakcageServiceRepo.findAllBy();
	}


	@Override
	public List<PackageService.Summary> searchByName(String keyword) {
		
		return pakcageServiceRepo.findByTitleNameContaining(keyword);
	}

	@Override
	public List<PackageService.Summary> sortByOneField(ServiceField fieldName, SortRequired orderRequire) {
		
		List<PackageService.Summary> packageServices;
		try {
			Sort nameSort = Sort.by(fieldName.getFieldName().trim());
			if(orderRequire.name().equalsIgnoreCase("asc")) packageServices = pakcageServiceRepo.findAllBy(nameSort.ascending());
			else packageServices = pakcageServiceRepo.findAllBy(nameSort.descending());
		}catch (Exception e) {
			packageServices = null;
			e.printStackTrace();
		}			
			return packageServices;
	}

	
	@Override
	public List<PackageService.Summary> filterBySaleStatus(SaleStatus saleStatus) {
		return pakcageServiceRepo.findBySaleStatus(saleStatus);
	}

	@Override
	public List<PackageService.Summary> filterByRating(int ratingRequired) {
		return pakcageServiceRepo.findByAvgRatingGreaterThanEqual(ratingRequired);
		 
	}

	
	@Override
	public PackageService getOne(int packageServiceId) {
		
		return pakcageServiceRepo.findById(packageServiceId).orElse(null);
	}

	@Override
	public PackageService createNew(PackageService packageService) {

		try {
			// save package info first then save the package service item
			packageService.setOriginalPrice(0);
			packageService = pakcageServiceRepo.save(packageService);

			// start to save package service items = child services
			List<PackageServiceItem> itemList = packageService.getPackageServiceItemList();

			final int packageId = packageService.getPackageServiceId();
			
			//check if package info saved successfully by getting the packageId
			if (packageId > 0)
				itemList.forEach(s -> s.setPackageId(packageId));

			packageServiceIemRepo.saveAll(itemList);
			
			//save all done then update the price
			//pakcageServiceRepo.updateTheOriginalPrice(packageService);

		} catch (Exception e) {
			logger.warn("Saved failed");
		}
		return pakcageServiceRepo.findById(packageService.getPackageServiceId()).orElse(null);
	}

	@Override
	public PackageService updateInfo(int packageServiceId, PackageService packageServiceInfo) {
		PackageService packageService = pakcageServiceRepo.findById(packageServiceId).orElse(null);
		try {

			if (packageService == null) {
				logger.warn("The Service Not Exists To Update !");
				throw new Exception("The service with the ID: " + packageServiceId + "Not Exist !");
			}
			packageService.setTitleName(packageService.getTitleName().trim());
			packageService.setDescription(packageService.getTitleName().trim());
			packageService.setSalePrice(packageService.getSalePrice());
			packageService.setSaleStatus(packageService.getSaleStatus());
			packageService.setCreatorId(packageService.getCreatorId());
			packageService.setCreatedAt(packageService.getCreatedAt());
			
			packageService = pakcageServiceRepo.save(packageService);
			
			final int packageId = packageService.getPackageServiceId();
			
			List<PackageServiceItem> itemList = packageService.getPackageServiceItemList();
			
			PackageServiceItem updateItem;
			
			for (PackageServiceItem item : itemList) {
				updateItem = packageServiceIemRepo.findByPackageIdAndServiceId(packageId, item.getServiceId());
				updateItem.setUsageLimit(item.getUsageLimit());
				updateItem.setUsageDurationValue(item.getUsageDurationValue());
				updateItem.setUsageDurationUnit(item.getUsageDurationUnit());
			}	
			
			//pakcageServiceRepo.updateTheOriginalPrice(packageService);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return packageService;
	}

	@Override
	public PackageService updateSaleStatus(int packageServiceId, SaleStatus saleStatus) {
		PackageService packageService = pakcageServiceRepo.findById(packageServiceId).orElse(null);
		if (packageService != null) {
			packageService.setSaleStatus(saleStatus);
			packageService = pakcageServiceRepo.save(packageService);
			return packageService;
		}
		return packageService;
	}

	@Override
	public PackageService removeOne(int packageServiceId) {
		PackageService delPackageService = pakcageServiceRepo.findById(packageServiceId).orElse(null);
		try {
			if (delPackageService == null) 
				throw new IllegalArgumentException("The service with the ID: " + packageServiceId + "Do Not Exist !");
			pakcageServiceRepo.deleteById(packageServiceId);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			logger.warn("The Service Not Exist To Remove !");
			return null;
		}
		return delPackageService;
	}


	


}
