package pl.kamhol1.todoapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.kamhol1.todoapp.model.Task;
import pl.kamhol1.todoapp.model.TaskRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(TaskController.class)
@ActiveProfiles("integration")
class TaskControllerLightIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository repository;

    @Test
    void httpGet_returnsGivenTask() throws Exception {
        // given
        when(repository.findById(anyInt())).thenReturn(Optional.of(new Task("foo", LocalDateTime.now())));

        //when + then
        mockMvc.perform(get("/tasks/111"))
                .andExpect(content().string(containsString("\"description\":\"foo\"")));
    }
}
