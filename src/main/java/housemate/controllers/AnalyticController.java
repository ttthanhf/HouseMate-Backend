/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.models.AnalyticPageDTO;
import housemate.services.AnalyticService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import housemate.constants.SortEnum;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/analytics")
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Analytics")
public class AnalyticController {

    @Autowired
    AnalyticService analyticService;

    @GetMapping("/user/days-ago/{daysAgo}")
    public ResponseEntity<?> getAnalyticUser(HttpServletRequest request, @PathVariable int daysAgo) {
        return analyticService.getAnalyticUser(request, daysAgo);
    }

    @PostMapping("/service-page") //method get ko nhận requestbody nên phải xài post
    public ResponseEntity<?> getAnalyticPage(HttpServletRequest request, @RequestBody AnalyticPageDTO analyticPageDTO) {
        return analyticService.getAnalyticServicePage(request, analyticPageDTO);
    }

    @GetMapping("/overview/days-ago/{daysAgo}")
    public ResponseEntity<?> getAnalyticOverview(HttpServletRequest request, @PathVariable int daysAgo) {
        return analyticService.getAnalyticOverview(request, daysAgo);
    }

    @GetMapping("/revenue/days-ago/{daysAgo}")
    public ResponseEntity<?> getAnalyticRevenue(HttpServletRequest request, @PathVariable int daysAgo) {
        return analyticService.getAnalyticRevenue(request, daysAgo);
    }

    @PostMapping("/customer") //method get ko nhận requestbody nên phải xài post
    public ResponseEntity<?> getAnalyticCustomer(HttpServletRequest request, @RequestBody AnalyticPageDTO analyticPageDTO,
            @RequestParam(required = false) String searchCustomerName,
            @RequestParam(required = false) SortEnum sortTotalOrderPrice,
            @RequestParam(required = false) SortEnum sortNumberOfOrder) {
        return analyticService.getAnalyticCustomer(request, analyticPageDTO, searchCustomerName, sortTotalOrderPrice, sortNumberOfOrder);
    }

}
