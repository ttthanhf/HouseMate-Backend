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
public class AnalyticUserResponse {

    private List<AnalyticUserDetail> current;
    private List<AnalyticUserDetail> before;

    @Data
    public class AnalyticUserDetail {

        private LocalDate date;
        private int totalActiveUser;
        private int totalNewUser;
        private double percentActiveUser;
        private double percentNewUser;
    }

}
