package housemate.responses;

import housemate.entities.UserAccount;
import lombok.Data;

import java.util.List;

@Data
public class StaffDetailRes {
    private List<ReportRes> achievement;
    private List<ReportRes> monthlyReport;
    private UserAccount userInfo;
}
