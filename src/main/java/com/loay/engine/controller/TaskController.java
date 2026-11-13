package com.loay.engine.controller;

import com.loay.engine.dto.request.CreateTaskRequest;
import com.loay.engine.dto.request.TaskFilterRequest;
import com.loay.engine.dto.request.UpdateTaskRequest;
import com.loay.engine.dto.response.TaskResponse;
import com.loay.engine.entity.enums.TaskStatus;
import com.loay.engine.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateTaskRequest request) {

        TaskResponse response = taskService.createTask(userId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{taskId}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable UUID userId,
            @PathVariable Long taskId) {

        return ResponseEntity.ok(taskService.getTaskById(userId, taskId));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getUserTasks(
            @PathVariable UUID userId,
            @ModelAttribute TaskFilterRequest filter,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(taskService.getUserTasks(userId, filter, pageable));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID userId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request) {

        return ResponseEntity.ok(taskService.updateTask(userId, taskId, request));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable UUID userId,
            @PathVariable Long taskId,
            @RequestParam TaskStatus status) {

        return ResponseEntity.ok(taskService.updateTaskStatus(userId, taskId, status));
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable UUID userId,
            @PathVariable Long taskId) {

        taskService.deleteTask(userId, taskId);
    }
}