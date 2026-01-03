package com.davidpe.tasker.application.ui.events; 

import com.davidpe.tasker.application.ui.common.UiScreenId;

public final class WindowClosedEvent extends WindowEvent {

    public WindowClosedEvent(UiScreenId screenId) { 
        
        super(screenId); 
    }
}