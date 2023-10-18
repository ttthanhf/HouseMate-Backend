/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.Service;
import housemate.entities.UserUsage;
import housemate.models.responses.UserUsageResponse;
import housemate.repositories.ServiceRepository;
import housemate.repositories.UserUsageRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ThanhF
 */
@org.springframework.stereotype.Service
public class UserUsageService {

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private UserUsageRepository userUsageRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    public ResponseEntity<List<UserUsageResponse>> getAllUserUsage(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        List<UserUsageResponse> listUserUsageResponse = new ArrayList<>();

        List<UserUsage> listUserUsage = userUsageRepository.getAllUserUsageByUserId(userId);
        for (UserUsage userUsage : listUserUsage) {
            Service service = serviceRepository.getServiceByServiceId(userUsage.getServiceId());

            UserUsageResponse userUsageResponse = new UserUsageResponse();
            userUsageResponse.setService(service);

            List<UserUsage> listUserUsageSet = userUsageRepository.getAllUserUsageByServiceIdAndUserId(userUsage.getServiceId(), userId);
            userUsageResponse.setListUserUsage(listUserUsageSet);

            listUserUsageResponse.add(userUsageResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(listUserUsageResponse);
    }
}
