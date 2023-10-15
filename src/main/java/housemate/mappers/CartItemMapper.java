/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.models.CartItemDTO;
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class CartItemMapper {

    public CartItemDTO mapToEntity(CartItemDTO cartItemDTO) {
        CartItemDTO cartItem = new CartItemDTO();
        cartItem.setServiceId(cartItemDTO.getServiceId());
        cartItem.setUserId(cartItemDTO.getUserId());
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItem.setPeriodId(cartItemDTO.getPeriodId());
        return cartItem;
    }

}
