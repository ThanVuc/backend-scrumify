package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrumify.scrumify.domain.entity.comment.Comment;

public interface JpaCommentRepository extends JpaRepository<Comment, UUID> {
}
