package com.scrumify.scrumify.application.scrum.query.detailpbi;

import java.util.List;

import com.scrumify.scrumify.application.scrum.query.listpbi.PbiResult;

public record PbiDetailResult(
        PbiResult pbi,
        List<TaskItemResult> tasks) {
}
