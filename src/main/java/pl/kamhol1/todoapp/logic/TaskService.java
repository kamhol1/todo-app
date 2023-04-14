package pl.kamhol1.todoapp.logic;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kamhol1.todoapp.model.Task;
import pl.kamhol1.todoapp.model.TaskRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskService {
    private final TaskRepository repository;

    TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<List<Task>> findAllTasksAsync() {
        return CompletableFuture.supplyAsync(repository::findAll);
    }
}
