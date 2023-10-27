package housemate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import housemate.entities.Task;

public interface taskRepository extends JpaRepository<Task, Integer> {

}
