/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services.interfaces;

import java.util.List;
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
	public List<Service> fitlerAndSortAllKind(
			ServiceCategory category,
			SaleStatus saleStatus,
			int ratingUpperFrom,
			ServiceField fieldname,
			SortRequired requireOrder
			);
	public List<Service> searchAllKind(
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
	public ServiceViewDTO createNew(ServiceNewDTO service) throws Exception;
//	//----------//
	//UPDATE
	public ServiceViewDTO updateInfo(int serviceId, ServiceNewDTO newServiceInfo) throws Exception ;

	
	
}
