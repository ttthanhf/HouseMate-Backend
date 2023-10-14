/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import housemate.constants.Cycle;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Date;

/**
 *
 * @author ThanhF
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CleanlinessScheduleDTO {
    @Positive
    private int serviceId;

    @FutureOrPresent
    private Date cleanDate;

    private Cycle cycle;

    @NotNull
    @NotEmpty
    private String note;
}
