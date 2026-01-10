package com.davidpe.tasker.application.ui.events;
 
import com.davidpe.tasker.application.ui.common.UiScreenId;

public final class WindowNewTaskOpenedEvent extends WindowEvent {
     
 
    public WindowNewTaskOpenedEvent() {
        
        super(UiScreenId.NEW_TASK_DIALOG);         
    }
    
}