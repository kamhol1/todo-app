package pl.kamhol1.todoapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kamhol1.todoapp.TaskConfigurationProperties;

@RestController
@RequestMapping("/info")
class InfoController {
    private DataSourceProperties dataSource;
    private TaskConfigurationProperties taskConfiguration;

    public InfoController(DataSourceProperties dataSource, TaskConfigurationProperties taskConfiguration) {
        this.dataSource = dataSource;
        this.taskConfiguration = taskConfiguration;
    }

    @GetMapping("/url")
    String url() {
        return dataSource.getUrl();
    }

    @GetMapping("/prop")
    boolean myProp() {
        return taskConfiguration.getTemplate().isAllowMultipleTasks();
    }
}
