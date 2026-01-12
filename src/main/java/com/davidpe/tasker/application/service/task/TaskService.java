package com.davidpe.tasker.application.service.task;

import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.project.ProjectRepository;
import com.davidpe.tasker.domain.task.Priority;
import com.davidpe.tasker.domain.task.PriorityRepository;
import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final ProjectRepository projectRepository;
    private final PriorityRepository priorityRepository;
    private final TagRepository tagRepository;

    public TaskService(ProjectRepository projectRepository,
                       PriorityRepository priorityRepository,
                       TagRepository tagRepository) {
        this.projectRepository = projectRepository;
        this.priorityRepository = priorityRepository;
        this.tagRepository = tagRepository;
    }

    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    public List<Priority> getPriorities() {
        return priorityRepository.findAll();
    }

    public List<Tag> getTagsForProject(Long projectId) {
        return tagRepository.findByProjectId(projectId);
    }
}
