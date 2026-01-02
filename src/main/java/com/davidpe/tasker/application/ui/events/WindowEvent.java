package com.davidpe.tasker.application.ui.events;
 import com.davidpe.tasker.application.ui.common.UiScreenId;
import java.time.Instant;

public abstract class WindowEvent {

    private final UiScreenId screenId;
    private final Instant timestamp;

    protected WindowEvent(UiScreenId screenId) {

        this.screenId = screenId;
        this.timestamp = Instant.now();
    }

    public UiScreenId screenId() { 
    
        return screenId; 
    }
    
    public Instant timestamp() { 
    
        return timestamp; 
    }
}