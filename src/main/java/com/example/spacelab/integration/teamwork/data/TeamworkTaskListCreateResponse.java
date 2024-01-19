package com.example.spacelab.integration.teamwork.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TeamworkTaskListCreateResponse(
        @JsonProperty("TASKLISTID")
        String taskListId,
        @JsonProperty("STATUS")
        String status
) {
}
