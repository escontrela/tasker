package com.davidpe.tasker.application.ui.common.newer;

 @FunctionalInterface
public interface ScreenBuilder {
 
    Screen build(ScreenContext ctx);
}