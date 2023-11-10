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
public class AnalyticServicePageResponse {

    private String serviceName;
    private double totalPrice;
    private int numberOfSold;
    private int totalSessionView;

}
