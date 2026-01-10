package com.davidpe.tasker.application.ui.events;
 
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.domain.task.Task;

public final class WindowEditTaskOpenedEvent extends WindowEvent {
     
    private Task task;

    public WindowEditTaskOpenedEvent(Task task) {
        
        super(UiScreenId.NEW_TASK_DIALOG);         
        this.task = task;
    }   

    public Task getTask() {

        return task;
    }

}