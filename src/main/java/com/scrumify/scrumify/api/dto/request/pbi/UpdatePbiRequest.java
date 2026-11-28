package com.scrumify.scrumify.api.dto.request.pbi;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdatePbiRequest {

    @Size(max = 256, message = "Title must not exceed 256 characters")
    private String title;
    private boolean titlePresent;

    private String description;
    private boolean descriptionPresent;

    private String type;
    private boolean typePresent;

    private String priority;
    private boolean priorityPresent;

    @Min(value = 0, message = "Story points must be greater than or equal to 0")
    private Integer storyPoints;
    private boolean storyPointsPresent;

    private UUID epicId;
    private boolean epicIdPresent;

    private String status;
    private boolean statusPresent;

    private UUID sprintId;
    private boolean sprintIdPresent;

    private Boolean moveToTop;
    private Boolean archived;

    @JsonSetter("title")
    public void setTitle(String title) {
        this.title = title;
        this.titlePresent = true;
    }

    @JsonSetter("description")
    public void setDescription(String description) {
        this.description = description;
        this.descriptionPresent = true;
    }

    @JsonSetter("type")
    public void setType(String type) {
        this.type = type;
        this.typePresent = true;
    }

    @JsonSetter("priority")
    public void setPriority(String priority) {
        this.priority = priority;
        this.priorityPresent = true;
    }

    @JsonSetter("storyPoints")
    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
        this.storyPointsPresent = true;
    }

    @JsonSetter("epicId")
    public void setEpicId(UUID epicId) {
        this.epicId = epicId;
        this.epicIdPresent = true;
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = status;
        this.statusPresent = true;
    }

    @JsonSetter("sprintId")
    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
        this.sprintIdPresent = true;
    }

    public void setMoveToTop(Boolean moveToTop) {
        this.moveToTop = moveToTop;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    @AssertTrue(message = "Title must not be blank")
    public boolean isTitleValid() {
        return !titlePresent || (title != null && !title.isBlank());
    }

    @AssertTrue(message = "Type must be feature, bug, or technical")
    public boolean isTypeValid() {
        return !typePresent || (type != null && type.matches("^(feature|bug|technical)$"));
    }

    @AssertTrue(message = "Priority must be LOW, MEDIUM, HIGH, or HIGHEST")
    public boolean isPriorityValid() {
        return !priorityPresent || (priority != null && priority.matches("^(LOW|MEDIUM|HIGH|HIGHEST)$"));
    }

    @AssertTrue(message = "Status must be TODO, INPROGRESS, INREVIEW, or DONE")
    public boolean isStatusValid() {
        return !statusPresent || (status != null && status.matches("^(TODO|INPROGRESS|INREVIEW|DONE)$"));
    }
}
