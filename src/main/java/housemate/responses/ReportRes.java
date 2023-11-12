package housemate.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRes {
    private int serviceId;
    private String serviceName;
    private long quantity;
    private String unitOfMeasure;
}
