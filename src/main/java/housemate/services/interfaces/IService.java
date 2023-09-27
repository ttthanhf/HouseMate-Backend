/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services.interfaces;

import java.util.List;

import housemate.entities.Service;
import housemate.entities.enums.SaleStatus;

/**
 *
 * @author ThanhF
 */

@org.springframework.stereotype.Service
public interface IService {
    //READ LIST
	public List<Service> getAll();
	public List<Service> searchByName(String keyword);
	public List<Service> sortByOneField(String fieldName, String orderRequire);
	public List<Service> filterBySaleStatus(SaleStatus saleStatus);
	//READ ONE
	public Service getOne(int serviceId);
	//CREATE
	public Service createNew(Service service);
	//UPDATE
	public Service updateInfo(int serviceId, Service newServiceInfo);
	public Service updateSaleStatus(int serviceId, SaleStatus saleStatus);
	//DELETE
	public void removeOne(int serviceId );
	//public void removeMulti(Service service);
	
	
	
}
