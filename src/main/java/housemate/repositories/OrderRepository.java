/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Order;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ThanhF
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.isComplete = false")
    Order getOrderNotCompleteByUserId(int userId);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.isComplete = true")
    List<Order> getAllOrderCompleteByUserId(int userId);

    @Query("SELECT o FROM Order o WHERE o.transactionId = :transactionId AND o.transactionDate = :transactionDate")
    Order getOrderByTransactionIdAndTransactionDate(String transactionId, String transactionDate);

    @Query(value = "SELECT COUNT(o) FROM Order o WHERE o.date BETWEEN :startDate AND :endDate AND o.isComplete = true")
    int countOrderFromDateInputToDateInput(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COALESCE(SUM(o.finalPrice),0) FROM Order o WHERE o.date BETWEEN :startDate AND :endDate AND o.isComplete = true")
    double sumOrderPriceFromDateInputToDateInput(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    int countByUserId(int userId);

    @Query("SELECT COALESCE(SUM(o.finalPrice), 0) FROM Order o WHERE o.userId = :userId")
    long sumFinalPriceByUserId(int userId);
}
