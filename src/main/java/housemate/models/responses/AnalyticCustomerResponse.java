/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models.responses;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class AnalyticCustomerResponse {

    private String userName;
    private int numberOfOrder;
    private double totalOrderPrice;
    private int numberOfSchedule;
    private LocalDate date;
}
