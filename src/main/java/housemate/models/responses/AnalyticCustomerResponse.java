/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models.responses;

import housemate.constants.AccountStatus;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class AnalyticCustomerResponse {

    private int userId;
    private String avatar;
    private String userName;
    private AccountStatus accountStatus;
    private int numberOfOrder;
    private double amountSpent;
    private int numberOfSchedule;
    private LocalDate date;
}
