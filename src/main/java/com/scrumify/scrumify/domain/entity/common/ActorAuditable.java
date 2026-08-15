package com.scrumify.scrumify.domain.entity.common;

import java.util.UUID;

public interface ActorAuditable {
    UUID getCreatedBy();

    UUID getUpdatedBy();

    void setCreatedBy(UUID createdBy);

    void setUpdatedBy(UUID updatedBy);
}

