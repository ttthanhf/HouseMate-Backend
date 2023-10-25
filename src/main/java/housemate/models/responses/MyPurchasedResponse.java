/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models.responses;

import housemate.entities.Service;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class MyPurchasedResponse {

    private int orderItemId;
    private Service service;
    private List<String> singleServiceName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
