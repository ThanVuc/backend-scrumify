package com.scrumify.scrumify.application.scrum.query.detailproject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.scrum.repository.MemberRepository;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintRepository;
import com.scrumify.scrumify.domain.exception.ProjectNotFoundException;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

@Service
public class ProjectDetailQueryHandler {
    private final CurrentUserService currentUserService;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final SprintRepository sprintRepository;
    private final PbiRepository pbiRepository;

    public ProjectDetailQueryHandler(
            CurrentUserService currentUserService,
            ProjectRepository projectRepository,
            MemberRepository memberRepository,
            SprintRepository sprintRepository,
            PbiRepository pbiRepository) {
        this.currentUserService = currentUserService;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.sprintRepository = sprintRepository;
        this.pbiRepository = pbiRepository;
    }

    @Transactional(readOnly = true)
    public ProjectDetailResult Handle(ProjectDetailQuery query) {
        var claim = currentUserService.getCurrentUser();
        if (claim == null) {
            throw new UserNotFoundException();
        }
        var project = projectRepository.findDetailByIdAndUserId(query.projectId(), claim.id())
                .orElseThrow(ProjectNotFoundException::new);
        var members = memberRepository.findByProjectId(project.getId());
        var memberDetails = members.stream()
                .map(member -> new ProjectDetailResult.MemberDetailResult(
                        member.getId(),
                        member.getUser().getId(),
                        member.getUser().getFullName(),
                        member.getRole().name().toLowerCase()))
                .toList();
        var createdMember = members.stream()
                .filter(member -> member.getUser().getId().equals(project.getCreatedBy().getId()))
                .findFirst()
                .orElse(null);
        var createdBy = createdMember == null
                ? null
                : new ProjectDetailResult.MemberSummaryResult(
                        createdMember.getId(),
                        createdMember.getUser().getId(),
                        createdMember.getUser().getFullName());
        var activeSprint = sprintRepository.findActiveByProjectId(project.getId())
                .map(sprint -> new ProjectDetailResult.ActiveSprintDetailResult(
                        sprint.getId(),
                        sprint.getName(),
                        sprint.getStartDate(),
                        sprint.getEndDate()))
                .orElse(null);
        var statistic = new ProjectDetailResult.PbiStatisticResult(
                pbiRepository.countTotalByProjectId(project.getId()),
                pbiRepository.countBacklogByProjectId(project.getId()),
                pbiRepository.countDebtByProjectId(project.getId()));
        return new ProjectDetailResult(
                project.getId(),
                createdBy,
                project.getName(),
                project.getCodePrefix(),
                project.getDescription(),
                memberDetails,
                activeSprint,
                statistic,
                project.getCreatedAt(),
                project.getUpdatedAt());
    }
}
