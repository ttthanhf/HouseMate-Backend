package housemate.constants;

public enum ScheduleStatus {
    DONE, //staff finish task
    PROCESSING,//finding staff
    INCOMING, //found staff - waiting for staff coming in around 4 5 6 12 hours
    CANCEL,// customer cancel staff
    PENDING, //found staff - waiting for staff coming
    PROCESSING_ONTASK, //has posted task for finding staff for this schedule
    DOING //staff arrived and doing
}
