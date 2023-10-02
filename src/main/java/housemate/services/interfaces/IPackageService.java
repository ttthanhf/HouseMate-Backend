package housemate.services.interfaces;

import java.util.List;

import org.springframework.stereotype.Service;

import housemate.constants.Enum.PackageServiceField;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.SortRequired;
import housemate.entities.PackageService;

@Service
public interface IPackageService {
    //READ LIST
	public List<PackageService> getAll();
	public List<PackageService.Summary> getAllSummary();
	public List<PackageService> searchByName(String keyword);
	public List<PackageService> sortByOneField(PackageServiceField fieldName, SortRequired orderRequired);
	public List<PackageService> filterBySaleStatus(SaleStatus saleStatus);
	public List<PackageService> filterByRating(int ratingRequired);
	//READ ONE
	public PackageService getOne(int packageServiceId);
	//CREATE
	public PackageService createNew(PackageService packageService);
	//UPDATE
	public PackageService updateInfo(int packageServiceId, PackageService packageServiceInfo);
	public PackageService updateSaleStatus(int packageServiceId, SaleStatus saleStatus);
	//DELETE
	public PackageService removeOne(int packageServiceId );
}
