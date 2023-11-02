package housemate.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import housemate.constants.Enum.TaskStatus;
import housemate.entities.Task;

public interface TaskReposiotory extends JpaRepository<Task, Integer> {
	
	List<Task> findByScheduleId(int scheduleId);
	
	@Query("SELECT t FROM Task t WHERE t.scheduleId = ?1 AND t.taskStatus NOT LIKE '%CANCELLED%'")
	Task findExistingTaskForSchedule(int scheduleId); 
	
	Page<Task> findAllByTaskStatus(TaskStatus taskStatus, Pageable pagableTaskList);
	
	@Query("SELECT t FROM Task t WHERE t.staffId = ?1 "
	     + "AND (?2 IS NULL OR t.taskStatus = ?2) ")
	Page<Task> findAllByTaskStatusAndStaffId(int staffId , TaskStatus taskStatus, Pageable pagableTaskList);

	
}
