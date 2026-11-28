package com.scrumify.scrumify.domain.exception;

public class PbiNotFoundException extends ResourceNotFoundException {
    public PbiNotFoundException() {
        super("core.pbi.not_found", "PBI not found");
    }
}
