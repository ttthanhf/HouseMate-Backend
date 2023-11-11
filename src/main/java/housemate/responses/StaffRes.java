package housemate.responses;

import housemate.constants.AccountStatus;
import lombok.Data;

@Data
public class StaffRes {
    private int id;
    private String staffAvatar;
    private String staffName;
    private int point;
    private AccountStatus status;
    private int numberOfJobs;
    private int successRate;
}
