package com.scrumify.scrumify.domain.entity.common;

import java.time.Instant;

public interface TimeAuditable {

    Instant getCreatedAt();

    Instant getUpdatedAt();

    void setCreatedAt(Instant createdAt);

    void setUpdatedAt(Instant updatedAt);
}
