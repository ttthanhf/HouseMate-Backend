/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Cart;
import jakarta.transaction.Transactional;
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
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c WHERE c.userId = :userId")
    List<Cart> getCartByUserId(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.userId = :userId AND c.id = :cartId")
    int deleteCart(@Param("userId") int userId, @Param("cartId") int cartId);

    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.serviceId = :serviceId")
    Cart getCartByUserIdAndServiceId(@Param("userId") int userId, @Param("serviceId") int serviceId);

    @Modifying
    @Transactional
    @Query("UPDATE Cart c SET c.quantity = :quanlity WHERE c.userId = :userId AND c.serviceId = :serviceId")
    void updateCartQuantity(@Param("userId") int userId, @Param("serviceId") int serviceId, @Param("quanlity") int quanlity);
}
