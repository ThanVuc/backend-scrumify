package com.scrumify.scrumify.application.scrum.command.createproject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrumify.scrumify.application.auth.repository.UserRepository;
import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.domain.entity.backlog.Backlog;
import com.scrumify.scrumify.domain.entity.member.Member;
import com.scrumify.scrumify.domain.entity.member.MemberRole;
import com.scrumify.scrumify.domain.entity.project.Project;
import com.scrumify.scrumify.domain.exception.UserNotFoundException;

@Service
public class CreateProjectCommandHandler {
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public CreateProjectCommandHandler(
            CurrentUserService currentUserService,
            UserRepository userRepository,
            ProjectRepository projectRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project Handle(CreateProjectCommand command) {
        var claim = currentUserService.getCurrentUser();
        if (claim == null) {
            throw new UserNotFoundException();
        }

        var user = userRepository.findById(claim.id())
                .orElseThrow(UserNotFoundException::new);

        var project = new Project(
                user,
                command.name(),
                command.codePrefix(),
                command.description());

        var member = new Member();
        member.setUser(user);
        member.setRole(MemberRole.OWNER);
        project.addMember(member);

        var backlog = new Backlog();
        project.setBacklog(backlog);

        return projectRepository.save(project);
    }
}
