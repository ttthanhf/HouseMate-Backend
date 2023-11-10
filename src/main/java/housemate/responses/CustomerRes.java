package housemate.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerRes {
    private int id;
    private String customerAvatar;
    private String customerName;
    private int numberOfSchedule;
    private long amountSpent;
    private int numberOfTransactions;
    private LocalDateTime joinDate;
}
