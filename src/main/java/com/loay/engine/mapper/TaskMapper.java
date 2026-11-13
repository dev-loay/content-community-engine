package com.loay.engine.mapper;

import com.loay.engine.dto.request.CreateTaskRequest;
import com.loay.engine.dto.request.UpdateTaskRequest;
import com.loay.engine.dto.response.TaskResponse;
import com.loay.engine.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", defaultValue = "IN_PROGRESS")
    @Mapping(target = "priority", defaultValue = "MEDIUM")
    Task toEntity(CreateTaskRequest request);

    TaskResponse toResponse(Task task);

    List<TaskResponse> toResponseList(List<Task> tasks);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateTaskRequest request, @MappingTarget Task task);
}