package pl.kamhol1.todoapp.logic;

import org.springframework.stereotype.Service;
import pl.kamhol1.todoapp.TaskConfigurationProperties;
import pl.kamhol1.todoapp.model.*;
import pl.kamhol1.todoapp.model.projection.GroupReadModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;

    public ProjectService(ProjectRepository repository, TaskGroupRepository taskGroupRepository, TaskConfigurationProperties config) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
    }

    private List<Project> findAll() {
        return repository.findAll();
    }

    public Project save(Project toSave) {
        return repository.save(toSave);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, Integer projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId))
            throw new IllegalStateException("Only one undone group from project is allowed");

        TaskGroup result = repository.findById(projectId)
                .map(project -> new TaskGroup(project.getDescription(), project.getSteps().stream()
                        .map(step -> new Task(step.getDescription(), deadline.plusDays(step.getDaysToDeadline())))
                        .collect(Collectors.toSet()))).orElseThrow(() -> new IllegalArgumentException("Project with given ID not found"));

        return new GroupReadModel(result);
    }
}
