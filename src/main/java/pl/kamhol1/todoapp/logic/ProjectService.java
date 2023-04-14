package pl.kamhol1.todoapp.logic;

import org.springframework.stereotype.Service;
import pl.kamhol1.todoapp.TaskConfigurationProperties;
import pl.kamhol1.todoapp.model.*;
import pl.kamhol1.todoapp.model.projection.GroupReadModel;
import pl.kamhol1.todoapp.model.projection.GroupTaskWriteModel;
import pl.kamhol1.todoapp.model.projection.GroupWriteModel;
import pl.kamhol1.todoapp.model.projection.ProjectWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;
    private TaskGroupService taskGroupService;

    public ProjectService(ProjectRepository repository, TaskGroupRepository taskGroupRepository, TaskGroupService taskGroupService, TaskConfigurationProperties config) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskGroupService = taskGroupService;
        this.config = config;
    }

    public List<Project> findAll() {
        return repository.findAll();
    }

    public Project save(ProjectWriteModel toSave) {
        return repository.save(toSave.toProject());
    }

    public GroupReadModel createGroup(LocalDateTime deadline, Integer projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId))
            throw new IllegalStateException("Only one undone group from project is allowed");

        return repository.findById(projectId)
                .map(project -> {
                    GroupWriteModel targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(project.getSteps().stream()
                            .map(step -> {
                                    GroupTaskWriteModel task = new GroupTaskWriteModel();
                                    task.setDescription(step.getDescription());
                                    task.setDeadline(deadline.plusDays(step.getDaysToDeadline()));
                                    return task;
                                }
                            ).collect(Collectors.toList()));
                    return taskGroupService.createModel(targetGroup, project);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given ID not found"));
    }
}
