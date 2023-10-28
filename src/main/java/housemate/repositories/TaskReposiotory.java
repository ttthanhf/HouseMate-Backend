package housemate.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import housemate.constants.Enum.TaskStatus;
import housemate.entities.Task;

public interface TaskReposiotory extends JpaRepository<Task, Integer> {
	
	Task findByServiceScheduleId(int scheduleId);
	
	Page<Task> findAllByTaskStatus(TaskStatus taskStatus, Pageable pagableTaskList);

}
