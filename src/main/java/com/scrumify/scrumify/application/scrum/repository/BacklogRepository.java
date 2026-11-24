package com.scrumify.scrumify.application.scrum.repository;

import com.scrumify.scrumify.domain.entity.backlog.Backlog;

public interface BacklogRepository {
    Backlog save(Backlog backlog);
}
