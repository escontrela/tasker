package com.davidpe.tasker.application.ui.common;

/**
 * This interface defines methods for setting and getting data of a specific type.
 * Its used in various parts of the application to manage data flow between components.
 * Its the way to set or get the data to the screen controllers.
 */
public interface ControllerDataAware<T>{

    void setData(T data);
    T getData();
}