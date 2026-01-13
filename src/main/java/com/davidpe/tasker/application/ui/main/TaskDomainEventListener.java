package com.davidpe.tasker.application.ui.main;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.davidpe.tasker.domain.task.TaskCreatedEvent;
import com.davidpe.tasker.domain.task.TaskDeletedEvent;
import com.davidpe.tasker.domain.task.TaskUpdatedEvent;

@Component
public class TaskDomainEventListener {

    @ApplicationModuleListener
    @Transactional
    void on(TaskCreatedEvent event) {
        System.out.println("[TaskCreatedEvent] id=" + event.task().getId()
                + " title=" + event.task().getTitle());
    }

    @ApplicationModuleListener
    @Transactional
    void on(TaskUpdatedEvent event) {
        System.out.println("[TaskUpdatedEvent] id=" + event.task().getId()
                + " title=" + event.task().getTitle());
    }

    @ApplicationModuleListener
    @Transactional
    void on(TaskDeletedEvent event) {
        System.out.println("[TaskDeletedEvent] id=" + event.task().getId()
                + " title=" + event.task().getTitle());
    }
}
