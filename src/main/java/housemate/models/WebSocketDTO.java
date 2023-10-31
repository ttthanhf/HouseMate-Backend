/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class WebSocketDTO {

    private String userId;
    private String serviceName;
    private LocalDateTime date;
    private String status;
    private LocalDateTime notificationDate;
    private String type;

}
