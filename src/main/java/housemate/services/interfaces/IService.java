/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services.interfaces;

import java.util.List;
import org.springframework.data.domain.Sort;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.ServiceCategory;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SortRequired;
import housemate.entities.Service;
import housemate.models.ServiceNewDTO;
import housemate.models.ServiceViewDTO;

/**
 *
 * @author Anh
 */

@org.springframework.stereotype.Service
public interface IService {
    //READ LIST
	public List<Service> getAllAvailable(); 
	//public List<ServiceAvailableView> FitlerAndSortForAvailable(SaleStatus saleStatus, int ratingUpperFrom, ServiceField fieldname, SortRequired requireOrder);
	public List<Service> fitlerAndSortForAllKind(
			ServiceCategory category,
			SaleStatus saleStatus,
			int ratingUpperFrom,
			ServiceField fieldname,
			SortRequired requireOrder
			);
	public List<Service> searchForAllKind(
			String keyword,
			ServiceCategory category,
			SaleStatus saleStatus,
			int ratingUpperFrom,
			ServiceField fieldname,
			SortRequired requireOrder
			);
	
	//READ ONE
	public ServiceViewDTO getOne(int serviceId);

	//CREATE
	public ServiceViewDTO createNew(ServiceNewDTO service);
//	//----------//
//	//UPDATE
//	public Service updateInfo(int serviceId, Service newServiceInfo);
//	public Service updateSaleStatus(int serviceId, SaleStatus saleStatus);
//	//DELETE
//	public Service removeOne(int serviceId );
//	//public void removeMulti(Service service);
//	
//	
	
	
}
