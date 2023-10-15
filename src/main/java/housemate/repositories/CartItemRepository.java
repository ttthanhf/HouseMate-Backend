/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.CartItem;
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
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @Query("SELECT c FROM CartItem c WHERE c.userId = :userId")
    List<CartItem> getAllCartItemByUserId(@Param("userId") int userId);

    @Query("SELECT c FROM CartItem c WHERE c.cartId = :cartId")
    CartItem getCartById(@Param("cartId") int cartId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId AND c.id = :cartId")
    int deleteCart(@Param("userId") int userId, @Param("cartId") int cartId);

    @Modifying
    @Transactional
    @Query("UPDATE CartItem c SET c.quantity = :quantity, c.price = :price, c.periodId = :periodId WHERE c.userId = :userId AND c.serviceId = :serviceId")
    void updateCart(@Param("userId") int userId, @Param("serviceId") int serviceId, @Param("quantity") int quantity, @Param("price") int price, @Param("periodId") int periodId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId")
    int deleteAllCartByUserId(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId AND c.serviceId = :serviceId")
    int deleteCartByUserIdAndServiceId(@Param("userId") int userId, @Param("serviceId") int serviceId);

    @Query("SELECT c FROM CartItem c WHERE c.userId = :userId AND c.serviceId = :serviceId")
    CartItem getCartByUserIdAndServiceId(@Param("userId") int userId, @Param("serviceId") int serviceId);

    @Modifying
    @Transactional
    @Query("UPDATE CartItem c SET c.quantity = :quanlity, c.price = :price, c.periodId = :periodId WHERE c.userId = :userId AND c.serviceId = :serviceId")
    void updateCartQuantity(@Param("userId") int userId, @Param("serviceId") int serviceId, @Param("quanlity") int quanlity, @Param("price") int price, @Param("periodId") int periodId);

    @Query("SELECT c.cartId FROM CartItem c WHERE c.periodId = :periodId")
    List<Integer> getAllCartIdByPeriodId(@Param("periodId") int periodId);

    @Modifying
    @Transactional
    @Query("UPDATE CartItem c SET c.price = :price WHERE c.cartId = :cartId")
    void updateCartPriceByCartId(@Param("cartId") int cartId, @Param("price") int price);

    @Query("SELECT sum(c.quantity) FROM CartItem c WHERE c.userId = :userId")
    int getTotalQuantityCart(@Param("userId") int userId);
}
