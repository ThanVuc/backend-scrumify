package com.scrumify.scrumify.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scrumify.scrumify.api.dto.request.pbi.CreatePbiRequest;
import com.scrumify.scrumify.api.dto.request.pbi.UpdatePbiRequest;
import com.scrumify.scrumify.api.dto.response.pbi.BacklogResponse;
import com.scrumify.scrumify.api.dto.response.pbi.PbiDetailResponse;
import com.scrumify.scrumify.api.dto.response.pbi.PbiResponse;
import com.scrumify.scrumify.api.mapper.PbiMapper;
import com.scrumify.scrumify.application.scrum.command.createpbi.CreatePbiCommandHandler;
import com.scrumify.scrumify.application.scrum.command.updatepbi.UpdatePbiCommandHandler;
import com.scrumify.scrumify.application.scrum.query.detailpbi.DetailPbiQuery;
import com.scrumify.scrumify.application.scrum.query.detailpbi.DetailPbiQueryHandler;
import com.scrumify.scrumify.application.scrum.query.listpbi.ListPbiQueryHandler;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/v1")
public class PbiController {
    private final ListPbiQueryHandler listPbiQueryHandler;
    private final DetailPbiQueryHandler detailPbiQueryHandler;
    private final CreatePbiCommandHandler createPbiCommandHandler;
    private final UpdatePbiCommandHandler updatePbiCommandHandler;

    public PbiController(
            ListPbiQueryHandler listPbiQueryHandler,
            DetailPbiQueryHandler detailPbiQueryHandler,
            CreatePbiCommandHandler createPbiCommandHandler,
            UpdatePbiCommandHandler updatePbiCommandHandler) {
        this.listPbiQueryHandler = listPbiQueryHandler;
        this.detailPbiQueryHandler = detailPbiQueryHandler;
        this.createPbiCommandHandler = createPbiCommandHandler;
        this.updatePbiCommandHandler = updatePbiCommandHandler;
    }

    @GetMapping("/projects/{projectId}/pbis")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MEMBER')")
    public BacklogResponse list(
            @PathVariable UUID projectId,
            @RequestParam(required = false)
            @Pattern(regexp = "^(TODO|INPROGRESS|INREVIEW|DONE)$", message = "Status must be TODO, INPROGRESS, INREVIEW, or DONE")
            String status,
            @RequestParam(required = false)
            @Pattern(regexp = "^(feature|bug|technical)$", message = "Type must be feature, bug, or technical")
            String type,
            @RequestParam(required = false) UUID epicId,
            @RequestParam(required = false) String search) {
        var result = listPbiQueryHandler.handle(
                PbiMapper.toListPbiQuery(projectId, status, type, epicId, search));
        return PbiMapper.toBacklogResponse(result);
    }

    @PostMapping("/projects/{projectId}/pbis")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MEMBER')")
    public PbiResponse create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreatePbiRequest request) {
        var result = createPbiCommandHandler.handle(
                PbiMapper.toCreatePbiCommand(projectId, request));
        return PbiMapper.toPbiResponse(result);
    }

    @GetMapping("/pbis/{pbiId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MEMBER')")
    public PbiDetailResponse detail(@PathVariable UUID pbiId) {
        var result = detailPbiQueryHandler.handle(new DetailPbiQuery(pbiId));
        return PbiMapper.toPbiDetailResponse(result);
    }

    @PatchMapping("/pbis/{pbiId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MEMBER')")
    public PbiResponse update(
            @PathVariable UUID pbiId,
            @Valid @RequestBody UpdatePbiRequest request) {
        var result = updatePbiCommandHandler.handle(
                PbiMapper.toUpdatePbiCommand(pbiId, request));
        return PbiMapper.toPbiResponse(result);
    }
}
