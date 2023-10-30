package housemate.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import housemate.constants.Enum;
import housemate.constants.Enum.TaskStatus;
import housemate.entities.Task;

public interface TaskReposiotory extends JpaRepository<Task, Integer> {
	
	List<Task> findByScheduleId(int scheduleId);
	
	@Query("SELECT t FROM Task t WHERE t.scheduleId = ?1 AND t.taskStatus NOT LIKE '%CANCELLED%'")
	Task findExistingTaskForSchedule(int scheduleId); 
	
	@Query("SELECT t FROM Task t WHERE t.parentScheduleId = ?1 AND t.taskStatus NOT LIKE '%CANCELLED%'")
	List<Task> findAllExistingRepetitiveTasksOfPapaSchedule(int papaScheduleId);
	
	Page<Task> findAllByTaskStatus(TaskStatus taskStatus, Pageable pagableTaskList);
	
	Page<Task> findAllByTaskStatusAndStaffId(TaskStatus taskStatus, Pageable pagableTaskList);

}
