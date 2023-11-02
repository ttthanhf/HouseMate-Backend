package housemate.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import housemate.constants.Enum.TaskStatus;
import housemate.entities.TaskReport;

public interface TaskReportRepository extends JpaRepository<TaskReport, Integer> {

	TaskReport findByTaskIdAndTaskStatus(int taskId, TaskStatus taskStatus);
	
	List<TaskReport> findAllByTaskId(int taskId);
	
}
