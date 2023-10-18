/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models.responses;

import housemate.entities.Service;
import housemate.entities.UserUsage;
import java.util.List;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class UserUsageResponse {

    private Service service;

    private List<UserUsage> listUserUsage;

}
