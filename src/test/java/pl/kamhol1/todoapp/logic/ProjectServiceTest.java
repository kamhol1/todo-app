package pl.kamhol1.todoapp.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.kamhol1.todoapp.TaskConfigurationProperties;
import pl.kamhol1.todoapp.model.*;
import pl.kamhol1.todoapp.model.projection.GroupReadModel;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {
    @Test
    @DisplayName("Should throw IllegalStateException when configured no multiple groups and the other undone group exists")
    void createGroup_noMultipleGroupsConfigAndUndoneGroupExists_throwsIllegalStateException() {
        //given
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(false);

        // system under test
        ProjectService toTest = new ProjectService(null, mockGroupRepository, null, mockConfig);

        //when
        Throwable exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("undone group");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when configuration is OK and there are no projects for a given ID")
    void createGroup_configurationOkAndNoProjects_throwsIllegalArgumentException() {
        //given
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);

        // system under test
        ProjectService toTest = new ProjectService(mockRepository, null, null, mockConfig);

        //when
        Throwable exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID not found");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when configured no multiple groups and there are no group and projects for a given ID")
    void createGroup_noMultipleGroupsConfigAndNoUndoneGroupExistsNoProjects_throwsIllegalArgumentException() {
        //given
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);

        // system under test
        ProjectService toTest = new ProjectService(mockRepository, mockGroupRepository, null, mockConfig);

        //when
        Throwable exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));

        //then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID not found");
    }

    @Test
    @DisplayName("Should create new group from project")
    void createGroup_configurationOkProjectExists_createsAndSavesGroup() {
        // given
        LocalDateTime today = LocalDate.now().atStartOfDay();
        // and
        Project project = projectWith("bar", Set.of(-1, -2));
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt()))
                .thenReturn(Optional.of(project));
        // and
        InMemoryGroupRepository inMemoryGroupRepository = inMemoryGroupRepository();
        TaskGroupService serviceWithInMemRepository = dummyGroupService(inMemoryGroupRepository);
        int countBeforeCall = inMemoryGroupRepository.count();
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);

        // system under test
        ProjectService toTest = new ProjectService(mockRepository, inMemoryGroupRepository, serviceWithInMemRepository, mockConfig);

        //when
        GroupReadModel result = toTest.createGroup(today, 1);

        //then
        assertThat(result.getDescription()).isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks()).allMatch(task -> task.getDescription().equals("foo"));
        assertThat(countBeforeCall + 1).isEqualTo(inMemoryGroupRepository.count());
    }

    private static TaskGroupService dummyGroupService(InMemoryGroupRepository inMemoryGroupRepository) {
        return new TaskGroupService(inMemoryGroupRepository, null);
    }

    private Project projectWith(String description, Set<Integer> daysToDeadline) {
        Set<ProjectStep> steps = daysToDeadline.stream()
                .map(days -> {
                    ProjectStep step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("foo");
                    when(step.getDaysToDeadline()).thenReturn(days);
                    return step;
                }).collect(Collectors.toSet());
        Project result = mock(Project.class);
        when(result.getDescription()).thenReturn(description);
        when(result.getSteps()).thenReturn(steps);
        return result;
    }

    private InMemoryGroupRepository inMemoryGroupRepository() {
        return new InMemoryGroupRepository();
    }

    private static TaskGroupRepository groupRepositoryReturning(boolean value) {
        TaskGroupRepository mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(value);
        return mockGroupRepository;
    }

    private static TaskConfigurationProperties configurationReturning(boolean value) {
        TaskConfigurationProperties.Template mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(value);
        TaskConfigurationProperties mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }

    private static class InMemoryGroupRepository implements TaskGroupRepository {
        private int index = 0;
        private Map<Integer, TaskGroup> map = new HashMap<>();

        public int count() {
            return map.values().size();
        }

        @Override
        public List<TaskGroup> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public Optional<TaskGroup> findById(Integer id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public TaskGroup save(TaskGroup entity) {
            if (entity.getId() == 0) {
                try {
                    Field field = TaskGroup.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, ++index);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            map.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public boolean existsByDoneIsFalseAndProject_Id(Integer projectId) {
            return map.values().stream()
                    .filter(group -> !group.isDone())
                    .anyMatch(group -> group.getProject() != null && group.getProject().getId() == projectId);
        }

        @Override
        public boolean existsByDescription(String description) {
            return map.values().stream()
                    .anyMatch(group -> !group.getDescription().equals(description));
        }
    }
}
