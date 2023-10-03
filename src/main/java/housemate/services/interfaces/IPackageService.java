package housemate.services.interfaces;

import java.util.List;
import org.springframework.stereotype.Service;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.SortRequired;
import housemate.entities.PackageService;
import housemate.entities.PackageService.Summary;

/**
*
* @author Anh
*/

@Service
public interface IPackageService {
    //READ LIST
	public List<PackageService.Summary> getAll();
	public List<PackageService.Summary> searchByName(String keyword);
	public List<PackageService.Summary> sortByOneField(ServiceField fieldName, SortRequired orderRequired);
	public List<PackageService.Summary> filterBySaleStatus(SaleStatus saleStatus);
	public List<PackageService.Summary> filterByRating(int ratingRequired);
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
