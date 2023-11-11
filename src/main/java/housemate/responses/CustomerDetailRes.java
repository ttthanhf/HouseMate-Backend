package housemate.responses;

import housemate.entities.Schedule;
import housemate.entities.UserAccount;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDetailRes  {
    private int numberOfOrder;
    private long amountSpent;
    private UserAccount userInfo;
    private List<Schedule> usageHistory;
    private List<ReportRes> monthlyReport;
}
