/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ThanhF
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {

    @Query("SELECT s.originalPrice FROM Service s WHERE s.serviceId = :serviceId")
    int getOriginalPriceByServiceId(@Param("serviceId") int serviceId);

    @Query("SELECT s.salePrice FROM Service s WHERE s.serviceId = :serviceId")
    int getSalePriceByServiceId(@Param("serviceId") int serviceId);

    @Query("SELECT s FROM Service s WHERE s.serviceId = :serviceId")
    Service getServiceByServiceId(@Param("serviceId") int serviceId);

    @Modifying
    @Query("UPDATE Service s SET s.numberOfSold = s.numberOfSold + :quantity WHERE s.serviceId = :serviceId")
    void updateNumberOfSoldByServiceId(@Param("serviceId") int serviceId, int quantity);

}
