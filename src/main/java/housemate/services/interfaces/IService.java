/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services.interfaces;

import java.util.List;
import java.util.Optional;

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

	public List<Service> searchFilterAllKind(
//			Optional<String> keyword,
			String keyword,
			Optional<ServiceCategory> category,
			Optional<SaleStatus> saleStatus,
			Optional<Integer> rating,
			Optional<ServiceField> sortBy,
			Optional<SortRequired> orderBy
			);
	
	//READ ONE
	public ServiceViewDTO getOne(int serviceId);

	//CREATE
	public ServiceViewDTO createNew(ServiceNewDTO service);
//	//----------//
	//UPDATE
	public ServiceViewDTO updateInfo(int serviceId, ServiceNewDTO newServiceInfo) throws Exception ;

	
	
}
