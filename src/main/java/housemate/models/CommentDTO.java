/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ThanhF
 */
public class CommentDTO {

    @Getter
    @Setter
    public static class Add {

        @Positive
        private int serviceId;

        @Positive
        private int userId;

        @Size(min = 1, max = 50000)
        private String text;

        @PastOrPresent
        private LocalDateTime date;

    }

    @Getter
    @Setter
    public static class Remove {

        @Positive
        private int id;

        @Positive
        private int serviceId;

        @Positive
        private int userId;
    }
}
