package com.loay.engine.service;

import com.loay.engine.dto.request.CreateTaskRequest;
import com.loay.engine.dto.request.TaskFilterRequest;
import com.loay.engine.dto.request.UpdateTaskRequest;
import com.loay.engine.dto.response.TaskResponse;
import com.loay.engine.entity.Task;
import com.loay.engine.entity.User;
import com.loay.engine.entity.enums.TaskStatus;
import com.loay.engine.exception.ForbiddenOperationException;
import com.loay.engine.exception.ResourceNotFoundException;
import com.loay.engine.mapper.TaskMapper;
import com.loay.engine.repository.TaskRepository;
import com.loay.engine.repository.specification.TaskSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponse createTask(UUID userId, CreateTaskRequest request){
        User user = userService.getUserEntityById(userId);

        Task task = taskMapper.toEntity(request);
        task.setUser(user);

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse getTaskById(UUID userId, Long taskId) {
        Task task = getTaskAndVerifyOwnership(userId, taskId);
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(UUID userId, Long taskId, UpdateTaskRequest request){
        Task task = getTaskAndVerifyOwnership(userId, taskId);
        taskMapper.updateEntityFromRequest(request, task);
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updateTaskStatus(UUID userId, Long taskId, TaskStatus status){
        Task task = getTaskAndVerifyOwnership(userId, taskId);
        task.setStatus(status);
        return taskMapper.toResponse(task);
    }

    public Page<TaskResponse> getUserTasks(UUID userId, TaskFilterRequest filter, Pageable pageable){
        userService.getUserEntityById(userId);

        Specification<Task> spec = Specification
                .where(TaskSpec.hasUserId(userId))
                .and(TaskSpec.dueBefore(filter.dueBefore()))
                .and(TaskSpec.hasStatus(filter.status()));

        return taskRepository.findAll(spec, pageable).map(taskMapper::toResponse);
    }

    @Transactional
    public void deleteTask(UUID userId, Long taskId){
        Task task = getTaskAndVerifyOwnership(userId, taskId);
        taskRepository.delete(task);
    }

    private Task getTaskAndVerifyOwnership(UUID userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getUser().getId().equals(userId)) {
            throw new ForbiddenOperationException("You do not have permission to access or modify this task");
        }

        return task;
    }
}
