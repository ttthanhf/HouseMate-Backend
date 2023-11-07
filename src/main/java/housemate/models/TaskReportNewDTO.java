package housemate.models;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@JsonInclude(value = Include.ALWAYS)
public class TaskReportNewDTO {

    @Nullable
    private String note;
    
    @Positive(message = "Điền số lượng lớn hơn 0")
    @Nullable
    private Integer qtyOfGroupReturn;

}
