package com.scrumify.scrumify.domain.exception;

public class OrderCannotBeModifiedException extends BusinessValidationException {

    public OrderCannotBeModifiedException() {
        super("core.order.cannot_be_modified", "Order cannot be modified in its current status");
    }
}
