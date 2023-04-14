package pl.kamhol1.todoapp.model.projection;

import jakarta.validation.constraints.NotBlank;
import pl.kamhol1.todoapp.model.Task;
import pl.kamhol1.todoapp.model.TaskGroup;

import java.time.LocalDateTime;

public class GroupTaskWriteModel {
    @NotBlank(message = "Task group description cannot be empty")
    private String description;
    private LocalDateTime deadline;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Task toTask(TaskGroup group) {
        return new Task(description, deadline, group);
    }
}
