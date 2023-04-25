package pl.kamhol1.todoapp.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.kamhol1.todoapp.logic.TaskGroupService;
import pl.kamhol1.todoapp.logic.TaskService;
import pl.kamhol1.todoapp.model.ProjectStep;
import pl.kamhol1.todoapp.model.Task;
import pl.kamhol1.todoapp.model.TaskGroup;
import pl.kamhol1.todoapp.model.TaskRepository;
import pl.kamhol1.todoapp.model.projection.GroupReadModel;
import pl.kamhol1.todoapp.model.projection.GroupTaskWriteModel;
import pl.kamhol1.todoapp.model.projection.GroupWriteModel;
import pl.kamhol1.todoapp.model.projection.ProjectWriteModel;

import javax.print.attribute.standard.Media;
import java.net.URI;
import java.util.List;

@Controller
@IllegalExceptionProcessing
@RequestMapping("/groups")
class TaskGroupController {
    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskGroupService service;
    private final TaskRepository repository;

    TaskGroupController(TaskGroupService service, TaskRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model) {
        model.addAttribute("group", new GroupWriteModel());
        return "groups";
    }

    @PostMapping(produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroup(
            @ModelAttribute("group") @Valid GroupWriteModel current,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) return "groups";

        service.createModel(current);
        model.addAttribute("group", new GroupWriteModel());
        model.addAttribute("groups", getGroups());
        model.addAttribute("message", "Group created successfully");
        return "groups";
    }

    @ModelAttribute("groups")
    List<GroupReadModel> getGroups() {
        return service.readAll();
    }

    @PostMapping(params = "addTask", produces = MediaType.TEXT_HTML_VALUE)
    String addGroupTask(@ModelAttribute("group") GroupWriteModel current) {
        current.getTasks().add(new GroupTaskWriteModel());
        return "groups";
    }

    @ResponseBody
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate) {
        GroupReadModel result = service.createModel(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupReadModel>> readAllGroups() {
        return ResponseEntity.ok(service.readAll());
    }

    @ResponseBody
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id) {
        return ResponseEntity.ok(repository.findAllByGroup_Id(id));
    }

    @ResponseBody
    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleGroup(@PathVariable int id) {
        service.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }
}
