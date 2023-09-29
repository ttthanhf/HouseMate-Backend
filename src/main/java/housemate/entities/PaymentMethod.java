/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "Payment_Method")
public class PaymentMethod {

    @Id
    @Column(name = "payment_method_id")
    private int id;

    @Column(name = "method_name")
    private String methodName;

    public PaymentMethod(int id, String methodName) {
        this.id = id;
        this.methodName = methodName;
    }

}
