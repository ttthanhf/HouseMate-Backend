/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.entities.Cart;
import housemate.models.CartAddDTO;
import housemate.models.CartUpdateDTO;
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class CartMapper {

    public Cart mapToEntity(CartUpdateDTO cartDTO) {
        Cart cart = new Cart();
        cart.setServiceId(cartDTO.getServiceId());
        cart.setUserId(cartDTO.getUserId());
        cart.setQuantity(cartDTO.getQuantity());
        cart.setPeriodId(cartDTO.getPeriodId());
        return cart;
    }

    public Cart mapToEntity(CartAddDTO cartDTO) {
        Cart cart = new Cart();
        cart.setServiceId(cartDTO.getServiceId());
        cart.setUserId(cartDTO.getUserId());
        return cart;
    }
}
