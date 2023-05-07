package pl.kamhol1.todoapp.model.event;

import pl.kamhol1.todoapp.model.Task;

import java.time.Clock;

public class TaskDone extends TaskEvent {
    public TaskDone(Task source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
