/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import housemate.entities.CartItem;
import java.util.List;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class CartDTO {

    private int userId;

    private int subTotal;

    private int discountPrice;

    private int finalTotalPrice;

    private List<CartItem> listCartItem;
}
