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
public class NotificationDTO {

    public LocalDateTime datetime;
    public String serviceName;
    public String status;
}
