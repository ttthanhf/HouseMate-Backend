/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.OrderItem;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
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
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem o WHERE o.orderId = :orderId")
    void removeAllOrderItemByOrderId(@Param("orderId") int orderId);

    @Query("SELECT o FROM OrderItem o WHERE o.orderId = :orderId")
    List<OrderItem> getAllOrderItemByOrderId(@Param("orderId") int orderId);

    OrderItem findById(int orderItem);

    @Query("SELECT COALESCE(SUM(o.finalPrice), 0) FROM OrderItem o WHERE o.serviceId = :serviceId AND o.createDate BETWEEN :startDate AND :endDate")
    double sumAllPriceOfServiceByServiceIdAndRangeDate(@Param("serviceId") int serviceId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM OrderItem o WHERE  o.serviceId = :serviceId AND o.createDate BETWEEN :startDate AND :endDate")
    int countAllServiceTransitionByServiceIdAndRangeDate(@Param("serviceId") int serviceId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
