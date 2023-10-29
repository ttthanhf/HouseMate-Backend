/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.entities.UserUsage;
import housemate.models.responses.UserUsageResponse;
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class UserUsageMapper {

    public UserUsageResponse maptoResponse(UserUsage userUsage) {
        UserUsageResponse userUsageResponse = new UserUsageResponse();
        userUsageResponse.setEndDate(userUsage.getEndDate());
        userUsageResponse.setRemaining(userUsage.getRemaining());
        userUsageResponse.setStartDate(userUsage.getStartDate());
        userUsageResponse.setTotal(userUsage.getTotal());
        return userUsageResponse;
    }
}
