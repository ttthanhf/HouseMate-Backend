/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models.responses;

import housemate.entities.Service;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class ServiceUsageResponse {

    private int total;
    private int remaining;
    private Service service;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
