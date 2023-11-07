package housemate.repositories;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import housemate.constants.Enum.TaskStatus;
import housemate.entities.Task;

public interface TaskReposiotory extends JpaRepository<Task, Integer> {

	List<Task> findByScheduleId(int scheduleId);

	@Query("SELECT t FROM Task t WHERE t.scheduleId = :scheduleId AND t.taskStatus NOT LIKE '%CANCELLED%'")
	Task findExistingTaskForSchedule(@Param("scheduleId") int scheduleId);
	
	Task findByScheduleIdAndTaskStatus(int scheduleId, TaskStatus taskStatus);

	Page<Task> findAllByTaskStatus(TaskStatus taskStatus, Pageable pagableTaskList);

	@Query("SELECT t FROM Task t WHERE t.staffId = :staffId "
	     + "AND (:taskStatus IS NULL OR t.taskStatus = :taskStatus) ")
	Page<Task> findAllByTaskStatusAndStaffId(@Param("staffId") int staffId, @Param("taskStatus") TaskStatus taskStatus, Pageable pagableTaskList);

	@Query("SELECT t FROM Task t WHERE t.staffId = :staffId "
		+ "AND t.taskStatus LIKE '%CANCELLED%' ")
	Page<Task> findAllCancelledByStaffId(@Param("staffId") int staffId, Pageable pagableTaskList);

}
