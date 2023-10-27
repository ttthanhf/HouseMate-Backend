/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
