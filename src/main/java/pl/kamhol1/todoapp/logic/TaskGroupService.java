package pl.kamhol1.todoapp.logic;

import org.springframework.stereotype.Service;
import pl.kamhol1.todoapp.model.Project;
import pl.kamhol1.todoapp.model.TaskGroup;
import pl.kamhol1.todoapp.model.TaskGroupRepository;
import pl.kamhol1.todoapp.model.TaskRepository;
import pl.kamhol1.todoapp.model.projection.GroupReadModel;
import pl.kamhol1.todoapp.model.projection.GroupWriteModel;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskGroupService {
    private TaskGroupRepository repository;
    private TaskRepository taskRepository;

    TaskGroupService(TaskGroupRepository repository, TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createModel(GroupWriteModel source) {
        return createModel(source, null);
    }

    GroupReadModel createModel(GroupWriteModel source, Project project) {
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll() {
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(Integer groupId) {
        if (taskRepository.existsByDoneIsFalseAndGroup_Id(groupId))
            throw new IllegalStateException("Group has undone tasks. Close all tasks first.");

        TaskGroup result = repository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Task group with given ID not found"));

        result.setDone(!result.isDone());
        repository.save(result);
    }
}
