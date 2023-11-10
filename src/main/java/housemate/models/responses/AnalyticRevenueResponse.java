/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models.responses;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class AnalyticRevenueResponse {

    private List<AllOrderPrice> current;
    private List<AllOrderPrice> before;

    @Data
    public class AllOrderPrice {

        private LocalDate date;
        private double allOrderPrice;
        private double percentAllOrderPrice;
    }
}
