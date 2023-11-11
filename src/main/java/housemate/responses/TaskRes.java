package housemate.responses;

import lombok.Data;
import org.springframework.lang.Nullable;
import housemate.constants.Enum.TaskMessType;

@Data
public class TaskRes<T> {



	private T object;
	private TaskMessType messType;
	private String message;

	TaskRes(@Nullable T object, TaskMessType messType, String message) {
		this.object = object;
		this.messType = messType;
		this.message = message;
	}

	public static <T> TaskRes<T> build(@Nullable T object, @Nullable TaskMessType messType, String message) {
		return new TaskRes(object, messType, message);
	}

}
