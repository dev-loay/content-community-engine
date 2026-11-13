package com.loay.engine.repository.specification;

import com.loay.engine.entity.Task;
import com.loay.engine.entity.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class TaskSpec {
    public static Specification<Task> hasUserId(UUID userId){
        return (root, query, cb) -> {
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<Task> dueBefore(LocalDateTime date){
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.lessThan(root.get("dueDate"), date);
        };
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }
}
