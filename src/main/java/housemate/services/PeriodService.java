/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.constants.Role;
import housemate.entities.Period;
import housemate.repositories.PeriodRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author ThanhF
 */
@Service
public class PeriodService {

    @Autowired
    PeriodRepository periodRepository;

    @Autowired
    CartService cartService;

    @Autowired
    AuthorizationUtil authorizationUtil;

    public ResponseEntity<List<Period>> getAllPeriod() {
        List<Period> listPeriod = periodRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(listPeriod);
    }

    public ResponseEntity<String> updatePeriodPercentByPeriodId(HttpServletRequest request, int periodId, Float percent) {
        Period period = periodRepository.getPeriodByid(periodId);
        if (period == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PeriodId not found");
        }
        if (!authorizationUtil.getRoleFromAuthorizationHeader(request).equals(Role.ADMIN.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");
        }
        periodRepository.updatePeriodPercentByPeriodId(periodId, percent);
        cartService.updateAllCartPriceWhenPeriodIdChange(periodId, percent);
        return ResponseEntity.status(HttpStatus.OK).body("Updated success");
    }
}
