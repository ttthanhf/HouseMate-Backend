package housemate.models;

import org.springframework.lang.Nullable;

import housemate.constants.Enum.TaskReportType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TaskReportNewDTO {

	@NotNull
	private TaskReportType taskStatus;

	private String note;

	@Positive
	@Nullable
	private Integer qtyOfGroupReturn;

}
