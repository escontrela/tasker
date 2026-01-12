package com.davidpe.tasker.application.ui.events;
 
import com.davidpe.tasker.application.ui.common.UiScreenId; 

public final class WindowEditTaskOpenedEvent extends WindowEvent {
     
    private Long taskId;

    public WindowEditTaskOpenedEvent(Long taskId) {
        
        super(UiScreenId.NEW_TASK_DIALOG);         
        this.taskId = taskId;
    }   

    public Long getTaskId() {

        return taskId;
    }

}