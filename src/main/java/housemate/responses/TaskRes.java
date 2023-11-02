package housemate.responses;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class TaskRes<T> {

	public static enum TaskMessType {
		OK,
		REJECT_UPDATE_TASK,
		REJECT_CANCELLED,
		REJECT_APPROVE_STAFF,
		REJECT_REPORT_TASK
	}

	private T object;
	private TaskMessType messType;
	private String message;
	private Integer userIdResponseTo;

	TaskRes(@Nullable T object, TaskMessType messType, String message) {
		this.object = object;
		this.messType = messType;
		this.message = message;
	}

	public static <T> TaskRes<T> build(@Nullable T object, @Nullable TaskMessType messType, String message) {
		return new TaskRes(object, messType, message);
	}

}
