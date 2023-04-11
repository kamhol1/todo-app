package pl.kamhol1.todoapp.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.kamhol1.todoapp.model.TaskGroup;
import pl.kamhol1.todoapp.model.TaskGroupRepository;
import pl.kamhol1.todoapp.model.TaskRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {
    @Test
    @DisplayName("Should throw IllegalStateException when there are undone tasks")
    void toggleGroup_undoneTasks_throwsIllegalStateException() {
        // given
        TaskRepository mockTaskRepository = taskRepositoryReturning(true);

        // system under test
        TaskGroupService toTest = new TaskGroupService(null, mockTaskRepository);

        // when
        Throwable exception = catchThrowable(() -> toTest.toggleGroup(1));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("undone tasks");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when there is no group for a given ID")
    void toggleGroup_wrongId_throwsIllegalArgumentException() {
        // given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        // and
        TaskGroupRepository mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.findById(anyInt())).thenReturn(Optional.empty());

        // system under test
        TaskGroupService toTest = new TaskGroupService(mockGroupRepository, mockTaskRepository);

        // when
        Throwable exception = catchThrowable(() -> toTest.toggleGroup(1));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID not found");
    }

    @Test
    @DisplayName("Should toggle group")
    void toggleGroup_worksAsExpected() {
        // given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        // and
        TaskGroup group = new TaskGroup();
        boolean beforeToggle = group.isDone();
        // and
        TaskGroupRepository mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.findById(anyInt())).thenReturn(Optional.of(group));

        // system under test
        TaskGroupService toTest = new TaskGroupService(mockGroupRepository, mockTaskRepository);

        // when
        toTest.toggleGroup(1);

        // then
        assertThat(group.isDone()).isEqualTo(!beforeToggle);
    }

    private static TaskRepository taskRepositoryReturning(boolean value) {
        TaskRepository mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(value);
        return mockTaskRepository;
    }
}