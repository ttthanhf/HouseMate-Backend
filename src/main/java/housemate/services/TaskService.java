package housemate.services;

import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import housemate.entities.Schedule;
import housemate.repositories.ScheduleRepository;

public class TaskService {

	@Autowired
	ScheduleRepository scheduleRepo;
	private final ZoneId dateTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
 
	//create task
	//update task
	//remove task
	//getAllTaskByStaff
	//getAllTaskBySchedule
	//taskEmployeeSelection
	
	public void createTaskAutomatic() {
		//automatic scanning schedule at 12:00PM
		//get all schedule and filter which schedule will coming up in next 7 day
		//open task for these schedule
		
	}
	
	//scanning schedule function
	public List<Schedule> upComingScheduleInNext7Days(){
		List<Schedule> schedules = scheduleRepo.findAllScheduleUpComing(7);
		return schedules;
	}
	

	
	
}
