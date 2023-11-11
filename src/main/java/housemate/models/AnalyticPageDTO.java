/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class AnalyticPageDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private int size;
    private int page;
}
