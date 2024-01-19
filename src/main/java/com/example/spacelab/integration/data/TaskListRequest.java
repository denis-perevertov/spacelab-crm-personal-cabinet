package com.example.spacelab.integration.data;

public record TaskListRequest(
        Long id,
        Boolean applyDefaultsToExistingTasks,
        TaskListDescription taskList
) {
}
