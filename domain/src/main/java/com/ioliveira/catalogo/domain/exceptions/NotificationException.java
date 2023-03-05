package com.ioliveira.catalogo.domain.exceptions;

import com.ioliveira.catalogo.domain.validation.handler.Notification;

public class NotificationException extends DomainException {
    public NotificationException(final Notification notification) {
        super(notification.getErrors());
    }
}
