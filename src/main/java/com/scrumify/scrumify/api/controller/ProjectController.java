package com.scrumify.scrumify.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scrumify.scrumify.api.dto.request.project.CreateProjectRequest;
import com.scrumify.scrumify.api.dto.response.project.ProjectResponse;
import com.scrumify.scrumify.api.dto.response.project.ProjectListResponse;
import com.scrumify.scrumify.api.dto.response.project.ProjectDetailResponse;
import com.scrumify.scrumify.api.mapper.ProjectMapper;
import com.scrumify.scrumify.application.scrum.command.createproject.CreateProjectCommandHandler;
import com.scrumify.scrumify.application.scrum.query.detailproject.ProjectDetailQuery;
import com.scrumify.scrumify.application.scrum.query.detailproject.ProjectDetailQueryHandler;
import com.scrumify.scrumify.application.scrum.query.listproject.ListProjectQuery;
import com.scrumify.scrumify.application.scrum.query.listproject.ListProjectQueryHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final CreateProjectCommandHandler createProjectCommandHandler;
    private final ListProjectQueryHandler listProjectQueryHandler;
    private final ProjectDetailQueryHandler projectDetailQueryHandler;

    public ProjectController(
            CreateProjectCommandHandler createProjectCommandHandler,
            ListProjectQueryHandler listProjectQueryHandler,
            ProjectDetailQueryHandler projectDetailQueryHandler) {
        this.createProjectCommandHandler = createProjectCommandHandler;
        this.listProjectQueryHandler = listProjectQueryHandler;
        this.projectDetailQueryHandler = projectDetailQueryHandler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MEMBER')")
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        var project = createProjectCommandHandler.Handle(
                ProjectMapper.toCreateProjectCommand(request));
        return ProjectMapper.toProjectResponse(project);
    }

    @GetMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ProjectListResponse list() {
        var projects = listProjectQueryHandler.Handle(new ListProjectQuery());
        return ProjectMapper.toProjectListResponse(projects);
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ProjectDetailResponse detail(@PathVariable UUID projectId) {
        var project = projectDetailQueryHandler.Handle(new ProjectDetailQuery(projectId));
        return ProjectMapper.toProjectDetailResponse(project);
    }
}
