package de.interaapps.pastefy.exceptions;

public class NotFoundException extends HTTPException {
    public NotFoundException(String message) {
        super(404, message);
    }
