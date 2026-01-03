package com.davidpe.tasker.application.ui.events;
 
import com.davidpe.tasker.application.ui.common.UiScreenId;

public final class WindowOpenedEvent extends WindowEvent {
    
    public WindowOpenedEvent(UiScreenId screenId) { 

        super(screenId); 
    }
}