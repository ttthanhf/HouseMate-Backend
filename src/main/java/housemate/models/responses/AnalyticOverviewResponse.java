/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models.responses;

import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class AnalyticOverviewResponse {

    private int currentAllTransition;
    private int beforeAllTransition;
    private double percentAllTransition;
    private double currentAllOrderPrice;
    private double beforeAllOrderPrice;
    private double percentAllOrderPrice;
    private int totalCustomer;
    private int currentAllNewGuest;
    private int beforeAllNewGuest;
    private double percentAllNewGuest;
}
