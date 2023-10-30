package housemate.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import housemate.entities.Schedule;
import housemate.entities.Staff;
import housemate.entities.Task;
import housemate.responses.TaskRes;

public interface ITaskService {

	//======CREATE TASK======
	
	/**<h3>Create task</h3>
	 * <p>
	 * This will create new tasks by scanning the schedules need to be processing in next 7 days.
	 * This will trigger at 23:00 PM Everyday
	 * </p>
	 * @return Task
	 * @author HOANGANH
	 */	
	public List<Task> createTasksOnUpcomingSchedulesAutoByFixedRate();

	/**<h3>Create task</h3>
	 * <p>
	 * This will create tasks for the new schedule. If this schedule has the cycle,
	 * this will consider which repetitive schedules need to be processed in next 7 days from today 
	 * and create the tasks for these repetitive schedules.
	 * </p>
	 * @return Task
	 * @author HOANGANH
	 */	
//	public List<Task> createTaskOnNewScheduleHasCycle(Schedule schedule);
	
	/**<h3>Create task</h3>
	 * <p>
	 * This will create new task for new schedule and consider if the schedule in the next upcoming under 1 day to allow to create.
	 * It also consider if this schedule have the child schedule but also in the next day too
	 * </p>
	 * @return Task
	 * @author HOANGANH
	 */	
	public List<Task> createTaskOnUpComingSchedule(Schedule schedule);
	
	/**<h3>Create task</h3>
	 * <p>
	 * This will create new task for new schedule.
	 * </p>
	 * <h5> Note </h5>
	 * <p> Be careful: this function do not consider if the schedule is in tomorrow or not</p>
	 * @return Task
	 * @author HOANGANH
	 */	
	public Task createTask(Schedule schedule);
	
	//======CANCEL TASK======
	
	/**
	 * <h3>Cancel task</h3>
	 * <p>
	 * This will cancel the task for this schedule by changing the task status into
	 * "CANCELLED_BY_CUSTOMER". It does't threat to the tasks which have this
	 * schedule as parent schedule. Note: The schedule call this function must have
	 * been set schedule status = "CANCELLED" too
	 * </p>
	 * @return Task
	 * @author HOANGANH
	 */
	public Task cancelTaskByCustomer(Schedule schedule, Optional<String> cancelReason);
	
	/**
	 * <h3>Cancel task</h3>
	 * <p>
	 * This will cancel all the tasks which has the parent schedule is this schedule
	 * by changing the task status into "CANCELLED_BY_CUSTOMER".
	 * </p>
	 * @return Task
	 * @author HOANGANH
	 */
//	public List<Task> cancelRepetitiveTaskByCustomer(Schedule papaSchedule);
		
	//======UPDATE TASK======
	
	/**<h3>Update task</h3>
	 * <p>Cancel old task for OLD Schedule - Then - Create new task for NEW Schedule</p>
	 * <p>
	 * When the task for old schedule change time by customer.
	 * This will cancel the task for this old schedule and create a new task for the new schedule with new time.
	 * </p>
	 * <h5> Note </h5>
	 * <p>This function do not consider the old staff of old schedule to apply on this new schedule</p>
	 * @return Task
	 * @author HOANGANH
	 */	
	public TaskRes<?> updateTaskOnScheduleChangeTime(Schedule oldSchedule, Schedule newSchedule);
	
	//======APPROVE STAFF======
	
	/**<h3>Approve staff</h3>
	 * <p>
	 * Return true if the staff meets the standards for the task
	 * </p>
	 * @return Task
	 * @author HOANGANH
	 */
	public boolean approveQualifiedStaff(Staff staff);
	
	//======CLOSE TASK======
	
	/**
	 * <h3>Close task</h3>
	 * <p>
	 * Close task in opening status into closing status by setting the task status
	 * into "PENDING_WORKING" - <b> Meaning : </b> The task found the staff and waiting for
	 * the staff coming to do the mission.
	 * </p>
	 * <h5>Note</h5>
	 * @return Task
	 * @author HOANGANH
	 */
	public void closeTaskWhenFoundStaff(Task task);
	
	/**
	 * <h3>Close task</h3>
	 * <p>
	 * Close task in opening status into closing status by setting the task status 
	 * into "NOT_FOUND_STAFF" - <b> Meaning : </b> The task found the staff and waiting for
	 * the staff coming to do the mission.
	 * </p>
	 * <h5>Note</h5>
	 * @return Task
	 * @author HOANGANH
	 */
	public void closeTaskAutomaticWhenTimesUp(Task task, LocalDateTime timeEnd);
	
	//======VIEW TASK======
}
