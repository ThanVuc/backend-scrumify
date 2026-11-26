package com.scrumify.scrumify.application.scrum.query.listproject;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.scrum.repository.MemberRepository;
import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.application.scrum.repository.SprintRepository;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

@Service
public class ListProjectQueryHandler {
    private final CurrentUserService currentUserService;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final SprintRepository sprintRepository;
    private final PbiRepository pbiRepository;

    public ListProjectQueryHandler(
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
    public List<ProjectListItemResult> Handle(ListProjectQuery query) {
        var claim = currentUserService.getCurrentUser();
        if (claim == null) {
            throw new UserNotFoundException();
        }
        return projectRepository.findAllByUserId(claim.id()).stream()
                .map(project -> {
                    var members = memberRepository.findByProjectId(project.getId()).stream()
                            .map(member -> new ProjectListItemResult.MemberSummaryResult(
                                    member.getId(),
                                    member.getUser().getId(),
                                    member.getUser().getFullName()))
                            .toList();
                    var activeSprint = sprintRepository.findActiveByProjectId(project.getId())
                            .map(sprint -> new ProjectListItemResult.ActiveSprintSummaryResult(
                                    sprint.getId(), sprint.getName(), sprint.getEndDate()))
                            .orElse(null);
                    return new ProjectListItemResult(
                            project.getId(),
                            project.getName(),
                            project.getDescription(),
                            members,
                            activeSprint,
                            pbiRepository.countActiveByProjectId(project.getId()),
                            pbiRepository.countTotalByProjectId(project.getId()));
                })
                .toList();
    }
}
