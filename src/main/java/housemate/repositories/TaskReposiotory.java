package housemate.repositories;

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
	
	@Query("SELECT t FROM Task t WHERE t.parentScheduleId = ?1 AND t.taskStatus NOT LIKE '%CANCELLED%'")
	List<Task> findAllExistingRepetitiveTasksOfPapaSchedule(int papaScheduleId);
	
	Page<Task> findAllByTaskStatus(TaskStatus taskStatus, Pageable pagableTaskList);
	
	@Query("SELECT t FROM Task t WHERE t.staffId = ?1 "
	     + "AND (?2 IS NULL OR t.taskStatus = ?2) "
		 + "AND (?2 IS NULL OR t.taskStatus = ?2) "
	     )
	Page<Task> findAllByTaskStatusAndStaffId(int staffId , TaskStatus taskStatus, Pageable pagableTaskList);
	
	@Modifying
	@Query("UPDATE Task t SET t.taskStatus = 'INCOMING', t.schedule.status = 'INCOMING' "
			+ "WHERE t.staffId IS NOT NULL "
			+ "AND t.schedule.status IN (PENDING)"
			+ "AND (FUNCTION('TIMESTAMPDIFF', HOUR, CURRENT_TIMESTAMP(), t.schedule.startDate) BETWEEN 0 AND 6) "
			+ "AND (FUNCTION('MOD', FUNCTION('TIMESTAMPDIFF', MINUTE, CURRENT_TIMESTAMP(), t.schedule.startDate), 60) BETWEEN 0 AND 60) ")
	void updateTaskStatusIntoIncoming();

}
