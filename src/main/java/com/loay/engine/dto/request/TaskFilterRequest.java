package com.loay.engine.dto.request;

import com.loay.engine.entity.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskFilterRequest(
        TaskStatus status,
        LocalDateTime dueBefore
) {}
