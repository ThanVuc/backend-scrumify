package com.scrumify.scrumify.api.mapper;

import java.util.List;

import com.scrumify.scrumify.api.dto.request.project.CreateProjectRequest;
import com.scrumify.scrumify.api.dto.response.project.ProjectDetailResponse;
import com.scrumify.scrumify.api.dto.response.project.ProjectListResponse;
import com.scrumify.scrumify.api.dto.response.project.ProjectResponse;
import com.scrumify.scrumify.application.scrum.command.createproject.CreateProjectCommand;
import com.scrumify.scrumify.application.scrum.query.detailproject.ProjectDetailResult;
import com.scrumify.scrumify.application.scrum.query.listproject.ProjectListItemResult;
import com.scrumify.scrumify.domain.entity.project.Project;

public final class ProjectMapper {
    private ProjectMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static CreateProjectCommand toCreateProjectCommand(CreateProjectRequest request) {
        return new CreateProjectCommand(request.name(), request.description(), request.codePrefix());
    }

    public static ProjectResponse toProjectResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getCreatedBy().getId(),
                project.getName(),
                project.getCodePrefix(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getUpdatedAt());
    }

    public static ProjectListResponse toProjectListResponse(List<ProjectListItemResult> projects) {
        var items = projects.stream()
                .map(project -> new ProjectListResponse.ProjectListItem(
                        project.id(),
                        project.name(),
                        project.members().stream()
                                .map(member -> new ProjectListResponse.MemberSummary(
                                        member.id(), member.userId(), member.name()))
                                .toList(),
                        project.activeSprint() == null
                                ? null
                                : new ProjectListResponse.ActiveSprintSummary(
                                        project.activeSprint().id(),
                                        project.activeSprint().name(),
                                        project.activeSprint().end()),
                        project.activePBIsCount(),
                        project.totalPBIsCount()))
                .toList();
        return new ProjectListResponse(items);
    }

    public static ProjectDetailResponse toProjectDetailResponse(ProjectDetailResult project) {
        var createdBy = project.createdBy() == null
                ? null
                : new ProjectDetailResponse.MemberSummary(
                        project.createdBy().id(),
                        project.createdBy().userId(),
                        project.createdBy().name());
        var activeSprint = project.activeSprint() == null
                ? null
                : new ProjectDetailResponse.ActiveSprintDetail(
                        project.activeSprint().id(),
                        project.activeSprint().name(),
                        project.activeSprint().start(),
                        project.activeSprint().end());
        return new ProjectDetailResponse(
                project.id(),
                createdBy,
                project.name(),
                project.codePrefix(),
                project.description(),
                project.members().stream()
                        .map(member -> new ProjectDetailResponse.MemberDetail(
                                member.id(), member.userId(), member.name(), member.role()))
                        .toList(),
                activeSprint,
                new ProjectDetailResponse.PbiStatistic(
                        project.pbiStatistic().total(),
                        project.pbiStatistic().backlogTotal(),
                        project.pbiStatistic().debtTotal()),
                project.createdAt(),
                project.updatedAt());
    }
}
